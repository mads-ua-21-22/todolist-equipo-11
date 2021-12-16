package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.TareaNotFoundException;
import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.ComentarioService;
import madstodolist.service.EquipoService;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ComentarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    TareaService tareaService;
    @Autowired
    ComentarioService comentarioService;
    @Autowired
    EquipoService equipoService;

    @Autowired
    ManagerUserSession managerUserSession;


    @GetMapping("/usuarios/{id}/tareas/{tarea}/comentario")
    public String formNuevoComentario(@PathVariable(value="tarea") Long idTarea,
                                      @PathVariable(value="id") Long idUsuario, Model model,
                                 HttpSession session) {

        managerUserSession.comprobarUsuarioLogeado(session, idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        Tarea tarea = tareaService.findById(idTarea);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        if(tarea == null)
            throw new TareaNotFoundException();
        if(tarea.getUsuario() != usuario)
            throw new UsuarioNotFoundException();

        model.addAttribute("usuario", usuario);
        model.addAttribute("tarea", tarea);
        model.addAttribute("comentarioData", new ComentarioData());

        return "formNuevoComentario";
    }

    @GetMapping("usuarios/{userid}/equipos/{id}/tareas/{tarea}/comentario")
    public String formNuevoComentarioEquipo(@PathVariable(value="userid") Long idUsuario,
                                            @PathVariable(value="tarea") Long idTarea,
                                            @PathVariable(value="id") Long idEquipo,
                                      Model model,
                                      HttpSession session) {

        managerUserSession.comprobarUsuarioLogeado(session, idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        Tarea tarea = tareaService.findById(idTarea);
        Equipo equipo = equipoService.findById(idEquipo);

        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        if(tarea == null)
            throw new TareaNotFoundException();
        if(!equipo.getUsuarios().contains(usuario))
            throw new UsuarioNotFoundException();

        model.addAttribute("usuario", usuario);
        model.addAttribute("tarea", tarea);
        model.addAttribute("comentarioData", new ComentarioData());

        return "formNuevoComentario";
    }

    @PostMapping("/usuarios/{id}/tareas/{tarea}/comentario")
    public String nuevaTarea(@PathVariable(value="tarea") Long idTarea, @PathVariable(value="id") Long idUsuario,
                             @ModelAttribute TareaData tareaData,
                             @ModelAttribute ComentarioData comentarioData,
                             Model model,
                             HttpSession session) {

        managerUserSession.comprobarUsuarioLogeado(session, idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        Tarea tarea = tareaService.findById(idTarea);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        if(tarea == null)
            throw new TareaNotFoundException();
        if(tarea.getUsuario() != usuario)
            throw new UsuarioNotFoundException();

        comentarioService.nuevoComentarioTarea(idUsuario,idTarea,comentarioData.getComentario());

        model.addAttribute("usuario", usuario);
        model.addAttribute("tarea",tarea);
        tareaData.setTitulo(tarea.getTitulo());
        tareaData.setDescripcion(tarea.getDescripcion());
        model.addAttribute("comentarios", tarea.getComentarios());
        return "";
     }

}

