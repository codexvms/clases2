package cl.gob.binexus.service;

import cl.gob.binexus.domain.entity.Parametro;
import cl.gob.binexus.domain.entity.PrecioProducto;
import cl.gob.binexus.domain.entity.Producto;
import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.enums.EstadoProducto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductoService {

    List<Producto> buscarProductos(Organizacion organizacion, EstadoProducto estado, String nombre);

    Producto obtenerPorId(Long id);

    Producto guardarProducto(Producto producto, BigDecimal precioUnitario, LocalDate fechaInicio, Parametro parametroAtributo);

    List<PrecioProducto> obtenerHistorialPrecios(Long productoId);

    Optional<PrecioProducto> obtenerPrecioVigente(Producto producto, LocalDate fecha);
}
