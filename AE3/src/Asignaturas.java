import java.util.ArrayList;

public class Asignaturas {
    private String nombreAsignatura;
    private ArrayList<String> cursos;
    private int horas;
    private String profesor;

    public Asignaturas(String nombreAsignatura, ArrayList<String> cursos, int horas, String profesor) {
        this.nombreAsignatura = nombreAsignatura;
        this.cursos = cursos;
        this.horas = horas;
        this.profesor = profesor;
    }

    public String getNombreAsignatura() {
        return nombreAsignatura;
    }

    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }

    public ArrayList<String> getCursos() {
        return cursos;
    }

    public void setCursos(ArrayList<String> cursos) {
        this.cursos = cursos;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    @Override
    public String toString() {
        return "Asignatura " + nombreAsignatura + " " + cursos + " " + horas + " " + profesor + " ";
    }
}
