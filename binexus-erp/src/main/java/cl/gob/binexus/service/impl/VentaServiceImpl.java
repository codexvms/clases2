package cl.gob.binexus.service.impl;

import cl.gob.binexus.domain.entity.DetalleVenta;
import cl.gob.binexus.domain.entity.Inventario;
import cl.gob.binexus.domain.entity.Local;
import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.entity.Producto;
import cl.gob.binexus.domain.entity.Venta;
import cl.gob.binexus.domain.enums.EstadoPago;
import cl.gob.binexus.domain.enums.TipoMovimientoInventario;
import cl.gob.binexus.repository.ProductoRepository;
import cl.gob.binexus.repository.VentaRepository;
import cl.gob.binexus.service.InventarioService;
import cl.gob.binexus.service.VentaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;

    public VentaServiceImpl(VentaRepository ventaRepository,
                            ProductoRepository productoRepository,
                            InventarioService inventarioService) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.inventarioService = inventarioService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> buscar(Organizacion organizacion, EstadoPago estadoPago, Local local, LocalDate desde, LocalDate hasta) {
        LocalDateTime fechaDesde = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime fechaHasta = hasta != null ? hasta.atTime(23, 59, 59) : null;
        return ventaRepository.buscar(organizacion, estadoPago, local, fechaDesde, fechaHasta);
    }

    @Override
    @Transactional(readOnly = true)
    public Venta obtenerPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));
    }

    @Override
    public Venta registrarVenta(Venta venta) {
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un producto a la venta");
        }

        List<DetalleVenta> detallesPreparados = new ArrayList<>();
        Map<Long, Inventario> inventarios = new HashMap<>();

        venta.getDetalles().forEach(detalle -> {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto de la venta no existe"));
            Inventario inventario = inventarioService.obtenerPorProducto(producto, venta.getOrganizacion());
            inventarios.put(producto.getId(), inventario);

            BigDecimal cantidad = detalle.getCantidad();
            if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
            }
            if (inventario.getStockTotal().compareTo(cantidad) < 0) {
                throw new IllegalArgumentException("Stock insuficiente para el producto " + producto.getNombre());
            }

            detalle.setProducto(producto);
            detalle.setVenta(venta);
            if (detalle.getPrecioUnitario() == null) {
                throw new IllegalArgumentException("Debe indicar el precio unitario");
            }
            detalle.recalcularTotal();
            detallesPreparados.add(detalle);
        });

        venta.setDetalles(detallesPreparados);
        venta.recalcularTotales();

        Venta guardada = ventaRepository.save(venta);

        detallesPreparados.forEach(detalle -> {
            Inventario inventario = inventarios.get(detalle.getProducto().getId());
            inventarioService.registrarMovimiento(inventario, TipoMovimientoInventario.SALIDA, detalle.getCantidad());
        });

        return guardada;
    }

    @Override
    @Transactional(readOnly = true)
    public long contarVentasMes(Organizacion organizacion, LocalDate inicioMes, LocalDate finMes) {
        return ventaRepository.countByOrganizacionAndFechaVentaBetween(organizacion, inicioMes.atStartOfDay(), finMes.atTime(23, 59, 59));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<LocalDate, BigDecimal> totalPorDia(Organizacion organizacion, LocalDate inicio, LocalDate fin) {
        List<Object[]> filas = ventaRepository.totalPorDia(organizacion, inicio.atStartOfDay(), fin.atTime(23, 59, 59));
        Map<LocalDate, BigDecimal> resultado = new HashMap<>();
        for (Object[] fila : filas) {
            LocalDate dia = fila[0] instanceof LocalDate ? (LocalDate) fila[0] : ((java.sql.Date) fila[0]).toLocalDate();
            BigDecimal total = (BigDecimal) fila[1];
            resultado.put(dia, total);
        }
        return resultado;
    }
}
