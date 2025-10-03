package cl.gob.binexus.controller;

import cl.gob.binexus.domain.Usuario;
import cl.gob.binexus.dto.UsuarioDTO;
import cl.gob.binexus.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {
        Page<Usuario> usuarios = usuarioService.listarPaginado(PageRequest.of(page, size));
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios-list";
    }

    @GetMapping("/new")
    public String nuevoForm(Model model) {
        model.addAttribute("usuario", new UsuarioDTO());
        return "admin/usuarios-form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("usuario") UsuarioDTO usuarioDTO,
                        BindingResult bindingResult,
                        Model model) {
        if (!usuarioDTO.hasPassword()) {
            bindingResult.rejectValue("password", "validation.password", "La contraseña debe tener al menos 8 caracteres");
        }
        if (bindingResult.hasErrors()) {
            return "admin/usuarios-form";
        }
        try {
            usuarioService.registrar(usuarioDTO);
            return "redirect:/admin/users?created";
        } catch (DataIntegrityViolationException ex) {
            bindingResult.rejectValue("email", "message.user.duplicate", "El correo ya está registrado");
            return "admin/usuarios-form";
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("password", "validation.password", ex.getMessage());
            return "admin/usuarios-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editarForm(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        model.addAttribute("usuario", dto);
        model.addAttribute("roles", usuario.getRoles());
        return "admin/usuarios-form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("usuario") UsuarioDTO usuarioDTO,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", usuarioService.buscarPorId(id).map(Usuario::getRoles).orElse(null));
            return "admin/usuarios-form";
        }
        usuarioDTO.setId(id);
        try {
            usuarioService.actualizar(usuarioDTO);
            return "redirect:/admin/users?updated";
        } catch (DataIntegrityViolationException ex) {
            bindingResult.rejectValue("email", "message.user.duplicate", "El correo ya está registrado");
            model.addAttribute("roles", usuarioService.buscarPorId(id).map(Usuario::getRoles).orElse(null));
            return "admin/usuarios-form";
        }
    }

    @PostMapping("/{id}/roles")
    public String asignarRol(@PathVariable Long id, @RequestParam String rol) {
        usuarioService.asignarRol(id, rol);
        return "redirect:/admin/users/" + id + "/edit?roleAdded";
    }
}
