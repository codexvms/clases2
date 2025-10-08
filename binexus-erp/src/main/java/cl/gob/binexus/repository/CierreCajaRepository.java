package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.CierreCaja;
import cl.gob.binexus.domain.entity.Usuario;
import cl.gob.binexus.domain.enums.EstadoCaja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CierreCajaRepository extends JpaRepository<CierreCaja, Long> {

    Optional<CierreCaja> findFirstByUsuarioAndEstadoOrderByFechaAperturaDesc(Usuario usuario, EstadoCaja estado);

    boolean existsByUsuarioAndEstado(Usuario usuario, EstadoCaja estado);

    List<CierreCaja> findByUsuarioOrderByFechaAperturaDesc(Usuario usuario);
}
