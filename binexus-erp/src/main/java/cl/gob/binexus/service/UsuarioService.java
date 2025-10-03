package cl.gob.binexus.service;

import cl.gob.binexus.domain.Usuario;
import cl.gob.binexus.dto.UsuarioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UsuarioService {

    Usuario registrar(UsuarioDTO dto);

    Page<Usuario> listarPaginado(Pageable pageable);

    Optional<Usuario> buscarPorEmail(String email);

    Usuario asignarRol(Long usuarioId, String nombreRol);

    Optional<Usuario> buscarPorId(Long id);

    Usuario actualizar(UsuarioDTO dto);
}
