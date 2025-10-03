package cl.gob.binexus.controller;

import cl.gob.binexus.domain.Usuario;
import cl.gob.binexus.dto.AuthRequest;
import cl.gob.binexus.dto.AuthResponse;
import cl.gob.binexus.dto.UsuarioDTO;
import cl.gob.binexus.service.JwtService;
import cl.gob.binexus.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UsuarioService usuarioService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("usuario", new UsuarioDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("usuario") UsuarioDTO usuarioDTO, BindingResult bindingResult, Model model) {
        if (!usuarioDTO.hasPassword()) {
            bindingResult.rejectValue("password", "validation.password", "La contraseña debe tener al menos 8 caracteres");
        }
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            usuarioService.registrar(usuarioDTO);
            model.addAttribute("success", "message.user.created");
            model.addAttribute("usuario", new UsuarioDTO());
            return "auth/register";
        } catch (DataIntegrityViolationException ex) {
            bindingResult.rejectValue("email", "message.user.duplicate", "El correo ya está registrado");
            return "auth/register";
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("password", "validation.password", ex.getMessage());
            return "auth/register";
        }
    }

    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<AuthResponse> apiLogin(@RequestBody @Valid AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.buscarPorEmail(principal.getUsername())
            .orElseThrow();
        List<String> roles = principal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
        String token = jwtService.generateToken(principal.getUsername(), Map.of("roles", roles), 60);
        AuthResponse response = new AuthResponse(
            token,
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getRoles().stream().map(rol -> rol.getNombre()).collect(Collectors.toSet())
        );
        return ResponseEntity.ok(response);
    }
}
