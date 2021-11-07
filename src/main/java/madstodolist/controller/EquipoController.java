package madstodolist.controller;


import madstodolist.authentication.ManagerUserSession;
import madstodolist.authentication.UsuarioNoAdminException;
import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
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

        boolean aparezco = false;
        if(equipo.getUsuarios().contains(usuario))
            aparezco=true;

        model.addAttribute("aparezco", aparezco);
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("equipo", equipo);
        return "infoequipo";
    }

    @GetMapping("/equipos/crear")
    public String formcrearEquipo(@ModelAttribute EquipoData equipoData, Model model, HttpSession session) {
        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");
        managerUserSession.comprobarUsuarioLogeado(session, idUsuario);
        Usuario usuario = usuarioService.findById(idUsuario);

        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }

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
        equipoService.crearEquipo(equipoData.getNombre());
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
        return "redirect:/equipos";
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
}
