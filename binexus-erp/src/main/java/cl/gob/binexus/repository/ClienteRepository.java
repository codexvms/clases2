package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.Cliente;
import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.enums.EstadoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("SELECT c FROM Cliente c WHERE c.organizacion = :organizacion " +
            "AND (:estado IS NULL OR c.estado = :estado) " +
            "AND (:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))" +
            "ORDER BY c.nombre ASC")
    List<Cliente> buscar(@Param("organizacion") Organizacion organizacion,
                         @Param("estado") EstadoCliente estado,
                         @Param("nombre") String nombre);

    Optional<Cliente> findByOrganizacionAndCorreoIgnoreCase(Organizacion organizacion, String correo);
}
