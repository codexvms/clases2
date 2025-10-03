package cl.gob.binexus.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("usuario", "Usuario");
        return "dashboard";
    }
}
