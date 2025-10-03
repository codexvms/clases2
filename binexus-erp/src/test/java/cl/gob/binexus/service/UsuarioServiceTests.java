package cl.gob.binexus.service;

import cl.gob.binexus.domain.Usuario;
import cl.gob.binexus.dto.UsuarioDTO;
import cl.gob.binexus.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:binexus;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=none"
})
@ActiveProfiles("test")
class UsuarioServiceTests {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
    }

    @Test
    void registrarUsuarioNuevoGeneraPasswordBCryptYRolPorDefecto() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Usuario Prueba");
        dto.setEmail("nuevo@correo.com");
        dto.setPassword("Secreta123");

        Usuario usuario = usuarioService.registrar(dto);

        assertThat(usuario.getId()).isNotNull();
        assertThat(usuario.getPasswordHash()).isNotEqualTo(dto.getPassword());
        assertThat(usuario.getRoles())
            .extracting("nombre")
            .contains("ROLE_USER");
    }

    @Test
    void noPermiteDuplicarCorreo() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Usuario Prueba");
        dto.setEmail("duplicado@correo.com");
        dto.setPassword("Secreta123");
        usuarioService.registrar(dto);

        UsuarioDTO duplicado = new UsuarioDTO();
        duplicado.setNombre("Otro Usuario");
        duplicado.setEmail("duplicado@correo.com");
        duplicado.setPassword("Secreta456");

        assertThatThrownBy(() -> usuarioService.registrar(duplicado))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void actualizarPermiteModificarNombreYCorreo() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Original");
        dto.setEmail("original@correo.com");
        dto.setPassword("Secreta123");
        Usuario usuario = usuarioService.registrar(dto);

        UsuarioDTO cambios = new UsuarioDTO();
        cambios.setId(usuario.getId());
        cambios.setNombre("Actualizado");
        cambios.setEmail("actualizado@correo.com");
        cambios.setPassword("");

        Usuario actualizado = usuarioService.actualizar(cambios);

        assertThat(actualizado.getNombre()).isEqualTo("Actualizado");
        assertThat(actualizado.getEmail()).isEqualTo("actualizado@correo.com");
        assertThat(actualizado.getPasswordHash()).isEqualTo(usuario.getPasswordHash());
    }
}
