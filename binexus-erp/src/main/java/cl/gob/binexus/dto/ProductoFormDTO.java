package cl.gob.binexus.dto;

import cl.gob.binexus.domain.enums.EstadoProducto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductoFormDTO {

    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 255)
    private String nombre;

    @NotNull(message = "Debe seleccionar la organización")
    private Long organizacionId;

    @NotNull
    private EstadoProducto estado = EstadoProducto.ACTIVO;

    @DecimalMin(value = "0.0", message = "El stock inicial no puede ser negativo")
    private BigDecimal stockInicial = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a cero")
    private BigDecimal precioUnitario;

    private LocalDate fechaInicioPrecio = LocalDate.now();

    private Long parametroId;

    private boolean nuevoPrecio = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getOrganizacionId() {
        return organizacionId;
    }

    public void setOrganizacionId(Long organizacionId) {
        this.organizacionId = organizacionId;
    }

    public EstadoProducto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProducto estado) {
        this.estado = estado;
    }

    public BigDecimal getStockInicial() {
        return stockInicial;
    }

    public void setStockInicial(BigDecimal stockInicial) {
        this.stockInicial = stockInicial;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public LocalDate getFechaInicioPrecio() {
        return fechaInicioPrecio;
    }

    public void setFechaInicioPrecio(LocalDate fechaInicioPrecio) {
        this.fechaInicioPrecio = fechaInicioPrecio;
    }

    public Long getParametroId() {
        return parametroId;
    }

    public void setParametroId(Long parametroId) {
        this.parametroId = parametroId;
    }

    public boolean isNuevoPrecio() {
        return nuevoPrecio;
    }

    public void setNuevoPrecio(boolean nuevoPrecio) {
        this.nuevoPrecio = nuevoPrecio;
    }
}
