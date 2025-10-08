package cl.gob.binexus.service;

import cl.gob.binexus.domain.entity.Usuario;
import cl.gob.binexus.dto.UsuarioFormDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {

    Page<Usuario> listarUsuarios(String filtro, Pageable pageable);

    UsuarioFormDTO obtenerFormulario(Long id);

    Usuario guardarUsuario(UsuarioFormDTO dto);

    void eliminarUsuario(Long id);

    Usuario obtenerPorCorreo(String correo);
}
