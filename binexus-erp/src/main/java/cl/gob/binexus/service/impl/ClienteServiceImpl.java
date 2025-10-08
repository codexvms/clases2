package cl.gob.binexus.service.impl;

import cl.gob.binexus.domain.entity.Cliente;
import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.enums.EstadoCliente;
import cl.gob.binexus.repository.ClienteRepository;
import cl.gob.binexus.service.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscar(Organizacion organizacion, EstadoCliente estado, String nombre) {
        return clienteRepository.buscar(organizacion, estado, nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        clienteRepository.findByOrganizacionAndCorreoIgnoreCase(cliente.getOrganizacion(), cliente.getCorreo())
                .filter(existing -> !existing.getId().equals(cliente.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Ya existe un cliente con ese correo en la organización");
                });
        return clienteRepository.save(cliente);
    }
}
