package cl.gob.binexus.controller;

import cl.gob.binexus.domain.entity.CierreCaja;
import cl.gob.binexus.domain.entity.Usuario;
import cl.gob.binexus.domain.enums.EstadoCaja;
import cl.gob.binexus.dto.CierreCajaAperturaForm;
import cl.gob.binexus.dto.CierreCajaCerrarForm;
import cl.gob.binexus.dto.CierreCajaResumenDTO;
import cl.gob.binexus.service.CierreCajaService;
import cl.gob.binexus.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/caja")
public class CajaController {

    private final CierreCajaService cierreCajaService;
    private final UsuarioService usuarioService;

    public CajaController(CierreCajaService cierreCajaService, UsuarioService usuarioService) {
        this.cierreCajaService = cierreCajaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/abrir")
    public String mostrarApertura(Model model,
                                  @AuthenticationPrincipal User userDetails,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(userDetails);
        Optional<CierreCaja> cajaAbierta = cierreCajaService.obtenerCajaAbierta(usuario);
        if (cajaAbierta.isPresent()) {
            redirectAttributes.addFlashAttribute("toastInfo", "Ya tienes una caja abierta");
            return "redirect:/caja/cerrar";
        }
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new CierreCajaAperturaForm());
        }
        model.addAttribute("pageTitle", "Apertura de caja");
        return "cashbox/caja-abrir";
    }

    @PostMapping("/abrir")
    public String abrirCaja(@Valid @ModelAttribute("form") CierreCajaAperturaForm form,
                             BindingResult result,
                             @AuthenticationPrincipal User userDetails,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(userDetails);
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Apertura de caja");
            return "cashbox/caja-abrir";
        }
        if (cierreCajaService.obtenerCajaAbierta(usuario).isPresent()) {
            result.reject("caja.abierta", "Ya tienes una caja abierta");
            model.addAttribute("pageTitle", "Apertura de caja");
            return "cashbox/caja-abrir";
        }
        cierreCajaService.abrirCaja(usuario, form.getMontoInicial());
        redirectAttributes.addFlashAttribute("toastSuccess", "Caja abierta correctamente");
        return "redirect:/caja/cerrar";
    }

    @GetMapping("/cerrar")
    public String mostrarCierre(Model model,
                                @AuthenticationPrincipal User userDetails,
                                RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(userDetails);
        Optional<CierreCaja> cajaOpt = cierreCajaService.obtenerCajaAbierta(usuario);
        if (cajaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("toastInfo", "No tienes una caja abierta actualmente");
            return "redirect:/caja/historial";
        }
        CierreCaja caja = cajaOpt.get();
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new CierreCajaCerrarForm());
        }
        CierreCajaResumenDTO resumen = cierreCajaService.generarResumen(caja, caja.getMontoFinal());
        model.addAttribute("caja", caja);
        model.addAttribute("resumen", resumen);
        model.addAttribute("pageTitle", "Cerrar caja");
        return "cashbox/caja-cerrar";
    }

    @PostMapping("/cerrar")
    public String cerrarCaja(@Valid @ModelAttribute("form") CierreCajaCerrarForm form,
                              BindingResult result,
                              @AuthenticationPrincipal User userDetails,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(userDetails);
        Optional<CierreCaja> cajaOpt = cierreCajaService.obtenerCajaAbierta(usuario);
        if (cajaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("toastInfo", "No existe una caja abierta para cerrar");
            return "redirect:/caja/historial";
        }
        CierreCaja caja = cajaOpt.get();
        if (result.hasErrors()) {
            CierreCajaResumenDTO resumen = cierreCajaService.generarResumen(caja, form.getMontoFinal());
            model.addAttribute("caja", caja);
            model.addAttribute("resumen", resumen);
            model.addAttribute("pageTitle", "Cerrar caja");
            return "cashbox/caja-cerrar";
        }
        CierreCaja cerrada = cierreCajaService.cerrarCaja(caja, form.getMontoFinal());
        CierreCajaResumenDTO resumen = cierreCajaService.generarResumen(cerrada, form.getMontoFinal());
        model.addAttribute("caja", cerrada);
        model.addAttribute("resumen", resumen);
        model.addAttribute("cerrada", true);
        model.addAttribute("pageTitle", "Caja cerrada");
        model.addAttribute("toastSuccess", "Caja cerrada correctamente");
        return "cashbox/caja-cerrar";
    }

    @GetMapping("/historial")
    public String historial(Model model,
                            @AuthenticationPrincipal User userDetails) {
        Usuario usuario = obtenerUsuario(userDetails);
        List<CierreCaja> historial = cierreCajaService.historial(usuario);
        Map<Long, CierreCajaResumenDTO> resumenes = historial.stream()
                .filter(caja -> caja.getEstado() == EstadoCaja.CERRADO)
                .collect(Collectors.toMap(CierreCaja::getId,
                        caja -> cierreCajaService.generarResumen(caja, caja.getMontoFinal())));
        model.addAttribute("historial", historial);
        model.addAttribute("resumenes", resumenes);
        model.addAttribute("pageTitle", "Historial de cajas");
        return "cashbox/caja-historial";
    }

    private Usuario obtenerUsuario(User userDetails) {
        if (userDetails == null) {
            throw new IllegalStateException("No hay usuario autenticado");
        }
        return usuarioService.obtenerPorCorreo(userDetails.getUsername());
    }
}
