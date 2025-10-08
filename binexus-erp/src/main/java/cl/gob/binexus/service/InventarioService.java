package cl.gob.binexus.service;

import cl.gob.binexus.domain.entity.Inventario;
import cl.gob.binexus.domain.entity.MovimientoInventario;
import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.entity.Producto;
import cl.gob.binexus.domain.enums.TipoMovimientoInventario;

import java.math.BigDecimal;
import java.util.List;

public interface InventarioService {

    List<Inventario> listarPorOrganizacion(Organizacion organizacion);

    Inventario obtenerPorId(Long id);

    Inventario obtenerPorProducto(Producto producto, Organizacion organizacion);

    Inventario inicializarInventario(Producto producto, Organizacion organizacion, BigDecimal stockInicial);

    MovimientoInventario registrarMovimiento(Inventario inventario, TipoMovimientoInventario tipo, BigDecimal cantidad);

    List<MovimientoInventario> listarMovimientos(Inventario inventario);
}
