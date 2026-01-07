package com.example.garantias.model;

public class SesionOdoo {
    private String url;
    private String baseDatos;
    private int usuarioId;
    private String nombreUsuario;
    private String cookieSession;

    // Singleton simple
    private static SesionOdoo instancia;

    private SesionOdoo() {}

    public static SesionOdoo getInstancia() {
        if (instancia == null) instancia = new SesionOdoo();
        return instancia;
    }

    // getters y setters

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBaseDatos() {
        return baseDatos;
    }

    public void setBaseDatos(String baseDatos) {
        this.baseDatos = baseDatos;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCookieSession() {
        return cookieSession;
    }

    public void setCookieSession(String cookieSession) {
        this.cookieSession = cookieSession;
    }
}