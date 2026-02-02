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
    private LocalTime horaLlegadaPaciente;
    private Paciente paciente;
    private Odontologo odontologo;

    // Constructor vacío
    public Cita() {
    }

    // Constructor con parámetros principales
    public Cita(LocalDate fecha, LocalTime hora, String motivo, EstadoCita estado, LocalTime horaLlegadaPaciente) {
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.estado = estado;
        this.horaLlegadaPaciente = horaLlegadaPaciente;
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

    public LocalTime getHoraLlegadaPaciente() {
        return horaLlegadaPaciente;
    }

    public void setHoraLlegadaPaciente(LocalTime horaLlegadaPaciente) {
        this.horaLlegadaPaciente = horaLlegadaPaciente;
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

    /**
     * Reprograma la cita con nueva fecha y hora
     * @param nuevaFecha Nueva fecha de la cita
     * @param nuevaHora Nueva hora de la cita
     */
    public void reprogramar(LocalDate nuevaFecha, LocalTime nuevaHora) {
        this.fecha = nuevaFecha;
        this.hora = nuevaHora;
        this.estado = EstadoCita.PENDIENTE;
        this.horaLlegadaPaciente = null; // Resetear la hora de llegada
    }

    /**
     * Registra la hora de llegada del paciente
     * @param horaLlegada Hora en que llegó el paciente
     */
    public void registrarLlegada(LocalTime horaLlegada) {
        this.horaLlegadaPaciente = horaLlegada;
    }

    /**
     * Evalúa la asistencia del paciente basándose en si registró su llegada
     * Si hay hora de llegada registrada -> ATENDIDA
     * Si no hay hora de llegada -> AUSENTE
     */
    public void evaluarAsistencia() {
        if (this.horaLlegadaPaciente != null) {
            this.estado = EstadoCita.ATENDIDA;
        } else {
            this.estado = EstadoCita.AUSENTE;
        }
    }
    
    /**
     * Verifica si el paciente llegó tarde
     * @return true si llegó después de la hora de la cita, false en caso contrario
     */
    public boolean llegoTarde() {
        if (this.horaLlegadaPaciente == null) {
            return false;
        }
        return this.horaLlegadaPaciente.isAfter(this.hora);
    }
    
    /**
     * Calcula los minutos de retraso (o adelanto si es negativo)
     * @return minutos de diferencia entre hora de llegada y hora de cita
     */
    public int calcularMinutosRetraso() {
        if (this.horaLlegadaPaciente == null) {
            return 0;
        }
        return (int) java.time.Duration.between(this.hora, this.horaLlegadaPaciente).toMinutes();
    }
    
    /**
     * Verifica si la cita es para el día de hoy
     * @return true si la fecha de la cita es hoy
     */
    public boolean esHoy() {
        return this.fecha != null && this.fecha.equals(LocalDate.now());
    }
    
    /**
     * Verifica si la cita ya pasó
     * @return true si la fecha/hora ya pasaron
     */
    public boolean yaPaso() {
        if (this.fecha == null || this.hora == null) {
            return false;
        }
        
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
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cita cita = (Cita) obj;
        return id == cita.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
