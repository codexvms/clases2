package cl.gob.binexus.service;

import cl.gob.binexus.domain.entity.CierreCaja;
import cl.gob.binexus.domain.entity.Usuario;
import cl.gob.binexus.dto.CierreCajaResumenDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CierreCajaService {

    CierreCaja abrirCaja(Usuario usuario, BigDecimal montoInicial);

    CierreCaja cerrarCaja(CierreCaja cierreCaja, BigDecimal montoFinal);

    Optional<CierreCaja> obtenerCajaAbierta(Usuario usuario);

    List<CierreCaja> historial(Usuario usuario);

    CierreCajaResumenDTO generarResumen(CierreCaja cierreCaja, BigDecimal montoFinalDeclarado);
}
