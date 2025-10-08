package cl.gob.binexus.service.impl;

import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.dto.OrganizacionFormDTO;
import cl.gob.binexus.repository.OrganizacionRepository;
import cl.gob.binexus.service.OrganizacionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrganizacionServiceImpl implements OrganizacionService {

    private final OrganizacionRepository organizacionRepository;

    public OrganizacionServiceImpl(OrganizacionRepository organizacionRepository) {
        this.organizacionRepository = organizacionRepository;
    }

    @Override
    public List<Organizacion> listarOrganizaciones() {
        return organizacionRepository.findAll();
    }

    @Override
    public OrganizacionFormDTO obtenerFormulario(Long id) {
        if (id == null) {
            return new OrganizacionFormDTO();
        }
        Organizacion organizacion = organizacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organización no encontrada"));
        OrganizacionFormDTO dto = new OrganizacionFormDTO();
        dto.setId(organizacion.getId());
        dto.setNombre(organizacion.getNombre());
        dto.setEstado(organizacion.getEstado());
        return dto;
    }

    @Override
    public Organizacion guardarOrganizacion(OrganizacionFormDTO dto) {
        Organizacion organizacion = dto.getId() != null
                ? organizacionRepository.findById(dto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Organización no encontrada"))
                : new Organizacion();
        organizacion.setNombre(dto.getNombre());
        organizacion.setEstado(dto.getEstado());
        return organizacionRepository.save(organizacion);
    }

    @Override
    public Organizacion obtenerPorId(Long id) {
        return organizacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organización no encontrada"));
    }
}
