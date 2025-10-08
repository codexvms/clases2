package cl.gob.binexus.service;

import cl.gob.binexus.domain.entity.Parametro;
import cl.gob.binexus.domain.entity.TipoParametro;
import cl.gob.binexus.domain.enums.EstadoParametro;

import java.util.List;

public interface ParametroService {

    List<Parametro> listarPorTipo(TipoParametro tipoParametro, boolean soloActivos);

    Parametro obtenerPorId(Long id);

    Parametro guardar(Parametro parametro);

    void cambiarEstado(Long id, EstadoParametro estadoParametro);
}
