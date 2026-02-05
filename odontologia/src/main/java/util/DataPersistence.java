package util;

import model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Clase para manejar la persistencia de datos en archivos JSON
 * Guarda y carga: Citas, Pacientes, Odontólogos, Horarios y Facturas
 */
public class DataPersistence {
    
    private static final String DATA_DIRECTORY = "data";
    private static final String CITAS_FILE = DATA_DIRECTORY + "/citas.json";
    private static final String PACIENTES_FILE = DATA_DIRECTORY + "/pacientes.json";
    private static final String ODONTOLOGOS_FILE = DATA_DIRECTORY + "/odontologos.json";
    private static final String HORARIOS_FILE = DATA_DIRECTORY + "/horarios.json";
    private static final String FACTURAS_FILE = DATA_DIRECTORY + "/facturas.json";
    private static final String COUNTERS_FILE = DATA_DIRECTORY + "/counters.json";
    
    private static Gson gson = CitaSerializer.getGson();
    
    /**
     * Inicializa el directorio de datos si no existe
     */
    public static void initializeDataDirectory() {
        File directory = new File(DATA_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
            System.out.println("✓ Directorio de datos creado: " + DATA_DIRECTORY);
        }
    }
    
    // ========== GUARDAR DATOS ==========
    
    /**
     * Guarda todas las citas en el archivo JSON
     */
    public static boolean guardarCitas(Map<Integer, Cita> citas) {
        return guardarDatos(CITAS_FILE, citas);
    }
    
    /**
     * Guarda todos los pacientes en el archivo JSON
     */
    public static boolean guardarPacientes(Map<Integer, Paciente> pacientes) {
        return guardarDatos(PACIENTES_FILE, pacientes);
    }
    
    /**
     * Guarda todos los odontólogos en el archivo JSON
     */
    public static boolean guardarOdontologos(Map<Integer, Odontologo> odontologos) {
        return guardarDatos(ODONTOLOGOS_FILE, odontologos);
    }
    
    /**
     * Guarda todos los horarios en el archivo JSON
     */
    public static boolean guardarHorarios(Map<Integer, Horario> horarios) {
        return guardarDatos(HORARIOS_FILE, horarios);
    }
    
    /**
     * Guarda todas las facturas en el archivo JSON
     */
    public static boolean guardarFacturas(Map<Integer, Factura> facturas) {
        return guardarDatos(FACTURAS_FILE, facturas);
    }
    
    /**
     * Guarda los contadores de IDs
     */
   
    public static boolean guardarContadores(int nextCitaId, int nextFacturaId, int nextHorarioId, int nextPacienteId) {
    Map<String, Integer> counters = new HashMap<>();
    counters.put("nextCitaId", nextCitaId);
    counters.put("nextFacturaId", nextFacturaId);
    counters.put("nextHorarioId", nextHorarioId);
    counters.put("nextPacienteId", nextPacienteId); // NUEVA LÍNEA
    return guardarDatos(COUNTERS_FILE, counters);
}


    
    /**
     * Guarda todos los datos de la base de datos
     */
    public static boolean guardarTodo(Database db) {
    initializeDataDirectory();
    
    boolean exito = true;
    exito &= guardarCitas(db.getCitas());
    exito &= guardarPacientes(db.getPacientes());
    exito &= guardarOdontologos(db.getOdontologos());
    exito &= guardarHorarios(db.getHorarios());
    exito &= guardarFacturas(db.getFacturas());
    exito &= guardarContadores(
        db.getNextCitaId(), 
        db.getNextFacturaId(), 
        db.getNextHorarioId(),
        db.getNextPacienteId()  // NUEVA LÍNEA
    );
    
    if (exito) {
        System.out.println("✓ Todos los datos guardados exitosamente");
    } else {
        System.err.println("✗ Error al guardar algunos datos");
    }
    
    return exito;
}

    
    // ========== CARGAR DATOS ==========
    
    /**
     * Carga las citas desde el archivo JSON
     */
    public static Map<Integer, Cita> cargarCitas() {
        Type type = new TypeToken<Map<Integer, Cita>>(){}.getType();
        Map<Integer, Cita> citas = cargarDatos(CITAS_FILE, type);
        return citas != null ? citas : new HashMap<>();
    }
    
    /**
     * Carga los pacientes desde el archivo JSON
     */
    public static Map<Integer, Paciente> cargarPacientes() {
        Type type = new TypeToken<Map<Integer, Paciente>>(){}.getType();
        Map<Integer, Paciente> pacientes = cargarDatos(PACIENTES_FILE, type);
        return pacientes != null ? pacientes : new HashMap<>();
    }
    
    /**
     * Carga los odontólogos desde el archivo JSON
     */
    public static Map<Integer, Odontologo> cargarOdontologos() {
        Type type = new TypeToken<Map<Integer, Odontologo>>(){}.getType();
        Map<Integer, Odontologo> odontologos = cargarDatos(ODONTOLOGOS_FILE, type);
        return odontologos != null ? odontologos : new HashMap<>();
    }
    
    /**
     * Carga los horarios desde el archivo JSON
     */
    public static Map<Integer, Horario> cargarHorarios() {
        Type type = new TypeToken<Map<Integer, Horario>>(){}.getType();
        Map<Integer, Horario> horarios = cargarDatos(HORARIOS_FILE, type);
        return horarios != null ? horarios : new HashMap<>();
    }
    
    /**
     * Carga las facturas desde el archivo JSON
     */
    public static Map<Integer, Factura> cargarFacturas() {
        Type type = new TypeToken<Map<Integer, Factura>>(){}.getType();
        Map<Integer, Factura> facturas = cargarDatos(FACTURAS_FILE, type);
        return facturas != null ? facturas : new HashMap<>();
    }
    
    /**
     * Carga los contadores de IDs
     */
    public static Map<String, Integer> cargarContadores() {
    Type type = new TypeToken<Map<String, Integer>>(){}.getType();
    Map<String, Integer> counters = cargarDatos(COUNTERS_FILE, type);
    
    if (counters == null) {
        counters = new HashMap<>();
        counters.put("nextCitaId", 1);
        counters.put("nextFacturaId", 1);
        counters.put("nextHorarioId", 1);
        counters.put("nextPacienteId", 103); // NUEVA LÍNEA
    }
    
    return counters;
}

    
    // ========== MÉTODOS AUXILIARES GENÉRICOS ==========
    
    /**
     * Método genérico para guardar datos en un archivo JSON
     */
    private static <T> boolean guardarDatos(String archivo, T datos) {
        try (Writer writer = new FileWriter(archivo)) {
            gson.toJson(datos, writer);
            System.out.println("✓ Datos guardados: " + archivo);
            return true;
        } catch (IOException e) {
            System.err.println("✗ Error al guardar " + archivo + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Método genérico para cargar datos desde un archivo JSON
     */
    private static <T> T cargarDatos(String archivo, Type type) {
        File file = new File(archivo);
        
        if (!file.exists()) {
            System.out.println("⚠ Archivo no encontrado: " + archivo + " (se creará al guardar)");
            return null;
        }
        
        try (Reader reader = new FileReader(archivo)) {
            T datos = gson.fromJson(reader, type);
            System.out.println("✓ Datos cargados: " + archivo);
            return datos;
        } catch (IOException e) {
            System.err.println("✗ Error al cargar " + archivo + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Verifica si existen archivos de datos guardados
     */
    public static boolean existenDatosGuardados() {
        return new File(CITAS_FILE).exists() || 
               new File(PACIENTES_FILE).exists() ||
               new File(ODONTOLOGOS_FILE).exists();
    }
    
    /**
     * Elimina todos los archivos de datos (útil para reset completo)
     */
    public static void eliminarTodosLosDatos() {
        File directory = new File(DATA_DIRECTORY);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.delete()) {
                        System.out.println("✓ Eliminado: " + file.getName());
                    }
                }
            }
            System.out.println("✓ Todos los datos eliminados");
        }
    }
}
