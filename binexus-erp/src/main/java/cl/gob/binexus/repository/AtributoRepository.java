package cl.gob.binexus.repository;

import cl.gob.binexus.domain.entity.Atributo;
import cl.gob.binexus.domain.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AtributoRepository extends JpaRepository<Atributo, Long> {
    Optional<Atributo> findByProducto(Producto producto);
}
