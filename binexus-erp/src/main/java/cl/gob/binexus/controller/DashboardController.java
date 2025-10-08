package cl.gob.binexus.controller;

import cl.gob.binexus.domain.entity.Inventario;
import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.service.ClienteService;
import cl.gob.binexus.service.InventarioService;
import cl.gob.binexus.service.OrganizacionService;
import cl.gob.binexus.service.ProductoService;
import cl.gob.binexus.service.VentaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    private final OrganizacionService organizacionService;
    private final ProductoService productoService;
    private final VentaService ventaService;
    private final ClienteService clienteService;
    private final InventarioService inventarioService;

    public DashboardController(OrganizacionService organizacionService,
                               ProductoService productoService,
                               VentaService ventaService,
                               ClienteService clienteService,
                               InventarioService inventarioService) {
        this.organizacionService = organizacionService;
        this.productoService = productoService;
        this.ventaService = ventaService;
        this.clienteService = clienteService;
        this.inventarioService = inventarioService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        List<Organizacion> organizaciones = organizacionService.listarOrganizaciones();
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = inicioMes.plusMonths(1).minusDays(1);

        long productosActivos = organizaciones.stream()
                .mapToLong(org -> productoService.buscarProductos(org, cl.gob.binexus.domain.enums.EstadoProducto.ACTIVO, null).size())
                .sum();

        long totalClientes = organizaciones.stream()
                .mapToLong(org -> clienteService.buscar(org, null, null).size())
                .sum();

        BigDecimal stockGeneral = organizaciones.stream()
                .flatMap(org -> inventarioService.listarPorOrganizacion(org).stream())
                .map(Inventario::getStockTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long ventasMes = organizaciones.stream()
                .mapToLong(org -> ventaService.contarVentasMes(org, inicioMes, finMes))
                .sum();

        Organizacion organizacionPrincipal = organizaciones.isEmpty() ? null : organizaciones.get(0);
        Map<LocalDate, BigDecimal> ventasPorDia = organizacionPrincipal != null
                ? ventaService.totalPorDia(organizacionPrincipal, inicioMes, LocalDate.now())
                : Collections.emptyMap();

        List<String> labels = new ArrayList<>();
        List<BigDecimal> data = new ArrayList<>();
        ventasPorDia.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    labels.add(entry.getKey().toString());
                    data.add(entry.getValue());
                });

        model.addAttribute("username", user != null ? user.getUsername() : "Usuario");
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("productosActivos", productosActivos);
        model.addAttribute("ventasMes", ventasMes);
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("stockGeneral", stockGeneral);
        model.addAttribute("ventasLabels", labels);
        model.addAttribute("ventasData", data);
        return "dashboard";
    }
}
