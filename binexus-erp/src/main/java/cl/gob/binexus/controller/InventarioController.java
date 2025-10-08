package cl.gob.binexus.controller;

import cl.gob.binexus.domain.entity.Inventario;
import cl.gob.binexus.domain.entity.MovimientoInventario;
import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.enums.TipoMovimientoInventario;
import cl.gob.binexus.service.InventarioService;
import cl.gob.binexus.service.OrganizacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    private final InventarioService inventarioService;
    private final OrganizacionService organizacionService;
    public InventarioController(InventarioService inventarioService,
                                OrganizacionService organizacionService) {
        this.inventarioService = inventarioService;
        this.organizacionService = organizacionService;
    }

    @GetMapping
    public String listar(@RequestParam(value = "organizacionId", required = false) Long organizacionId,
                         Model model) {
        List<Organizacion> organizaciones = organizacionService.listarOrganizaciones();
        Organizacion organizacion = organizaciones.isEmpty() ? null : organizaciones.get(0);
        if (organizacionId != null) {
            organizacion = organizacionService.obtenerPorId(organizacionId);
        }
        if (organizacion != null) {
            List<Inventario> inventarios = inventarioService.listarPorOrganizacion(organizacion);
            model.addAttribute("inventarios", inventarios);
        }
        model.addAttribute("organizaciones", organizaciones);
        model.addAttribute("organizacionSeleccionada", organizacion != null ? organizacion.getId() : null);
        model.addAttribute("tiposMovimiento", TipoMovimientoInventario.values());
        model.addAttribute("pageTitle", "Inventario");
        return "inventory/inventory-list";
    }

    @GetMapping("/{inventarioId}/movimientos")
    public String movimientos(@PathVariable Long inventarioId, Model model) {
        Inventario inventario = inventarioService.obtenerPorId(inventarioId);
        List<MovimientoInventario> movimientos = inventarioService.listarMovimientos(inventario);
        model.addAttribute("inventario", inventario);
        model.addAttribute("movimientos", movimientos);
        return "inventory/inventory-movements";
    }

    @PostMapping("/{inventarioId}/ajustar")
    public String ajustar(@PathVariable Long inventarioId,
                          @RequestParam("tipo") TipoMovimientoInventario tipo,
                          @RequestParam("cantidad") BigDecimal cantidad,
                          RedirectAttributes redirectAttributes) {
        Inventario inventario = inventarioService.obtenerPorId(inventarioId);
        try {
            inventarioService.registrarMovimiento(inventario, tipo, cantidad);
            redirectAttributes.addFlashAttribute("toastSuccess", "Movimiento registrado correctamente");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("toastError", ex.getMessage());
        }
        return "redirect:/inventario?organizacionId=" + inventario.getOrganizacion().getId();
    }
}
