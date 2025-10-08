package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.Parametro;
import cl.gob.binexus.domain.entity.TipoParametro;
import cl.gob.binexus.domain.enums.EstadoParametro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParametroRepository extends JpaRepository<Parametro, Long> {

    List<Parametro> findByTipoParametroOrderByDescripcionAsc(TipoParametro tipoParametro);

    List<Parametro> findByTipoParametroAndEstadoOrderByDescripcionAsc(TipoParametro tipoParametro, EstadoParametro estado);

    Optional<Parametro> findByTipoParametroAndDescripcionIgnoreCase(TipoParametro tipoParametro, String descripcion);
}
