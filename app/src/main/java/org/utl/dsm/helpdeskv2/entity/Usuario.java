package org.utl.dsm.helpdeskv2.entity;
public class Usuario {
    private Long idUsuario;
    private String user, contrasenia, lastConection, token;

    public Usuario() {}

    public Usuario(Long idUsuario, String user, String contrasenia, String lastConection, String token) {
        this.idUsuario = idUsuario;
        this.user = user;
        this.contrasenia = contrasenia;
        this.lastConection = lastConection;
        this.token = token;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getLastConection() {
        return lastConection;
    }

    public void setLastConection(String lastConection) {
        this.lastConection = lastConection;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}