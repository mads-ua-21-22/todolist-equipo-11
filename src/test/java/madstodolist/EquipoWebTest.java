package madstodolist;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import madstodolist.service.UsuarioServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EquipoWebTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UsuarioService usuarioService;
    @MockBean
    private EquipoService equipoService;
    // Al mocker el manegerUserSession, no lanza la excepción cuando
    // se intenta comprobar si un usuario está logeado
    @MockBean
    private ManagerUserSession managerUserSession;

    @Test
    public void listadoDeEquipos() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(containsString("Listado de equipos")));
    }

    @Test
    public void verEquiposApareceNavBar() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(containsString("Equipos")));
    }
    @Test
    public void infoDelEquipo() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);
        List<Equipo> equipos = new ArrayList<>();
        Equipo equipo = new Equipo("Nombre");
        equipo.addUsuario(usuario);
        equipos.add(equipo);
        when(usuarioService.findById(null)).thenReturn(usuario);
        when(equipoService.findById(1L)).thenReturn(equipo);

        when(equipoService.findAllOrderedByName()).thenReturn(equipos);

        this.mockMvc.perform(get("/equipos/1"))
                .andExpect(content().string(containsString("Listado de usuarios que componen el equipo")));
    }

    @Test
    public void apareceBotonEnEquipos() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(containsString("Crear equipo")));
    }
    @Test
    public void vistaCrearEquipo() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(get("/equipos/crear"))
                .andExpect(content().string(containsString("Creando nuevo equipo")));
    }
    @Test
    @Transactional
    public void agregaAEquipo() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(post("/equipos/1/agregar"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"));
    }

    @Test
    @Transactional
    public void borrarmeDeEquipo() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(delete("/equipos/1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("")));
    }

    @Test
    @Transactional
    public void borrarEquipo() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(delete("/equipo/1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("")));
    }
    @Test
    @Transactional
    public void editarEquipo() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(post("/equipos/1/editar"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"));
    }
}
