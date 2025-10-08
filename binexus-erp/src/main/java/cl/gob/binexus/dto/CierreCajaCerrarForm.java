package cl.gob.binexus.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CierreCajaCerrarForm {

    @NotNull(message = "Debe indicar el monto final contado")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto debe ser positivo")
    @Digits(integer = 12, fraction = 2, message = "Formato inválido")
    private BigDecimal montoFinal;

    public BigDecimal getMontoFinal() {
        return montoFinal;
    }

    public void setMontoFinal(BigDecimal montoFinal) {
        this.montoFinal = montoFinal;
    }
}
