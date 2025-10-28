public class Asignaturas {
    private String nombre;
    private String cursos;
    private int horas;
    private String Nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCursos() {
        return cursos;
    }

    public void setCursos(String cursos) {
        this.cursos = cursos;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public Asignaturas(String nombre, String cursos, int horas, String nombre1) {
        this.nombre = nombre;
        this.cursos = cursos;
        this.horas = horas;
        Nombre = nombre1;
    }
}
