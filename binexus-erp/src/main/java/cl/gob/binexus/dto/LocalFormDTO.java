package cl.gob.binexus.dto;

import cl.gob.binexus.domain.enums.EstadoLocal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LocalFormDTO {

    private Long id;

    @NotNull(message = "Debe seleccionar una organización")
    private Long organizacionId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "Debe seleccionar un estado")
    private EstadoLocal estado = EstadoLocal.ACTIVO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizacionId() {
        return organizacionId;
    }

    public void setOrganizacionId(Long organizacionId) {
        this.organizacionId = organizacionId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public EstadoLocal getEstado() {
        return estado;
    }

    public void setEstado(EstadoLocal estado) {
        this.estado = estado;
    }
}
