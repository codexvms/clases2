package cl.gob.binexus.service.impl;

import cl.gob.binexus.domain.entity.Local;
import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.dto.LocalFormDTO;
import cl.gob.binexus.repository.LocalRepository;
import cl.gob.binexus.repository.OrganizacionRepository;
import cl.gob.binexus.service.LocalService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class LocalServiceImpl implements LocalService {

    private final LocalRepository localRepository;
    private final OrganizacionRepository organizacionRepository;

    public LocalServiceImpl(LocalRepository localRepository, OrganizacionRepository organizacionRepository) {
        this.localRepository = localRepository;
        this.organizacionRepository = organizacionRepository;
    }

    @Override
    public List<Local> listarLocales() {
        return localRepository.findAll();
    }

    @Override
    public Local guardarLocal(LocalFormDTO dto) {
        Organizacion organizacion = organizacionRepository.findById(dto.getOrganizacionId())
                .orElseThrow(() -> new EntityNotFoundException("Organización no encontrada"));
        Local local = dto.getId() != null
                ? localRepository.findById(dto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Local no encontrado"))
                : new Local();
        local.setOrganizacion(organizacion);
        local.setNombre(dto.getNombre());
        local.setEstado(dto.getEstado());
        return localRepository.save(local);
    }
}
