package madstodolist.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "equipos")
public class Equipo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String nombre;

    //Relacion Equipo Tareas
    @OneToMany(mappedBy =  "equipo",fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    Set<Tarea> tareas = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "equipo_usuario",
            joinColumns = { @JoinColumn(name = "fk_equipo") },
            inverseJoinColumns = { @JoinColumn(name = "fk_usuario") })
    Set<Usuario> usuarios = new HashSet<>();

    @Column(columnDefinition = "varchar(255) default ''")
    private String descripcion;

    //Relacion muchos-a-uno entre equipos y usuario
    @ManyToOne
    //Nombre de la columna en la BD que guarda fisicamente
    //el ID del lider con el que está asociado el equipo
    @JoinColumn(name = "lider")
    private Usuario lider;

    // Constructor público con los atributos obligatorios. En este caso el correo electrónico.
    public Equipo(String nombre) {
        this.nombre = nombre;
    }

    private Equipo() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Usuario> getUsuarios() {
        return usuarios;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setUsuarios(Set<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public void setTareas(Set<Tarea> tareas){this.tareas = tareas;}

    public Set<Tarea> getTareas(){return tareas;}

    public Usuario getLider() { return  lider; }

    public void setLider(Usuario lider) { this.lider = lider; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipo equipo = (Equipo) o;
        if (id != null && equipo.id != null)
            // Si tenemos los ID, comparamos por ID
            return Objects.equals(id, equipo.id);
        // sino comparamos por campos obligatorios
        return nombre.equals(equipo.nombre);
    }

    public void addUsuario(Usuario usuario) {
        this.getUsuarios().add(usuario);
        usuario.getEquipos().add(this);
    }

    public void deleteUsuario(Usuario usuario) {
        this.getUsuarios().remove(usuario);
        usuario.getEquipos().remove(this);
    }

    @Override
    public int hashCode() {
        // Generamos un hash basado en los campos obligatorios
        return Objects.hash(nombre);
    }
}
