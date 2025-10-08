package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.Inventario;
import cl.gob.binexus.domain.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    List<MovimientoInventario> findByInventarioOrderByFechaMovimientoDesc(Inventario inventario);
}
