package cl.gob.binexus.dto;

import cl.gob.binexus.domain.enums.EstadoParametro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ParametroFormDTO {

    private Long id;

    @NotNull(message = "Debe seleccionar un tipo")
    private Long tipoParametroId;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 255)
    private String descripcion;

    @NotNull
    private EstadoParametro estado = EstadoParametro.ACTIVO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTipoParametroId() {
        return tipoParametroId;
    }

    public void setTipoParametroId(Long tipoParametroId) {
        this.tipoParametroId = tipoParametroId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoParametro getEstado() {
        return estado;
    }

    public void setEstado(EstadoParametro estado) {
        this.estado = estado;
    }
}
