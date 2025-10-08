package cl.gob.binexus.controller;

import cl.gob.binexus.domain.entity.AutorizacionRemota;
import cl.gob.binexus.domain.entity.CierreCaja;
import cl.gob.binexus.domain.entity.Usuario;
import cl.gob.binexus.domain.enums.EstadoAutorizacion;
import cl.gob.binexus.service.AutorizacionRemotaService;
import cl.gob.binexus.service.CierreCajaService;
import cl.gob.binexus.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/autorizaciones")
public class AutorizacionRemotaController {

    private final AutorizacionRemotaService autorizacionRemotaService;
    private final CierreCajaService cierreCajaService;
    private final UsuarioService usuarioService;

    public AutorizacionRemotaController(AutorizacionRemotaService autorizacionRemotaService,
                                        CierreCajaService cierreCajaService,
                                        UsuarioService usuarioService) {
        this.autorizacionRemotaService = autorizacionRemotaService;
        this.cierreCajaService = cierreCajaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(@RequestParam(value = "estado", required = false) EstadoAutorizacion estado,
                         @AuthenticationPrincipal User userDetails,
                         Model model) {
        Usuario usuario = obtenerUsuario(userDetails);
        List<AutorizacionRemota> autorizaciones = autorizacionRemotaService.listarPorEstado(estado);
        Optional<CierreCaja> caja = cierreCajaService.obtenerCajaAbierta(usuario);
        model.addAttribute("autorizaciones", autorizaciones);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("estados", EstadoAutorizacion.values());
        model.addAttribute("caja", caja.orElse(null));
        model.addAttribute("pageTitle", "Autorizaciones remotas");
        return "authorizations/autorizaciones-list";
    }

    @PostMapping
    public String solicitar(@RequestParam("accion") String accion,
                            @AuthenticationPrincipal User userDetails,
                            RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(userDetails);
        Optional<CierreCaja> cajaOpt = cierreCajaService.obtenerCajaAbierta(usuario);
        if (cajaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("toastError", "Debes abrir una caja para solicitar autorizaciones");
            return "redirect:/autorizaciones";
        }
        if (!StringUtils.hasText(accion)) {
            redirectAttributes.addFlashAttribute("toastError", "La acción es obligatoria");
            return "redirect:/autorizaciones";
        }
        autorizacionRemotaService.solicitar(usuario, cajaOpt.get(), accion);
        redirectAttributes.addFlashAttribute("toastSuccess", "Autorización solicitada correctamente");
        return "redirect:/autorizaciones";
    }

    @PostMapping("/{id}/resolver")
    public String resolver(@PathVariable("id") Long id,
                           @RequestParam("estado") EstadoAutorizacion estado,
                           @AuthenticationPrincipal User userDetails,
                           RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(userDetails);
        try {
            autorizacionRemotaService.resolver(id, usuario, estado);
            redirectAttributes.addFlashAttribute("toastSuccess", "Autorización actualizada");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("toastError", ex.getMessage());
        }
        return "redirect:/autorizaciones";
    }

    private Usuario obtenerUsuario(User userDetails) {
        if (userDetails == null) {
            throw new IllegalStateException("No hay usuario autenticado");
        }
        return usuarioService.obtenerPorCorreo(userDetails.getUsername());
    }
}
