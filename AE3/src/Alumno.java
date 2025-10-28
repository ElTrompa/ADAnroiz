public class Alumno {
    private String nombre;
    private String curso;
    private int dni;
    private String fechaNacimiento;
    private String correoPadres;
    private String nombrePadre;
    private String nombreMadre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCorreoPadres() {
        return correoPadres;
    }

    public void setCorreoPadres(String correoPadres) {
        this.correoPadres = correoPadres;
    }

    public String getNombrePadre() {
        return nombrePadre;
    }

    public void setNombrePadre(String nombrePadre) {
        this.nombrePadre = nombrePadre;
    }

    public String getNombreMadre() {
        return nombreMadre;
    }

    public void setNombreMadre(String nombreMadre) {
        this.nombreMadre = nombreMadre;
    }

    public Alumno(String nombreMadre, String nombrePadre, String correoPadres, String fechaNacimiento, int dni, String curso, String nombre) {
        this.nombreMadre = nombreMadre;
        this.nombrePadre = nombrePadre;
        this.correoPadres = correoPadres;
        this.fechaNacimiento = fechaNacimiento;
        this.dni = dni;
        this.curso = curso;
        this.nombre = nombre;
    }
}
