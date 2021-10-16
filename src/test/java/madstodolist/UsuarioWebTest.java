package madstodolist;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.authentication.UsuarioNoLogeadoException;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession userSession;

    // Ejemplo de test en el que se utiliza un mock
    @Test
    public void servicioLoginUsuarioOK() throws Exception {

        Usuario anaGarcia = new Usuario("ana.garcia@gmail.com");
        anaGarcia.setId(1L);
        anaGarcia.setAdministrador(false);

        when(usuarioService.login("ana.garcia@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        when(usuarioService.findByEmail("ana.garcia@gmail.com"))
                .thenReturn(anaGarcia);

        this.mockMvc.perform(post("/login")
                .param("eMail", "ana.garcia@gmail.com")
                .param("password", "12345678"))

                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/1/tareas"));
    }

    // En este test usamos los datos cargados en el fichero de prueba
    @Test
    public void servicioLoginUsuarioNotFound() throws Exception {
        when(usuarioService.login("pepito.perez@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.USER_NOT_FOUND);

        this.mockMvc.perform(post("/login")
                    .param("eMail","pepito.perez@gmail.com")
                    .param("password","12345678"))
                .andExpect(content().string(containsString("No existe usuario")));
    }

    @Test
    public void servicioLoginUsuarioErrorPassword() throws Exception {
        when(usuarioService.login("ana.garcia@gmail.com", "000"))
                .thenReturn(UsuarioService.LoginStatus.ERROR_PASSWORD);

        this.mockMvc.perform(post("/login")
                    .param("eMail","ana.garcia@gmail.com")
                    .param("password","000"))
                .andExpect(content().string(containsString("Contraseña incorrecta")));
    }

    @Test
    public void servicioLoginRedirectContraseñaIncorrecta() throws Exception {
        this.mockMvc.perform(get("/login")
                .flashAttr("error", "Contraseña incorrecta"))
                .andExpect(content().string(containsString("Contraseña incorrecta")));
    }

    @Test
    public void servicioLoginRedirectUsuarioNotFound() throws Exception {
        this.mockMvc.perform(get("/login")
                .flashAttr("error", "No existe usuario"))
                .andExpect(content().string(containsString("No existe usuario")));
    }

    @Test
    public void servicioAllUsuariosError() throws Exception {
        this.mockMvc.perform(get("/usuarios"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void servicioAllUsuariosNotAdmin() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(false);
        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(get("/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void servicioAllUsuarios() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);
        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(get("/usuarios"))
                .andExpect(content().string(containsString("Email")));
    }

    @Test
    public void servicioAllUsuarios2() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(usuario);
        when(usuarioService.findById(null)).thenReturn(usuario);
        when(usuarioService.allUsuarios()).thenReturn(usuarios);

        this.mockMvc.perform(get("/usuarios"))
                .andExpect(content().string(containsString("domingo@ua.es")))
                .andExpect(content().string(containsString("1")));
        ;
    }

    @Test
    public void servicioDescripciónUsuarioError() throws Exception {
        this.mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void servicioDescripciónUsuarioNotAdmin() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(false);
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(usuario);
        when(usuarioService.findById(null)).thenReturn(usuario);
        when(usuarioService.allUsuarios()).thenReturn(usuarios);

        this.mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void servicioDescripcionUsuario() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);
        when(usuarioService.findById(1L)).thenReturn(usuario);
        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(get("/usuarios/1"))
                .andExpect(content().string(containsString("domingo@ua.es")));
    }

    @Test
    public void servicioUsuarioBloqueadoLogin() throws Exception {
        Usuario anaGarcia = new Usuario("ana.garcia@gmail.com");
        anaGarcia.setId(1L);
        anaGarcia.setBloqueado(true);

        when(usuarioService.login("ana.garcia@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        when(usuarioService.findByEmail("ana.garcia@gmail.com"))
                .thenReturn(anaGarcia);

        this.mockMvc.perform(post("/login")
                        .param("eMail", "ana.garcia@gmail.com")
                        .param("password", "12345678"))
                .andExpect(content().string(containsString("El usuario ha sido bloqueado")));
    }
}
