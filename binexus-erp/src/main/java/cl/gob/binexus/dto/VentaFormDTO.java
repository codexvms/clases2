package cl.gob.binexus.dto;

import cl.gob.binexus.domain.enums.EstadoPago;
import cl.gob.binexus.domain.enums.MetodoPago;
import cl.gob.binexus.domain.enums.TipoVenta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VentaFormDTO {

    @NotNull(message = "Debe seleccionar un cliente")
    private Long clienteId;

    @NotNull(message = "Debe seleccionar un local")
    private Long localId;

    @NotNull
    private TipoVenta tipoVenta = TipoVenta.PRESENCIAL;

    @NotNull
    private MetodoPago metodoPago = MetodoPago.EFECTIVO;

    @NotNull
    private EstadoPago estadoPago = EstadoPago.PAGADO;

    private BigDecimal descuentoTotal = BigDecimal.ZERO;

    @Valid
    private List<VentaDetalleFormDTO> detalles = new ArrayList<>();

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public TipoVenta getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(TipoVenta tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public EstadoPago getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(EstadoPago estadoPago) {
        this.estadoPago = estadoPago;
    }

    public BigDecimal getDescuentoTotal() {
        return descuentoTotal;
    }

    public void setDescuentoTotal(BigDecimal descuentoTotal) {
        this.descuentoTotal = descuentoTotal;
    }

    public List<VentaDetalleFormDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<VentaDetalleFormDTO> detalles) {
        this.detalles = detalles;
    }
}
