package madstodolist.controller;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ComentarioData {
    private String comentario;

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
