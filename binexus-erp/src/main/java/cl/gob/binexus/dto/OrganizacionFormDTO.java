package cl.gob.binexus.dto;

import cl.gob.binexus.domain.enums.EstadoOrganizacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrganizacionFormDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "Debe seleccionar un estado")
    private EstadoOrganizacion estado = EstadoOrganizacion.ACTIVO;

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

    public EstadoOrganizacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrganizacion estado) {
        this.estado = estado;
    }
}
