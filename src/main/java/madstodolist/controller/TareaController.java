package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.TareaNotFoundException;
import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class TareaController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    TareaService tareaService;

    @Autowired
    EquipoService equipoService;

    @Autowired
    ManagerUserSession managerUserSession;


    @GetMapping("/usuarios/{id}/tareas/nueva")
    public String formNuevaTarea(@PathVariable(value="id") Long idUsuario,
                                 @ModelAttribute TareaData tareaData, Model model,
                                 HttpSession session) {

        managerUserSession.comprobarUsuarioLogeado(session, idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        model.addAttribute("usuario", usuario);
        return "formNuevaTarea";
    }

    @PostMapping("/usuarios/{id}/tareas/nueva")
    public String nuevaTarea(@PathVariable(value="id") Long idUsuario, @ModelAttribute TareaData tareaData,
                             Model model, RedirectAttributes flash,
                             HttpSession session) {

        managerUserSession.comprobarUsuarioLogeado(session, idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        tareaService.nuevaTareaUsuario(idUsuario, tareaData.getTitulo(),tareaData.getDescripcion(),tareaData.getFechaLimite());
        flash.addFlashAttribute("mensaje", "Tarea creada correctamente");
        return "redirect:/usuarios/" + idUsuario + "/tareas";
     }

    @GetMapping("/usuarios/{id}/tareas")
    public String listadoTareas(@PathVariable(value="id") Long idUsuario, Model model, HttpSession session) {

        managerUserSession.comprobarUsuarioLogeado(session, idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        List<Tarea> tareasCompletadas = tareaService.allTareasCompletadasUsuario(idUsuario);
        List<Tarea> tareasNoCompletadas = tareaService.allTareasNoCompletadasUsuario(idUsuario);

        float porcentajeCompletadas = 0;

        if(tareasCompletadas.size() > 0)
            porcentajeCompletadas = (float) tareasCompletadas.size() / (tareasCompletadas.size() + tareasNoCompletadas.size());

        model.addAttribute("usuario", usuario);
        model.addAttribute("tareasCompletadas", tareasCompletadas);
        model.addAttribute("tareasNoCompletadas", tareasNoCompletadas);
        model.addAttribute("porcentajeCompletadas", porcentajeCompletadas);
        session.setAttribute("tareaequipo",false);
        return "listaTareas";
    }

    @GetMapping("/tareas/{id}")
    public String formTarea(@PathVariable(value = "id") Long idTarea, @ModelAttribute TareaData tareaData,
                            Model model, HttpSession session) {
        Tarea tarea = tareaService.findById(idTarea);
        if (tarea == null)
            throw new TareaNotFoundException();

        managerUserSession.comprobarUsuarioLogeado(session,tarea.getUsuario().getId());
        model.addAttribute("tarea",tarea);
        tareaData.setTitulo(tarea.getTitulo());
        tareaData.setDescripcion(tarea.getDescripcion());
        model.addAttribute("comentarios", tarea.getComentarios());
        return "infotarea";
    }

    @GetMapping("/equipos/{equipo}/tareas/{id}")
    public String formTareaEquipo(@PathVariable(value = "equipo") Long idEquipo,@PathVariable(value = "id") Long idTarea, @ModelAttribute TareaData tareaData,
                            Model model, HttpSession session) {
        Tarea tarea = tareaService.findById(idTarea);
        if (tarea == null)
            throw new TareaNotFoundException();

        Usuario usuario = usuarioService.findById(managerUserSession.usuarioLogeado(session));
        Equipo equipo = equipoService.findById(idEquipo);

        if(!equipo.getUsuarios().contains(usuario))
            throw new UsuarioNotFoundException();
        if(!equipo.getTareas().contains(tarea))
            throw new TareaNotFoundException();
        model.addAttribute("equipo",equipo);
        model.addAttribute("usuario",usuario);
        model.addAttribute("tarea",tarea);
        model.addAttribute("comentarios",tarea.getComentarios());
        tareaData.setTitulo(tarea.getTitulo());
        tareaData.setDescripcion(tarea.getDescripcion());
        return "infotareaequipo";
    }

    @GetMapping("/tareas/{id}/editar")
    public String formEditaTarea(@PathVariable(value="id") Long idTarea, @ModelAttribute TareaData tareaData,
                                 Model model, HttpSession session) {

        Tarea tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }
        managerUserSession.comprobarUsuarioLogeado(session, tarea.getUsuario().getId());

        model.addAttribute("tarea", tarea);
        tareaData.setTitulo(tarea.getTitulo());
        tareaData.setDescripcion(tarea.getDescripcion());
        tareaData.setFechaLimite(tarea.getFechaLimite());
        return "formEditarTarea";
    }

    @PostMapping("/tareas/{id}/editar")
    public String grabaTareaModificada(@PathVariable(value="id") Long idTarea, @ModelAttribute TareaData tareaData,
                                       Model model, RedirectAttributes flash, HttpSession session) {
        Tarea tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        Long idUsuario = tarea.getUsuario().getId();

        managerUserSession.comprobarUsuarioLogeado(session, idUsuario);

        tareaService.modificaTarea(idTarea, tareaData.getTitulo(),tareaData.getDescripcion(),tareaData.getFechaLimite());
        flash.addFlashAttribute("mensaje", "Tarea modificada correctamente");
        if((Boolean) session.getAttribute("tareaequipo"))
            return "redirect:/equipos/" + tarea.getEquipo().getId();
        else
            return "redirect:/usuarios/" + tarea.getUsuario().getId() + "/tareas";
    }

    @DeleteMapping("/tareas/{id}")
    @ResponseBody
    // La anotación @ResponseBody sirve para que la cadena devuelta sea la resupuesta
    // de la petición HTTP, en lugar de una plantilla thymeleaf
    public String borrarTarea(@PathVariable(value="id") Long idTarea, RedirectAttributes flash, HttpSession session) {
        Tarea tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        managerUserSession.comprobarUsuarioLogeado(session, tarea.getUsuario().getId());

        tareaService.borraTarea(idTarea);
        return "";
    }

    @PostMapping("/tareas/{id}/completada")
    public String completarTarea(@PathVariable(value="id") Long idTarea, HttpSession session) {
        // Obtener tarea
        Tarea tarea = tareaService.findById(idTarea);
        // Usuario de la tarea
        Usuario usuario = tarea.getUsuario();
        // Cambiar estado de la tarea
        tareaService.completaTarea(tarea);
        if((Boolean) session.getAttribute("tareaequipo"))
            return "redirect:/equipos/" + tarea.getEquipo().getId();
        else
            return "redirect:/usuarios/" + tarea.getUsuario().getId() + "/tareas";
    }
}

