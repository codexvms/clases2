package cl.gob.binexus.domain.entity;

import cl.gob.binexus.domain.entity.support.AuditableEntity;
import cl.gob.binexus.domain.enums.EstadoAutorizacion;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "autorizacion_remota")
public class AutorizacionRemota extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_autorizacion_remota")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cierre_caja", nullable = false)
    private CierreCaja cierreCaja;

    @Column(name = "accion", nullable = false, length = 120)
    private String accion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_autorizacion", nullable = false, length = 20)
    private EstadoAutorizacion estado = EstadoAutorizacion.PENDIENTE;

    @Column(name = "codigo_verificacion", nullable = false, length = 6)
    private String codigoVerificacion;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autorizado_por")
    private Usuario autorizadoPor;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @PrePersist
    public void prePersist() {
        if (fechaSolicitud == null) {
            fechaSolicitud = LocalDateTime.now();
        }
        if (codigoVerificacion == null) {
            codigoVerificacion = generarCodigo();
        }
    }

    private String generarCodigo() {
        Random random = new Random();
        int numero = random.nextInt(1_000_000);
        return String.format("%06d", numero);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
    }

    public CierreCaja getCierreCaja() {
        return cierreCaja;
    }

    public void setCierreCaja(CierreCaja cierreCaja) {
        this.cierreCaja = cierreCaja;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public EstadoAutorizacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoAutorizacion estado) {
        this.estado = estado;
    }

    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public Usuario getAutorizadoPor() {
        return autorizadoPor;
    }

    public void setAutorizadoPor(Usuario autorizadoPor) {
        this.autorizadoPor = autorizadoPor;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDateTime fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }
}
