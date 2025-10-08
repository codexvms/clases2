package cl.gob.binexus.dto;

import cl.gob.binexus.domain.enums.EstadoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ClienteFormDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255)
    private String nombre;

    @NotNull(message = "Debe seleccionar la organización")
    private Long organizacionId;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String correo;

    @Size(max = 50)
    private String telefono;

    @NotNull
    private EstadoCliente estado = EstadoCliente.ACTIVO;

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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public EstadoCliente getEstado() {
        return estado;
    }

    public void setEstado(EstadoCliente estado) {
        this.estado = estado;
    }
}
