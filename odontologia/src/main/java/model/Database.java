package model;

import util.DataPersistence;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Database {
    private static Database instance;
    private Map<Integer, Paciente> pacientes = new HashMap<>();
    private Map<Integer, Odontologo> odontologos = new HashMap<>();
    private Map<Integer, Cita> citas = new HashMap<>();
    private Map<Integer, Factura> facturas = new HashMap<>();
    private Map<Integer, Horario> horarios = new HashMap<>();
    private int nextPacienteId = 103; 
    private int nextCitaId = 1;
    private int nextFacturaId = 1;
    private int nextHorarioId = 1;

    private Database() {
        cargarDatos();
    }

    public static synchronized Database getInstance() {
        if (instance == null) instance = new Database();
        return instance;
    }
    
    /**
     * Carga los datos desde archivos JSON o genera datos de prueba si no existen
     */
    private void cargarDatos() {
    if (DataPersistence.existenDatosGuardados()) {
        // Cargar desde archivos
        System.out.println("📂 Cargando datos guardados...");
        pacientes = DataPersistence.cargarPacientes();
        odontologos = DataPersistence.cargarOdontologos();
        citas = DataPersistence.cargarCitas();
        facturas = DataPersistence.cargarFacturas();
        horarios = DataPersistence.cargarHorarios();
        
        // Cargar contadores
        Map<String, Integer> counters = DataPersistence.cargarContadores();
        nextCitaId = counters.getOrDefault("nextCitaId", 1);
        nextFacturaId = counters.getOrDefault("nextFacturaId", 1);
        nextHorarioId = counters.getOrDefault("nextHorarioId", 1);
        nextPacienteId = counters.getOrDefault("nextPacienteId", 103); // AGREGAR ESTA LÍNEA
        
        System.out.println("✅ Datos cargados exitosamente");
        System.out.println("   - Pacientes: " + pacientes.size());
        System.out.println("   - Odontólogos: " + odontologos.size());
        System.out.println("   - Citas: " + citas.size());
        System.out.println("   - Horarios: " + horarios.size());
    } else {
        // Primera ejecución - generar datos de prueba
        System.out.println("🆕 Primera ejecución - generando datos de prueba...");
        poblarDatosFalsos();
        guardarDatos(); // Guardar los datos iniciales
        System.out.println("✅ Datos de prueba generados y guardados");
    }
}


    
    /**
     * Guarda todos los datos en archivos JSON
     */
    public void guardarDatos() {
        DataPersistence.guardarTodo(this);
    }

    private void poblarDatosFalsos() {
        // 1. Crear Odontólogos
        Odontologo o1 = new Odontologo();
        o1.setId(1); 
        o1.setNombre("Dr. Gregory House"); 
        o1.setEspecialidad("Cirugía Maxilofacial");
        o1.setTelefono(88881111);
        
        Odontologo o2 = new Odontologo();
        o2.setId(2); 
        o2.setNombre("Dra. Meredith Grey"); 
        o2.setEspecialidad("Ortodoncia");
        o2.setTelefono(88882222);
        
        odontologos.put(o1.getId(), o1);
        odontologos.put(o2.getId(), o2);

        // 2. Crear Pacientes
        Paciente p1 = new Paciente();
        p1.setId(101); 
        p1.setNombre("Juan Pérez"); 
        p1.setTelefono(77770001);
        p1.setNumeroExpediente("EXP-001");
        
        Paciente p2 = new Paciente();
        p2.setId(102); 
        p2.setNombre("María García"); 
        p2.setTelefono(77770002);
        p2.setNumeroExpediente("EXP-002");
        
        pacientes.put(p1.getId(), p1);
        pacientes.put(p2.getId(), p2);

        // 3. Crear Horarios disponibles
        LocalDate hoy = LocalDate.now();
        
        // Horarios para Dr. House (próximos 3 días)
        for (int dia = 1; dia <= 3; dia++) {
            LocalDate fecha = hoy.plusDays(dia);
            
            // Horarios de mañana: 9:00, 10:00, 11:00
            crearHorario(fecha, LocalTime.of(9, 0), o1);
            crearHorario(fecha, LocalTime.of(10, 0), o1);
            crearHorario(fecha, LocalTime.of(11, 0), o1);
            
            // Horarios de tarde: 14:00, 15:00, 16:00
            crearHorario(fecha, LocalTime.of(14, 0), o1);
            crearHorario(fecha, LocalTime.of(15, 0), o1);
            crearHorario(fecha, LocalTime.of(16, 0), o1);
        }
        
        // Horarios para Dra. Grey (próximos 3 días)
        for (int dia = 1; dia <= 3; dia++) {
            LocalDate fecha = hoy.plusDays(dia);
            
            // Horarios de mañana: 8:00, 9:30, 11:00
            crearHorario(fecha, LocalTime.of(8, 0), o2);
            crearHorario(fecha, LocalTime.of(9, 30), o2);
            crearHorario(fecha, LocalTime.of(11, 0), o2);
            
            // Horarios de tarde: 13:00, 14:30, 16:00
            crearHorario(fecha, LocalTime.of(13, 0), o2);
            crearHorario(fecha, LocalTime.of(14, 30), o2);
            crearHorario(fecha, LocalTime.of(16, 0), o2);
        }

        // 4. Crear Citas (algunas ocupan horarios)
        // Cita 1
        Cita c1 = new Cita();
        c1.setId(nextCitaId++);
        c1.setPaciente(p1);
        c1.setOdontologo(o1);
        c1.setFecha(hoy.plusDays(1));
        c1.setHora(LocalTime.of(9, 0));
        c1.setMotivo("Limpieza profunda");
        c1.setEstado(EstadoCita.PENDIENTE);
        citas.put(c1.getId(), c1);
        // Marcar horario como ocupado
        marcarHorarioOcupado(hoy.plusDays(1), LocalTime.of(9, 0), o1);
        
        // Cita 2
        Cita c2 = new Cita();
        c2.setId(nextCitaId++);
        c2.setPaciente(p2);
        c2.setOdontologo(o1);
        c2.setFecha(hoy.plusDays(1));
        c2.setHora(LocalTime.of(10, 0));
        c2.setMotivo("Dolor de muela");
        c2.setEstado(EstadoCita.CONFIRMADA);
        citas.put(c2.getId(), c2);
        // Marcar horario como ocupado
        marcarHorarioOcupado(hoy.plusDays(1), LocalTime.of(10, 0), o1);
        
        // Cita 3
        Cita c3 = new Cita();
        c3.setId(nextCitaId++);
        c3.setPaciente(p1);
        c3.setOdontologo(o2);
        c3.setFecha(hoy.plusDays(2));
        c3.setHora(LocalTime.of(14, 30));
        c3.setMotivo("Ajuste de Brackets");
        c3.setEstado(EstadoCita.PENDIENTE);
        citas.put(c3.getId(), c3);
        // Marcar horario como ocupado
        marcarHorarioOcupado(hoy.plusDays(2), LocalTime.of(14, 30), o2);

        // 5. Crear Facturas
        // Factura 1 para Cita 1
        Factura f1 = new Factura();
        f1.setId(nextFacturaId++);
        f1.setMonto(150.00);
        f1.setCita(c1);
        f1.setPaciente(p1);
        facturas.put(f1.getId(), f1);
        
        // Factura 2 para Cita 2
        Factura f2 = new Factura();
        f2.setId(nextFacturaId++);
        f2.setMonto(200.00);
        f2.setCita(c2);
        f2.setPaciente(p2);
        facturas.put(f2.getId(), f2);
        
        // Factura 3 para Cita 3
        Factura f3 = new Factura();
        f3.setId(nextFacturaId++);
        f3.setMonto(300.00);
        f3.setCita(c3);
        f3.setPaciente(p1);
        facturas.put(f3.getId(), f3);
          nextPacienteId = 103;
    }

    public void recargarDesdeArchivos() {
    if (DataPersistence.existenDatosGuardados()) {
        pacientes = DataPersistence.cargarPacientes();
        odontologos = DataPersistence.cargarOdontologos();
        citas = DataPersistence.cargarCitas();
        facturas = DataPersistence.cargarFacturas();
        horarios = DataPersistence.cargarHorarios();

        Map<String, Integer> counters = DataPersistence.cargarContadores();
        nextCitaId = counters.getOrDefault("nextCitaId", nextCitaId);
        nextFacturaId = counters.getOrDefault("nextFacturaId", nextFacturaId);
        nextHorarioId = counters.getOrDefault("nextHorarioId", nextHorarioId);
        nextPacienteId = counters.getOrDefault("nextPacienteId", nextPacienteId); // AGREGAR ESTA LÍNEA
    }
}

public void recalcularNextCitaId() {
    if (citas.isEmpty()) {
        nextCitaId = 1;
        System.out.println("🔄 NextCitaId establecido a 1 (no hay citas)");
    } else {
        int maxId = citas.keySet().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        nextCitaId = maxId + 1;
        System.out.println("🔄 NextCitaId recalculado: " + nextCitaId);
    }
}

public boolean eliminarCita(int idCita) {
    Cita cita = citas.remove(idCita);
    if (cita != null) {
        // Liberar el horario asociado
        liberarHorario(cita.getFecha(), cita.getHora(), cita.getOdontologo());
        
        // Eliminar factura asociada si existe
        eliminarFacturaPorCita(idCita);
        recalcularNextCitaId();
        guardarDatos(); // Guardar cambios
        return true;
    }
    return false;
}

private void liberarHorario(LocalDate fecha, LocalTime hora, Odontologo odontologo) {
    horarios.values().stream()
            .filter(h -> h.getFecha().equals(fecha) 
                    && h.getHora().equals(hora)
                    && h.getOdontologo().getId() == odontologo.getId())
            .findFirst()
            .ifPresent(Horario::marcarDisponible);
}

private void eliminarFacturaPorCita(int idCita) {
    facturas.entrySet().removeIf(entry -> 
        entry.getValue().getCita() != null && 
        entry.getValue().getCita().getId() == idCita
    );
}



    


    // ========== MÉTODOS AUXILIARES PARA HORARIOS ==========
    
    private void crearHorario(LocalDate fecha, LocalTime hora, Odontologo odontologo) {
        Horario h = new Horario();
        h.setId(nextHorarioId++);
        h.setFecha(fecha);
        h.setHora(hora);
        h.setOdontologo(odontologo);
        h.setDisponible(true);
        horarios.put(h.getId(), h);
    }
    
    private void marcarHorarioOcupado(LocalDate fecha, LocalTime hora, Odontologo odontologo) {
        horarios.values().stream()
                .filter(h -> h.getFecha().equals(fecha) 
                        && h.getHora().equals(hora)
                        && h.getOdontologo().getId() == odontologo.getId())
                .findFirst()
                .ifPresent(Horario::marcarOcupado);
    }

    // ========== GETTERS Y MÉTODOS DE ACCESO ==========
    
  
    public Map<Integer, Factura> getFacturas() {
        return facturas;
    }
    
    public Factura getFacturaPorIdCita(int idCita) {
    return facturas.values().stream()
        .filter(f -> f.getCita() != null && f.getCita().getId() == idCita)
        .findFirst()
        .orElse(null);
}
    
    public Map<Integer, Cita> getCitas() { 
        return citas; 
    }
    
    public Map<Integer, Horario> getHorarios() {
        return horarios;
    }
    
    public int generarProximoIdCita() { 
        return nextCitaId++; 
    }
    
    public int generarProximoIdHorario() {
        return nextHorarioId++;
    }
    
    public int generarProximoIdFactura() {
        return nextFacturaId++;
    }
    public int generarProximoIdPaciente() {
    return nextPacienteId++;
}
public int getNextPacienteId() {
    return nextPacienteId;
}

    public Map<Integer, Paciente> getPacientes() { 
        return pacientes; 
    }
    
    public Map<Integer, Odontologo> getOdontologos() { 
        return odontologos; 
    }
    
    // ========== MÉTODOS PARA OBTENER CONTADORES (usados por DataPersistence) ==========
    
    public int getNextCitaId() {
        return nextCitaId;
    }
    
    public int getNextFacturaId() {
        return nextFacturaId;
    }
    
    public int getNextHorarioId() {
        return nextHorarioId;
    }
}