public class Profesor {
    private String nombre;
    private int hora;
    private String clase;

    public Profesor(String nombre, int hora, String clase) {
        this.nombre = nombre;
        this.hora = hora;
        this.clase = clase;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }
}
