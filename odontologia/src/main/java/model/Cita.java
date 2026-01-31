package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {

    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private EstadoCita estado;
    private LocalTime horaLlegadaPaciente;

    // Constructor vacío
    public Cita() {
    }

    // Constructor con parámetros
    public Cita(LocalDate fecha, LocalTime hora, String motivo, EstadoCita estado, LocalTime horaLlegadaPaciente) {
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.estado = estado;
        this.horaLlegadaPaciente = horaLlegadaPaciente;
    }

    // Getters y Setters
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

    // Métodos del diagrama
public void confirmar() {
    // Si ya está cancelada, no se puede confirmar
    if (this.estado == EstadoCita.CANCELADA) {
        System.out.println("Error: La cita no se puede confirmar porque esta CANCELADA.");
    } else {
        this.estado = EstadoCita.CONFIRMADA;
        System.out.println("Cita confirmada exitosamente.");
    }
}

  public void cancelar() {
    this.estado = EstadoCita.CANCELADA;
    System.out.println("La cita ha sido marcada como CANCELADA.");
}


public void reprogramar(LocalDate nuevaFecha, LocalTime nuevaHora) {
    this.fecha = nuevaFecha;
    this.hora = nuevaHora;
    this.estado = EstadoCita.PENDIENTE; 
    System.out.println("Cita reprogramada para: " + nuevaFecha + " a las " + nuevaHora);
}
public void registrarLlegada(LocalTime horaLlegada) {
    this.horaLlegadaPaciente = horaLlegada;
}

public void evaluarAsistencia() {
    if (this.horaLlegadaPaciente != null) {
        this.estado = EstadoCita.ATENDIDA;
        System.out.println("El paciente asistio.");
    } else {
        this.estado = EstadoCita.AUSENTE;
        System.out.println("Paciente AUSENTE.");
    }


}
    @Override
    public String toString() {
        return "Cita{" +
                "fecha=" + fecha +
                ", hora=" + hora +
                ", motivo='" + motivo + '\'' +
                ", estado=" + estado +
                ", horaLlegadaPaciente=" + horaLlegadaPaciente +
                '}';
    }
}
