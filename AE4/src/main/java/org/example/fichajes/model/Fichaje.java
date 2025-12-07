package org.example.fichajes.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "fichajes")
public class Fichaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_id", nullable = false)
    private Trabajador trabajador;

    @Transient
    private String trabajadorNombre;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoFichaje tipo;

    @Column(precision = 5, scale = 2)
    private Double temperatura;

    @Column(name = "descripcion_clima", length = 100)
    private String descripcionClima;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "created_at", updatable = false, insertable = false)
    private java.time.LocalDateTime createdAt;

    public enum TipoFichaje {
        ENTRADA, SALIDA
    }

    public Fichaje() {
    }

    public Fichaje(Trabajador trabajador, LocalDate fecha, LocalTime hora, TipoFichaje tipo) {
        this.trabajador = trabajador;
        this.fecha = fecha;
        this.hora = hora;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Trabajador getTrabajador() {
        return trabajador;
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
    }

    public int getTrabajadorId() {
        return trabajador != null ? trabajador.getId() : 0;
    }

    public String getTrabajadorNombre() {
        if (trabajadorNombre != null) {
            return trabajadorNombre;
        }
        return trabajador != null ? trabajador.getNombreCompleto() : "";
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

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public TipoFichaje getTipo() {
        return tipo;
    }

    public void setTipo(TipoFichaje tipo) {
        this.tipo = tipo;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public String getDescripcionClima() {
        return descripcionClima;
    }

    public void setDescripcionClima(String descripcionClima) {
        this.descripcionClima = descripcionClima;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return fecha + " " + hora + " - " + tipo + " (" + trabajadorNombre + ")";
    }
}

