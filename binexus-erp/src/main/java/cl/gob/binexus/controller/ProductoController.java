package cl.gob.binexus.controller;

import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.entity.Parametro;
import cl.gob.binexus.domain.entity.PrecioProducto;
import cl.gob.binexus.domain.entity.Producto;
import cl.gob.binexus.domain.enums.EstadoProducto;
import cl.gob.binexus.dto.ProductoFormDTO;
import cl.gob.binexus.service.InventarioService;
import cl.gob.binexus.service.OrganizacionService;
import cl.gob.binexus.service.ParametroService;
import cl.gob.binexus.service.ProductoService;
import cl.gob.binexus.service.TipoParametroService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final OrganizacionService organizacionService;
    private final ParametroService parametroService;
    private final TipoParametroService tipoParametroService;
    private final InventarioService inventarioService;

    public ProductoController(ProductoService productoService,
                              OrganizacionService organizacionService,
                              ParametroService parametroService,
                              TipoParametroService tipoParametroService,
                              InventarioService inventarioService) {
        this.productoService = productoService;
        this.organizacionService = organizacionService;
        this.parametroService = parametroService;
        this.tipoParametroService = tipoParametroService;
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public String listar(@RequestParam(value = "organizacionId", required = false) Long organizacionId,
                         @RequestParam(value = "estado", required = false) EstadoProducto estado,
                         @RequestParam(value = "q", required = false) String consulta,
                         Model model) {
        Organizacion organizacion = null;
        if (organizacionId != null) {
            organizacion = organizacionService.obtenerPorId(organizacionId);
        }
        List<Producto> productos = productoService.buscarProductos(organizacion, estado, consulta);
        Map<Long, java.math.BigDecimal> preciosVigentes = new java.util.HashMap<>();
        productos.forEach(prod -> productoService.obtenerPrecioVigente(prod, LocalDate.now())
                .ifPresent(precio -> preciosVigentes.put(prod.getId(), precio.getPrecioUnitario())));
        model.addAttribute("productos", productos);
        model.addAttribute("preciosVigentes", preciosVigentes);
        model.addAttribute("organizaciones", organizacionService.listarOrganizaciones());
        model.addAttribute("estados", EstadoProducto.values());
        model.addAttribute("organizacionId", organizacionId);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("query", consulta);
        model.addAttribute("pageTitle", "Productos");
        return "products/products-list";
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        ProductoFormDTO dto = new ProductoFormDTO();
        prepararFormulario(model, dto);
        model.addAttribute("pageTitle", "Nuevo producto");
        return "products/products-form";
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerPorId(id);
        ProductoFormDTO dto = mapearProducto(producto);
        dto.setNuevoPrecio(false);
        prepararFormulario(model, dto);
        model.addAttribute("pageTitle", "Editar producto");
        return "products/products-form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("producto") ProductoFormDTO dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        boolean esNuevo = dto.getId() == null;
        if ((esNuevo || dto.isNuevoPrecio()) && dto.getPrecioUnitario() == null) {
            result.rejectValue("precioUnitario", "precio.requerido", "Debe ingresar un precio vigente");
        }
        if (result.hasErrors()) {
            prepararFormulario(model, dto);
            model.addAttribute("pageTitle", esNuevo ? "Nuevo producto" : "Editar producto");
            return "products/products-form";
        }
        try {
            Producto producto = esNuevo ? new Producto() : productoService.obtenerPorId(dto.getId());
            producto.setNombre(dto.getNombre());
            producto.setEstado(dto.getEstado());
            producto.setStockInicial(dto.getStockInicial() != null ? dto.getStockInicial() : BigDecimal.ZERO);
            producto.setOrganizacion(organizacionService.obtenerPorId(dto.getOrganizacionId()));

            Parametro parametro = null;
            if (dto.getParametroId() != null) {
                parametro = parametroService.obtenerPorId(dto.getParametroId());
            }
            BigDecimal precio = (esNuevo || dto.isNuevoPrecio()) ? dto.getPrecioUnitario() : null;
            LocalDate fechaInicio = (esNuevo || dto.isNuevoPrecio()) ? dto.getFechaInicioPrecio() : null;
            Producto guardado = productoService.guardarProducto(producto, precio, fechaInicio, parametro);
            if (esNuevo) {
                inventarioService.inicializarInventario(guardado, guardado.getOrganizacion(), dto.getStockInicial());
            }
            redirectAttributes.addFlashAttribute("toastSuccess", "Producto guardado correctamente");
            return "redirect:/productos";
        } catch (IllegalArgumentException ex) {
            result.reject("error.global", ex.getMessage());
            prepararFormulario(model, dto);
            model.addAttribute("pageTitle", esNuevo ? "Nuevo producto" : "Editar producto");
            return "products/products-form";
        }
    }

    @GetMapping("/{id}/precios")
    public String historialPrecios(@PathVariable Long id, Model model) {
        List<PrecioProducto> historial = productoService.obtenerHistorialPrecios(id);
        model.addAttribute("historial", historial);
        return "products/components/price-history :: priceHistory";
    }

    private void prepararFormulario(Model model, ProductoFormDTO dto) {
        model.addAttribute("producto", dto);
        model.addAttribute("organizaciones", organizacionService.listarOrganizaciones());
        model.addAttribute("estados", EstadoProducto.values());
        model.addAttribute("parametrosPorTipo", obtenerParametrosPorTipo());
    }

    private Map<String, List<Parametro>> obtenerParametrosPorTipo() {
        Map<String, List<Parametro>> parametros = new LinkedHashMap<>();
        tipoParametroService.listar().forEach(tipo ->
                parametros.put(tipo.getDescripcion(), parametroService.listarPorTipo(tipo, true))
        );
        return parametros;
    }

    private ProductoFormDTO mapearProducto(Producto producto) {
        ProductoFormDTO dto = new ProductoFormDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setOrganizacionId(producto.getOrganizacion().getId());
        dto.setEstado(producto.getEstado());
        dto.setStockInicial(producto.getStockInicial());
        if (producto.getAtributo() != null && producto.getAtributo().getMantenedor() != null) {
            dto.setParametroId(producto.getAtributo().getMantenedor().getId());
        }
        return dto;
    }
}
