package madstodolist;

import madstodolist.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ComentarioTest {
    @Test
    public void crearComentario() {
        Usuario usuario = new Usuario("test@ua");
        Tarea tarea = new Tarea(usuario,"Titulo");
        Comentario comentario = new Comentario(usuario,tarea,"Un comentario más");
        assertThat(comentario.getComentario()).isEqualTo("Un comentario más");
    }
    @Autowired
    private TareaRepository tareaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ComentarioRepository comentarioRepository;


    @Test
    public void comprobarIgualdadComentarios() {
        // GIVEN
        Usuario usuario = new Usuario("test@ua");
        Tarea tarea = new Tarea(usuario,"Titulo");
        Comentario comentario1 = new Comentario(usuario,tarea,"Un comentario más");
        Comentario comentario2 = new Comentario(usuario,tarea,"Un comentario más");
        // THEN
        // Comprobamos igualdad
        assertThat(comentario1).isNotEqualTo(comentario2);

        // WHEN
        // Añadimos identificadores y comprobamos igualdad por identificadores
        comentario1.setId(1L);
        comentario2.setId(1L);

        // THEN
        // Comprobamos igualdad
        assertThat(comentario1).isEqualTo(comentario2);
    }

    @Test
    public void comprobarRecuperarComentario() {

        Comentario comentario = comentarioRepository.findById(1L).orElse(null);

        // THEN
        assertThat(comentario).isNotNull();
        assertThat(comentario.getId()).isEqualTo(1L);
        assertThat(comentario.getComentario()).isEqualTo("Un comentario más");
    }

    @Test
    @Transactional(readOnly = true)
    public void comprobarFindAll() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        List<Comentario> comentarios = comentarioRepository.findAll();

        // THEN
        assertThat(comentarios).hasSize(1);
    }


    @Test
    public void verComentarioTest() {
        // Equipo que incialmente tiene un string vacío
        Comentario comentario = comentarioRepository.findById(1L).orElse(null);

        assertThat(comentario.getComentario()).isEqualTo("Un comentario más");
    }

}
