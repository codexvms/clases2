package cl.gob.binexus.controller;

import cl.gob.binexus.domain.entity.CierreCaja;
import cl.gob.binexus.domain.entity.Usuario;
import cl.gob.binexus.domain.enums.EstadoAutorizacion;
import cl.gob.binexus.service.AutorizacionRemotaService;
import cl.gob.binexus.service.CierreCajaService;
import cl.gob.binexus.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AutorizacionRemotaController.class)
@AutoConfigureMockMvc
class AutorizacionRemotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AutorizacionRemotaService autorizacionRemotaService;

    @MockBean
    private CierreCajaService cierreCajaService;

    @MockBean
    private UsuarioService usuarioService;

    private Usuario usuarioAutenticado() {
        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setNombre("Usuario Demo");
        usuario.setCorreo("demo@binexus.cl");
        return usuario;
    }

    @Test
    @DisplayName("Solicitar autorización crea el registro cuando existe caja abierta")
    void solicitarAutorizacionConCajaAbierta() throws Exception {
        Usuario usuario = usuarioAutenticado();
        CierreCaja caja = new CierreCaja();
        caja.setId(4L);
        caja.setFechaApertura(LocalDateTime.now().minusHours(1));

        given(usuarioService.obtenerPorCorreo(usuario.getCorreo())).willReturn(usuario);
        given(cierreCajaService.obtenerCajaAbierta(usuario)).willReturn(Optional.of(caja));

        mockMvc.perform(post("/autorizaciones")
                        .param("accion", "Modificar precio")
                        .with(SecurityMockMvcRequestPostProcessors.user(usuario.getCorreo()))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/autorizaciones"))
                .andExpect(flash().attribute("toastSuccess", "Autorización solicitada correctamente"));

        verify(autorizacionRemotaService).solicitar(usuario, caja, "Modificar precio");
    }

    @Test
    @DisplayName("Solicitar autorización sin caja abierta muestra error")
    void solicitarAutorizacionSinCaja() throws Exception {
        Usuario usuario = usuarioAutenticado();
        given(usuarioService.obtenerPorCorreo(usuario.getCorreo())).willReturn(usuario);
        given(cierreCajaService.obtenerCajaAbierta(usuario)).willReturn(Optional.empty());

        mockMvc.perform(post("/autorizaciones")
                        .param("accion", "Anular venta")
                        .with(SecurityMockMvcRequestPostProcessors.user(usuario.getCorreo()))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/autorizaciones"))
                .andExpect(flash().attribute("toastError", "Debes abrir una caja para solicitar autorizaciones"));

        Mockito.verifyNoInteractions(autorizacionRemotaService);
    }

    @Test
    @DisplayName("Lista de autorizaciones incluye datos y estados")
    void listarAutorizaciones() throws Exception {
        Usuario usuario = usuarioAutenticado();
        given(usuarioService.obtenerPorCorreo(usuario.getCorreo())).willReturn(usuario);
        given(cierreCajaService.obtenerCajaAbierta(usuario)).willReturn(Optional.empty());
        given(autorizacionRemotaService.listarPorEstado(null)).willReturn(List.of());

        mockMvc.perform(get("/autorizaciones").with(SecurityMockMvcRequestPostProcessors.user(usuario.getCorreo())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("autorizaciones"))
                .andExpect(model().attribute("estadoSeleccionado", (EstadoAutorizacion) null))
                .andExpect(view().name("authorizations/autorizaciones-list"));
    }
}
