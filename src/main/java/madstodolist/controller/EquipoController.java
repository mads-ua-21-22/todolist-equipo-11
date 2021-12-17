package madstodolist.controller;


import madstodolist.authentication.ManagerUserSession;
import madstodolist.authentication.UsuarioNoAdminException;
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
import java.util.List;

@Controller
public class EquipoController {

    @Autowired
    EquipoService equipoService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    TareaService tareaService;
    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/equipos")
    public String listadoEquipos(Model model, HttpSession session) {

        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }

        List<Equipo> equipos = equipoService.findAllOrderedByName();
        model.addAttribute("usuario", usuario);
        model.addAttribute("equipos", equipos);
        return "equipos";
    }

    @GetMapping("/equipos/{id}")
    public String formacionEquipo(@PathVariable(value="id") Long idEquipo,Model model, HttpSession session) {

        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        Equipo equipo = equipoService.findById(idEquipo);

        List<Usuario> usuarios = equipoService.usuariosEquipo(idEquipo);
        List<Tarea> tareasCompletadas = tareaService.allTareasCompletadasEquipo(idEquipo);
        List<Tarea> tareasNoCompletadas = tareaService.allTareasNoCompletadasEquipo(idEquipo);

        float porcentajeCompletadas = 0;

        if(tareasCompletadas.size()>0)
            porcentajeCompletadas = (float) tareasCompletadas.size() / (tareasCompletadas.size() + tareasNoCompletadas.size());

        boolean aparezco = false;
        if(equipo.getUsuarios().contains(usuario))
            aparezco=true;

        model.addAttribute("aparezco", aparezco);
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("equipo", equipo);
        session.setAttribute("tareaequipo",true);
        model.addAttribute("tareasCompletadas", tareasCompletadas);
        model.addAttribute("tareasNoCompletadas", tareasNoCompletadas);
        model.addAttribute("porcentajeCompletadas", porcentajeCompletadas);

        return "infoequipo";
    }

    @GetMapping("/equipos/crear")
    public String formcrearEquipo(Model model, HttpSession session) {
        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");
        managerUserSession.comprobarUsuarioLogeado(session, idUsuario);
        Usuario usuario = usuarioService.findById(idUsuario);

        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        model.addAttribute("equipoData", new EquipoData());
        model.addAttribute("usuario", usuario);

        return "formCreaEquipo";
    }

    @PostMapping("/equipos/crear")
    public String creaEquipo(@ModelAttribute EquipoData equipoData,
                             Model model, RedirectAttributes flash,
                             HttpSession session) {
        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");
        managerUserSession.comprobarUsuarioLogeado(session, idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }

        Equipo equipo = new Equipo(equipoData.getNombre());
        equipo.setDescripcion(equipoData.getDescripcion());
        equipoService.crearEquipo(equipo);
        flash.addFlashAttribute("mensaje", "Equipo cread correctamente");
        return "redirect:/equipos";
    }

    @PostMapping("/equipos/{id}/agregar")
    public String agregarmeAEquipo(@PathVariable(value="id") Long idEquipo,
                             Model model, RedirectAttributes flash,
                             HttpSession session) {
        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");
        managerUserSession.comprobarUsuarioLogeado(session, idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        equipoService.agregarAEquipo(idUsuario,idEquipo);

        flash.addFlashAttribute("mensaje", "Agregado al equipo correctamente");
        return "redirect:/equipos/" + idEquipo;
    }

    @DeleteMapping("/equipos/{id}")
    @ResponseBody
    public String borrarmedeEquipo(@PathVariable(value="id") Long idEquipo,
                                   Model model, RedirectAttributes flash,
                                   HttpSession session) {
        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");
        managerUserSession.comprobarUsuarioLogeado(session, idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        equipoService.eliminarDeEquipo(idUsuario,idEquipo);

        return "";
    }

    @DeleteMapping("/equipo/{id}")
    @ResponseBody
    public String borrarEquipo(@PathVariable(value="id") Long idEquipo,
                                   Model model, RedirectAttributes flash,
                                   HttpSession session) {
        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");
        managerUserSession.comprobarUsuarioLogeado(session, idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        if (!usuario.getAdministrador())
            throw new UsuarioNoAdminException();

        equipoService.borrarEquipo(idEquipo);
        return "";
    }

    @GetMapping("/equipos/{id}/editar")
    public String formEditarEquipo(@PathVariable(value="id") Long idEquipo,
                                   Model model, HttpSession session) {
        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");
        managerUserSession.comprobarUsuarioLogeado(session, idUsuario);
        Usuario usuario = usuarioService.findById(idUsuario);

        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        if (!usuario.getAdministrador())
            throw new UsuarioNoAdminException();

        Equipo equipo = equipoService.findById(idEquipo);
        EquipoData equipoData = new EquipoData();
        equipoData.setNombre(equipo.getNombre()); equipoData.setDescripcion(equipo.getDescripcion());

        model.addAttribute("equipoData", equipoData);
        model.addAttribute("usuario", usuario);
        model.addAttribute("equipo",equipo);
        return "formEditarEquipo";
    }

    @PostMapping("/equipos/{id}/editar")
    public String editarEquipo(@PathVariable(value="id") Long idEquipo,
            @ModelAttribute EquipoData equipoData,
                             Model model,
                             HttpSession session) {
        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");
        managerUserSession.comprobarUsuarioLogeado(session, idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        if (!usuario.getAdministrador())
            throw new UsuarioNoAdminException();
        equipoService.editarNombreEquipo(equipoData.getNombre(),idEquipo);
        equipoService.cambiarDescripcion(idEquipo, equipoData.getDescripcion());

        return "redirect:/equipos";
    }

    @DeleteMapping("/equipo/{id}/{userid}")
    @ResponseBody
    public String borraUsuarioEquipo(@PathVariable(value="id")Long idEquipo,
                                     @PathVariable(value="userid")Long idUsuario,
                                     Model model,RedirectAttributes flash,
                                     HttpSession session){
        Long idlogin = (Long) session.getAttribute("idUsuarioLogeado");
        managerUserSession.comprobarUsuarioLogeado(session,idlogin);
        Equipo equipo = equipoService.findById(idEquipo);

        Usuario usuario = usuarioService.findById(idlogin);
        if(usuario == null)
            throw new UsuarioNotFoundException();
        if(equipo.getLider() != usuario && !usuario.getAdministrador())
            throw new UsuarioNoAdminException();
        equipoService.eliminarDeEquipo(idUsuario,idEquipo);
        return "";
    }

    @GetMapping("/equipos/{id}/tareas/nueva")
    public String formNuevaTarea(@PathVariable(value = "id") Long idEquipo,
                                 @ModelAttribute TareaData tareaData,Model model,
                                 HttpSession session) {
        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");
        managerUserSession.comprobarUsuarioLogeado(session,idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        if(usuario == null)
            throw new UsuarioNotFoundException();
        Equipo equipo = equipoService.findById(idEquipo);
        if(equipo.getLider() != usuario)
            throw new UsuarioNotFoundException();

        model.addAttribute("usuario",usuario);
        model.addAttribute("equipo",equipo);
        return "formNuevaTareaEquipo";
    }

    @PostMapping("/equipos/{id}/tareas/nueva")
    public String nuevaTarea(@PathVariable(value = "id") Long idEquipo, @ModelAttribute TareaData tareaData,
                             Model model, RedirectAttributes flash,
                             HttpSession session) {
        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");
        managerUserSession.comprobarUsuarioLogeado(session,idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null)
            throw new UsuarioNotFoundException();
        //Se le asigna la tarea directamente al usuario que la crea he tenido que hacer esto porque me da un
        //error inexplicable en la DB
        tareaService.nuevaTareaEquipo(idEquipo,idUsuario,tareaData.getTitulo(),tareaData.getDescripcion(),tareaData.getFechaLimite());
        flash.addFlashAttribute("mensaje","Tarea creada correctamente");
        return "redirect:/equipos/" + idEquipo;
    }
}
