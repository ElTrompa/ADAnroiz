public class Ausencias {
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String asignatura;
    private String alumno;
    private boolean justificada;
    private String curso;

    public Ausencias(String fecha, String horaInicio, String horaFin, String asignatura, String alumno, boolean justificada, String curso) {
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.asignatura = asignatura;
        this.alumno = alumno;
        this.justificada = justificada;
        this.curso = curso;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public String getAlumno() {
        return alumno;
    }

    public void setAlumno(String alumno) {
        this.alumno = alumno;
    }

    public boolean isJustificada() {
        return justificada;
    }

    public void setJustificada(boolean justificada) {
        this.justificada = justificada;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    @Override
    public String toString() {
        return "Ausencias " + fecha + " " + horaInicio  + " " + horaFin + " " + asignatura + " " + alumno + " " + justificada + " " + curso + " ";
    }
}
