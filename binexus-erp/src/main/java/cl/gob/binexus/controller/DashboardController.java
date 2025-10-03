package cl.gob.binexus.controller;

import cl.gob.binexus.domain.Usuario;
import cl.gob.binexus.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UsuarioService usuarioService;

    public DashboardController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName())
            .orElseThrow();
        model.addAttribute("usuario", usuario);
        return "dashboard";
    }
}
