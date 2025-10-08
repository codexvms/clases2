package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.PrecioProducto;
import cl.gob.binexus.domain.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PrecioProductoRepository extends JpaRepository<PrecioProducto, Long> {

    @Query("SELECT pp FROM PrecioProducto pp WHERE pp.producto = :producto ORDER BY pp.fechaInicioVigencia DESC")
    List<PrecioProducto> findHistorialByProducto(@Param("producto") Producto producto);

    @Query("SELECT pp FROM PrecioProducto pp WHERE pp.producto = :producto AND (pp.fechaFinVigencia IS NULL OR pp.fechaFinVigencia >= :fecha) " +
            "AND pp.fechaInicioVigencia <= :fecha ORDER BY pp.fechaInicioVigencia DESC")
    Optional<PrecioProducto> findPrecioVigente(@Param("producto") Producto producto, @Param("fecha") LocalDate fecha);
}
