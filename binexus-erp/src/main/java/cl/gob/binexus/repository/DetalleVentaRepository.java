package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.DetalleVenta;
import cl.gob.binexus.domain.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByVenta(Venta venta);
}
