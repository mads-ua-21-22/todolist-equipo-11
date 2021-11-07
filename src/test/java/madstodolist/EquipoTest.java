package madstodolist;

import madstodolist.model.Equipo;
import madstodolist.model.EquipoRepository;
import madstodolist.model.Usuario;
import madstodolist.model.UsuarioRepository;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class EquipoTest {
    @Test
    public void crearEquipo() {
        Equipo equipo = new Equipo("Proyecto P1");
        assertThat(equipo.getNombre()).isEqualTo("Proyecto P1");
    }
    @Autowired
    private EquipoRepository equipoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @Transactional
    public void grabarEquipo() {
        // GIVEN
        Equipo equipo = new Equipo("Proyecto P1");

        // WHEN
        equipoRepository.save(equipo);

        // THEN
        assertThat(equipo.getId()).isNotNull();
    }
    @Test
    public void comprobarIgualdadEquipos() {
        // GIVEN
        // Creamos tres equipos sin id, sólo con el nombre
        Equipo equipo1 = new Equipo("Proyecto P1");
        Equipo equipo2 = new Equipo("Proyecto P2");
        Equipo equipo3 = new Equipo("Proyecto P2");

        // THEN
        // Comprobamos igualdad basada en el atributo nombre
        assertThat(equipo1).isNotEqualTo(equipo2);
        assertThat(equipo2).isEqualTo(equipo3);

        // WHEN
        // Añadimos identificadores y comprobamos igualdad por identificadores
        equipo1.setId(1L);
        equipo2.setId(1L);
        equipo3.setId(2L);

        // THEN
        // Comprobamos igualdad basada en el atributo nombre
        assertThat(equipo1).isEqualTo(equipo2);
        assertThat(equipo2).isNotEqualTo(equipo3);
    }

    @Test
    public void comprobarRecuperarEquipo() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN

        Equipo equipo = equipoRepository.findById(1L).orElse(null);

        // THEN
        assertThat(equipo).isNotNull();
        assertThat(equipo.getId()).isEqualTo(1L);
        assertThat(equipo.getNombre()).isEqualTo("Proyecto P1");
    }

    @Test
    public void comprobarFindAll() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        List<Equipo> equipos = equipoRepository.findAll();

        // THEN
        assertThat(equipos).hasSize(2);
    }

    @Test
    @Transactional
    public void actualizarRelacionUsuarioEquipos() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql
        Usuario usuario = usuarioRepository.findById(1L).orElse(null);
        Equipo equipo = equipoRepository.findById(2L).orElse(null);
        // WHEN
        equipo.addUsuario(usuario);
        // THEN
        assertThat(equipo.getUsuarios()).contains(usuario);
        assertThat(usuario.getEquipos()).contains(equipo);
        //Para que no peten los tests automaticos
        equipo.deleteUsuario(usuario);
    }
    @Test
    @Transactional
    public void borrarUsuarioDeEquipo() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql
        Usuario usuario = usuarioRepository.findById(1L).orElse(null);
        Equipo equipo = equipoRepository.findById(1L).orElse(null);
        assertThat(equipo.getUsuarios()).contains(usuario);
        assertThat(usuario.getEquipos()).contains(equipo);
        // WHEN
        equipo.deleteUsuario(usuario);
        // THEN
        assertThat(equipo.getUsuarios()).doesNotContain(usuario);
        assertThat(usuario.getEquipos()).doesNotContain(equipo);
        //Para que no peten los tests automaticos
        equipo.addUsuario(usuario);
    }
}
