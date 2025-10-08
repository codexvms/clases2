package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.AutorizacionRemota;
import cl.gob.binexus.domain.entity.CierreCaja;
import cl.gob.binexus.domain.entity.Usuario;
import cl.gob.binexus.domain.enums.EstadoAutorizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutorizacionRemotaRepository extends JpaRepository<AutorizacionRemota, Long> {

    List<AutorizacionRemota> findByEstadoOrderByFechaSolicitudDesc(EstadoAutorizacion estado);

    List<AutorizacionRemota> findBySolicitanteOrderByFechaSolicitudDesc(Usuario solicitante);

    List<AutorizacionRemota> findByCierreCajaOrderByFechaSolicitudDesc(CierreCaja cierreCaja);
}
