package cl.gob.binexus.service.impl;

import cl.gob.binexus.domain.entity.AutorizacionRemota;
import cl.gob.binexus.domain.entity.CierreCaja;
import cl.gob.binexus.domain.entity.Rol;
import cl.gob.binexus.domain.entity.Usuario;
import cl.gob.binexus.domain.enums.EstadoAutorizacion;
import cl.gob.binexus.domain.enums.EstadoCaja;
import cl.gob.binexus.repository.AutorizacionRemotaRepository;
import cl.gob.binexus.service.AutorizacionRemotaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AutorizacionRemotaServiceImpl implements AutorizacionRemotaService {

    private static final String ROL_SUPERIOR = "ROLE_ADMIN";

    private final AutorizacionRemotaRepository autorizacionRemotaRepository;

    public AutorizacionRemotaServiceImpl(AutorizacionRemotaRepository autorizacionRemotaRepository) {
        this.autorizacionRemotaRepository = autorizacionRemotaRepository;
    }

    @Override
    public AutorizacionRemota solicitar(Usuario solicitante, CierreCaja cierreCaja, String accion) {
        if (solicitante == null || cierreCaja == null) {
            throw new IllegalArgumentException("Datos incompletos para la solicitud");
        }
        if (!StringUtils.hasText(accion)) {
            throw new IllegalArgumentException("La acción a autorizar es obligatoria");
        }
        if (cierreCaja.getEstado() != EstadoCaja.ABIERTO) {
            throw new IllegalStateException("La autorización solo puede asociarse a cajas abiertas");
        }
        AutorizacionRemota autorizacion = new AutorizacionRemota();
        autorizacion.setSolicitante(solicitante);
        autorizacion.setCierreCaja(cierreCaja);
        autorizacion.setAccion(accion.trim());
        autorizacion.setEstado(EstadoAutorizacion.PENDIENTE);
        return autorizacionRemotaRepository.save(autorizacion);
    }

    @Override
    public AutorizacionRemota resolver(Long autorizacionId, Usuario aprobador, EstadoAutorizacion estado) {
        if (autorizacionId == null || aprobador == null) {
            throw new IllegalArgumentException("Datos incompletos para resolver la autorización");
        }
        if (estado == null || estado == EstadoAutorizacion.PENDIENTE) {
            throw new IllegalArgumentException("Estado inválido");
        }
        AutorizacionRemota autorizacion = autorizacionRemotaRepository.findById(autorizacionId)
                .orElseThrow(() -> new EntityNotFoundException("Autorización no encontrada"));
        if (autorizacion.getEstado() != EstadoAutorizacion.PENDIENTE) {
            throw new IllegalStateException("La autorización ya fue resuelta");
        }
        if (autorizacion.getSolicitante().getId().equals(aprobador.getId())) {
            throw new IllegalStateException("El solicitante no puede aprobar su propia autorización");
        }
        if (aprobador.getRoles().stream().map(Rol::getNombre).noneMatch(ROL_SUPERIOR::equals)) {
            throw new IllegalStateException("El usuario no tiene privilegios para aprobar");
        }
        autorizacion.setEstado(estado);
        autorizacion.setAutorizadoPor(aprobador);
        autorizacion.setFechaResolucion(LocalDateTime.now());
        return autorizacionRemotaRepository.save(autorizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AutorizacionRemota> listarPorEstado(EstadoAutorizacion estado) {
        if (estado == null) {
            return autorizacionRemotaRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaSolicitud"));
        }
        return autorizacionRemotaRepository.findByEstadoOrderByFechaSolicitudDesc(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AutorizacionRemota> listarPorSolicitante(Usuario solicitante) {
        if (solicitante == null) {
            throw new IllegalArgumentException("Solicitante requerido");
        }
        return autorizacionRemotaRepository.findBySolicitanteOrderByFechaSolicitudDesc(solicitante);
    }
}
