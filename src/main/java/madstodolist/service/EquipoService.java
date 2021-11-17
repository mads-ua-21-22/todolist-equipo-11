package madstodolist.service;

import madstodolist.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class EquipoService {

    private EquipoRepository equipoRepository;

    private UsuarioRepository usuarioRepository;

    public EquipoService(UsuarioRepository usuarioRepository, EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
        this.usuarioRepository = usuarioRepository;
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

    @Transactional
    public Equipo crearEquipo(Equipo equipo) {
        equipoRepository.save(equipo);
        return equipo;
    }

    @Transactional
    public Equipo agregarAEquipo(Long idUsuario, Long idEquipo) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        equipo.addUsuario(usuario);
        return equipo;
    }

    @Transactional
    public Equipo eliminarDeEquipo(Long idUsuario, Long idEquipo) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        equipo.deleteUsuario(usuario);
        return equipo;
    }
    @Transactional
    public Equipo editarNombreEquipo(String nombreEquipo, Long idEquipo) {
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        equipo.setNombre(nombreEquipo);
        return equipo;
    }
    @Transactional
    public void borrarEquipo(Long idEquipo) {
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        equipoRepository.delete(equipo);
    }
    @Transactional
    public void cambiarDescripcion(Long id, String descripcion) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        if (equipo != null)
            equipo.setDescripcion(descripcion);
    }
}
