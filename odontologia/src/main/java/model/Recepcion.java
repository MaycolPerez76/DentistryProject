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
        
        // Validar que ya este ocupada la cita
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
              else if (cita.getEstado() == EstadoCita.FINALIZADO) {
               System.out.println("No se puede eliminar una cita finalizada");
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
        else if (cita.getEstado() == EstadoCita.PENDIENTE) {
            cancelarCita(cita);
            return true;    
        }
        return false;
    }
    
    
    //Se finaliza desde el metodo de la clase Cita donde se almacenaran las citas
    public void finalizarCita(Cita cita) {
       if (cita != null) {
           cita.finalizar();
       }
    }

    /**
     * Finaliza una cita confirmada, marcándola como FINALIZADO
     * @param idCita ID de la cita a finalizar
     * @return true si se finalizó exitosamente
     */
   public boolean finalizarCita(int idCita) {
    Cita cita = db.getCitas().get(idCita);

    if (cita == null) {
        return false;
    }

    if (cita.getEstado() != EstadoCita.CONFIRMADA) {
        return false;
    }

    cita.finalizar();
    return true;
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
    
    
    @Override
    public String toString() {
        return "Recepcion{" +
                "citasRegistradas=" + db.getCitas().size() +
                ", horariosDisponibles=" + db.getHorarios().values().stream()
                        .filter(Horario::isDisponible).count() +
                '}';
    }
}

