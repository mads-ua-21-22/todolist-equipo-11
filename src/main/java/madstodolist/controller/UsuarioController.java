package madstodolist.controller;


import madstodolist.authentication.ManagerUserSession;
import madstodolist.authentication.UsuarioNoAdminException;
import madstodolist.authentication.UsuarioNoLogeadoException;
import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import javax.validation.Valid;
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

    @GetMapping("/modificarPerfil")
    public String descripcionUsuario(Model model, HttpSession session) {
        //Para comprobar si el usuario si existe y si esta logeado (Evitar error null)
        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");

        managerUserSession.comprobarExisteUsuario(idUsuario);
        managerUserSession.comprobarUsuarioLogeado(session,idUsuario);
        //Comprobamos usuario de la SESION
        Usuario usuarioSesion = usuarioService.findById(idUsuario);
        if (usuarioSesion == null) {
            throw new UsuarioNotFoundException();
        }
        model.addAttribute("user",usuarioSesion);
        model.addAttribute("modificaData", new ModificaData());
        return "formModificaCuenta";
    }

    @PostMapping("/modificarPerfil")
    public String registroSubmit(@Valid ModificaData modificaData, BindingResult result, Model model,
                                 HttpSession session) {

        Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");
        Usuario user = usuarioService.findById(idUsuario);
        model.addAttribute("user",user);
        model.addAttribute("modificaData", new ModificaData());

        if (result.hasErrors()) {
            model.addAttribute("error", "Asegurate de que el formato de la fecha es DD-MM-YYYY");
            return "formModificaCuenta";
        }

        if(!modificaData.getActualpassword().equals(user.getPassword())) {
            model.addAttribute("error", "La contraseña actual es errónea");
            return "formModificaCuenta";
        }
        else if(!modificaData.getPassword().equals(modificaData.getPassword2())) {
            model.addAttribute("error", "Las contraseñas no coinciden ");
            return "formModificaCuenta";
        }
        user.setPassword(modificaData.getPassword());
        user.setFechaNacimiento(modificaData.getFechaNacimiento());
        user.setNombre(modificaData.getNombre());
        usuarioService.modificar(user);
        return "redirect:/modificarPerfil";
    }

    @PostMapping("/bloquear/{id}")
    public String cambiaBloqueado(@PathVariable(value="id") Long id,Model model, HttpSession session) {
        usuarioService.cambiaEstadoBloqueado(id);
        return "redirect:/usuarios";
    }

    @GetMapping("/usuarios/{id}/equipos")
    public String getEquiposDeUsuarios(@PathVariable(value = "id") Long id, Model model, HttpSession session) {
        // comprobamos que esté logueado este usuario
        // nota: al comprobar el logueo ya comprobamos si existe usuario con ese id
        if(!managerUserSession.comprobarUsuarioLogeado(session, id)) {
            throw new UsuarioNoLogeadoException();
        }

        // obtenemos usuario y sus equipos
        Usuario usuario = usuarioService.findById(id);
        List<Equipo> equipos = new ArrayList<>(usuarioService.allEquiposUsuario(id));
        model.addAttribute("usuario", usuario);
        model.addAttribute("equipos", equipos);

        return "listaEquiposUsuario";
    }
}
