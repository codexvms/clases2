package cl.gob.binexus.service.impl;

import cl.gob.binexus.domain.entity.Inventario;
import cl.gob.binexus.domain.entity.MovimientoInventario;
import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.entity.Producto;
import cl.gob.binexus.domain.enums.TipoMovimientoInventario;
import cl.gob.binexus.repository.InventarioRepository;
import cl.gob.binexus.repository.MovimientoInventarioRepository;
import cl.gob.binexus.service.InventarioService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    public InventarioServiceImpl(InventarioRepository inventarioRepository,
                                 MovimientoInventarioRepository movimientoInventarioRepository) {
        this.inventarioRepository = inventarioRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> listarPorOrganizacion(Organizacion organizacion) {
        return inventarioRepository.findByOrganizacionOrderByProducto_NombreAsc(organizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Inventario obtenerPorId(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventario no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public Inventario obtenerPorProducto(Producto producto, Organizacion organizacion) {
        return inventarioRepository.findByProductoAndOrganizacion(producto, organizacion)
                .orElseThrow(() -> new EntityNotFoundException("Inventario no encontrado para el producto"));
    }

    @Override
    public Inventario inicializarInventario(Producto producto, Organizacion organizacion, BigDecimal stockInicial) {
        return inventarioRepository.findByProductoAndOrganizacion(producto, organizacion)
                .orElseGet(() -> {
                    Inventario inventario = new Inventario();
                    inventario.setProducto(producto);
                    inventario.setOrganizacion(organizacion);
                    inventario.setStockTotal(stockInicial != null ? stockInicial : BigDecimal.ZERO);
                    Inventario guardado = inventarioRepository.save(inventario);
                    if (stockInicial != null && stockInicial.compareTo(BigDecimal.ZERO) > 0) {
                        registrarMovimientoInterno(guardado, TipoMovimientoInventario.ENTRADA, stockInicial);
                    }
                    return guardado;
                });
    }

    @Override
    public MovimientoInventario registrarMovimiento(Inventario inventario, TipoMovimientoInventario tipo, BigDecimal cantidad) {
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if (tipo == TipoMovimientoInventario.SALIDA) {
            BigDecimal nuevoStock = inventario.getStockTotal().subtract(cantidad);
            if (nuevoStock.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("La salida no puede dejar el inventario en negativo");
            }
            inventario.setStockTotal(nuevoStock);
        } else {
            inventario.setStockTotal(inventario.getStockTotal().add(cantidad));
        }
        inventarioRepository.save(inventario);
        return registrarMovimientoInterno(inventario, tipo, cantidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> listarMovimientos(Inventario inventario) {
        return movimientoInventarioRepository.findByInventarioOrderByFechaMovimientoDesc(inventario);
    }

    private MovimientoInventario registrarMovimientoInterno(Inventario inventario, TipoMovimientoInventario tipo, BigDecimal cantidad) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setInventario(inventario);
        movimiento.setOrganizacion(inventario.getOrganizacion());
        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidad(cantidad);
        return movimientoInventarioRepository.save(movimiento);
    }
}
