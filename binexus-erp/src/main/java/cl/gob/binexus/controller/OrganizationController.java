package cl.gob.binexus.controller;

import cl.gob.binexus.dto.OrganizacionFormDTO;
import cl.gob.binexus.service.OrganizacionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/organizations")
public class OrganizationController {

    private final OrganizacionService organizacionService;

    public OrganizationController(OrganizacionService organizacionService) {
        this.organizacionService = organizacionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("organizaciones", organizacionService.listarOrganizaciones());
        model.addAttribute("pageTitle", "Organizaciones");
        return "organizations/organizations-list";
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        model.addAttribute("organizacion", new OrganizacionFormDTO());
        model.addAttribute("pageTitle", "Nueva organización");
        return "organizations/organizations-form";
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("organizacion", organizacionService.obtenerFormulario(id));
        model.addAttribute("pageTitle", "Editar organización");
        return "organizations/organizations-form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("organizacion") OrganizacionFormDTO dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", dto.getId() != null ? "Editar organización" : "Nueva organización");
            return "organizations/organizations-form";
        }
        organizacionService.guardarOrganizacion(dto);
        redirectAttributes.addFlashAttribute("toastSuccess", "Organización guardada correctamente");
        return "redirect:/admin/organizations";
    }
}
