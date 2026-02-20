package controller;

import model.Recepcion;
import model.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controlador de Recepción
 * Actúa como intermediario entre la vista y el modelo Recepcion
 * OPTIMIZADO: Con auto-guardado y generación automática de facturas
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
            System.err.println("Paciente u Odontólogo no encontrado");
            return false;
        }
        
        boolean exito = recepcion.crearCita(paciente, odontologo, fecha, hora, motivo);
        
        if (exito) {
            // Obtener la cita recién creada (En el Map)
            Cita citaCreada = db.getCitas().values().stream()
                .reduce((primera, segunda) -> segunda) // Obtener la última
                .orElse(null);
            
            if (citaCreada != null) {
                // Crear factura con monto inicial de 0.0
                crearFacturaParaCita(citaCreada, paciente, 0.0);
                System.out.println("Cita " + citaCreada.getId() + " creada con factura asociada");
            }
            
            db.guardarDatos(); // GUARDAR AUTOMÁTICAMENTE
            System.out.println("Datos guardados correctamente");
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
            System.err.println("Paciente u Odontólogo no encontrado");
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
                System.out.println("Cita " + citaCreada.getId() + " creada con monto $" + monto);
            }
            
            db.guardarDatos();
            System.out.println("Datos guardados correctamente");
        }
        
        return exito;
    }
    
    public boolean actualizarMontoFactura(int idCita, double nuevoMonto) {
        // Usar el nuevo método de Database
        Factura factura = db.getFacturaPorIdCita(idCita);
        
        if (factura == null) {
            System.err.println("No se encontró factura para la cita " + idCita);
            return false;
        }
        
        factura.setMonto(nuevoMonto);
        db.guardarDatos();
        System.out.println("Monto de factura actualizado a $" + nuevoMonto);
        return true;
    }
    
    /**
     * OPTIMIZADO: Obtiene la factura asociada a una cita
     */
    public Factura obtenerFacturaPorCita(int idCita) {
        return db.getFacturaPorIdCita(idCita);
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
        System.out.println("Factura " + factura.getId() + " creada para cita #" + cita.getId());
    }
    
    
    /**
     * Crea una cita usando un horario predefinido
     */
    public boolean crearCitaConHorario(int idPaciente, int idOdontologo, int idHorario) {
        Paciente paciente = db.getPacientes().get(idPaciente);
        Odontologo odontologo = db.getOdontologos().get(idOdontologo);
        Horario horario = db.getHorarios().get(idHorario);
        
        if (paciente == null || odontologo == null || horario == null) {
            System.err.println("Datos incompletos para crear cita con horario");
            return false;
        }
        
        boolean exito = recepcion.crearCita(paciente, odontologo, horario);
        
        if (exito) {
            Cita citaCreada = db.getCitas().values().stream()
                .reduce((primera, segunda) -> segunda)
                .orElse(null);
            
            if (citaCreada != null) {
                crearFacturaParaCita(citaCreada, paciente, 0.0);
                System.out.println("Cita " + citaCreada.getId() + " creada con horario " + idHorario);
            }
            
            db.guardarDatos();
            System.out.println("Datos guardados correctamente");
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
            System.out.println("Cita " + idCita + " cancelada y guardada");
        }
        
        return exito;
    }
    
    /**
     * Libera el horario asociado a una cita
     */
    private void liberarHorarioAsociado(Cita cita) {
        if (cita == null || cita.getOdontologo() == null) return;
        
        db.getHorarios().values().stream()
                .filter(h -> h.getFecha().equals(cita.getFecha()) 
                        && h.getHora().equals(cita.getHora())
                        && h.getOdontologo().getId() == cita.getOdontologo().getId())
                .findFirst()
                .ifPresent(horario -> {
                    horario.marcarDisponible();
                    System.out.println("Horario liberado: " + cita.getFecha() + " " + cita.getHora());
                });
    }
    
    /**
     * OPTIMIZADO: Elimina una cita completamente del sistema
     */
    public boolean eliminarCitaCompletamente(int idCita) {
        Cita cita = db.getCitas().get(idCita);
        
        if (cita == null) {
            System.err.println("Cita " + idCita + " no encontrada");
            return false;
        }
        
        System.out.println("Eliminando cita " + idCita);
        
        // 1. Eliminar factura asociada si existe
        Factura factura = obtenerFacturaPorCita(idCita);
        if (factura != null) {
            db.getFacturas().remove(factura.getId());
            System.out.println("Factura " + factura.getId() + " eliminada");
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
            System.out.println("Cita " + idCita + " eliminada completamente");
            System.out.println("NextCitaId ahora es: " + db.getNextCitaId());
        }
        
        return eliminada;
    }
    
    /**
     * Reprograma una cita existente
     */
    public boolean reprogramarCita(int idCita, LocalDate nuevaFecha, LocalTime nuevaHora) {
        Cita cita = db.getCitas().get(idCita);
        
        if (cita == null || cita.getEstado() == EstadoCita.CANCELADA) {
            System.err.println("No se puede reprogramar la cita #" + idCita);
            return false;
        }
        
        if (nuevaFecha.isBefore(LocalDate.now())) {
            System.err.println("No se puede programar en una fecha pasada");
            return false;
        }
        
        boolean ocupado = db.getCitas().values().stream()
                .filter(c -> c.getId() != idCita)
                .filter(c -> c.getEstado() != EstadoCita.CANCELADA)
                .anyMatch(c -> c.getFecha().equals(nuevaFecha) 
                        && c.getHora().equals(nuevaHora)
                        && c.getOdontologo().getId() == cita.getOdontologo().getId());
        
        if (ocupado) {
            System.err.println("Horario ya ocupado");
            return false;
        }
        
        cita.reprogramar(nuevaFecha, nuevaHora);
        db.guardarDatos();
        System.out.println("Cita " + idCita + " reprogramada y guardada");
        return true;
    }
    
    /**
     * Confirma una cita
     */
    public boolean confirmarCita(int idCita) {
        Cita cita = db.getCitas().get(idCita);
        
        if (cita == null) {
            System.err.println("Cita " + idCita + " no encontrada");
            return false;
        }
        
        boolean exito = cita.confirmar();
        
        if (exito) {
            db.guardarDatos();
            System.out.println("Cita " + idCita + " confirmada y guardada");
        }
        
        return exito;
    }

    /**
     * Finaliza una cita, cambiando su estado a FINALIZADO
     */
    public boolean finalizarCita(int idCita) {
        boolean exito = recepcion.finalizarCita(idCita);
        
        if (exito) {
            db.guardarDatos();
            System.out.println("Cita " + idCita + " finalizada y guardada");
        }
        
        return exito;
    }
   
        
    
    // ========== BÚSQUEDA Y CONSULTAS ==========
    
    public List<Horario> buscarDisponibilidad(int idOdontologo) {
        Odontologo odontologo = db.getOdontologos().get(idOdontologo);
        
        if (odontologo == null) {
            System.err.println("Odontólogo " + idOdontologo + " no encontrado");
            return List.of();
        }
        
        return recepcion.buscarDisponibilidad(odontologo);
    }
    
    public List<Horario> buscarDisponibilidad(LocalDate fecha, int idOdontologo) {
        return recepcion.buscarDisponibilidad(fecha, idOdontologo);
    }
    
    /**
     * OPTIMIZADO: Obtiene todas las citas (sin recarga innecesaria)
     */
    public List<Cita> obtenerTodasLasCitas() {
        return List.copyOf(db.getCitas().values());
    }
    
    
    public List<Cita> obtenerCitasPendientes() {
        return recepcion.obtenerCitasPendientesConfirmacion();
    }
    
    /**
     * NUEVO: Obtiene una cita específica por su ID
     */
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
    

    
    // ========== REPORTES ==========
    
    public String generarReporteCitas() {
        return recepcion.generarReporteCitas();
    }
    
    }
