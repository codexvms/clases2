package cl.gob.binexus.domain.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tipo_parametro")
public class TipoParametro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_parametro")
    private Long id;

    @Column(name = "descripcion_tipo_parametro", nullable = false, unique = true, length = 120)
    private String descripcion;

    @OneToMany(mappedBy = "tipoParametro", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Parametro> parametros = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<Parametro> getParametros() {
        return parametros;
    }

    public void setParametros(Set<Parametro> parametros) {
        this.parametros = parametros;
    }
}
