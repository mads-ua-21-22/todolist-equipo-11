# Practica 1

## Repositorios
[Repositorios en Docker](https://hub.docker.com/repository/docker/dargue94/mads-todolist)

[Github](https://github.com/mads-ua-21-22/mads-todolist-dargue94)

[Trello](https://trello.com/b/SVVvfCcB/todolist-mads)
## *Resumen*
En esta práctica se ha realizado una pequeña web que nos permitirá gestionar los usuarios.
Podemos encontrar el rol de administrador que nos permitirá bloquear y desbloquear usuarios y es el único usuario que puede acceder al listado de los mismos.

## *Cómo se hizo*
Para la realización de esta práctica se ha trabajado sobretodo sobre una metodología. Para cada funcionalidad se ha creado una rama, label e Issue en GIthub y una tarea en Trello para tener un control sobre lo que hacemos en cada momento.

## *Listado de nuevas clases*
* UsuarioNoAdminException
* UsuarioController

## *Listado de nuevas plantillas*
* about.html
* usuarios.html
* descripcion.html

## *A tener en cuenta*
La documentación la he realizado siguiento el orden de las funcionalidades que se solicitan.
Como el código sale de la versión final, algunas funcionalidades no tendrán toda la plantilla Thymeleaf para evitar repetición de código.

### Acerca de
"Acerca de" es únicamente una vista con mis datos asi como la versión en la que se encuentra el proyecto y su fecha.
El código principal del mismo es el siguiente:
```javascript
<div class="container-fluid">
    <h1>ToDoList</h1>
    <ul>
        <li>Desarrollada por Darío Guerrero Montero</li>
        <li>Versión 1.1.0 </li>
        <li>Fecha de release: 16/10/21</li>
    </ul>
</div>
```
### NavBar
El NavBar es una barra de menú bastante sencilla que nos permitirá el acceso a Acerca de, al listado de tareas y tendrá un Dropdown para poder acceder a la cuenta en un futuro y cerrar la sesión.

Este codigo aparece en fragments.html.

Esta barra de menú aparece en todas las vistas excepto en las de Login, Registro y Acerca de.
```javascript
<div th:fragment="header">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item">
                    <a class="nav-link" href="/about">ToDoList <span class="sr-only">(current)</span></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" th:href="@{/usuarios/{id}/tareas(id=${userid})}">Tareas</a>
                </li>

            </ul>
            <ul class="navbar-nav ml-auto">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        Dropdown
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdown">
                        <a class="dropdown-item" href="#">Cuenta</a>
                        <a class="dropdown-item" href="/logout">Cerrar sesión
                            <td th:text="${username}"></td>
                        </a>
                    </div>
                </li>
            </ul>
        </div>
    </nav>
</div>
```
Para que este menu aparezca en el resto de vistas, hay que insertarlo con el id del usuario y su nombre como parametro para poder mostrar los datos requeridos:
```javascript
    <header th:replace="/navbar.html :: header (username=${usuario.nombre}, userid=${usuario.id})"> </header>
```
### Listado de Usuarios
En */usuarios* se nos muetra un listado de todos los usuarios donde podemos observar su Id y Email.

El método lo que hace es comprobar que el usuario que accede existe y que esta logeado (para mostrar bien los errores),tras esto se pide el listado de usuarios.
Para poder mostrar bien los datos de la Barra de Menú, es necesario pasar el usuario de la sesión asi como la lista de usuarios.

Aqui ya podemos ver que aparece el administrador, pero se comentará más adelante.

En el controlador se ha realizado el siguiente método:
```javascript
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
```
El metodo service se encarga de devolver el listado de usuarios de forma ordenada por Id:
```javascript
    @Transactional(readOnly = true)
public List<Usuario> allUsuarios() {
    logger.debug("Devolviendo todos los usuarios " );

    List<Usuario> usuarios = new ArrayList<>();
    usuarioRepository.findAll().forEach(usuarios::add);

    Collections.sort(usuarios, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
    return usuarios;
}
```
La vista relacionada con esta funcionalidad sería usuarios.html:
```javascript
   <body>
    <header th:replace="/navbar.html :: header (username=${usuario.nombre}, userid=${usuario.id})"> </header>

    <div class="container-fluid">
        <div class="row mt-3">
            <div class="col">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Id</th>
                            <th>Email</th>
                            <th>Descripción</th>
                            <th>Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr th:each="usuario: ${usuarios}">
                            <td th:text="${usuario.id}"></td>
                            <td th:text="${usuario.email}"></td>
                            <td>
                                <a class="btn btn-primary btn-xs" th:href="@{/usuarios/{id}/(id=${usuario.id})}"/>Ver más</a>
                        </td>
                        <td>
                            <form th:action="@{/bloquear/{id}(id=${usuario.id})}" method="post">
                                <button th:if="${usuario.bloqueado == false}" class="btn btn-danger btn-xs" >Bloquear</button>
                                <button th:if="${usuario.bloqueado == true}" class="btn btn-danger btn-xs"  >Desbloquear</button>
                            </form>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div th:replace="fragments::javascript"/>

</body>
```
### Descripción del Usuario
Para la descripción del usuario todo lo que necesitamos es un metodo controller y su vista.

El servicio no llega a ser necesario, ya que ya tenemos la información de los usuarios al listarlos.

En el método del controller (UsuarioController) podemos observar que principalmente busca que el usuario exista, y devuelve el usuario de la sesión y el usuario del que queremos ver la información.

```javascript
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
```
En la vista de la descripción podemos observar todos sus atributos:

```javascript
<div class="container-fluid">
    <div class="container-fluid">
        <h1>Descripcion del usuario: <td th:text="${usuario.id}"></td></h1>
        <ul>
            <li>Id del usuario: <td th:text="${usuario.id}"></td> </li>
            <li>Nombre del usuario: <td th:text="${usuario.nombre}"></td> </li>
            <li>Email del usuario: <td th:text="${usuario.email}"></td> </li>
            <li>Fecha de nacimiento del usuario: <td th:text="${usuario.fechaNacimiento}"></td> </li>
        </ul>
        <a class="btn btn-primary btn-xs" th:href="@{/usuarios}"/>Volver atrás</a>
</div>

</div>
```

### Rol de administrador
Se solicita que un usuario pueda registrar como un Administrador a través de un checkbox, y este solo aparecera si no existe ninguno todavia.

Para esta función lo que se ha realizado fundamentalmente es lo siguiente:

He comenzado añadiendo el atributo tanto a RegisterData como al Objeto usuario.

De esta forma se crea tambien su Get y Set.
```javascript
private Boolean administrador = false;

public Boolean getAdministrador() {
    return administrador;
}

public void setAdministrador(Boolean administrador) {
    this.administrador = administrador;
}
```
Después se ha creado la checkbox en registro:
```javascript
<div class="form-group" th:if="${hayadmin == false}">
    <label for="administrador">Administrador</label>
    <input type="checkbox" name="mycheckbox" id="administrador" th:field="*{administrador}" th:value="true"/>
</div>
```

Esta checkbox como se puede observar, llama a la variable hayadmin, que esta es una llamada al método existeAdmin, que tiene el siguiente codigo:

Este código lo unico que hace es coger todos los usuarios que hayan y buscar si alguno es administrador.

```javascript
public Boolean existeAdmin() {
    List<Usuario> usuarios = new ArrayList<>();
    usuarioRepository.findAll().forEach(usuarios::add);
    for(Usuario user : usuarios)
    {
        if(user.getAdministrador() == true)
            return true;
    }
    return false;
}
```

Y como se mencionó antes, en el login se tiene que redirigir en caso de que tenga el rol de administrador. Podemos observar en el Controller este pequeño codigo que hace la comprobación:

Lo unico que hace es, tras logear el usuario correctamente, ver si este es administrador:
```javascript
managerUserSession.logearUsuario(session, usuario.getId());
if(usuario.getAdministrador() == true)
return "redirect:/usuarios/";
```

### Protección en listado y descripción.
Como comenté anteriormente, esta protección es una excepción llamada UsuarioNoAdminException que saltará siempre que el usuario no sea administrador:
```javascript
 if(usuario.getAdministrador() == false)
    throw new UsuarioNoAdminException();
```

### Bloqueo de usuarios por usuario administrador
Para bloquear los usuarios hemos tenido que hacer lo mismo que con administrador, añadiendo un atributo bloqueado en el objeto Usuario:
```javascript
private Boolean bloqueado = false;

public Boolean getBloqueado() {
    return bloqueado;
}

public void setBloqueado(Boolean bloqueado) {
    this.bloqueado = bloqueado;
}
```
La variable debe estar inicializada a false, ya que no es un atributo que se seleccione en el registro.

Para poder bloquear o desbloquear como administrador podemos encontrar el siguiente codigo en la vista:
```javascript
<form th:action="@{/bloquear/{id}(id=${usuario.id})}" method="post">
    <button th:if="${usuario.bloqueado == false}" class="btn btn-danger btn-xs" >Bloquear</button>
    <button th:if="${usuario.bloqueado == true}" class="btn btn-danger btn-xs"  >Desbloquear</button>
</form>
```
Este action llama al controlador que todo lo que hace es llamar al servicio para modificar su atributo bloqueado:

Controller:
```javascript
public String cambiaBloqueado(@PathVariable(value="id") Long id,Model model, HttpSession session) {
    usuarioService.cambiaEstadoBloqueado(id);
    return "redirect:/usuarios";
}
```
Service:
```javascript
    public void cambiaEstadoBloqueado(Long usuario_id) {
    Usuario usuario = usuarioRepository.findById(usuario_id).orElse(null);

    if (usuario == null) {
        throw new UsuarioNotFoundException();
    }

    if(usuario.getBloqueado() == false) {
        usuario.setBloqueado(true);
    } else {
        usuario.setBloqueado(false);
    }
    usuarioRepository.save(usuario);
}
```

## Tests
Podemos encontrar los siguientes tests. Dado que el nombre me parece suficiente indicativo de lo que hacen no comentaré mucho sobre estos:

TEST 1
```javascript
 public void getAboutDevuelveNombreAplicacion() throws Exception {
    this.mockMvc.perform(get("/about"))
        .andExpect(content().string(containsString("ToDoList")));
}
```

TEST 2
```javascript
    public void navBarTest1() throws Exception {
    Usuario usuario = new Usuario("domingo@ua.es");
    usuario.setId(1L);
    usuario.setAdministrador(true);
    when(usuarioService.findById(null)).thenReturn(usuario);

    this.mockMvc.perform(get("/usuarios"))
        .andExpect(content().string(containsString("Tareas")));
}
```

TEST 3
```javascript
public void navBarTest2() throws Exception {
    Usuario usuario = new Usuario("domingo@ua.es");
    usuario.setId(1L);
    usuario.setAdministrador(true);

    when(usuarioService.findById(null)).thenReturn(usuario);

    this.mockMvc.perform(get("/usuarios"))
        .andExpect(content().string(containsString("ToDoList")));
}
```

TEST 4
```javascript
public void servicioAllUsuariosError() throws Exception {
this.mockMvc.perform(get("/usuarios"))
.andExpect(status().isNotFound());
}
```
TEST 5
```javascript
 public void servicioAllUsuariosNotAdmin() throws Exception {
    Usuario usuario = new Usuario("domingo@ua.es");
    usuario.setId(1L);
    usuario.setAdministrador(false);
    when(usuarioService.findById(null)).thenReturn(usuario);

    this.mockMvc.perform(get("/usuarios"))
        .andExpect(status().isUnauthorized());
}
```
TEST 6
```javascript
public void servicioAllUsuarios() throws Exception {
    Usuario usuario = new Usuario("domingo@ua.es");
    usuario.setId(1L);
    usuario.setAdministrador(true);
    when(usuarioService.findById(null)).thenReturn(usuario);

    this.mockMvc.perform(get("/usuarios"))
        .andExpect(content().string(containsString("Email")));
}
```
TEST 7
```javascript
    public void servicioAllUsuarios2() throws Exception {
    Usuario usuario = new Usuario("domingo@ua.es");
    usuario.setId(1L);
    usuario.setAdministrador(true);
    List<Usuario> usuarios = new ArrayList<>();
    usuarios.add(usuario);
    when(usuarioService.findById(null)).thenReturn(usuario);
    when(usuarioService.allUsuarios()).thenReturn(usuarios);

    this.mockMvc.perform(get("/usuarios"))
        .andExpect(content().string(containsString("domingo@ua.es")))
        .andExpect(content().string(containsString("1")));
    ;
}
```
TEST 8
```javascript
    public void servicioDescripciónUsuarioError() throws Exception {
    this.mockMvc.perform(get("/usuarios/1"))
        .andExpect(status().isNotFound());
}
```
TEST 9
```javascript
public void servicioDescripciónUsuarioNotAdmin() throws Exception {
    Usuario usuario = new Usuario("domingo@ua.es");
    usuario.setId(1L);
    usuario.setAdministrador(false);
    List<Usuario> usuarios = new ArrayList<>();
    usuarios.add(usuario);
    when(usuarioService.findById(null)).thenReturn(usuario);
    when(usuarioService.allUsuarios()).thenReturn(usuarios);

    this.mockMvc.perform(get("/usuarios/1"))
        .andExpect(status().isUnauthorized());
}
```
TEST 9
```javascript
    public void servicioDescripcionUsuario() throws Exception {
    Usuario usuario = new Usuario("domingo@ua.es");
    usuario.setId(1L);
    usuario.setAdministrador(true);
    when(usuarioService.findById(1L)).thenReturn(usuario);
    when(usuarioService.findById(null)).thenReturn(usuario);

    this.mockMvc.perform(get("/usuarios/1"))
        .andExpect(content().string(containsString("domingo@ua.es")));
}
```
TEST 10
```javascript
public void servicioUsuarioBloqueadoLogin() throws Exception {
    Usuario anaGarcia = new Usuario("ana.garcia@gmail.com");
    anaGarcia.setId(1L);
    anaGarcia.setBloqueado(true);

    when(usuarioService.login("ana.garcia@gmail.com", "12345678"))
        .thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
    when(usuarioService.findByEmail("ana.garcia@gmail.com"))
        .thenReturn(anaGarcia);

    this.mockMvc.perform(post("/login")
        .param("eMail", "ana.garcia@gmail.com")
        .param("password", "12345678"))
        .andExpect(content().string(containsString("El usuario ha sido bloqueado")));
}
```