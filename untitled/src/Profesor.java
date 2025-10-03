public class Profesor {
    private String nombre;
    private int hora;
    private String clase;
    private String dia;

    public Profesor(String nombre, int hora, String clase, String dia) {
        this.nombre = nombre;
        this.hora = hora;
        this.clase = clase;
        this.dia = dia;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
