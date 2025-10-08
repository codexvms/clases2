package cl.gob.binexus.service;

import cl.gob.binexus.domain.entity.TipoParametro;

import java.util.List;

public interface TipoParametroService {

    List<TipoParametro> listar();

    TipoParametro obtenerPorId(Long id);

    TipoParametro guardar(TipoParametro tipoParametro);

    void eliminar(Long id);
}
