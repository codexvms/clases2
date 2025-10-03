package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.Organizacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizacionRepository extends JpaRepository<Organizacion, Long> {
    Optional<Organizacion> findByNombreIgnoreCase(String nombre);

    @Override
    @EntityGraph(attributePaths = "locales")
    List<Organizacion> findAll();
}
