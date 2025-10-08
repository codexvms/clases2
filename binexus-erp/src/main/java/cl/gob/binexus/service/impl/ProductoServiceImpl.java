package cl.gob.binexus.service.impl;

import cl.gob.binexus.domain.entity.Atributo;
import cl.gob.binexus.domain.entity.Parametro;
import cl.gob.binexus.domain.entity.PrecioProducto;
import cl.gob.binexus.domain.entity.Producto;
import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.enums.EstadoProducto;
import cl.gob.binexus.repository.AtributoRepository;
import cl.gob.binexus.repository.ParametroRepository;
import cl.gob.binexus.repository.PrecioProductoRepository;
import cl.gob.binexus.repository.ProductoRepository;
import cl.gob.binexus.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final PrecioProductoRepository precioProductoRepository;
    private final AtributoRepository atributoRepository;
    private final ParametroRepository parametroRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               PrecioProductoRepository precioProductoRepository,
                               AtributoRepository atributoRepository,
                               ParametroRepository parametroRepository) {
        this.productoRepository = productoRepository;
        this.precioProductoRepository = precioProductoRepository;
        this.atributoRepository = atributoRepository;
        this.parametroRepository = parametroRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarProductos(Organizacion organizacion, EstadoProducto estado, String nombre) {
        return productoRepository.buscar(organizacion, estado, nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    }

    @Override
    public Producto guardarProducto(Producto producto, BigDecimal precioUnitario, LocalDate fechaInicio, Parametro parametroAtributo) {
        boolean esNuevo = producto.getId() == null;
        if (producto.getEstado() == null) {
            producto.setEstado(EstadoProducto.ACTIVO);
        }
        if (producto.getStockInicial() == null) {
            producto.setStockInicial(BigDecimal.ZERO);
        }

        Producto guardado = productoRepository.save(producto);

        if (parametroAtributo != null) {
            Atributo atributo = atributoRepository.findByProducto(guardado)
                    .orElseGet(Atributo::new);
            atributo.setProducto(guardado);
            atributo.setMantenedor(parametroRepository.findById(parametroAtributo.getId()).orElse(parametroAtributo));
            atributoRepository.save(atributo);
        }

        if (precioUnitario != null && precioUnitario.compareTo(BigDecimal.ZERO) > 0) {
            LocalDate inicio = fechaInicio != null ? fechaInicio : LocalDate.now();
            cerrarPrecioVigente(guardado, inicio);
            PrecioProducto precio = new PrecioProducto();
            precio.setProducto(guardado);
            precio.setPrecioUnitario(precioUnitario);
            precio.setFechaInicioVigencia(inicio);
            precioProductoRepository.save(precio);
        } else if (esNuevo) {
            throw new IllegalArgumentException("El precio unitario es requerido para nuevos productos");
        }
        return guardado;
    }

    private void cerrarPrecioVigente(Producto producto, LocalDate fechaInicio) {
        Optional<PrecioProducto> vigente = precioProductoRepository.findPrecioVigente(producto, fechaInicio);
        vigente.ifPresent(precio -> {
            if (precio.getFechaFinVigencia() == null || precio.getFechaFinVigencia().isAfter(fechaInicio)) {
                precio.setFechaFinVigencia(fechaInicio.minusDays(1));
                precioProductoRepository.save(precio);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrecioProducto> obtenerHistorialPrecios(Long productoId) {
        Producto producto = obtenerPorId(productoId);
        return precioProductoRepository.findHistorialByProducto(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PrecioProducto> obtenerPrecioVigente(Producto producto, LocalDate fecha) {
        return precioProductoRepository.findPrecioVigente(producto, fecha != null ? fecha : LocalDate.now());
    }
}
