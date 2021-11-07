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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class EquipoController {

    @Autowired
    EquipoService equipoService;

    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/equipos")
    public String listadoEquipos(Model model, HttpSession session) {
        List<Equipo> equipos = equipoService.findAllOrderedByName();
        model.addAttribute("equipos", equipos);
        return "equipos";
    }

}
