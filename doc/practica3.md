# Practica 3

## Repositorios
[Repositorios en Docker](https://hub.docker.com/repository/docker/dargue94/mads-todolist)

[Github](https://github.com/mads-ua-21-22/mads-todolist-dargue94)

[Trello](https://trello.com/b/SVVvfCcB/todolist-mads)
## *Resumen*
En esta práctica se ha continuado con la web anterior, añadiendo funcionalidades siguiento el sistema TDD.

## *Cómo se hizo*
Para la realización de esta práctica se ha trabajado sobretodo sobre la metodologia TDD. Esto implica que se han hecho los tests y se ha programado sobre estos intentando programa lo necesario sin pasarse a otras funcionalidades.

## *Listado de nuevas clases*
* Equipo
* EquipoController
* EquipoService
* EquipoRepository

## *Listado de nuevas plantillas*
* equipos.html
* infoequipo.html
* formCreaEquipo.html
* formEditarEquipo.html

## *A tener en cuenta*
Hay tests que han sufrido modificaciones para cumplir con una funcionalidad que se añada despues, el código mostrado es el código final por lo que tal vez se haya insertado código de un test para otro en el mismo método.

### Postgres
La práctica se ha realizado utilizando postgres como base de datos lanzada desde un docker.

Podemos encontrar las siguientes tablas:

![alt text](equipostabla.png)

![alt text](usuariostabla.png)

![alt text](tareas.png)

![alt text](equiposusuariosrelaciontabla.png)

### Equipo
"Equipo" es la clase principal de esta práctica. Un equipo esta formado por un id y un nombre.
Inicialmente apenas tenemos código y encontramos únicamente los constructores con getters/setters.

Cabe destacar el uso de ManyToMany para la creación de la tabla auxiliar equipo_usuario para relacionar ambas clases.

El código del Equipo acabó de la siguiente forma (Sin getters ni setters):
```javascript
 private static final long serialVersionUID = 1L;

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
@NotNull
private String nombre;

@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
    name = "equipo_usuario",
    joinColumns = @JoinColumn(name = "fk_usuario"),
    inverseJoinColumns = @JoinColumn(name = "fk_equipo"))
Set<Usuario> usuarios = new HashSet<>();
// Constructor público con los atributos obligatorios. En este caso el correo electrónico.
public Equipo(String nombre) {
    this.nombre = nombre;
}

public void addUsuario(Usuario usuario) {
    this.getUsuarios().add(usuario);
    usuario.getEquipos().add(this);
}

public void deleteUsuario(Usuario usuario) {
    this.getUsuarios().remove(usuario);
    usuario.getEquipos().remove(this);
}

```
### EquipoService
En equipo service los métodos más caracteristicos serían los siguientes_

* Encontrar todos los equipos ordenados alfabeticamente.
* Encontrar por id.
* Listar usuarios que pertenecen a un equipo
* Crear / Borrar / Editar un equipo.
* Eliminar a un usuario de un equipo.

Este es el código final de este archivo:
```javascript
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
public Equipo crearEquipo(String nombreEquipo) {
    Equipo equipo = new Equipo(nombreEquipo);
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
```

### Test relacionados con estas clases

Vistas estas dos clases, podemos ver los tests que encontramos en EquipoTest:

Test 1
```javascript
   @Test
public void crearEquipo() {
    Equipo equipo = new Equipo("Proyecto P1");
    assertThat(equipo.getNombre()).isEqualTo("Proyecto P1");
}
```
Test 2
```javascript

@Test
@Transactional
public void grabarEquipo() {
    // GIVEN
    Equipo equipo = new Equipo("Proyecto P1");

    // WHEN
    equipoRepository.save(equipo);

    // THEN
    assertThat(equipo.getId()).isNotNull();
}
```

Test 3
```javascript
@Test
public void comprobarIgualdadEquipos() {
    // GIVEN
    // Creamos tres equipos sin id, sólo con el nombre
    Equipo equipo1 = new Equipo("Proyecto P1");
    Equipo equipo2 = new Equipo("Proyecto P2");
    Equipo equipo3 = new Equipo("Proyecto P2");

    // THEN
    // Comprobamos igualdad basada en el atributo nombre
    assertThat(equipo1).isNotEqualTo(equipo2);
    assertThat(equipo2).isEqualTo(equipo3);

    // WHEN
    // Añadimos identificadores y comprobamos igualdad por identificadores
    equipo1.setId(1L);
    equipo2.setId(1L);
    equipo3.setId(2L);

    // THEN
    // Comprobamos igualdad basada en el atributo nombre
    assertThat(equipo1).isEqualTo(equipo2);
    assertThat(equipo2).isNotEqualTo(equipo3);
}
```
Test 4
```javascript
@Test
public void comprobarRecuperarEquipo() {
    // GIVEN
    // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

    // WHEN

    Equipo equipo = equipoRepository.findById(1L).orElse(null);

    // THEN
    assertThat(equipo).isNotNull();
    assertThat(equipo.getId()).isEqualTo(1L);
    assertThat(equipo.getNombre()).isEqualTo("Proyecto P1");
}
```
Test 5
```javascript
@Test
@Transactional(readOnly = true)
public void comprobarFindAll() {
    // GIVEN
    // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

    // WHEN
    List<Equipo> equipos = equipoRepository.findAll();

    // THEN
    assertThat(equipos).hasSize(2);
}
```
Test 6
```javascript
@Test
@Transactional
public void actualizarRelacionUsuarioEquipos() {
    // GIVEN
    // En el application.properties se cargan los datos de prueba del fichero datos-test.sql
    Usuario usuario = usuarioRepository.findById(1L).orElse(null);
    Equipo equipo = equipoRepository.findById(2L).orElse(null);
    // WHEN
    equipo.addUsuario(usuario);
    // THEN
    assertThat(equipo.getUsuarios()).contains(usuario);
    assertThat(usuario.getEquipos()).contains(equipo);
    //Para que no peten los tests automaticos
    equipo.deleteUsuario(usuario);
}
```
Test 7
```javascript
@Test
@Transactional
public void borrarUsuarioDeEquipo() {
    // GIVEN
    // En el application.properties se cargan los datos de prueba del fichero datos-test.sql
    Usuario usuario = usuarioRepository.findById(1L).orElse(null);
    Equipo equipo = equipoRepository.findById(1L).orElse(null);
    assertThat(equipo.getUsuarios()).contains(usuario);
    assertThat(usuario.getEquipos()).contains(equipo);
    // WHEN
    equipo.deleteUsuario(usuario);
    // THEN
    assertThat(equipo.getUsuarios()).doesNotContain(usuario);
    assertThat(usuario.getEquipos()).doesNotContain(equipo);
    //Para que no peten los tests automaticos
    equipo.addUsuario(usuario);
}
```

Y estos serían los tests de EquipoServiceTest:

Test 1
```javascript
  @Test
    public void obtenerListadoEquipos() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        List<Equipo> equipos = equipoService.findAllOrderedByName();

        // THEN
        assertThat(equipos).hasSize(2);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Proyecto A1");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto P1");
    }
```
Test 2
```javascript
    @Test
    public void obtenerEquipo() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        Equipo equipo = equipoService.findById(1L);

        // THEN
        assertThat(equipo.getNombre()).isEqualTo("Proyecto P1");
        // Comprobamos que la relación con Usuarios es lazy: al
        // intentar acceder a la colección de usuarios se debe lanzar una
        // excepción de tipo LazyInitializationException.
        assertThatThrownBy(() -> {
            equipo.getUsuarios().size();
        }).isInstanceOf(LazyInitializationException.class);
    }
```
Test 3
```javascript
    @Test
    public void comprobarRelacionUsuarioEquipos() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        Usuario usuario = usuarioService.findById(1L);

        // THEN

        assertThat(usuario.getEquipos()).hasSize(1);
    }
```
Test 4
```javascript
    @Test
    public void obtenerUsuariosEquipo() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        List<Usuario> usuarios = equipoService.usuariosEquipo(1L);

        // THEN
        assertThat(usuarios).hasSize(1);
        assertThat(usuarios.get(0).getEmail()).isEqualTo("user@ua");
        // Comprobamos que la relación entre usuarios y equipos es eager
        // Primero comprobamos que la colección de equipos tiene 1 elemento
        assertThat(usuarios.get(0).getEquipos()).hasSize(1);
        // Y después que el elemento es el equipo Proyecto P1
        assertThat(usuarios.get(0).getEquipos().stream().findFirst().get().getNombre()).isEqualTo("Proyecto P1");
    }
```
Test 5
```javascript
    @Test
    @Transactional
    public void creaEquipos() {
        List<Equipo> equipos = equipoService.findAllOrderedByName();
        // THEN
        assertThat(equipos).hasSize(2);
        equipoService.crearEquipo("equipoTest");
        equipos = equipoService.findAllOrderedByName();
        assertThat(equipos).hasSize(3);
    }
```
Test 6
```javascript
    @Test
    @Transactional
    public void addUsuarioEquipo() {
        Usuario usuario = usuarioService.findById(1L);
        assertThat(usuario.getEquipos()).hasSize(1);
        equipoService.agregarAEquipo(1L, 2L);
        assertThat(usuario.getEquipos()).hasSize(2);
        Equipo equipo = equipoService.findById(1L);
        Equipo equipo2 = equipoService.findById(2L);
        assertThat(equipo.getUsuarios()).contains(usuario);
        assertThat(equipo2.getUsuarios()).contains(usuario);
        //Para que no pete ejecucion de tests automaticos...
        equipoService.eliminarDeEquipo(1L, 2L);
    }
```
Test 7
```javascript
    @Test
    @Transactional
    public void deleteUsuarioEquipo() {
        Usuario usuario = usuarioService.findById(1L);
        assertThat(usuario.getEquipos()).hasSize(1);
        equipoService.eliminarDeEquipo(1L, 1L);
        assertThat(usuario.getEquipos()).hasSize(0);
        Equipo equipo = equipoService.findById(1L);
        Equipo equipo2 = equipoService.findById(2L);
        assertThat(equipo.getUsuarios()).doesNotContain(usuario);
        assertThat(equipo2.getUsuarios()).doesNotContain(usuario);
        equipoService.agregarAEquipo(1L, 1L);
    }
```
Test 8
```javascript
    @Test
    @Transactional
    public void editarNombreEquipo() {
        Equipo equipo = equipoService.findById(1L);
        assertThat(equipo.getNombre()).doesNotMatch("No es su nombre");
        equipoService.editarNombreEquipo("No es su nombre",1L);
        assertThat(equipo.getNombre()).matches("No es su nombre");
    }
```
Test 8
```javascript
    @Test
    @Transactional
    public void borrarEquipo() {
        List<Equipo> equipos = equipoService.findAllOrderedByName();
        assertThat(equipos).hasSize(2);
        equipoService.borrarEquipo(1L);
        List<Equipo> equipos2 = equipoService.findAllOrderedByName();
        assertThat(equipos2).hasSize(1);
    }
```
### EquipoRepository
En Equipo repository tenemos los métodos justos para realizar las llamadas a los métodos del Servicio, por lo que cumple con las mismas funcionalidades.

Podemos encontrar algunos Gets para las distintas vistas, algunos Post para editar equipos o los usuarios que pertenecen al mismo , asi como un par de Delete.

```javascript
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

@GetMapping("/equipos/{id}/editar")
public String formEditarEquipo(@PathVariable(value="id") Long idEquipo,
@ModelAttribute EquipoData equipoData, Model model, HttpSession session) {
    Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");
    managerUserSession.comprobarUsuarioLogeado(session, idUsuario);
    Usuario usuario = usuarioService.findById(idUsuario);

    if (usuario == null) {
        throw new UsuarioNotFoundException();
    }
    if (!usuario.getAdministrador())
        throw new UsuarioNoAdminException();

    Equipo equipo = equipoService.findById(idEquipo);

    model.addAttribute("usuario", usuario);
    model.addAttribute("equipo",equipo);
    return "formEditarEquipo";
}

@PostMapping("/equipos/{id}/editar")
public String editarEquipo(@PathVariable(value="id") Long idEquipo,
@ModelAttribute EquipoData equipoData,
    Model model,
    HttpSession session) {
    Long idUsuario = (Long) session.getAttribute("idUsuarioLogeado");
    managerUserSession.comprobarUsuarioLogeado(session, idUsuario);

    Usuario usuario = usuarioService.findById(idUsuario);
    if (usuario == null) {
        throw new UsuarioNotFoundException();
    }
    if (!usuario.getAdministrador())
        throw new UsuarioNoAdminException();
    equipoService.editarNombreEquipo(equipoData.getNombre(),idEquipo);

    return "redirect:/equipos";
}
```

## Vistas

Cabe destacar la inserción de Equipos en el Header que permite acceder a las vistas que comentaré a continuación.

### equipos.html
La primera vista que se ha creado es equipos.html, donde simplemente listamos los equipos que tenemos y en caso de ser administrador, aparecen dos botones para editar o eliminar el equipo.

```javascript
   <div class="row mt-3">
    <div class="col">
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Equipo</th>
                    <th th:if="${usuario.getAdministrador()}" >Accion</th>
                </tr>
            </thead>
            <tbody>
                <tr th:each="equipo: ${equipos}">
                    <td>
                        <a th:href="@{/equipos/{id}(id=${equipo.id})}" th:text="${equipo.nombre}"></a>
                    </td>
                    <td th:if="${usuario.getAdministrador()}">
                        <a  class="btn btn-primary btn-xs" th:href="@{/equipos/{id}/editar(id=${equipo.id})}"/>Editar</a>
                    <button type="button" class="btn btn-danger btn-xs" onmouseover="" style="cursor: pointer;"
                            th:onclick="'del(\'/equipo/' + ${equipo.id} + '\')'">Borrar equipo</button></td>

            </tr>
        </tbody>
    </table>
    <p><a class="btn btn-primary" th:href="@{/equipos/crear}"> Crear equipo</a>
</div>
</div>
```

### infoequipo.html
Esta vista permite acceder al listado de usuarios de un equipo. En la misma podemos añadirnos o eliminarnos segun si aparecemos o no en la lista.

```javascript
    <div class="row mt-3">
    <div class="col">
        <h2 th:text="'Listado de usuarios que componen el equipo ' + ${equipo.getNombre()} "></h2>
    </div>
</div>

<div class="row mt-3">
    <div class="col">
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Usuarios</th>
                </tr>
            </thead>
            <tbody>
                <tr th:each="usuario: ${usuarios}">
                    <td th:text="${usuario.nombre}"></td>
                </tr>


            </tbody>
        </table>
        <form th:action="@{/equipos/{id}/agregar(id=${equipo.id})}" method="post">
            <button class="btn btn-primary" th:if="${aparezco == false}" >Agregarme</button>
        </form>
        <button th:if="${aparezco == true}" class="btn btn-danger btn-xs" onmouseover="" style="cursor: pointer;"
                th:onclick="'del(\'/equipos/' + ${equipo.id} + '\')'">Borrarme</button>

    </div>
</div>
```

### formCreaEquipo.html
Esta vista es un pequeño formulario que solicita un nombre para crear un equipo.

```javascript
    <div class="container-fluid">

    <h2 th:text="'Creando nuevo equipo'"></h2>

    <form method="post" th:action="@{/equipos/crear}" th:object="${equipoData}">
        <div class="col-6">
            <div class="form-group">
                <label for="titulo">Nombre del equipo:</label>
                <input class="form-control" id="titulo" name="titulo" required th:field="*{nombre}" type="text"/>
            </div>
            <button class="btn btn-primary" type="submit">Crear equipo</button>
        </div>
    </form>
</div>
```

### formEditarEquipo.html
Esta vista es prácticamente como la anterior con la diferencia de que modificará una ya existente.

```javascript
<div class="container-fluid">

    <h2 th:text="'Editando nombre del equipo '+ ${equipo.getNombre()}"></h2>

    <form method="post" th:action="@{/equipos/{id}/editar(id=${equipo.id})}" th:object="${equipoData}">
        <div class="col-6">
            <div class="form-group">
                <label for="titulo">Nombre del equipo:</label>
                <input class="form-control" id="titulo" name="titulo" required th:field="*{nombre}" type="text"/>
            </div>
            <button class="btn btn-primary" type="submit">Editar equipo</button>
        </div>
    </form>
</div>
```

### Tests de EquipoWebTest
Test 1
```javascript
  @Test
    public void listadoDeEquipos() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(containsString("Listado de equipos")));
    }
```
Test 2
```javascript
 @Test
    public void verEquiposApareceNavBar() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(containsString("Equipos")));
    }
```
Test 3
```javascript
    @Test
    public void infoDelEquipo() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);
        List<Equipo> equipos = new ArrayList<>();
        Equipo equipo = new Equipo("Nombre");
        equipo.addUsuario(usuario);
        equipos.add(equipo);
        when(usuarioService.findById(null)).thenReturn(usuario);
        when(equipoService.findById(1L)).thenReturn(equipo);

        when(equipoService.findAllOrderedByName()).thenReturn(equipos);

        this.mockMvc.perform(get("/equipos/1"))
                .andExpect(content().string(containsString("Listado de usuarios que componen el equipo")));
    }
```
Test 4
```javascript
    @Test
    public void apareceBotonEnEquipos() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(containsString("Crear equipo")));
    }
```
Test 5
```javascript
    @Test
    public void vistaCrearEquipo() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(get("/equipos/crear"))
                .andExpect(content().string(containsString("Creando nuevo equipo")));
    }
```
Test 6
```javascript
    @Test
    @Transactional
    public void agregaAEquipo() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(post("/equipos/1/agregar"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"));
    }
```
Test 7
```javascript
    @Test
    @Transactional
    public void borrarmeDeEquipo() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(delete("/equipos/1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("")));
    }
```
Test 8
```javascript
    @Test
    @Transactional
    public void borrarEquipo() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(delete("/equipo/1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("")));
    }
```
Test 9
```javascript
    @Test
    @Transactional
    public void editarEquipo() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setAdministrador(true);

        when(usuarioService.findById(null)).thenReturn(usuario);

        this.mockMvc.perform(post("/equipos/1/editar"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"));
    }
```