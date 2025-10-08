package cl.gob.binexus.service;

import cl.gob.binexus.domain.entity.Cliente;
import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.enums.EstadoCliente;

import java.util.List;

public interface ClienteService {

    List<Cliente> buscar(Organizacion organizacion, EstadoCliente estado, String nombre);

    Cliente obtenerPorId(Long id);

    Cliente guardar(Cliente cliente);
}
