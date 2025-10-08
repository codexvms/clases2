package cl.gob.binexus.controller;

import cl.gob.binexus.domain.entity.*;
import cl.gob.binexus.domain.enums.EstadoCliente;
import cl.gob.binexus.domain.enums.EstadoProducto;
import cl.gob.binexus.domain.enums.EstadoPago;
import cl.gob.binexus.domain.enums.MetodoPago;
import cl.gob.binexus.domain.enums.TipoVenta;
import cl.gob.binexus.dto.VentaDetalleFormDTO;
import cl.gob.binexus.dto.VentaFormDTO;
import cl.gob.binexus.service.ClienteService;
import cl.gob.binexus.service.LocalService;
import cl.gob.binexus.service.OrganizacionService;
import cl.gob.binexus.service.ProductoService;
import cl.gob.binexus.service.UsuarioService;
import cl.gob.binexus.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final LocalService localService;
    private final UsuarioService usuarioService;
    private final OrganizacionService organizacionService;

    public VentaController(VentaService ventaService,
                           ClienteService clienteService,
                           ProductoService productoService,
                           LocalService localService,
                           UsuarioService usuarioService,
                           OrganizacionService organizacionService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.localService = localService;
        this.usuarioService = usuarioService;
        this.organizacionService = organizacionService;
    }

    @GetMapping
    public String listar(@RequestParam(value = "organizacionId", required = false) Long organizacionId,
                         @RequestParam(value = "estadoPago", required = false) EstadoPago estadoPago,
                         @RequestParam(value = "localId", required = false) Long localId,
                         @RequestParam(value = "desde", required = false) LocalDate desde,
                         @RequestParam(value = "hasta", required = false) LocalDate hasta,
                         Model model) {
        Organizacion organizacion = null;
        if (organizacionId != null) {
            organizacion = organizacionService.obtenerPorId(organizacionId);
        }
        Local local = null;
        if (localId != null) {
            local = localService.obtenerPorId(localId);
        }
        if (organizacion != null) {
            List<Venta> ventas = ventaService.buscar(organizacion, estadoPago, local, desde, hasta);
            model.addAttribute("ventas", ventas);
        }
        model.addAttribute("organizaciones", organizacionService.listarOrganizaciones());
        model.addAttribute("locales", localService.listarLocales());
        model.addAttribute("organizacionId", organizacionId);
        model.addAttribute("localId", localId);
        model.addAttribute("estadoPago", estadoPago);
        model.addAttribute("estadosPago", EstadoPago.values());
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("pageTitle", "Ventas");
        return "sales/sales-list";
    }

    @GetMapping("/new")
    public String nuevaVenta(Model model) {
        VentaFormDTO form = new VentaFormDTO();
        form.getDetalles().add(new VentaDetalleFormDTO());
        prepararFormulario(model, form);
        model.addAttribute("pageTitle", "Nueva venta");
        return "sales/sales-form";
    }

    @PostMapping
    public String registrarVenta(@Valid @ModelAttribute("venta") VentaFormDTO form,
                                 BindingResult result,
                                 @AuthenticationPrincipal User userDetails,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (form.getDetalles() == null) {
            form.setDetalles(new ArrayList<>());
        }
        List<VentaDetalleFormDTO> detallesValidos = form.getDetalles().stream()
                .filter(detalle -> detalle.getProductoId() != null)
                .collect(Collectors.toList());
        if (detallesValidos.isEmpty()) {
            result.reject("detalle.vacio", "Debe agregar al menos un producto");
        }
        if (result.hasErrors()) {
            prepararFormulario(model, form);
            model.addAttribute("pageTitle", "Nueva venta");
            return "sales/sales-form";
        }
        try {
            if (userDetails == null) {
                throw new IllegalArgumentException("No se pudo identificar al usuario de la sesión");
            }
            Venta venta = new Venta();
            Cliente cliente = clienteService.obtenerPorId(form.getClienteId());
            Local local = localService.obtenerPorId(form.getLocalId());
            Usuario usuario = usuarioService.obtenerPorCorreo(userDetails.getUsername());
            venta.setCliente(cliente);
            venta.setLocal(local);
            venta.setOrganizacion(local.getOrganizacion());
            venta.setUsuario(usuario);
            venta.setTipoVenta(form.getTipoVenta());
            venta.setMetodoPago(form.getMetodoPago());
            venta.setEstadoPago(form.getEstadoPago());
            venta.setDescuentoTotal(form.getDescuentoTotal() != null ? form.getDescuentoTotal() : BigDecimal.ZERO);

            List<DetalleVenta> detalles = new ArrayList<>();
            for (VentaDetalleFormDTO detalleDto : detallesValidos) {
                DetalleVenta detalle = new DetalleVenta();
                Producto producto = new Producto();
                producto.setId(detalleDto.getProductoId());
                detalle.setProducto(producto);
                detalle.setCantidad(detalleDto.getCantidad());
                detalle.setPrecioUnitario(detalleDto.getPrecioUnitario());
                detalle.setCostoEnvio(detalleDto.getCostoEnvio());
                detalles.add(detalle);
            }
            venta.setDetalles(detalles);
            ventaService.registrarVenta(venta);
            redirectAttributes.addFlashAttribute("toastSuccess", "Venta registrada correctamente");
            return "redirect:/ventas";
        } catch (IllegalArgumentException ex) {
            result.reject("error.global", ex.getMessage());
            prepararFormulario(model, form);
            model.addAttribute("pageTitle", "Nueva venta");
            return "sales/sales-form";
        }
    }

    @GetMapping("/{id}/ticket")
    public String verTicket(@PathVariable Long id, Model model) {
        Venta venta = ventaService.obtenerPorId(id);
        model.addAttribute("venta", venta);
        model.addAttribute("pageTitle", "Boleta " + venta.getOrderId());
        return "sales/sales-ticket";
    }

    private void prepararFormulario(Model model, VentaFormDTO form) {
        model.addAttribute("venta", form);
        model.addAttribute("clientes", cargarClientesActivos());
        model.addAttribute("locales", localService.listarLocales());
        model.addAttribute("productos", productoService.buscarProductos(null, EstadoProducto.ACTIVO, null));
        model.addAttribute("tiposVenta", TipoVenta.values());
        model.addAttribute("metodosPago", MetodoPago.values());
        model.addAttribute("estadosPago", EstadoPago.values());
    }

    private List<Cliente> cargarClientesActivos() {
        return organizacionService.listarOrganizaciones().stream()
                .flatMap(org -> clienteService.buscar(org, EstadoCliente.ACTIVO, null).stream())
                .collect(Collectors.toList());
    }
}
