package madstodolist;


import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
public class TareaServiceTest {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    TareaService tareaService;

    @Autowired
    EquipoService equipoService;

    @Test
    @Transactional
    public void testNuevaTareaUsuario() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        Tarea tarea = tareaService.nuevaTareaUsuario(1L, "Práctica 1 de MADS");

        // THEN

        Usuario usuario = usuarioService.findByEmail("user@ua");
        assertThat(usuario.getTareas()).contains(tarea);
    }

    @Test
    public void testListadoTareas() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        Usuario usuario = new Usuario("ana.garcia@gmail.com");
        usuario.setId(1L);

        Tarea lavarCoche = new Tarea(usuario, "Lavar coche");
        lavarCoche.setId(1L);

        // WHEN

        List<Tarea> tareas = tareaService.allTareasUsuario(1L);

        // THEN

        assertThat(tareas.size()).isEqualTo(2);
        assertThat(tareas).contains(lavarCoche);
    }

    @Test
    public void testBuscarTarea() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN

        Tarea lavarCoche = tareaService.findById(1L);

        // THEN

        assertThat(lavarCoche).isNotNull();
        assertThat(lavarCoche.getTitulo()).isEqualTo("Lavar coche");
    }

    @Test
    @Transactional
    public void testModificarTarea() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        Tarea tarea = tareaService.nuevaTareaUsuario(1L, "Pagar el recibo");
        Long idNuevaTarea = tarea.getId();

        // WHEN

        Tarea tareaModificada = tareaService.modificaTarea(idNuevaTarea, "Pagar la matrícula");
        Tarea tareaBD = tareaService.findById(idNuevaTarea);

        // THEN

        assertThat(tareaModificada.getTitulo()).isEqualTo("Pagar la matrícula");
        assertThat(tareaBD.getTitulo()).isEqualTo("Pagar la matrícula");
    }

    @Test
    @Transactional
    public void testBorrarTarea() {
        // GIVEN

        Tarea tarea = tareaService.nuevaTareaUsuario(1L, "Estudiar MADS");

        // WHEN

        tareaService.borraTarea(tarea.getId());

        // THEN

        assertThat(tareaService.findById(tarea.getId())).isNull();
    }

    @Test
    @Transactional
    public void allTareasCompletadasTest() {
        Tarea t1 = tareaService.nuevaTareaUsuario(1L, "Tarea 1");
        Tarea t2 = tareaService.nuevaTareaUsuario(1L, "Tarea 2");
        Tarea t3 = tareaService.nuevaTareaUsuario(1L, "Tarea 3");

        t2.setComplete(); t3.setComplete();
        ArrayList<Tarea> tareasCompletadas = tareaService.allTareasCompletadasUsuario(1L);

        assertThat(tareasCompletadas).hasSize(2);
    }

    @Test
    @Transactional
    public void allTareasNoCompletadasTest() {
        Tarea t1 = tareaService.nuevaTareaUsuario(1L, "Tarea 1");
        Tarea t2 = tareaService.nuevaTareaUsuario(1L, "Tarea 2");
        Tarea t3 = tareaService.nuevaTareaUsuario(1L, "Tarea 3");

        t2.setComplete(); t3.setComplete();
        ArrayList<Tarea> tareasCompletadas = tareaService.allTareasNoCompletadasUsuario(1L);

        assertThat(tareasCompletadas).hasSize(3);
    }

    @Test
    public void cambiarTareaACompletada() {
        Tarea tarea = tareaService.findById(1L);
        tareaService.completaTarea(tarea);

        assertThat(tarea.isComplete()).isTrue();
    }

    @Test
    @Transactional
    public void cambiarUsuarioTareaEquipoTest() {
        Tarea tarea = tareaService.findById(1L);
        Equipo equipo = equipoService.findById(1L);
        tarea.setEquipo(equipo);

        Usuario usuario = new Usuario("ejemplo@ua");
        usuario.setPassword("123");
        usuarioService.registrar(usuario);

        equipoService.agregarAEquipo(usuario.getId(), equipo.getId());
        assertThat(tarea.getUsuario()).isNotEqualTo(usuario);
        boolean resultado = tareaService.cambiarUsuarioTarea(tarea.getId(), usuario.getId());

        assertThat(resultado).isEqualTo(true);
        assertThat(tarea.getUsuario()).isEqualTo(usuario);
    }

    @Test
    @Transactional
    public void cambiarUsuarioTareaEquipoYaNoSePuedeTest() {
        // inicializamos datos
        Tarea tarea = tareaService.findById(1L);
        Equipo equipo = equipoService.findById(1L);
        tarea.setEquipo(equipo);
        Usuario userTarea = usuarioService.findById(2L);

        // cambiamos usuario a tarea
        boolean resultado = tareaService.cambiarUsuarioTarea(tarea.getId(), userTarea.getId());
        assertThat(resultado).isEqualTo(true);

        // intetamos cambiar usuario cuando ya no se puede
        Usuario usuario = new Usuario("ejemplo@ua");
        usuario.setPassword("123");
        usuarioService.registrar(usuario);

        equipoService.agregarAEquipo(usuario.getId(), equipo.getId());
        assertThat(tarea.getUsuario()).isNotEqualTo(usuario);
        resultado = tareaService.cambiarUsuarioTarea(tarea.getId(), usuario.getId());

        // cmprobamos resultados
        assertThat(resultado).isEqualTo(false);
    }
}
