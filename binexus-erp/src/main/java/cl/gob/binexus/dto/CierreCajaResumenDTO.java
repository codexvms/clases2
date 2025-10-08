package cl.gob.binexus.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CierreCajaResumenDTO {

    private final BigDecimal montoInicial;
    private final BigDecimal montoFinalEsperado;
    private final BigDecimal montoFinalDeclarado;
    private final BigDecimal diferencia;
    private final BigDecimal totalVentas;
    private final LocalDateTime fechaApertura;
    private final LocalDateTime fechaCierre;

    public CierreCajaResumenDTO(BigDecimal montoInicial,
                                BigDecimal montoFinalEsperado,
                                BigDecimal montoFinalDeclarado,
                                BigDecimal diferencia,
                                BigDecimal totalVentas,
                                LocalDateTime fechaApertura,
                                LocalDateTime fechaCierre) {
        this.montoInicial = montoInicial;
        this.montoFinalEsperado = montoFinalEsperado;
        this.montoFinalDeclarado = montoFinalDeclarado;
        this.diferencia = diferencia;
        this.totalVentas = totalVentas;
        this.fechaApertura = fechaApertura;
        this.fechaCierre = fechaCierre;
    }

    public BigDecimal getMontoInicial() {
        return montoInicial;
    }

    public BigDecimal getMontoFinalEsperado() {
        return montoFinalEsperado;
    }

    public BigDecimal getMontoFinalDeclarado() {
        return montoFinalDeclarado;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }
}
