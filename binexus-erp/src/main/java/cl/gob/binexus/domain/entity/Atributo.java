package cl.gob.binexus.domain.entity;

import cl.gob.binexus.domain.entity.support.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "atributo")
public class Atributo extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_atributo")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false, unique = true)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mantenedor")
    private Parametro mantenedor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Parametro getMantenedor() {
        return mantenedor;
    }

    public void setMantenedor(Parametro mantenedor) {
        this.mantenedor = mantenedor;
    }
}
