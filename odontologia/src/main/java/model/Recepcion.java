package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase Recepcion según diagrama de clases
 * Responsable de gestionar citas, llegadas y disponibilidad
 */
public class Recepcion {
    
    private Database db;
    
    public Recepcion() {
        this.db = Database.getInstance();
    }
    
    // ========== MÉTODOS DEL DIAGRAMA ==========
    
    /**
     * Crea una nueva cita validando disponibilidad
     * @param paciente Paciente para la cita
     * @param odontologo Odontólogo que atenderá
     * @param horario Horario seleccionado
     * @return true si se creó exitosamente
     */
    public boolean crearCita(Paciente paciente, Odontologo odontologo, Horario horario) {
        if (paciente == null || odontologo == null || horario == null) {
            return false;
        }
        
        // Verificar que el horario esté disponible
        if (!horario.isDisponible()) {
            return false;
        }
        
        // Validar que no haya solapamiento
        boolean ocupado = db.getCitas().values().stream()
            .filter(c -> c.getEstado() != EstadoCita.CANCELADA)
            .anyMatch(c -> c.getFecha().equals(horario.getFecha()) 
                    && c.getHora().equals(horario.getHora()) 
                    && c.getOdontologo().getId() == odontologo.getId());
        
        if (ocupado) {
            return false;
        }
        
        // Crear la cita
        Cita nuevaCita = new Cita();
        nuevaCita.setId(db.generarProximoIdCita());
        nuevaCita.setPaciente(paciente);
        nuevaCita.setOdontologo(odontologo);
        nuevaCita.setFecha(horario.getFecha());
        nuevaCita.setHora(horario.getHora());
        nuevaCita.setMotivo("Consulta general");
        nuevaCita.setEstado(EstadoCita.PENDIENTE);
        
        // Guardar en la base de datos
        db.getCitas().put(nuevaCita.getId(), nuevaCita);
        
        // Marcar el horario como ocupado
        horario.marcarOcupado();
        
        return true;
    }
    
    /**
     * Sobrecarga del método crearCita con parámetros individuales
     */
    public boolean crearCita(Paciente paciente, Odontologo odontologo, LocalDate fecha, LocalTime hora, String motivo) {
        if (paciente == null || odontologo == null || fecha == null || hora == null) {
            return false;
        }
        
        // Validar fecha no pasada
        if (fecha.isBefore(LocalDate.now())) {
            return false;
        }
        
        // Validar solapamiento
        boolean ocupado = db.getCitas().values().stream()
            .filter(c -> c.getEstado() != EstadoCita.CANCELADA)
            .anyMatch(c -> c.getFecha().equals(fecha) 
                    && c.getHora().equals(hora) 
                    && c.getOdontologo().getId() == odontologo.getId());
        
        if (ocupado) {
            return false;
        }
        
        // Crear la cita
        Cita nuevaCita = new Cita();
        nuevaCita.setId(db.generarProximoIdCita());
        nuevaCita.setPaciente(paciente);
        nuevaCita.setOdontologo(odontologo);
        nuevaCita.setFecha(fecha);
        nuevaCita.setHora(hora);
        nuevaCita.setMotivo(motivo != null && !motivo.trim().isEmpty() ? motivo : "Consulta general");
        nuevaCita.setEstado(EstadoCita.PENDIENTE);
        
        db.getCitas().put(nuevaCita.getId(), nuevaCita);
        
        return true;
    }
    
    /**
     * Cancela una cita existente
     * @param cita Cita a cancelar
     */
    public void cancelarCita(Cita cita) {
        if (cita != null) {
            cita.cancelar();
            
            // Liberar el horario si existe
            Horario horario = buscarHorarioPorFechaHora(cita.getFecha(), cita.getHora());
            if (horario != null) {
                horario.marcarDisponible();
            }
        }
    }
    
    /**
     * Sobrecarga: Cancela una cita por ID
     */
    public boolean cancelarCita(int idCita) {
        Cita cita = db.getCitas().get(idCita);
        if (cita != null && cita.getEstado() != EstadoCita.CANCELADA) {
            cancelarCita(cita);
            return true;
        }
        return false;
    }
    
    /**
     * Registra la llegada de un paciente a su cita
     * @param cita Cita del paciente
     * @param horaLlegada Hora en que llegó
     * @return true si se registró exitosamente
     */
    public boolean registrarLlegadaPaciente(Cita cita, LocalTime horaLlegada) {
        if (cita == null || horaLlegada == null) {
            return false;
        }
        
        // Verificar que la cita sea de hoy
        if (!cita.getFecha().equals(LocalDate.now())) {
            return false;
        }
        
        cita.registrarLlegada(horaLlegada);
        
        // Si llegó más de 15 minutos tarde, cancelar automáticamente
        if (cita.calcularMinutosRetraso() > 15) {
            cita.setEstado(EstadoCita.CANCELADA);
            return false;
        }
        
        return true;
    }
    
    /**
     * Sobrecarga: Registra llegada por ID de cita
     */
    public boolean registrarLlegadaPaciente(int idCita, LocalTime horaLlegada) {
        Cita cita = db.getCitas().get(idCita);
        return registrarLlegadaPaciente(cita, horaLlegada);
    }
    
    /**
     * Busca horarios disponibles para un odontólogo en una fecha
     * @param odontologo Odontólogo a consultar
     * @return Lista de horarios disponibles
     */
    public List<Horario> buscarDisponibilidad(Odontologo odontologo) {
        if (odontologo == null) {
            return new ArrayList<>();
        }
        
        // Obtener todos los horarios del odontólogo que estén disponibles
        return db.getHorarios().values().stream()
                .filter(h -> h.getOdontologo() != null 
                        && h.getOdontologo().getId() == odontologo.getId())
                .filter(Horario::isDisponible)
                .filter(h -> !h.getFecha().isBefore(LocalDate.now()))
                .sorted((h1, h2) -> {
                    int fechaComp = h1.getFecha().compareTo(h2.getFecha());
                    if (fechaComp != 0) return fechaComp;
                    return h1.getHora().compareTo(h2.getHora());
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Busca horarios disponibles por fecha y odontólogo
     */
    public List<Horario> buscarDisponibilidad(LocalDate fecha, int idOdontologo) {
        if (fecha == null) {
            return new ArrayList<>();
        }
        
        Odontologo odontologo = db.getOdontologos().get(idOdontologo);
        if (odontologo == null) {
            return new ArrayList<>();
        }
        
        return db.getHorarios().values().stream()
                .filter(h -> h.getFecha().equals(fecha))
                .filter(h -> h.getOdontologo() != null 
                        && h.getOdontologo().getId() == idOdontologo)
                .filter(Horario::isDisponible)
                .sorted((h1, h2) -> h1.getHora().compareTo(h2.getHora()))
                .collect(Collectors.toList());
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    /**
     * Busca un horario específico por fecha y hora
     */
    private Horario buscarHorarioPorFechaHora(LocalDate fecha, LocalTime hora) {
        return db.getHorarios().values().stream()
                .filter(h -> h.getFecha().equals(fecha) && h.getHora().equals(hora))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Obtiene todas las citas del día actual
     */
    public List<Cita> obtenerCitasDelDia() {
        LocalDate hoy = LocalDate.now();
        return db.getCitas().values().stream()
                .filter(c -> c.getFecha().equals(hoy))
                .filter(c -> c.getEstado() != EstadoCita.CANCELADA)
                .sorted((c1, c2) -> c1.getHora().compareTo(c2.getHora()))
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene citas pendientes que requieren confirmación
     */
    public List<Cita> obtenerCitasPendientesConfirmacion() {
        return db.getCitas().values().stream()
                .filter(c -> c.getEstado() == EstadoCita.PENDIENTE)
                .filter(c -> !c.getFecha().isBefore(LocalDate.now()))
                .sorted((c1, c2) -> {
                    int fechaComp = c1.getFecha().compareTo(c2.getFecha());
                    if (fechaComp != 0) return fechaComp;
                    return c1.getHora().compareTo(c2.getHora());
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Verifica si un paciente tiene citas programadas
     */
    public boolean tieneCitasProgramadas(Paciente paciente) {
        if (paciente == null) return false;
        
        return db.getCitas().values().stream()
                .anyMatch(c -> c.getPaciente() != null 
                        && c.getPaciente().getId() == paciente.getId()
                        && c.getEstado() != EstadoCita.CANCELADA
                        && c.getEstado() != EstadoCita.ATENDIDA
                        && !c.getFecha().isBefore(LocalDate.now()));
    }
    
    /**
     * Genera un reporte de citas por estado
     */
    public String generarReporteCitas() {
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
        
        return String.format(
            "REPORTE DE CITAS:\n" +
            "Pendientes: %d\n" +
            "Confirmadas: %d\n" +
            "Atendidas: %d\n" +
            "Canceladas: %d\n" +
            "Ausentes: %d\n" +
            "Total: %d",
            pendientes, confirmadas, atendidas, canceladas, ausentes,
            db.getCitas().size()
        );
    }
    
    @Override
    public String toString() {
        return "Recepcion{" +
                "citasRegistradas=" + db.getCitas().size() +
                ", horariosDisponibles=" + db.getHorarios().values().stream()
                        .filter(Horario::isDisponible).count() +
                '}';
    }
}

