# Practica 4

## Repositorios
[Repositorios en Docker](https://hub.docker.com/repository/docker/dargue94/mads-todolist-equipo11)

[Github](https://github.com/mads-ua-21-22/todolist-equipo-11)

[Trello](https://trello.com/b/SVVvfCcB/todolist-mads)

## *Resumen*
En esta práctica hemos agregado nuevas funcionalidades que han sido pensadas entre los miembros del equipo para tratar de mejorar la Web y aprender a trabajar en equipo.

## *Forma de trabajar y Pair Programming*
Hemos trabajado de la forma que hemos ido siguiendo en las prácticas anteriores: creabamos una nueva rama a partir de Develop en lugar de Main, y para cada funcionalidad se trabajaba en una rama distinta. Respecto al código hemos trabajado principal comenzando con el Objeto/Service y después con el Controller, Vista y por último tests en caso de ser necesario.
Dado que en esta práctica lo que hemos hecho ha sido ampliar funcionalidades, hay tests que han sido refactorizados ligeramente en lugar de crear un nuevo test.

Respecto al Pair Programming, lo hemos utilizado en algunas ocasiones ya que es una forma de trabajar que nos ha gustado ya que permite dividir el trabajo entre los miembros que la realizan y, además, cuando uno de los miembros comete algún error es más facil solucionarlo.

## *Listado de nuevas features*
* Los equipos tienen un Lider
* Podemos modificar nuestros datos de Usuario
* Podemos comentar en las tareas

### Equipos tienen un Lider

Esta funcionalidad trata de crear un Lider el cuál será quien tenga el poder de añadir, editar o eliminar tareas a un equipo, así como eliminar a usuarios de el equipo del que es lider.
Adicionalmente, hemos hecho que las Tareas de un equipo que ya se veian(otra funcionalidad) aparezcan únicamente si eres miembro del equipo.

Para esta funcionalidad podemos encontrar las vistas:

* infotareaequipo
* formEditarTareaEquipo
* formNuevaTareaEquipo

El código asociado (a modo de resumen) es el siguiente:

Se hacen numerosas comprobaciones de dos tipos: si un usuario aparece en el listado de usuarios que devuelve un equipo y si el usuario que ha iniciado la sesion, es el Lider.

Esta primera se utiliza para saber si podrá ver las tareas de un equipo, asi como para evitar su acceso a la tarea a través de la URL.
```javascript
 if(equipo.getUsuarios().contains(usuario))
`````

Por otro lado, esta comprobación nos permitirá dar más controles al Lider, ya sea con la aparición de botonoes como el de Agregar Tarea, como la posibilidad de poder editar la tarea de un equipo desde "infotarea"
```javascript
equipo.getLider() != usuario
`````

Estos son algunos ejemplos en código:
```javascript
 @GetMapping("/equipos/{equipo}/tareas/{id}")
    public String formTareaEquipo(@PathVariable(value = "equipo") Long idEquipo,@PathVariable(value = "id") Long idTarea, @ModelAttribute TareaData tareaData,
                            Model model, HttpSession session) {
        Tarea tarea = tareaService.findById(idTarea);
        if (tarea == null)
            throw new TareaNotFoundException();

        Usuario usuario = usuarioService.findById(managerUserSession.usuarioLogeado(session));
        Equipo equipo = equipoService.findById(idEquipo);

        if(!equipo.getUsuarios().contains(usuario))
            throw new UsuarioNotFoundException();
        if(!equipo.getTareas().contains(tarea))
            throw new TareaNotFoundException();
        model.addAttribute("equipo",equipo);
        model.addAttribute("usuario",usuario);
        model.addAttribute("tarea",tarea);
        model.addAttribute("comentarios",comentarioService.allComentariosTarea(tarea.getId()));
        tareaData.setTitulo(tarea.getTitulo());
        tareaData.setDescripcion(tarea.getDescripcion());
        return "infotareaequipo";
    }
`````

## *Detalles del despliegue de producción*
El despliegue de producción se ha generado desde alu21.
Los scripts de .sql se encuentran en mi-host. Uno es el principal schema-final.sql y el otro es schema-final-X.sql