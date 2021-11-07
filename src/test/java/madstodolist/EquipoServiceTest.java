package madstodolist;


import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
public class EquipoServiceTest {

    @Autowired
    EquipoService equipoService;

    @Autowired
    UsuarioService usuarioService;

    @Test
    public void obtenerListadoEquipos() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        List<Equipo> equipos = equipoService.findAllOrderedByName();

        // THEN
        assertThat(equipos).hasSize(2);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Proyecto A1");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto P1");
    }

    @Test
    public void obtenerEquipo() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        Equipo equipo = equipoService.findById(1L);

        // THEN
        assertThat(equipo.getNombre()).isEqualTo("Proyecto P1");
        // Comprobamos que la relación con Usuarios es lazy: al
        // intentar acceder a la colección de usuarios se debe lanzar una
        // excepción de tipo LazyInitializationException.
        assertThatThrownBy(() -> {
            equipo.getUsuarios().size();
        }).isInstanceOf(LazyInitializationException.class);
    }

    @Test
    public void comprobarRelacionUsuarioEquipos() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        Usuario usuario = usuarioService.findById(1L);

        // THEN

        assertThat(usuario.getEquipos()).hasSize(1);
    }

    @Test
    public void obtenerUsuariosEquipo() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        List<Usuario> usuarios = equipoService.usuariosEquipo(1L);

        // THEN
        assertThat(usuarios).hasSize(1);
        assertThat(usuarios.get(0).getEmail()).isEqualTo("user@ua");
        // Comprobamos que la relación entre usuarios y equipos es eager
        // Primero comprobamos que la colección de equipos tiene 1 elemento
        assertThat(usuarios.get(0).getEquipos()).hasSize(1);
        // Y después que el elemento es el equipo Proyecto P1
        assertThat(usuarios.get(0).getEquipos().stream().findFirst().get().getNombre()).isEqualTo("Proyecto P1");
    }

    @Test
    @Transactional
    public void creaEquipos() {
        List<Equipo> equipos = equipoService.findAllOrderedByName();
        // THEN
        assertThat(equipos).hasSize(2);
        equipoService.crearEquipo("equipoTest");
        equipos = equipoService.findAllOrderedByName();
        assertThat(equipos).hasSize(3);
    }

    @Test
    @Transactional
    public void addUsuarioEquipo() {
        Usuario usuario = usuarioService.findById(1L);
        assertThat(usuario.getEquipos()).hasSize(1);
        equipoService.agregarAEquipo(1L, 2L);
        assertThat(usuario.getEquipos()).hasSize(2);
        Equipo equipo = equipoService.findById(1L);
        Equipo equipo2 = equipoService.findById(2L);
        assertThat(equipo.getUsuarios()).contains(usuario);
        assertThat(equipo2.getUsuarios()).contains(usuario);
        //Para que no pete ejecucion de tests automaticos...
        equipoService.eliminarDeEquipo(1L, 2L);
    }

    @Test
    @Transactional
    public void deleteUsuarioEquipo() {
        Usuario usuario = usuarioService.findById(1L);
        assertThat(usuario.getEquipos()).hasSize(1);
        equipoService.eliminarDeEquipo(1L, 1L);
        assertThat(usuario.getEquipos()).hasSize(0);
        Equipo equipo = equipoService.findById(1L);
        Equipo equipo2 = equipoService.findById(2L);
        assertThat(equipo.getUsuarios()).doesNotContain(usuario);
        assertThat(equipo2.getUsuarios()).doesNotContain(usuario);
        equipoService.agregarAEquipo(1L, 1L);
    }

    @Test
    @Transactional
    public void editarNombreEquipo() {
        Equipo equipo = equipoService.findById(1L);
        assertThat(equipo.getNombre()).doesNotMatch("No es su nombre");
        equipoService.editarNombreEquipo("No es su nombre",1L);
        assertThat(equipo.getNombre()).matches("No es su nombre");
    }
}
