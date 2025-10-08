package cl.gob.binexus.service;

import cl.gob.binexus.domain.entity.CierreCaja;
import cl.gob.binexus.domain.entity.Usuario;
import cl.gob.binexus.domain.enums.EstadoCaja;
import cl.gob.binexus.dto.CierreCajaResumenDTO;
import cl.gob.binexus.repository.CierreCajaRepository;
import cl.gob.binexus.repository.VentaRepository;
import cl.gob.binexus.service.impl.CierreCajaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CierreCajaServiceImplTest {

    @Mock
    private CierreCajaRepository cierreCajaRepository;

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private CierreCajaServiceImpl cierreCajaService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
    }

    @Test
    @DisplayName("No permite abrir una caja si ya existe una abierta para el usuario")
    void abrirCajaConCajaExistenteLanzaExcepcion() {
        when(cierreCajaRepository.existsByUsuarioAndEstado(usuario, EstadoCaja.ABIERTO)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> cierreCajaService.abrirCaja(usuario, BigDecimal.TEN));
        verify(cierreCajaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Calcula correctamente el resumen del cierre de caja")
    void generarResumenCalculaMontos() {
        CierreCaja caja = new CierreCaja();
        caja.setUsuario(usuario);
        caja.setFechaApertura(LocalDateTime.now().minusHours(4));
        caja.setFechaCierre(LocalDateTime.now());
        caja.setMontoInicial(new BigDecimal("10000"));

        when(ventaRepository.totalPorUsuarioYRango(eq(usuario), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("25000"));

        CierreCajaResumenDTO resumen = cierreCajaService.generarResumen(caja, new BigDecimal("36000"));

        assertEquals(new BigDecimal("10000"), resumen.getMontoInicial());
        assertEquals(new BigDecimal("25000"), resumen.getTotalVentas());
        assertEquals(new BigDecimal("35000"), resumen.getMontoFinalEsperado());
        assertEquals(new BigDecimal("36000"), resumen.getMontoFinalDeclarado());
        assertEquals(new BigDecimal("1000"), resumen.getDiferencia());
    }

    @Test
    @DisplayName("Cierra la caja abierta actualizando estado y montos")
    void cerrarCajaActualizaEstado() {
        CierreCaja caja = new CierreCaja();
        caja.setId(5L);
        caja.setUsuario(usuario);
        caja.setEstado(EstadoCaja.ABIERTO);
        caja.setFechaApertura(LocalDateTime.now().minusHours(2));

        when(cierreCajaRepository.findById(5L)).thenReturn(Optional.of(caja));
        when(cierreCajaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CierreCaja cerrada = cierreCajaService.cerrarCaja(caja, new BigDecimal("15000"));

        assertEquals(EstadoCaja.CERRADO, cerrada.getEstado());
        assertNotNull(cerrada.getFechaCierre());
        assertEquals(new BigDecimal("15000"), cerrada.getMontoFinal());
        verify(cierreCajaRepository).save(cerrada);
    }
}
