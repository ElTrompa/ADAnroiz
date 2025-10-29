import java.sql.Timestamp;

public class Ausencias {
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String asignatura;
    private String nombre;
    private boolean justificado;
    private String curso;

    public Ausencias(String fecha, String horaInicio, String horaFin, String asignatura, String nombre, boolean justificado, String curso) {
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.asignatura = asignatura;
        this.nombre = nombre;
        this.justificado = justificado;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isJustificado() {
        return justificado;
    }

    public void setJustificado(boolean justificado) {
        this.justificado = justificado;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    @Override
    public String toString() {
        return "Ausencia de " + nombre + " en " + asignatura + " (" + curso + ") el " + fecha + horaInicio + horaFin +
                " | Justificada: " + justificado;
    }
}
