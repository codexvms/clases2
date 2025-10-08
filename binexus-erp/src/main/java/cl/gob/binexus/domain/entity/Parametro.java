package cl.gob.binexus.domain.entity;

import cl.gob.binexus.domain.enums.EstadoParametro;
import jakarta.persistence.*;

@Entity
@Table(name = "parametro", uniqueConstraints = {
        @UniqueConstraint(name = "uk_parametro_tipo_descripcion", columnNames = {"id_tipo_parametro", "descripcion_parametro"})
})
public class Parametro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_parametro", nullable = false)
    private TipoParametro tipoParametro;

    @Column(name = "descripcion_parametro", nullable = false, length = 255)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_parametro", nullable = false, length = 20)
    private EstadoParametro estado = EstadoParametro.ACTIVO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoParametro getTipoParametro() {
        return tipoParametro;
    }

    public void setTipoParametro(TipoParametro tipoParametro) {
        this.tipoParametro = tipoParametro;
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
