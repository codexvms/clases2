package cl.gob.binexus.service;

import cl.gob.binexus.domain.entity.Local;
import cl.gob.binexus.dto.LocalFormDTO;
import java.util.List;

public interface LocalService {

    List<Local> listarLocales();

    Local guardarLocal(LocalFormDTO dto);

    Local obtenerPorId(Long id);
}
