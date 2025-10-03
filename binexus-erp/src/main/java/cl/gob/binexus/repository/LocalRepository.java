package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.Local;
import cl.gob.binexus.domain.entity.Organizacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalRepository extends JpaRepository<Local, Long> {
    List<Local> findByOrganizacion(Organizacion organizacion);
}
