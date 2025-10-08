package cl.gob.binexus.service.impl;

import cl.gob.binexus.domain.entity.TipoParametro;
import cl.gob.binexus.repository.TipoParametroRepository;
import cl.gob.binexus.service.TipoParametroService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TipoParametroServiceImpl implements TipoParametroService {

    private final TipoParametroRepository tipoParametroRepository;

    public TipoParametroServiceImpl(TipoParametroRepository tipoParametroRepository) {
        this.tipoParametroRepository = tipoParametroRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoParametro> listar() {
        return tipoParametroRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoParametro obtenerPorId(Long id) {
        return tipoParametroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de parámetro no encontrado"));
    }

    @Override
    public TipoParametro guardar(TipoParametro tipoParametro) {
        tipoParametroRepository.findByDescripcionIgnoreCase(tipoParametro.getDescripcion())
                .filter(existing -> !existing.getId().equals(tipoParametro.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Ya existe un tipo de parámetro con esa descripción");
                });
        return tipoParametroRepository.save(tipoParametro);
    }

    @Override
    public void eliminar(Long id) {
        TipoParametro tipo = obtenerPorId(id);
        tipoParametroRepository.delete(tipo);
    }
}
