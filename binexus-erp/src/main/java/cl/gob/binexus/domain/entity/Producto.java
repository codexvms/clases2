package cl.gob.binexus.domain.entity;

import cl.gob.binexus.domain.enums.EstadoProducto;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "producto", indexes = {
        @Index(name = "idx_producto_nombre", columnList = "nombre_producto"),
        @Index(name = "idx_producto_estado", columnList = "estado_producto")
})
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long id;

    @Column(name = "nombre_producto", nullable = false, length = 255)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organizacion", nullable = false)
    private Organizacion organizacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_producto", nullable = false, length = 20)
    private EstadoProducto estado = EstadoProducto.ACTIVO;

    @Column(name = "stock_p0", precision = 12, scale = 2)
    private BigDecimal stockInicial = BigDecimal.ZERO;

    @Column(name = "fecha_creacion_producto", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion_producto")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaInicioVigencia DESC")
    private List<PrecioProducto> precios = new ArrayList<>();

    @OneToOne(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Atributo atributo;

    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
        if (stockInicial == null) {
            stockInicial = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

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

    public Organizacion getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(Organizacion organizacion) {
        this.organizacion = organizacion;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public List<PrecioProducto> getPrecios() {
        return precios;
    }

    public void setPrecios(List<PrecioProducto> precios) {
        this.precios = precios;
    }

    public Atributo getAtributo() {
        return atributo;
    }

    public void setAtributo(Atributo atributo) {
        this.atributo = atributo;
    }
}
