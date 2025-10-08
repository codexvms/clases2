package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.entity.Producto;
import cl.gob.binexus.domain.enums.EstadoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("SELECT p FROM Producto p WHERE (:organizacion IS NULL OR p.organizacion = :organizacion) " +
            "AND (:estado IS NULL OR p.estado = :estado) " +
            "AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))")
    List<Producto> buscar(@Param("organizacion") Organizacion organizacion,
                          @Param("estado") EstadoProducto estado,
                          @Param("nombre") String nombre);

    Optional<Producto> findByNombreIgnoreCaseAndOrganizacion(String nombre, Organizacion organizacion);
}
