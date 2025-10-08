package cl.gob.binexus.service.impl;

import cl.gob.binexus.domain.entity.CierreCaja;
import cl.gob.binexus.domain.entity.Usuario;
import cl.gob.binexus.domain.enums.EstadoCaja;
import cl.gob.binexus.dto.CierreCajaResumenDTO;
import cl.gob.binexus.repository.CierreCajaRepository;
import cl.gob.binexus.repository.VentaRepository;
import cl.gob.binexus.service.CierreCajaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CierreCajaServiceImpl implements CierreCajaService {

    private final CierreCajaRepository cierreCajaRepository;
    private final VentaRepository ventaRepository;

    public CierreCajaServiceImpl(CierreCajaRepository cierreCajaRepository,
                                 VentaRepository ventaRepository) {
        this.cierreCajaRepository = cierreCajaRepository;
        this.ventaRepository = ventaRepository;
    }

    @Override
    public CierreCaja abrirCaja(Usuario usuario, BigDecimal montoInicial) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario requerido");
        }
        if (montoInicial == null || montoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto inicial debe ser mayor o igual a cero");
        }
        if (cierreCajaRepository.existsByUsuarioAndEstado(usuario, EstadoCaja.ABIERTO)) {
            throw new IllegalStateException("El usuario ya tiene una caja abierta");
        }
        CierreCaja caja = new CierreCaja();
        caja.setUsuario(usuario);
        caja.setMontoInicial(montoInicial);
        caja.setEstado(EstadoCaja.ABIERTO);
        return cierreCajaRepository.save(caja);
    }

    @Override
    public CierreCaja cerrarCaja(CierreCaja cierreCaja, BigDecimal montoFinal) {
        if (cierreCaja == null || cierreCaja.getId() == null) {
            throw new IllegalArgumentException("Caja no válida");
        }
        CierreCaja existente = cierreCajaRepository.findById(cierreCaja.getId())
                .orElseThrow(() -> new EntityNotFoundException("Caja no encontrada"));
        if (existente.getEstado() != EstadoCaja.ABIERTO) {
            throw new IllegalStateException("La caja ya fue cerrada");
        }
        existente.setEstado(EstadoCaja.CERRADO);
        existente.setFechaCierre(LocalDateTime.now());
        existente.setMontoFinal(montoFinal);
        return cierreCajaRepository.save(existente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CierreCaja> obtenerCajaAbierta(Usuario usuario) {
        if (usuario == null) {
            return Optional.empty();
        }
        return cierreCajaRepository.findFirstByUsuarioAndEstadoOrderByFechaAperturaDesc(usuario, EstadoCaja.ABIERTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CierreCaja> historial(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario requerido");
        }
        return cierreCajaRepository.findByUsuarioOrderByFechaAperturaDesc(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public CierreCajaResumenDTO generarResumen(CierreCaja cierreCaja, BigDecimal montoFinalDeclarado) {
        if (cierreCaja == null) {
            throw new IllegalArgumentException("Caja requerida");
        }
        LocalDateTime fechaFin = cierreCaja.getFechaCierre() != null ? cierreCaja.getFechaCierre() : LocalDateTime.now();
        BigDecimal totalVentas = ventaRepository.totalPorUsuarioYRango(
                cierreCaja.getUsuario(),
                cierreCaja.getFechaApertura(),
                fechaFin
        );
        if (totalVentas == null) {
            totalVentas = BigDecimal.ZERO;
        }
        BigDecimal montoInicial = cierreCaja.getMontoInicial() != null ? cierreCaja.getMontoInicial() : BigDecimal.ZERO;
        BigDecimal esperado = montoInicial.add(totalVentas);
        BigDecimal declarado = montoFinalDeclarado != null ? montoFinalDeclarado : cierreCaja.getMontoFinal();
        if (declarado == null) {
            declarado = esperado;
        }
        BigDecimal diferencia = declarado.subtract(esperado);
        return new CierreCajaResumenDTO(
                montoInicial,
                esperado,
                declarado,
                diferencia,
                totalVentas,
                cierreCaja.getFechaApertura(),
                fechaFin
        );
    }
}
