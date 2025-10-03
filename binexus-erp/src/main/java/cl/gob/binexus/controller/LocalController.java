package cl.gob.binexus.controller;

import cl.gob.binexus.dto.LocalFormDTO;
import cl.gob.binexus.service.LocalService;
import cl.gob.binexus.service.OrganizacionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/locals")
public class LocalController {

    private final LocalService localService;
    private final OrganizacionService organizacionService;

    public LocalController(LocalService localService, OrganizacionService organizacionService) {
        this.localService = localService;
        this.organizacionService = organizacionService;
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        model.addAttribute("local", new LocalFormDTO());
        model.addAttribute("organizaciones", organizacionService.listarOrganizaciones());
        model.addAttribute("pageTitle", "Nuevo local");
        return "organizations/locals-form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("local") LocalFormDTO dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("organizaciones", organizacionService.listarOrganizaciones());
            model.addAttribute("pageTitle", "Nuevo local");
            return "organizations/locals-form";
        }
        localService.guardarLocal(dto);
        redirectAttributes.addFlashAttribute("toastSuccess", "Local guardado correctamente");
        return "redirect:/admin/organizations";
    }
}
