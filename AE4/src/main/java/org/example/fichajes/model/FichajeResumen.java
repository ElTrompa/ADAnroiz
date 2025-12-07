package org.example.fichajes.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

public class FichajeResumen {
    private int trabajadorId;
    private String trabajadorNombre;
    private LocalDate fecha;
    private LocalTime primeraEntrada;
    private LocalTime ultimaSalida;
    private String clima;
    private Double temperatura;
    private long minutosTotal;
    private String horasTrabajadas;

    public FichajeResumen(int trabajadorId, String trabajadorNombre, LocalDate fecha) {
        this.trabajadorId = trabajadorId;
        this.trabajadorNombre = trabajadorNombre;
        this.fecha = fecha;
        this.minutosTotal = 0;
    }

    public void calcularHorasTrabajadas(LocalTime entrada, LocalTime salida) {
        if (entrada != null && salida != null) {
            Duration duration = Duration.between(entrada, salida);
            minutosTotal += duration.toMinutes();
        }
    }

    public void setHorasTrabajadas() {
        long horas = minutosTotal / 60;
        long minutos = minutosTotal % 60;
        this.horasTrabajadas = String.format("%02d:%02d", horas, minutos);
    }

    public int getTrabajadorId() {
        return trabajadorId;
    }

    public void setTrabajadorId(int trabajadorId) {
        this.trabajadorId = trabajadorId;
    }

    public String getTrabajadorNombre() {
        return trabajadorNombre;
    }

    public void setTrabajadorNombre(String trabajadorNombre) {
        this.trabajadorNombre = trabajadorNombre;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getPrimeraEntrada() {
        return primeraEntrada;
    }

    public void setPrimeraEntrada(LocalTime primeraEntrada) {
        this.primeraEntrada = primeraEntrada;
    }

    public LocalTime getUltimaSalida() {
        return ultimaSalida;
    }

    public void setUltimaSalida(LocalTime ultimaSalida) {
        this.ultimaSalida = ultimaSalida;
    }

    public String getClima() {
        return clima;
    }

    public void setClima(String clima) {
        this.clima = clima;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public long getMinutosTotal() {
        return minutosTotal;
    }

    public void setMinutosTotal(long minutosTotal) {
        this.minutosTotal = minutosTotal;
    }

    public String getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public String getTemperaturaStr() {
        return temperatura != null ? String.format("%.1f°C", temperatura) : "N/A";
    }
}

