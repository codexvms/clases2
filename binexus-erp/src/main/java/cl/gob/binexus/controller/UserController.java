package cl.gob.binexus.controller;

import cl.gob.binexus.domain.entity.Rol;
import cl.gob.binexus.domain.entity.Usuario;
import cl.gob.binexus.dto.UsuarioFormDTO;
import cl.gob.binexus.service.OrganizacionService;
import cl.gob.binexus.service.RolService;
import cl.gob.binexus.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    private final UsuarioService usuarioService;
    private final OrganizacionService organizacionService;
    private final RolService rolService;

    public UserController(UsuarioService usuarioService,
                          OrganizacionService organizacionService,
                          RolService rolService) {
        this.usuarioService = usuarioService;
        this.organizacionService = organizacionService;
        this.rolService = rolService;
    }

    @GetMapping
    public String listarUsuarios(@RequestParam(value = "q", required = false) String filtro,
                                 @PageableDefault(size = 10) Pageable pageable,
                                 Model model) {
        Page<Usuario> pagina = usuarioService.listarUsuarios(filtro, pageable);
        model.addAttribute("page", pagina);
        model.addAttribute("filtro", filtro);
        model.addAttribute("pageTitle", "Usuarios");
        return "users/users-list";
    }

    @GetMapping("/new")
    public String nuevoUsuario(Model model) {
        prepararFormulario(model, usuarioService.obtenerFormulario(null));
        model.addAttribute("pageTitle", "Nuevo usuario");
        return "users/users-form";
    }

    @GetMapping("/{id}/edit")
    public String editarUsuario(@PathVariable Long id, Model model) {
        prepararFormulario(model, usuarioService.obtenerFormulario(id));
        model.addAttribute("pageTitle", "Editar usuario");
        return "users/users-form";
    }

    @PostMapping
    public String guardarUsuario(@Valid @ModelAttribute("usuario") UsuarioFormDTO dto,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasFieldErrors("password") && dto.getId() == null && (dto.getPassword() == null || dto.getPassword().isBlank())) {
            bindingResult.rejectValue("password", "usuario.password", "La contraseña es obligatoria para nuevos usuarios");
        }
        if (bindingResult.hasErrors()) {
            prepararFormulario(model, dto);
            model.addAttribute("pageTitle", dto.getId() != null ? "Editar usuario" : "Nuevo usuario");
            return "users/users-form";
        }
        try {
            usuarioService.guardarUsuario(dto);
            redirectAttributes.addFlashAttribute("toastSuccess", "Usuario guardado correctamente");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException ex) {
            if (ex.getMessage().toLowerCase().contains("contraseña")) {
                bindingResult.rejectValue("password", "usuario.password", ex.getMessage());
            } else if (ex.getMessage().toLowerCase().contains("rol")) {
                bindingResult.rejectValue("rolesIds", "usuario.rolesIds", ex.getMessage());
            } else {
                bindingResult.rejectValue("correo", "usuario.correo", ex.getMessage());
            }
            prepararFormulario(model, dto);
            model.addAttribute("pageTitle", dto.getId() != null ? "Editar usuario" : "Nuevo usuario");
            return "users/users-form";
        }
    }

    private void prepararFormulario(Model model, UsuarioFormDTO dto) {
        List<Rol> roles = rolService.listarRoles();
        model.addAttribute("usuario", dto);
        model.addAttribute("roles", roles);
        model.addAttribute("organizaciones", organizacionService.listarOrganizaciones());
    }
}
