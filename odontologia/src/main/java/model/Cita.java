package model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Modelo que representa una cita odontológica
 */
public class Cita {

    private int id;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private EstadoCita estado;
    private Paciente paciente;
    private Odontologo odontologo;

    // Constructor vacío
    public Cita() {
    }

    // Constructor con parámetros principales
    public Cita(LocalDate fecha, LocalTime hora, String motivo, EstadoCita estado) {
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.estado = estado;
    }
    
    // Constructor completo
    public Cita(int id, LocalDate fecha, LocalTime hora, String motivo, EstadoCita estado, 
                Paciente paciente, Odontologo odontologo) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.estado = estado;
        this.paciente = paciente;
        this.odontologo = odontologo;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Odontologo getOdontologo() {
        return odontologo;
    }

    public void setOdontologo(Odontologo odontologo) {
        this.odontologo = odontologo;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }


    // Métodos de negocio
    
    /**
     * Confirma la cita si no está cancelada
     * @return true si se confirmó exitosamente, false si no se pudo confirmar
     */
    public boolean confirmar() {
        if (this.estado == EstadoCita.CANCELADA) {
            return false; // No se puede confirmar una cita cancelada
        }
        this.estado = EstadoCita.CONFIRMADA;
        return true;
    }

    /**
     * Cancela la cita
     */
    public void cancelar() {
       this.estado = EstadoCita.CANCELADA;
    }
    
    public void finalizar() {
        this.estado = EstadoCita.FINALIZADO;
    }

    /**
     * Reprograma la cita con nueva fecha y hora
     * @param nuevaFecha Nueva fecha de la cita
     * @param nuevaHora Nueva hora de la cita
     */
    public void reprogramar(LocalDate nuevaFecha, LocalTime nuevaHora) {
        if (this.estado == EstadoCita.CANCELADA ||this.estado == EstadoCita.FINALIZADO) {
            System.out.println("No se puede reprogramar la cita, revise que no este cancelada o finalizada la cita");
} else {
        this.fecha = nuevaFecha;
        this.hora = nuevaHora;
        this.estado = EstadoCita.PENDIENTE;
        }
        
    }

    @Override
    public String toString() {
        return String.format("Cita #%d [Paciente: %s, Odontólogo: %s, Fecha: %s, Hora: %s, Estado: %s, Motivo: %s]",
                id,
                paciente != null ? paciente.getNombre() : "N/A",
                odontologo != null ? odontologo.getNombre() : "N/A",
                fecha,
                hora,
                estado,
                motivo);
    }
}
