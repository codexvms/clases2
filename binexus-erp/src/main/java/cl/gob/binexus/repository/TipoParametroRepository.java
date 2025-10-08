package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.TipoParametro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoParametroRepository extends JpaRepository<TipoParametro, Long> {
    Optional<TipoParametro> findByDescripcionIgnoreCase(String descripcion);
}
