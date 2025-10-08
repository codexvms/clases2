package cl.gob.binexus.service.impl;

import cl.gob.binexus.domain.entity.Parametro;
import cl.gob.binexus.domain.entity.TipoParametro;
import cl.gob.binexus.domain.enums.EstadoParametro;
import cl.gob.binexus.repository.ParametroRepository;
import cl.gob.binexus.service.ParametroService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ParametroServiceImpl implements ParametroService {

    private final ParametroRepository parametroRepository;

    public ParametroServiceImpl(ParametroRepository parametroRepository) {
        this.parametroRepository = parametroRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Parametro> listarPorTipo(TipoParametro tipoParametro, boolean soloActivos) {
        if (soloActivos) {
            return parametroRepository.findByTipoParametroAndEstadoOrderByDescripcionAsc(tipoParametro, EstadoParametro.ACTIVO);
        }
        return parametroRepository.findByTipoParametroOrderByDescripcionAsc(tipoParametro);
    }

    @Override
    @Transactional(readOnly = true)
    public Parametro obtenerPorId(Long id) {
        return parametroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parámetro no encontrado"));
    }

    @Override
    public Parametro guardar(Parametro parametro) {
        parametroRepository.findByTipoParametroAndDescripcionIgnoreCase(parametro.getTipoParametro(), parametro.getDescripcion())
                .filter(existing -> !existing.getId().equals(parametro.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Ya existe un parámetro con esa descripción para el tipo seleccionado");
                });
        return parametroRepository.save(parametro);
    }

    @Override
    public void cambiarEstado(Long id, EstadoParametro estadoParametro) {
        Parametro parametro = obtenerPorId(id);
        parametro.setEstado(estadoParametro);
        parametroRepository.save(parametro);
    }
}
