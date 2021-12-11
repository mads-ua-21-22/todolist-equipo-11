package madstodolist.service;

import madstodolist.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TareaService {

    Logger logger = LoggerFactory.getLogger(TareaService.class);

    private UsuarioRepository usuarioRepository;
    private TareaRepository tareaRepository;
    private EquipoRepository equipoRepository;

    @Autowired
    public TareaService(UsuarioRepository usuarioRepository, TareaRepository tareaRepository, EquipoRepository equipoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tareaRepository = tareaRepository;
        this.equipoRepository = equipoRepository;
    }

    @Transactional
    public Tarea nuevaTareaUsuario(Long idUsuario, String tituloTarea) {
        logger.debug("Añadiendo tarea " + tituloTarea + " al usuario " + idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al crear tarea " + tituloTarea);
        }
        Tarea tarea = new Tarea(usuario, tituloTarea);
        tareaRepository.save(tarea);
        return tarea;
    }

    @Transactional
    public Tarea nuevaTareaUsuario(Long idUsuario,String tituloTarea, String descTarea){
        logger.debug("Añadiendo tarea " + tituloTarea + "al usuario " + idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al crear tarea " + tituloTarea);
        }
        Tarea tarea = new Tarea(usuario,tituloTarea);
        tarea.setDescripcion(descTarea);
        tareaRepository.save(tarea);
        return tarea;
    }

    //Se le asigna la tarea al usuario que la crea por un problema con la BD que aunque le he quitado el NotNull
    // me sigue pidiendo una id
    @Transactional
    public Tarea nuevaTareaEquipo(Long idEquipo,Long idUsuario,String tituloTarea) {
        logger.debug("Añadiendo tarea "+tituloTarea+" al equipo "+idEquipo);

        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null)
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al crear tarea " + tituloTarea);

        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        if(equipo == null)
            throw new TareaServiceException("Equipo "+idEquipo+" no exite al crear tarea "+ tituloTarea);

        Tarea tarea = new Tarea(usuario,tituloTarea);
        tarea.setEquipo(equipo);
        tareaRepository.save(tarea);

        return tarea;
    }

    @Transactional
    public Tarea nuevaTareaEquipo(Long idEquipo,Long idUsuario,String tituloTarea,String descTarea) {
        logger.debug("Añadiendo tarea "+tituloTarea+" al equipo "+idEquipo);

        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null)
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al crear tarea " + tituloTarea);

        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        if(equipo == null)
            throw new TareaServiceException("Equipo "+idEquipo+" no exite al crear tarea "+ tituloTarea);

        Tarea tarea = new Tarea(usuario,tituloTarea);
        tarea.setEquipo(equipo);
        tarea.setDescripcion(descTarea);
        tareaRepository.save(tarea);

        return tarea;
    }

    @Transactional(readOnly = true)
    public List<Tarea> allTareasUsuario(Long idUsuario) {
        logger.debug("Devolviendo todas las tareas del usuario " + idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al listar tareas ");
        }
        List<Tarea> tareas = new ArrayList(usuario.getTareas());
        Collections.sort(tareas, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
        return tareas;
    }

    @Transactional(readOnly = true)
    public Tarea findById(Long tareaId) {
        logger.debug("Buscando tarea " + tareaId);
        return tareaRepository.findById(tareaId).orElse(null);
    }

    @Transactional
    public Tarea modificaTarea(Long idTarea, String nuevoTitulo) {
        logger.debug("Modificando tarea " + idTarea + " - " + nuevoTitulo);
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        tarea.setTitulo(nuevoTitulo);
        tareaRepository.save(tarea);
        return tarea;
    }

    @Transactional
    public Tarea modificaTarea(Long idTarea, String nuevoTitulo,String nuevaDescripcion) {
        logger.debug("Modificando tarea " + idTarea + " - " + nuevoTitulo);
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        tarea.setTitulo(nuevoTitulo);
        tarea.setDescripcion(nuevaDescripcion);
        tareaRepository.save(tarea);
        return tarea;
    }

    @Transactional
    public void borraTarea(Long idTarea) {
        logger.debug("Borrando tarea " + idTarea);
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        tareaRepository.delete(tarea);
    }

    public ArrayList<Tarea> allTareasCompletadasUsuario(Long idUsuario) {
        return new ArrayList(tareaRepository.allTareasCompletadas(idUsuario));
    }

    public ArrayList<Tarea> allTareasNoCompletadasUsuario(Long idUsuario) {
        return new ArrayList(tareaRepository.allTareasNoCompletadas(idUsuario));
    }

    public ArrayList<Tarea> allTareasCompletadasEquipo(Long idEquipo){
        return new ArrayList(tareaRepository.allTareasCompletadasEquipo(idEquipo));
    }

    public ArrayList<Tarea> allTareasNoCompletadasEquipo(Long idEquipo){
        return  new ArrayList(tareaRepository.allTareasNoCompletadasEquipo(idEquipo));
    }

    @Transactional
    public void completaTarea(Tarea tarea) {
        tarea.setComplete();
    }
}
