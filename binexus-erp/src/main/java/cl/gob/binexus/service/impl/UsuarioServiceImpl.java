package cl.gob.binexus.service.impl;

import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.entity.Rol;
import cl.gob.binexus.domain.entity.Usuario;
import cl.gob.binexus.dto.UsuarioFormDTO;
import cl.gob.binexus.repository.OrganizacionRepository;
import cl.gob.binexus.repository.RolRepository;
import cl.gob.binexus.repository.UsuarioRepository;
import cl.gob.binexus.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final OrganizacionRepository organizacionRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RolRepository rolRepository,
                              OrganizacionRepository organizacionRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.organizacionRepository = organizacionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<Usuario> listarUsuarios(String filtro, Pageable pageable) {
        if (StringUtils.hasText(filtro)) {
            return usuarioRepository.search(filtro.trim(), pageable);
        }
        return usuarioRepository.findAll(pageable);
    }

    @Override
    public UsuarioFormDTO obtenerFormulario(Long id) {
        if (id == null) {
            return new UsuarioFormDTO();
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        UsuarioFormDTO dto = new UsuarioFormDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setCorreo(usuario.getCorreo());
        dto.setEstado(usuario.getEstado());
        dto.setOrganizacionId(usuario.getOrganizacion().getId());
        Set<Long> rolesIds = new HashSet<>();
        usuario.getRoles().forEach(rol -> rolesIds.add(rol.getId()));
        dto.setRolesIds(rolesIds);
        return dto;
    }

    @Override
    public Usuario guardarUsuario(UsuarioFormDTO dto) {
        validarCorreo(dto);
        Organizacion organizacion = organizacionRepository.findById(dto.getOrganizacionId())
                .orElseThrow(() -> new EntityNotFoundException("Organización no encontrada"));
        Set<Rol> roles = new HashSet<>(rolRepository.findAllById(dto.getRolesIds()));
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un rol válido");
        }
        Usuario usuario = dto.getId() != null
                ? usuarioRepository.findById(dto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"))
                : new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setCorreo(dto.getCorreo());
        usuario.setEstado(dto.getEstado());
        usuario.setOrganizacion(organizacion);
        usuario.setRoles(roles);
        if (StringUtils.hasText(dto.getPassword())) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else if (usuario.getId() == null) {
            throw new IllegalArgumentException("La contraseña es obligatoria para nuevos usuarios");
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    private void validarCorreo(UsuarioFormDTO dto) {
        boolean correoExiste = usuarioRepository.findByCorreo(dto.getCorreo())
                .filter(usuario -> !usuario.getId().equals(dto.getId()))
                .isPresent();
        if (correoExiste) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }
    }
}
