package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.Local;
import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.entity.Usuario;
import cl.gob.binexus.domain.entity.Venta;
import cl.gob.binexus.domain.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("SELECT v FROM Venta v WHERE v.organizacion = :organizacion " +
            "AND (:estado IS NULL OR v.estadoPago = :estado) " +
            "AND (:local IS NULL OR v.local = :local) " +
            "AND (:desde IS NULL OR v.fechaVenta >= :desde) " +
            "AND (:hasta IS NULL OR v.fechaVenta <= :hasta) ORDER BY v.fechaVenta DESC")
    List<Venta> buscar(@Param("organizacion") Organizacion organizacion,
                       @Param("estado") EstadoPago estado,
                       @Param("local") Local local,
                       @Param("desde") LocalDateTime desde,
                       @Param("hasta") LocalDateTime hasta);

    long countByOrganizacionAndFechaVentaBetween(Organizacion organizacion, LocalDateTime desde, LocalDateTime hasta);

    @Query("SELECT DATE(v.fechaVenta) as dia, SUM(v.montoFinal) FROM Venta v " +
            "WHERE v.organizacion = :organizacion AND v.fechaVenta BETWEEN :desde AND :hasta " +
            "GROUP BY DATE(v.fechaVenta) ORDER BY DATE(v.fechaVenta)")
    List<Object[]> totalPorDia(@Param("organizacion") Organizacion organizacion,
                               @Param("desde") LocalDateTime desde,
                               @Param("hasta") LocalDateTime hasta);

    @Query("SELECT COALESCE(SUM(v.montoFinal), 0) FROM Venta v " +
            "WHERE v.usuario = :usuario AND v.fechaVenta BETWEEN :desde AND :hasta")
    BigDecimal totalPorUsuarioYRango(@Param("usuario") Usuario usuario,
                                     @Param("desde") LocalDateTime desde,
                                     @Param("hasta") LocalDateTime hasta);
}
