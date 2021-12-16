package madstodolist.controller;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class TareaData {
    private String titulo;

    private String descripcion;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaLimite;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Date getFechaLimite() { return fechaLimite; }

    public void setFechaLimite(Date fechaLimite) { this.fechaLimite = fechaLimite; }
}
