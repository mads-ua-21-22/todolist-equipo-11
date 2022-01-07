package madstodolist.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TareaRepository extends CrudRepository<Tarea, Long> {
    @Query(value = "SELECT * from tareas where completada = true and usuario_id = ?1", nativeQuery = true)
    List<Tarea> allTareasCompletadas(Long idUsuario);

    @Query(value = "SELECT * from tareas where completada = false and usuario_id = ?1", nativeQuery = true)
    List<Tarea> allTareasNoCompletadas(Long idUsuario);

    @Query(value = "SELECT * from tareas where completada = true and equipo_id = ?1", nativeQuery = true)
    List<Tarea> allTareasCompletadasEquipo(Long idEquipo);

    @Query(value = "SELECT * from tareas where completada = false and equipo_id = ?1", nativeQuery = true)
    List<Tarea> allTareasNoCompletadasEquipo(Long idEquipo);
}
