package controller;

import model.Recepcion;
import model.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controlador de Recepción
 * Actúa como intermediario entre la vista y el modelo Recepcion
 */
public class RecepcionController {
    
    private Database db = Database.getInstance();
    private Recepcion recepcion = new Recepcion();
    
    // ========== GESTIÓN DE CITAS ==========
    
    /**
     * Crea una nueva cita
     */
    public boolean crearCita(int idPaciente, int idOdontologo, LocalDate fecha, LocalTime hora, String motivo) {
        Paciente paciente = db.getPacientes().get(idPaciente);
        Odontologo odontologo = db.getOdontologos().get(idOdontologo);
        
        if (paciente == null || odontologo == null) {
            return false;
        }
        
        return recepcion.crearCita(paciente, odontologo, fecha, hora, motivo);
    }
    
    /**
     * Crea una cita usando un horario predefinido
     */
    public boolean crearCitaConHorario(int idPaciente, int idOdontologo, int idHorario) {
        Paciente paciente = db.getPacientes().get(idPaciente);
        Odontologo odontologo = db.getOdontologos().get(idOdontologo);
        Horario horario = db.getHorarios().get(idHorario);
        
        if (paciente == null || odontologo == null || horario == null) {
            return false;
        }
        
        return recepcion.crearCita(paciente, odontologo, horario);
    }
    
    /**
     * Cancela una cita por su ID
     */
    public boolean cancelarCita(int idCita) {
        return recepcion.cancelarCita(idCita);
    }
    
    /**
     * Reprograma una cita existente
     */
    public boolean reprogramarCita(int idCita, LocalDate nuevaFecha, LocalTime nuevaHora) {
        Cita cita = db.getCitas().get(idCita);
        
        if (cita == null || cita.getEstado() == EstadoCita.CANCELADA) {
            return false;
        }
        
        // Verificar que la nueva fecha no sea pasada
        if (nuevaFecha.isBefore(LocalDate.now())) {
            return false;
        }
        
        // Validar disponibilidad del odontólogo en el nuevo horario
        boolean ocupado = db.getCitas().values().stream()
                .filter(c -> c.getId() != idCita) // Excluir la cita actual
                .filter(c -> c.getEstado() != EstadoCita.CANCELADA)
                .anyMatch(c -> c.getFecha().equals(nuevaFecha) 
                        && c.getHora().equals(nuevaHora)
                        && c.getOdontologo().getId() == cita.getOdontologo().getId());
        
        if (ocupado) {
            return false;
        }
        
        // Reprogramar la cita
        cita.reprogramar(nuevaFecha, nuevaHora);
        return true;
    }
    
    /**
     * Confirma una cita
     */
    public boolean confirmarCita(int idCita) {
        Cita cita = db.getCitas().get(idCita);
        
        if (cita == null) {
            return false;
        }
        
        return cita.confirmar();
    }
    
    // ========== GESTIÓN DE LLEGADAS ==========
    
    /**
     * Registra la llegada de un paciente (check-in)
     */
    public boolean registrarLlegada(int idCita) {
        return recepcion.registrarLlegadaPaciente(idCita, LocalTime.now());
    }
    
    /**
     * Registra llegada con hora específica
     */
    public boolean registrarLlegada(int idCita, LocalTime horaLlegada) {
        return recepcion.registrarLlegadaPaciente(idCita, horaLlegada);
    }
    
    /**
     * Evalúa la asistencia de una cita
     */
    public boolean evaluarAsistencia(int idCita) {
        Cita cita = db.getCitas().get(idCita);
        
        if (cita == null) {
            return false;
        }
        
        cita.evaluarAsistencia();
        return true;
    }
    
    // ========== BÚSQUEDA Y CONSULTAS ==========
    
    /**
     * Busca horarios disponibles para un odontólogo
     */
    public List<Horario> buscarDisponibilidad(int idOdontologo) {
        Odontologo odontologo = db.getOdontologos().get(idOdontologo);
        
        if (odontologo == null) {
            return List.of();
        }
        
        return recepcion.buscarDisponibilidad(odontologo);
    }
    
    /**
     * Busca horarios disponibles por fecha y odontólogo
     */
    public List<Horario> buscarDisponibilidad(LocalDate fecha, int idOdontologo) {
        return recepcion.buscarDisponibilidad(fecha, idOdontologo);
    }
    
    /**
     * Obtiene todas las citas
     */
    public List<Cita> obtenerTodasLasCitas() {
        return List.copyOf(db.getCitas().values());
    }
    
    /**
     * Obtiene las citas del día actual
     */
    public List<Cita> obtenerCitasDelDia() {
        return recepcion.obtenerCitasDelDia();
    }
    
    /**
     * Obtiene las citas pendientes de confirmación
     */
    public List<Cita> obtenerCitasPendientes() {
        return recepcion.obtenerCitasPendientesConfirmacion();
    }
    
    /**
     * Obtiene una cita por su ID
     */
    public Cita obtenerCitaPorId(int idCita) {
        return db.getCitas().get(idCita);
    }
    
    /**
     * Obtiene las citas de un paciente específico
     */
    public List<Cita> obtenerCitasPorPaciente(int idPaciente) {
        return db.getCitas().values().stream()
                .filter(c -> c.getPaciente() != null && c.getPaciente().getId() == idPaciente)
                .toList();
    }
    
    /**
     * Obtiene las citas de un odontólogo específico
     */
    public List<Cita> obtenerCitasPorOdontologo(int idOdontologo) {
        return db.getCitas().values().stream()
                .filter(c -> c.getOdontologo() != null && c.getOdontologo().getId() == idOdontologo)
                .toList();
    }
    
    /**
     * Verifica si un paciente tiene citas programadas
     */
    public boolean tieneCitasProgramadas(int idPaciente) {
        Paciente paciente = db.getPacientes().get(idPaciente);
        return recepcion.tieneCitasProgramadas(paciente);
    }
    
    // ========== REPORTES ==========
    
    /**
     * Genera un reporte de citas
     */
    public String generarReporteCitas() {
        return recepcion.generarReporteCitas();
    }
    
    /**
     * Obtiene estadísticas de citas por estado
     */
    public CitaEstadisticas obtenerEstadisticas() {
        long pendientes = db.getCitas().values().stream()
                .filter(c -> c.getEstado() == EstadoCita.PENDIENTE).count();
        long confirmadas = db.getCitas().values().stream()
                .filter(c -> c.getEstado() == EstadoCita.CONFIRMADA).count();
        long atendidas = db.getCitas().values().stream()
                .filter(c -> c.getEstado() == EstadoCita.ATENDIDA).count();
        long canceladas = db.getCitas().values().stream()
                .filter(c -> c.getEstado() == EstadoCita.CANCELADA).count();
        long ausentes = db.getCitas().values().stream()
                .filter(c -> c.getEstado() == EstadoCita.AUSENTE).count();
        
        return new CitaEstadisticas(pendientes, confirmadas, atendidas, canceladas, ausentes);
    }
    
    // ========== CLASE INTERNA PARA ESTADÍSTICAS ==========
    
    public static class CitaEstadisticas {
        public final long pendientes;
        public final long confirmadas;
        public final long atendidas;
        public final long canceladas;
        public final long ausentes;
        public final long total;
        
        public CitaEstadisticas(long pendientes, long confirmadas, long atendidas, long canceladas, long ausentes) {
            this.pendientes = pendientes;
            this.confirmadas = confirmadas;
            this.atendidas = atendidas;
            this.canceladas = canceladas;
            this.ausentes = ausentes;
            this.total = pendientes + confirmadas + atendidas + canceladas + ausentes;
        }
        
        @Override
        public String toString() {
            return String.format(
                "Pendientes: %d | Confirmadas: %d | Atendidas: %d | Canceladas: %d | Ausentes: %d | Total: %d",
                pendientes, confirmadas, atendidas, canceladas, ausentes, total
            );
        }
    }
}
