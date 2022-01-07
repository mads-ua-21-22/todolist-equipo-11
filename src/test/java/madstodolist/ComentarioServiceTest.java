package madstodolist;


import madstodolist.model.Comentario;
import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.ComentarioService;
import madstodolist.service.EquipoService;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
public class ComentarioServiceTest {

    @Autowired
    ComentarioService comentarioService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    TareaService tareaService;
    @Test
    public void obtenerListadoComentariosTarea() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        List<Comentario> comentarios = comentarioService.allComentariosTarea(1L);

        // THEN
        assertThat(comentarios).hasSize(1);

    }

    @Test
    public void obtenerComentario() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        Comentario comentario = comentarioService.findById(1L);

        // THEN
        assertThat(comentario.getComentario()).isEqualTo("Un comentario más");
        assertThat(comentario.getUsuario()).isEqualTo(usuarioService.findById(1L));
        assertThat(comentario.getTarea()).isEqualTo(tareaService.findById(1L));

    }

    @Test
    @Transactional
    public void creaComentarios() {
        List<Comentario> comentarios = comentarioService.allComentariosTarea(1L);
        // THEN
        assertThat(comentarios).hasSize(1);
        Comentario comentario = new Comentario(usuarioService.findById(1L),
                tareaService.findById(1L),"Otro comentario");
        Tarea tarea = tareaService.findById(1L);
        assertThat(tarea.getComentarios()).hasSize(2);
    }
}
