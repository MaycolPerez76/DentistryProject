package model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Clase Horario según diagrama de clases
 * Representa un slot de tiempo disponible para citas
 */
public class Horario {
    
    private int id;
    private LocalDate fecha;
    private LocalTime hora;
    private boolean disponible;
    private Odontologo odontologo;
    
    // Constructor vacío
    public Horario() {
        this.disponible = true;
    }
    
    // Constructor con parámetros
    public Horario(int id, LocalDate fecha, LocalTime hora, Odontologo odontologo) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.odontologo = odontologo;
        this.disponible = true;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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
    
    public boolean isDisponible() {
        return disponible;
    }
    
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    public Odontologo getOdontologo() {
        return odontologo;
    }
    
    public void setOdontologo(Odontologo odontologo) {
        this.odontologo = odontologo;
    }
    
    // ========== MÉTODOS DEL DIAGRAMA ==========
    
    /**
     * Marca el horario como ocupado
     */
    public void marcarOcupado() {
        this.disponible = false;
    }
    
    /**
     * Marca el horario como disponible
     */
    public void marcarDisponible() {
        this.disponible = true;
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    /**
     * Verifica si el horario ya pasó
     */
    public boolean yaPaso() {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        
        if (this.fecha.isBefore(hoy)) {
            return true;
        }
        
        if (this.fecha.equals(hoy) && this.hora.isBefore(ahora)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Verifica si el horario es de hoy
     */
    public boolean esHoy() {
        return this.fecha != null && this.fecha.equals(LocalDate.now());
    }
    
    @Override
    public String toString() {
        return String.format("Horario{id=%d, fecha=%s, hora=%s, disponible=%s, odontologo=%s}",
                id, 
                fecha, 
                hora, 
                disponible ? "Sí" : "No",
                odontologo != null ? odontologo.getNombre() : "N/A");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Horario horario = (Horario) obj;
        return id == horario.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
