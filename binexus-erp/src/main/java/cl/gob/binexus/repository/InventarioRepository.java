package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.Inventario;
import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByProductoAndOrganizacion(Producto producto, Organizacion organizacion);

    List<Inventario> findByOrganizacionOrderByProducto_NombreAsc(Organizacion organizacion);
}
