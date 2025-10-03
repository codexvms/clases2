package cl.gob.binexus.service.impl;

import cl.gob.binexus.domain.entity.Rol;
import cl.gob.binexus.repository.RolRepository;
import cl.gob.binexus.service.RolService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }
}
