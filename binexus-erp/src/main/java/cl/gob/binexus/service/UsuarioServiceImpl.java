package cl.gob.binexus.service;

import cl.gob.binexus.domain.Rol;
import cl.gob.binexus.domain.Usuario;
import cl.gob.binexus.dto.UsuarioDTO;
import cl.gob.binexus.repository.RolRepository;
import cl.gob.binexus.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario registrar(UsuarioDTO dto) {
        if (!dto.hasPassword()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new DataIntegrityViolationException("El correo ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuario.setEnabled(true);

        Rol rolUsuario = rolRepository.findByNombre("ROLE_USER")
            .orElseThrow(() -> new EntityNotFoundException("Rol ROLE_USER no configurado"));
        usuario.addRol(rolUsuario);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Page<Usuario> listarPaginado(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Usuario asignarRol(Long usuarioId, String nombreRol) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        Rol rol = rolRepository.findByNombre(nombreRol)
            .orElseThrow(() -> new EntityNotFoundException("Rol " + nombreRol + " no encontrado"));
        usuario.addRol(rol);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario actualizar(UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getId())
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new DataIntegrityViolationException("El correo ya está registrado");
        }

        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        if (dto.hasPassword()) {
            usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }
}
