package madstodolist.service;

import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class ComentarioService {

    Logger logger = LoggerFactory.getLogger(ComentarioService.class);

    private UsuarioRepository usuarioRepository;
    private TareaRepository tareaRepository;
    private ComentarioRepository comentarioRepository;

    @Autowired
    public ComentarioService(UsuarioRepository usuarioRepository, TareaRepository tareaRepository, ComentarioRepository comentarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tareaRepository = tareaRepository;
        this.comentarioRepository = comentarioRepository;
    }

    @Transactional
    public Comentario nuevoComentarioTarea(Long idUsuario, Long idTarea, String descComentario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if(tarea == null )
            throw new TareaServiceException("No existe la tarea " +idTarea );
        if(usuario == null)
            throw new UsuarioNotFoundException();
        Comentario comentario = new Comentario(usuario,tarea, descComentario);
        comentarioRepository.save(comentario);
        return comentario;
    }

    @Transactional(readOnly = true)
    public List<Comentario> allComentariosTarea(Long idTarea) {
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe la tarea " +idTarea );
        }
        List<Comentario> comentarios = new ArrayList(tarea.getComentarios());
        Collections.sort(comentarios, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
        return comentarios;
    }

    @Transactional(readOnly = true)
    public Comentario findById(Long comentarioId) {
        return comentarioRepository.findById(comentarioId).orElse(null);
    }

}
