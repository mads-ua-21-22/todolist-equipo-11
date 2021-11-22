# Practica 4

## Repositorios
[Repositorios en Docker](https://hub.docker.com/repository/docker/dargue94/mads-todolist-equipo11)

[Github](https://github.com/mads-ua-21-22/todolist-equipo-11)

## *Resumen*
En esta práctica hemos preparado la Web para poder producirla en producción y hemos añadido algunos cambios sencillos.
    
## *Listado de nuevas features*
* Podemos eliminar a usuarios de un equipo como Administrador
* Corrección - Aparece Usuarios en el NavBar como Administrador
* Se ha agregado el campo descripción a los equipos, el cual se puede editar y se puede ver en la página de un equipo.

### Parte del código asociado

En equipo controller para poder borrar un usuario

```javascript
  @DeleteMapping("/equipo/{id}/{userid}")
    @ResponseBody
    public String borraUsuarioEquipo(@PathVariable(value="id")Long idEquipo,
                                     @PathVariable(value="userid")Long idUsuario,
                                     Model model,RedirectAttributes flash,
                                     HttpSession session){
        Long idlogin = (Long) session.getAttribute("idUsuarioLogeado");
        managerUserSession.comprobarUsuarioLogeado(session,idlogin);

        Usuario usuario = usuarioService.findById(idlogin);
        if(usuario == null)
            throw new UsuarioNotFoundException();
        if(!usuario.getAdministrador())
            throw new UsuarioNoAdminException();
        equipoService.eliminarDeEquipo(idUsuario,idEquipo);
        return "";
    }
```

En su vista se comprueba y si es administrador aparecerá el botón

```javascript
<tr th:each="usuar: ${usuarios}">
    <td th:text="${usuario.nombre}"></td>
    <td th:text="${usuar.nombre}"></td>
    <td th:if="${usuario.getAdministrador()}">
        <button type="button" class="btn btn-danger btn-xs" onmouseover="" style="cursor: pointer;"
                th:onclick="'delu(\'/equipo/'+${equipo.id}+'/'+${usuar.id}+'\')'">Borrar miembro</button>
    </td>
```

Al llamar al NavBar pasamos una variable más la cual se llama esAdmin - código de fragments.html

```javascript
<li class="nav-item" th:if="${esAdmin}">
   <a class="nav-link" th:href="@{/usuarios}">Usuarios</a>
</li>
```

En Equipos hemos agregado el atributo descripción, y hemos generado unos pequeños métodos en función de esta, así como unos pequeños arreglos para que funcione correctamente.

```javascript
    @Column(columnDefinition = "varchar(255) default ''")
    private String descripcion;
```

## *Detalles del despliegue de producción*
El despliegue de producción se ha generado desde alu21.