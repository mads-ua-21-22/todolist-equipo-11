package madstodolist.model;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tareas")
public class Tarea implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String titulo;

    @NotNull
    // Relación muchos-a-uno entre tareas y usuario
    @ManyToOne
    // Nombre de la columna en la BD que guarda físicamente
    // el ID del usuario con el que está asociado una tarea
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    //Relacion muchos-a-uno entre tareas y equipo
    @ManyToOne
    //Nombre de la columan en la BD que guarda físicamente
    //el ID del equipo con el que está asociado una tarea
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;

    // Atributo completada para saber si una tarea está completada
    // Por defecto será falso
    @Column(columnDefinition = "bool default false", nullable = false)
    private boolean completada;

    private String descripcion;

    @Column(name = "fecha_limite")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaLimite;

    // Definimos el tipo de fetch como EAGER para que
    // cualquier consulta que devuelve un usuario rellene automáticamente
    // toda su lista de tareas
    // CUIDADO!! No es recomendable hacerlo en aquellos casos en los
    // que la relación pueda traer a memoria una gran cantidad de entidades
    @OneToMany(mappedBy = "tarea", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    Set<Comentario> comentarios = new HashSet<>();

    // Constructor vacío necesario para JPA/Hibernate.
    // Lo hacemos privado para que no se pueda usar desde el código de la aplicación. Para crear un
    // usuario en la aplicación habrá que llamar al constructor público. Hibernate sí que lo puede usar, a pesar
    // de ser privado.
    private Tarea() {}

    // Al crear una tarea la asociamos automáticamente a un
    // usuario. Actualizamos por tanto la lista de tareas del
    // usuario.
    public Tarea(Usuario usuario, String titulo) {
        this.usuario = usuario;
        this.titulo = titulo;
        usuario.getTareas().add(this);
    }

    public Tarea(Equipo equipo, String titulo) {
        this.titulo = titulo;
        this.equipo = equipo;
        equipo.getTareas().add(this);
    }

    public void setComentarios(Set<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public Set<Comentario> getComentarios() {
        return comentarios;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() { return  descripcion; }

    public void setDescripcion(String descripcion){ this.descripcion = descripcion; }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Equipo getEquipo() { return  equipo; }

    public void setEquipo(Equipo equipo) { this.equipo = equipo; }

    public boolean isComplete() { return completada; }

    public void setComplete() { this.completada = !this.completada; }

    public Date getFechaLimite() { return  fechaLimite; }

    public void setFechaLimite(Date fechaLimite) { this.fechaLimite = fechaLimite; }

    public boolean retrasada() {
        java.util.Date fecha = new Date();
        if(fecha.compareTo(fechaLimite) == -1)
            return false;
        else
            return true;
    }

    public boolean nofechaLimite() { return  fechaLimite == null; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarea tarea = (Tarea) o;
        if (id != null && tarea.id != null)
            // Si tenemos los ID, comparamos por ID
            return Objects.equals(id, tarea.id);
        // sino comparamos por campos obligatorios
        return titulo.equals(tarea.titulo) &&
                usuario.equals(tarea.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo, usuario);
    }
}
