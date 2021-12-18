/* Populate tables */
INSERT INTO usuarios (email, nombre, password, fecha_nacimiento) VALUES('user@ua', 'Usuario Ejemplo', '123', '2001-02-10');
INSERT INTO usuarios (email, nombre, password, fecha_nacimiento) VALUES('user1@ua', 'Usuario Ejemplo 2', '123', '2001-02-10');
INSERT INTO tareas (titulo, usuario_id) VALUES('Lavar coche', '1');
INSERT INTO tareas (titulo, usuario_id) VALUES('Renovar DNI', '1');

INSERT INTO equipos (nombre, lider) VALUES('Proyecto P1', '1');
INSERT INTO equipo_usuario (fk_equipo, fk_usuario) VALUES('1', '1');
INSERT INTO equipo_usuario (fk_equipo, fk_usuario) VALUES('1', '2');
INSERT INTO equipos (nombre) VALUES('Proyecto A1');