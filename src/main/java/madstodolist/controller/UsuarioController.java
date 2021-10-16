package madstodolist.controller;


import madstodolist.authentication.ManagerUserSession;
import madstodolist.authentication.UsuarioNoAdminException;
import madstodolist.authentication.UsuarioNoLogeadoException;
import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/usuarios")
    public String listadoUsuarios(Model model, HttpSession session) {

        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");

        managerUserSession.comprobarExisteUsuario(idUsuario);
        managerUserSession.comprobarUsuarioLogeado(session,idUsuario);
        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        if(usuario.getAdministrador() == false)
            throw new UsuarioNoAdminException();

        List<Usuario> usuarios = usuarioService.allUsuarios();
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarios", usuarios);
        return "usuarios";
    }

    @GetMapping("/usuarios/{id}")
    public String descripcionUsuario(@PathVariable(value="id") Long id, Model model, HttpSession session) {
       //Para comprobar si el usuario si existe y si esta logeado (Evitar error null)
        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");

        managerUserSession.comprobarExisteUsuario(idUsuario);
        managerUserSession.comprobarUsuarioLogeado(session,idUsuario);
        //Comprobamos usuario de la SESION
        Usuario usuarioSesion = usuarioService.findById(idUsuario);
        if (usuarioSesion == null) {
            throw new UsuarioNotFoundException();
        }
        if(usuarioSesion.getAdministrador() == false)
            throw new UsuarioNoAdminException();

        //Usuario del id que se busca
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        model.addAttribute("usuarioSesion",usuarioSesion);
        model.addAttribute("usuario",usuario);
        return "descripcion";
    }

    @PostMapping("/bloquear/{id}")
    public String cambiaBloqueado(@PathVariable(value="id") Long id,Model model, HttpSession session) {
        usuarioService.cambiaEstadoBloqueado(id);
        return "redirect:/usuarios";
    }
}
