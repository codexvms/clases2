package cl.gob.binexus.service;

import cl.gob.binexus.domain.entity.AutorizacionRemota;
import cl.gob.binexus.domain.entity.CierreCaja;
import cl.gob.binexus.domain.entity.Usuario;
import cl.gob.binexus.domain.enums.EstadoAutorizacion;

import java.util.List;

public interface AutorizacionRemotaService {

    AutorizacionRemota solicitar(Usuario solicitante, CierreCaja cierreCaja, String accion);

    AutorizacionRemota resolver(Long autorizacionId, Usuario aprobador, EstadoAutorizacion estado);

    List<AutorizacionRemota> listarPorEstado(EstadoAutorizacion estado);

    List<AutorizacionRemota> listarPorSolicitante(Usuario solicitante);
}
