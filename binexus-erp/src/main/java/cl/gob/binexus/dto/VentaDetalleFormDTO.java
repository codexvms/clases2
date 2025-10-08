package cl.gob.binexus.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class VentaDetalleFormDTO {

    @NotNull
    private Long productoId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "La cantidad debe ser mayor a cero")
    private BigDecimal cantidad;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a cero")
    private BigDecimal precioUnitario;

    private BigDecimal costoEnvio;

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }
}
