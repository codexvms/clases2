package cl.gob.binexus.domain.entity;

import cl.gob.binexus.domain.entity.support.AuditableEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rol")
public class Rol extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rol_privilegio",
            joinColumns = @JoinColumn(name = "id_rol"),
            inverseJoinColumns = @JoinColumn(name = "id_privilegio"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"id_rol", "id_privilegio"})
    )
    private Set<Privilegio> privilegios = new HashSet<>();

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

    public Set<Privilegio> getPrivilegios() {
        return privilegios;
    }

    public void setPrivilegios(Set<Privilegio> privilegios) {
        this.privilegios = privilegios;
    }
}
