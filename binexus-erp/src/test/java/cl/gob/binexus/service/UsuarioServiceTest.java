package cl.gob.binexus.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cl.gob.binexus.domain.entity.Organizacion;
import cl.gob.binexus.domain.entity.Rol;
import cl.gob.binexus.domain.enums.EstadoOrganizacion;
import cl.gob.binexus.domain.enums.EstadoUsuario;
import cl.gob.binexus.dto.UsuarioFormDTO;
import cl.gob.binexus.repository.OrganizacionRepository;
import cl.gob.binexus.repository.RolRepository;
import jakarta.transaction.Transactional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private OrganizacionRepository organizacionRepository;

    private Rol rolAdmin;
    private Organizacion organizacion;

    @BeforeEach
    void setUp() {
        if (rolRepository.count() == 0) {
            Rol rol = new Rol();
            rol.setNombre("ROLE_ADMIN");
            rolAdmin = rolRepository.save(rol);
        } else {
            rolAdmin = rolRepository.findByNombre("ROLE_ADMIN").orElseThrow();
        }

        if (organizacionRepository.count() == 0) {
            Organizacion org = new Organizacion();
            org.setNombre("Organización Test");
            org.setEstado(EstadoOrganizacion.ACTIVO);
            organizacion = organizacionRepository.save(org);
        } else {
            organizacion = organizacionRepository.findAll().get(0);
        }
    }

    @Test
    void guardarUsuario_creaNuevoUsuarioConPasswordEncriptado() {
        UsuarioFormDTO dto = new UsuarioFormDTO();
        dto.setNombre("Jane Doe");
        dto.setCorreo("jane@binexus.com");
        dto.setPassword("Segura123!");
        dto.setEstado(EstadoUsuario.ACTIVO);
        dto.setOrganizacionId(organizacion.getId());
        dto.setRolesIds(Set.of(rolAdmin.getId()));

        var usuario = usuarioService.guardarUsuario(dto);

        assertThat(usuario.getId()).isNotNull();
        assertThat(usuario.getPassword()).isNotEqualTo(dto.getPassword());
        assertThat(usuario.getCorreo()).isEqualTo("jane@binexus.com");
    }

    @Test
    void guardarUsuario_conCorreoDuplicadoLanzaExcepcion() {
        UsuarioFormDTO dto = new UsuarioFormDTO();
        dto.setNombre("Jane Doe");
        dto.setCorreo("jane@binexus.com");
        dto.setPassword("Segura123!");
        dto.setEstado(EstadoUsuario.ACTIVO);
        dto.setOrganizacionId(organizacion.getId());
        dto.setRolesIds(Set.of(rolAdmin.getId()));
        usuarioService.guardarUsuario(dto);

        UsuarioFormDTO duplicado = new UsuarioFormDTO();
        duplicado.setNombre("John Doe");
        duplicado.setCorreo("jane@binexus.com");
        duplicado.setPassword("Segura123!");
        duplicado.setEstado(EstadoUsuario.ACTIVO);
        duplicado.setOrganizacionId(organizacion.getId());
        duplicado.setRolesIds(Set.of(rolAdmin.getId()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.guardarUsuario(duplicado));

        assertThat(exception.getMessage()).contains("correo electrónico ya está registrado");
    }
}
