package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.Privilegio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegioRepository extends JpaRepository<Privilegio, Long> {
}
