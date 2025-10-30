public class Alumno {
    private String alumno;
    private String curso;
    private int dni;
    private String fecha;
    private String correoPadres;
    private String nombrePadre;
    private String nombreMadre;

    public Alumno(String alumno, String curso, int dni, String fecha, String correoPadres, String nombrePadre, String nombreMadre) {
        this.alumno = alumno;
        this.curso = curso;
        this.dni = dni;
        this.fecha = fecha;
        this.correoPadres = correoPadres;
        this.nombrePadre = nombrePadre;
        this.nombreMadre = this.nombreMadre;
    }

    public String getAlumno() {
        return alumno;
    }

    public String getCurso() {
        return curso;
    }

    public int getDni() {
        return dni;
    }

    public String getFecha() {
        return fecha;
    }

    public String getCorreoPadres() {
        return correoPadres;
    }

    public String getNombrePadre() {
        return nombrePadre;
    }

    public String getNombreMadre() {
        return nombreMadre;
    }

    @Override
    public String toString() {
        return "Alumno " + alumno + " " + curso + " " + dni + " " + fecha + " " + correoPadres + " " + nombrePadre + " " + nombreMadre + " ";
    }
}
