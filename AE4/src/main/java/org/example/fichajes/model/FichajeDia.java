package org.example.fichajes.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

public class FichajeDia {
    private LocalDate fecha;
    private String trabajadorNombre;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private String climaEntrada;
    private String climaSalida;
    private Double temperaturaEntrada;
    private Double temperaturaSalida;
    private String observacionesEntrada;
    private String observacionesSalida;

    public FichajeDia() {}

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTrabajadorNombre() {
        return trabajadorNombre;
    }

    public void setTrabajadorNombre(String trabajadorNombre) {
        this.trabajadorNombre = trabajadorNombre;
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getClimaEntrada() {
        return climaEntrada;
    }

    public void setClimaEntrada(String climaEntrada) {
        this.climaEntrada = climaEntrada;
    }

    public String getClimaSalida() {
        return climaSalida;
    }

    public void setClimaSalida(String climaSalida) {
        this.climaSalida = climaSalida;
    }

    public Double getTemperaturaEntrada() {
        return temperaturaEntrada;
    }

    public void setTemperaturaEntrada(Double temperaturaEntrada) {
        this.temperaturaEntrada = temperaturaEntrada;
    }

    public Double getTemperaturaSalida() {
        return temperaturaSalida;
    }

    public void setTemperaturaSalida(Double temperaturaSalida) {
        this.temperaturaSalida = temperaturaSalida;
    }

    public String getObservacionesEntrada() {
        return observacionesEntrada;
    }

    public void setObservacionesEntrada(String observacionesEntrada) {
        this.observacionesEntrada = observacionesEntrada;
    }

    public String getObservacionesSalida() {
        return observacionesSalida;
    }

    public void setObservacionesSalida(String observacionesSalida) {
        this.observacionesSalida = observacionesSalida;
    }

    public String getTiempoTrabajado() {
        if (horaEntrada != null && horaSalida != null) {
            Duration duracion = Duration.between(horaEntrada, horaSalida);
            long horas = duracion.toHours();
            long minutos = duracion.toMinutesPart();
            return String.format("%02d:%02d", horas, minutos);
        }
        return "-";
    }

    public boolean isCompleto() {
        return horaEntrada != null && horaSalida != null;
    }
}
