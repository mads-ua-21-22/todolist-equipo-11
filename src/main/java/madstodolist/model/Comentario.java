package madstodolist.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "comentarios")
public class Comentario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String comentario;

    @NotNull
    // Relación muchos-a-uno entre tareas y usuario
    @ManyToOne(cascade=CascadeType.REMOVE)
    // Nombre de la columna en la BD que guarda físicamente
    // el ID del usuario con el que está asociado una tarea
    @JoinColumn(name = "tarea_id")
    private Tarea tarea;

    @NotNull
    // Relación muchos-a-uno entre tareas y usuario
    @ManyToOne
    // Nombre de la columna en la BD que guarda físicamente
    // el ID del usuario con el que está asociado una tarea
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Constructor vacío necesario para JPA/Hibernate.
    // Lo hacemos privado para que no se pueda usar desde el código de la aplicación. Para crear un
    // usuario en la aplicación habrá que llamar al constructor público. Hibernate sí que lo puede usar, a pesar
    // de ser privado.
    public Comentario() {}

    // Al crear una tarea la asociamos automáticamente a un
    // usuario. Actualizamos por tanto la lista de tareas del
    // usuario.
    public Comentario(Usuario usuario, Tarea tarea, String comentario) {
        this.tarea = tarea;
        this.usuario = usuario;
        this.comentario = comentario;
        usuario.getComentarios().add(this);
        tarea.getComentarios().add(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getComentario() {
        return comentario;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comentario comentario = (Comentario) o;
        if (id != null && comentario.id != null)
            // Si tenemos los ID, comparamos por ID
            return Objects.equals(id, comentario.id);
        // sino comparamos por campos obligatorios
        return comentario.equals(comentario.comentario) &&
                comentario.equals(comentario.comentario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comentario, tarea);
    }
}
