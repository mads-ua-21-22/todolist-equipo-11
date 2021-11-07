package madstodolist.service;

import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class EquipoService {

    private EquipoRepository equipoRepository;

    public EquipoService(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    @Transactional(readOnly = true)
    public List<Equipo> findAllOrderedByName() {
        List<Equipo> equipos = equipoRepository.findAll();
        equipos.sort(Comparator.comparing(Equipo::getNombre));
        return equipos;
    }

    @Transactional
    public Equipo findById(Long id) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);

        return equipo;
    }

    @Transactional(readOnly = true)
    public List<Usuario> usuariosEquipo(Long idEquipo) {
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);

        List<Usuario> usuarios = new ArrayList(equipo.getUsuarios());
        Collections.sort(usuarios, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
        return usuarios;
    }
}
