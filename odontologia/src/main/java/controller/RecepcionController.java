package controller;

import model.Recepcion;
import model.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controlador de Recepción
 * Actúa como intermediario entre la vista y el modelo Recepcion
 * CON AUTO-GUARDADO Y GENERACIÓN AUTOMÁTICA DE FACTURAS
 */
public class RecepcionController {
    
    private Database db = Database.getInstance();
    private Recepcion recepcion = new Recepcion();
   
    
    // ========== GESTIÓN DE CITAS ==========
    
    /**
     * Crea una nueva cita Y su factura asociada
     */
    public boolean crearCita(int idPaciente, int idOdontologo, LocalDate fecha, LocalTime hora, String motivo) {
        Paciente paciente = db.getPacientes().get(idPaciente);
        Odontologo odontologo = db.getOdontologos().get(idOdontologo);
        
        if (paciente == null || odontologo == null) {
            return false;
        }
        
        boolean exito = recepcion.crearCita(paciente, odontologo, fecha, hora, motivo);
        
        if (exito) {
            // Obtener la cita recién creada (la última en el Map)
            Cita citaCreada = db.getCitas().values().stream()
                .reduce((primera, segunda) -> segunda) // Obtener la última
                .orElse(null);
            
            if (citaCreada != null) {
                // Crear factura automáticamente con monto inicial de 0.00
                crearFacturaParaCita(citaCreada, paciente, 0.0);
            }
            
            db.guardarDatos(); // ⭐ GUARDAR AUTOMÁTICAMENTE
            System.out.println("✅ Cita creada, factura generada y datos guardados");
        }
        
        return exito;
    }
    
    /**
     * Crea una nueva cita Y su factura asociada (con monto específico)
     */
    public boolean crearCitaConMonto(int idPaciente, int idOdontologo, LocalDate fecha, 
                                      LocalTime hora, String motivo, double monto) {
        Paciente paciente = db.getPacientes().get(idPaciente);
        Odontologo odontologo = db.getOdontologos().get(idOdontologo);
        
        if (paciente == null || odontologo == null) {
            return false;
        }
        
        boolean exito = recepcion.crearCita(paciente, odontologo, fecha, hora, motivo);
        
        if (exito) {
            // Obtener la cita recién creada
            Cita citaCreada = db.getCitas().values().stream()
                .reduce((primera, segunda) -> segunda)
                .orElse(null);
            
            if (citaCreada != null) {
                // Crear factura con el monto especificado
                crearFacturaParaCita(citaCreada, paciente, monto);
            }
            
            db.guardarDatos();
            System.out.println("✅ Cita creada con monto $" + monto + ", factura generada y datos guardados");
        }
        
        return exito;
    }
    
    /**
     * Actualiza el monto de una factura existente
     */
    public boolean actualizarMontoFactura(int idCita, double nuevoMonto) {
        // Buscar la factura asociada a la cita
        Factura factura = db.getFacturas().values().stream()
            .filter(f -> f.getCita() != null && f.getCita().getId() == idCita)
            .findFirst()
            .orElse(null);
        
        if (factura == null) {
            System.err.println("✗ No se encontró factura para la cita #" + idCita);
            return false;
        }
        
        factura.setMonto(nuevoMonto);
        db.guardarDatos();
        System.out.println("✅ Monto de factura actualizado a $" + nuevoMonto);
        return true;
    }
    
    /**
     * Obtiene la factura asociada a una cita
     */
    public Factura obtenerFacturaPorCita(int idCita) {
        return db.getFacturas().values().stream()
            .filter(f -> f.getCita() != null && f.getCita().getId() == idCita)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Método privado para crear una factura
     */
    private void crearFacturaParaCita(Cita cita, Paciente paciente, double monto) {
        Factura factura = new Factura();
        factura.setId(db.generarProximoIdFactura());
        factura.setCita(cita);
        factura.setPaciente(paciente);
        factura.setMonto(monto);
        
        db.getFacturas().put(factura.getId(), factura);
        System.out.println("📄 Factura #" + factura.getId() + " creada para cita #" + cita.getId());
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
        
        boolean exito = recepcion.crearCita(paciente, odontologo, horario);
        
        if (exito) {
            Cita citaCreada = db.getCitas().values().stream()
                .reduce((primera, segunda) -> segunda)
                .orElse(null);
            
            if (citaCreada != null) {
                crearFacturaParaCita(citaCreada, paciente, 0.0);
            }
            
            db.guardarDatos();
            System.out.println("✅ Cita creada con horario, factura generada y datos guardados");
        }
        
        return exito;
    }
    
    /**
     * Cancela una cita por su ID
     */
    public boolean cancelarCita(int idCita) {
        boolean exito = recepcion.cancelarCita(idCita);
        
        if (exito) {
            db.guardarDatos();
            System.out.println("✅ Cita cancelada y guardada");
        }
        
        return exito;
    }
    private void liberarHorarioAsociado(Cita cita) {
    if (cita == null || cita.getOdontologo() == null) return;
    
    db.getHorarios().values().stream()
            .filter(h -> h.getFecha().equals(cita.getFecha()) 
                    && h.getHora().equals(cita.getHora())
                    && h.getOdontologo().getId() == cita.getOdontologo().getId())
            .findFirst()
            .ifPresent(horario -> {
                horario.marcarDisponible();
                System.out.println("🕒 Horario liberado: " + cita.getFecha() + " " + cita.getHora());
            });
}
    
    
   public boolean eliminarCitaCompletamente(int idCita) {
    Cita cita = db.getCitas().get(idCita);
    
    if (cita == null) {
        System.err.println("✗ Cita #" + idCita + " no encontrada");
        return false;
    }
    
    System.out.println("🗑️ Eliminando cita #" + idCita);
    
    // 1. Eliminar factura asociada si existe
    Factura factura = obtenerFacturaPorCita(idCita);
    if (factura != null) {
        db.getFacturas().remove(factura.getId());
        System.out.println("📄 Factura #" + factura.getId() + " eliminada");
    }
    
    // 2. Liberar horario si existe
    liberarHorarioAsociado(cita);
    
    // 3. Eliminar la cita del mapa
    boolean eliminada = db.getCitas().remove(idCita) != null;
    
    if (eliminada) {
        // 4. RECALCULAR EL CONTADOR DE CITAS
        db.recalcularNextCitaId();
        
        // 5. Guardar los cambios
        db.guardarDatos();
        System.out.println("✅ Cita #" + idCita + " eliminada. NextCitaId ahora es: " + db.getNextCitaId());
    }
    
    return eliminada;
}
    
    /**
     * Reprograma una cita existente
     */
    public boolean reprogramarCita(int idCita, LocalDate nuevaFecha, LocalTime nuevaHora) {
        Cita cita = db.getCitas().get(idCita);
        
        if (cita == null || cita.getEstado() == EstadoCita.CANCELADA) {
            return false;
        }
        
        if (nuevaFecha.isBefore(LocalDate.now())) {
            return false;
        }
        
        boolean ocupado = db.getCitas().values().stream()
                .filter(c -> c.getId() != idCita)
                .filter(c -> c.getEstado() != EstadoCita.CANCELADA)
                .anyMatch(c -> c.getFecha().equals(nuevaFecha) 
                        && c.getHora().equals(nuevaHora)
                        && c.getOdontologo().getId() == cita.getOdontologo().getId());
        
        if (ocupado) {
            return false;
        }
        
        cita.reprogramar(nuevaFecha, nuevaHora);
        db.guardarDatos();
        System.out.println("✅ Cita reprogramada y guardada");
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
        
        boolean exito = cita.confirmar();
        
        if (exito) {
            db.guardarDatos();
            System.out.println("✅ Cita confirmada y guardada");
        }
        
        return exito;
    }
    
    // ========== GESTIÓN DE LLEGADAS ==========
    
    public boolean registrarLlegada(int idCita) {
        boolean exito = recepcion.registrarLlegadaPaciente(idCita, LocalTime.now());
        
        if (exito) {
            db.guardarDatos();
            System.out.println("✅ Llegada registrada y guardada");
        }
        
        return exito;
    }
    
    public boolean registrarLlegada(int idCita, LocalTime horaLlegada) {
        boolean exito = recepcion.registrarLlegadaPaciente(idCita, horaLlegada);
        
        if (exito) {
            db.guardarDatos();
            System.out.println("✅ Llegada registrada y guardada");
        }
        
        return exito;
    }
    
    public boolean evaluarAsistencia(int idCita) {
        Cita cita = db.getCitas().get(idCita);
        
        if (cita == null) {
            return false;
        }
        
        cita.evaluarAsistencia();
        db.guardarDatos();
        System.out.println("✅ Asistencia evaluada y guardada");
        return true;
    }
    
    // ========== BÚSQUEDA Y CONSULTAS ==========
    
    public List<Horario> buscarDisponibilidad(int idOdontologo) {
        Odontologo odontologo = db.getOdontologos().get(idOdontologo);
        
        if (odontologo == null) {
            return List.of();
        }
        
        return recepcion.buscarDisponibilidad(odontologo);
    }
    
    public List<Horario> buscarDisponibilidad(LocalDate fecha, int idOdontologo) {
        return recepcion.buscarDisponibilidad(fecha, idOdontologo);
    }
    
    public List<Cita> obtenerTodasLasCitas() {
        db.recargarDesdeArchivos();
        return List.copyOf(db.getCitas().values());
    }
    
    public List<Cita> obtenerCitasDelDia() {
        return recepcion.obtenerCitasDelDia();
    }
    
    public List<Cita> obtenerCitasPendientes() {
        return recepcion.obtenerCitasPendientesConfirmacion();
    }
    
public Cita obtenerCitaPorId(int idCita) {
    return db.getCitas().get(idCita);
}
    
    public List<Cita> obtenerCitasPorPaciente(int idPaciente) {
        return db.getCitas().values().stream()
                .filter(c -> c.getPaciente() != null && c.getPaciente().getId() == idPaciente)
                .toList();
    }
    
    public List<Cita> obtenerCitasPorOdontologo(int idOdontologo) {
        return db.getCitas().values().stream()
                .filter(c -> c.getOdontologo() != null && c.getOdontologo().getId() == idOdontologo)
                .toList();
    }
    
    public boolean tieneCitasProgramadas(int idPaciente) {
        Paciente paciente = db.getPacientes().get(idPaciente);
        return recepcion.tieneCitasProgramadas(paciente);
    }
    
    // ========== REPORTES ==========
    
    public String generarReporteCitas() {
        return recepcion.generarReporteCitas();
    }
    
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
