public class Asignaturas {
    private String nombre;
    private String cursos;
    private int horas;
    private String profeImparte;

    public Asignaturas(String nombre, String cursos, int horas, String profeImparte) {
        this.nombre = nombre;
        this.cursos = cursos;
        this.horas = horas;
        this.profeImparte = profeImparte;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCursos() { return cursos; }
    public void setCursos(String cursos) { this.cursos = cursos; }

    public int getHoras() { return horas; }
    public void setHoras(int horas) { this.horas = horas; }

    public String getProfeImparte() { return profeImparte; }
    public void setProfeImparte(String profeImparte) { this.profeImparte = profeImparte; }

    @Override
    public String toString() {
        return nombre + " (" + cursos + "), Profesor: " + profeImparte;
    }
}
