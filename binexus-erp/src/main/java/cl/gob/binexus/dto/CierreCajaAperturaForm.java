package cl.gob.binexus.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CierreCajaAperturaForm {

    @NotNull(message = "Debe ingresar un monto inicial")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto debe ser positivo")
    @Digits(integer = 12, fraction = 2, message = "Formato inválido")
    private BigDecimal montoInicial;

    public BigDecimal getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(BigDecimal montoInicial) {
        this.montoInicial = montoInicial;
    }
}
