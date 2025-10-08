package cl.gob.binexus.service;

import cl.gob.binexus.domain.entity.Local;
import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.entity.Venta;
import cl.gob.binexus.domain.enums.EstadoPago;

import java.time.LocalDate;
import java.util.List;

public interface VentaService {

    List<Venta> buscar(Organizacion organizacion, EstadoPago estadoPago, Local local, LocalDate desde, LocalDate hasta);

    Venta obtenerPorId(Long id);

    Venta registrarVenta(Venta venta);

    long contarVentasMes(Organizacion organizacion, LocalDate inicioMes, LocalDate finMes);

    java.util.Map<LocalDate, java.math.BigDecimal> totalPorDia(Organizacion organizacion, LocalDate inicio, LocalDate fin);
}
