package cl.gob.binexus.service;

import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.dto.OrganizacionFormDTO;
import java.util.List;

public interface OrganizacionService {

    List<Organizacion> listarOrganizaciones();

    OrganizacionFormDTO obtenerFormulario(Long id);

    Organizacion guardarOrganizacion(OrganizacionFormDTO dto);
}
