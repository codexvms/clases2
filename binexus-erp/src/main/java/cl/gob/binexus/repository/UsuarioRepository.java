package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.Usuario;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    @Override
    @EntityGraph(attributePaths = {"organizacion", "roles"})
    Page<Usuario> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"organizacion", "roles"})
    @Query("SELECT u FROM Usuario u WHERE lower(u.nombre) LIKE lower(concat('%', :filtro, '%')) OR lower(u.correo) LIKE lower(concat('%', :filtro, '%'))")
    Page<Usuario> search(@Param("filtro") String filtro, Pageable pageable);
}
