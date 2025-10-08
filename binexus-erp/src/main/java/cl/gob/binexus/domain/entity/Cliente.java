package cl.gob.binexus.domain.entity;

import cl.gob.binexus.domain.enums.EstadoCliente;
import jakarta.persistence.*;

@Entity
@Table(name = "cliente", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cliente_org_correo", columnNames = {"id_organizacion", "correo_cliente"})
})
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long id;

    @Column(name = "nombre_cliente", nullable = false, length = 255)
    private String nombre;

    @Column(name = "correo_cliente", nullable = false, length = 255)
    private String correo;

    @Column(name = "telefono_cliente", length = 50)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cliente", nullable = false, length = 20)
    private EstadoCliente estado = EstadoCliente.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organizacion", nullable = false)
    private Organizacion organizacion;

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

    public Organizacion getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(Organizacion organizacion) {
        this.organizacion = organizacion;
    }
}
