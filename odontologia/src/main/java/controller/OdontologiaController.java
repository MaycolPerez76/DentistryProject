package controller;

import model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la gestión de Odontólogos
 * 
 * RESPONSABILIDADES:
 * - Coordinar operaciones CRUD de odontólogos entre la vista y el modelo
 * - Validar datos antes de persistirlos
 * - Consultar y modificar la base de datos
 * - Gestionar la persistencia automática en JSON
 * 
 * FLUJO DE DATOS:
 * OdontologiaView → OdontologiaController → Database → DataPersistence → JSON
 * 
 * @author Sistema de Gestión Odontológica
 * @version 2.0
 */
public class OdontologiaController {
    
    private final Database db;
    
    /**
     * Constructor - Inicializa el controlador con la instancia de Database
     */
    public OdontologiaController() {
        this.db = Database.getInstance();
    }
    
    /**
     * Obtiene todos los odontólogos registrados en el sistema
     * 
     * @return Lista de todos los odontólogos
     */
    public List<Odontologo> obtenerTodosLosOdontologos() {
        // Recargar datos desde archivos para asegurar sincronización
        db.recargarDesdeArchivos();
        
        // Convertir el mapa a lista
        return new ArrayList<>(db.getOdontologos().values());
    }
    
    /**
     * Busca un odontólogo por su ID
     * 
     * @param id ID del odontólogo a buscar
     * @return El odontólogo encontrado o null si no existe
     */
    public Odontologo obtenerOdontologoPorId(int id) {
        return db.getOdontologos().get(id);
    }
    
    /**
     * Agrega un nuevo odontólogo al sistema (versión simplificada sin especialidad)
     * La especialidad se establecerá como "General" por defecto
     * 
     * VALIDACIONES:
     * - Verifica que el número de colegiado no esté duplicado
     * - Asigna automáticamente un ID único
     * - Guarda automáticamente en JSON
     * 
     * @param nombre Nombre completo del odontólogo
     * @param telefono Número de teléfono
     * @param numeroColegiado Número de colegiado único
     * @return true si se registró exitosamente, false si el número de colegiado ya existe
     */
    public boolean agregarOdontologoNuevo(String nombre, int telefono, int numeroColegiado) {
        // Llamar al método completo con especialidad por defecto
        return registrarNuevoOdontologo(nombre, telefono, "General", numeroColegiado);
    }
    
    /**
     * Registra un nuevo odontólogo en el sistema
     * 
     * VALIDACIONES:
     * - Verifica que el número de colegiado no esté duplicado
     * - Asigna automáticamente un ID único
     * - Guarda automáticamente en JSON
     * 
     * @param nombre Nombre completo del odontólogo
     * @param telefono Número de teléfono
     * @param especialidad Especialidad del odontólogo
     * @param numeroColegiado Número de colegiado único
     * @return true si se registró exitosamente, false si el número de colegiado ya existe
     */
    public boolean registrarNuevoOdontologo(String nombre, int telefono, 
                                           String especialidad, int numeroColegiado) {
        
        // Validar que el número de colegiado no esté duplicado
        if (existeNumeroColegiado(numeroColegiado, -1)) {
            System.err.println("❌ Error: El número de colegiado " + numeroColegiado + " ya está registrado");
            return false;
        }
        
        // Crear nuevo odontólogo
        Odontologo nuevoOdontologo = new Odontologo();
        
        // Generar ID automático
        int nuevoId = generarNuevoIdOdontologo();
        nuevoOdontologo.setId(nuevoId);
        
        // Asignar datos
        nuevoOdontologo.setNombre(nombre);
        nuevoOdontologo.setTelefono(telefono);
        nuevoOdontologo.setEspecialidad(especialidad);
        nuevoOdontologo.setNumeroColegiado(numeroColegiado);
        
        // Guardar en la base de datos
        db.getOdontologos().put(nuevoId, nuevoOdontologo);
        
        // Persistir en JSON automáticamente
        db.guardarDatos();
        
        System.out.println("✅ Odontólogo registrado exitosamente:");
        System.out.println("   - ID: " + nuevoId);
        System.out.println("   - Nombre: " + nombre);
        System.out.println("   - Especialidad: " + especialidad);
        System.out.println("   - Número Colegiado: " + numeroColegiado);
        
        return true;
    }
    
    /**
     * Actualiza los datos de un odontólogo existente
     * 
     * VALIDACIONES:
     * - Verifica que el odontólogo exista
     * - Valida que el nuevo número de colegiado no esté duplicado (si cambió)
     * - Guarda automáticamente en JSON
     * 
     * @param id ID del odontólogo a actualizar
     * @param nombre Nuevo nombre
     * @param telefono Nuevo teléfono
     * @param especialidad Nueva especialidad
     * @param numeroColegiado Nuevo número de colegiado
     * @return true si se actualizó exitosamente, false si hay error
     */
    public boolean actualizarOdontologo(int id, String nombre, int telefono, 
                                       String especialidad, int numeroColegiado) {
        
        // Verificar que el odontólogo existe
        Odontologo odontologo = db.getOdontologos().get(id);
        if (odontologo == null) {
            System.err.println("❌ Error: No existe odontólogo con ID " + id);
            return false;
        }
        
        // Validar que el número de colegiado no esté duplicado (excepto si es el mismo)
        if (existeNumeroColegiado(numeroColegiado, id)) {
            System.err.println("❌ Error: El número de colegiado " + numeroColegiado + " ya está en uso");
            return false;
        }
        
        // Actualizar datos
        odontologo.setNombre(nombre);
        odontologo.setTelefono(telefono);
        odontologo.setEspecialidad(especialidad);
        odontologo.setNumeroColegiado(numeroColegiado);
        
        // Persistir cambios en JSON
        db.guardarDatos();
        
        System.out.println("✅ Odontólogo actualizado exitosamente:");
        System.out.println("   - ID: " + id);
        System.out.println("   - Nombre: " + nombre);
        System.out.println("   - Especialidad: " + especialidad);
        
        return true;
    }
    
    /**
     * Sobrecarga del método actualizarOdontologo para compatibilidad con OdontologiaView
     * (cuando solo se actualiza nombre, teléfono y "expediente" que sería el número de colegiado)
     * 
     * @param id ID del odontólogo
     * @param nombre Nuevo nombre
     * @param telefono Nuevo teléfono
     * @param numeroColegiadoStr Número de colegiado como String
     * @return true si se actualizó exitosamente
     */
    public boolean actualizarOdontologo(int id, String nombre, int telefono, String numeroColegiadoStr) {
        try {
            int numeroColegiado = Integer.parseInt(numeroColegiadoStr);
            
            // Obtener el odontólogo actual para mantener su especialidad
            Odontologo odontologoActual = db.getOdontologos().get(id);
            if (odontologoActual == null) {
                System.err.println("❌ Error: No existe odontólogo con ID " + id);
                return false;
            }
            
            String especialidadActual = odontologoActual.getEspecialidad();
            
            // Llamar al método completo manteniendo la especialidad actual
            return actualizarOdontologo(id, nombre, telefono, especialidadActual, numeroColegiado);
            
        } catch (NumberFormatException e) {
            System.err.println("❌ Error: El número de colegiado debe ser un número válido");
            return false;
        }
    }
    
    /**
     * Elimina un odontólogo del sistema
     * 
     * PRECAUCIONES:
     * - Verifica que no tenga citas asignadas antes de eliminar
     * - Guarda automáticamente en JSON
     * 
     * @param id ID del odontólogo a eliminar
     * @return true si se eliminó exitosamente, false si tiene citas asignadas o no existe
     */
    public boolean eliminarOdontologo(int id) {
        // Verificar que el odontólogo existe
        Odontologo odontologo = db.getOdontologos().get(id);
        if (odontologo == null) {
            System.err.println("❌ Error: No existe odontólogo con ID " + id);
            return false;
        }
        
        // Verificar que no tenga citas asignadas
        boolean tieneCitas = db.getCitas().values().stream()
                .anyMatch(cita -> cita.getOdontologo().getId() == id);
        
        if (tieneCitas) {
            System.err.println("❌ Error: No se puede eliminar el odontólogo porque tiene citas asignadas");
            return false;
        }
        
        // Verificar que no tenga horarios asignados
        boolean tieneHorarios = db.getHorarios().values().stream()
                .anyMatch(horario -> horario.getOdontologo().getId() == id);
        
        if (tieneHorarios) {
            // Eliminar horarios asociados
            db.getHorarios().entrySet()
                    .removeIf(entry -> entry.getValue().getOdontologo().getId() == id);
            System.out.println("⚠️  Horarios del odontólogo eliminados");
        }
        
        // Eliminar odontólogo
        db.getOdontologos().remove(id);
        
        // Persistir cambios en JSON
        db.guardarDatos();
        
        System.out.println("✅ Odontólogo eliminado exitosamente (ID: " + id + ")");
        
        return true;
    }
    
    /**
     * Busca odontólogos por especialidad
     * 
     * @param especialidad Especialidad a buscar
     * @return Lista de odontólogos con esa especialidad
     */
    public List<Odontologo> buscarPorEspecialidad(String especialidad) {
        List<Odontologo> resultado = new ArrayList<>();
        
        for (Odontologo o : db.getOdontologos().values()) {
            if (o.getEspecialidad() != null && 
                o.getEspecialidad().toLowerCase().contains(especialidad.toLowerCase())) {
                resultado.add(o);
            }
        }
        
        return resultado;
    }
    
    /**
     * Busca odontólogos por nombre
     * 
     * @param nombre Nombre a buscar (puede ser parcial)
     * @return Lista de odontólogos que coinciden con el nombre
     */
    public List<Odontologo> buscarPorNombre(String nombre) {
        List<Odontologo> resultado = new ArrayList<>();
        
        for (Odontologo o : db.getOdontologos().values()) {
            if (o.getNombre() != null && 
                o.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                resultado.add(o);
            }
        }
        
        return resultado;
    }
    
    /**
     * Obtiene el total de odontólogos registrados
     * 
     * @return Número total de odontólogos
     */
    public int obtenerTotalOdontologos() {
        return db.getOdontologos().size();
    }
    
    /**
     * Obtiene las especialidades únicas disponibles
     * 
     * @return Lista de especialidades sin duplicados
     */
    public List<String> obtenerEspecialidades() {
        List<String> especialidades = new ArrayList<>();
        
        for (Odontologo o : db.getOdontologos().values()) {
            String esp = o.getEspecialidad();
            if (esp != null && !esp.trim().isEmpty() && !especialidades.contains(esp)) {
                especialidades.add(esp);
            }
        }
        
        return especialidades;
    }
    
    // ========== MÉTODOS AUXILIARES PRIVADOS ==========
    
    /**
     * Verifica si ya existe un número de colegiado en el sistema
     * 
     * @param numeroColegiado Número de colegiado a verificar
     * @param idExcluir ID a excluir de la búsqueda (útil para actualizaciones)
     * @return true si el número de colegiado ya existe, false si está disponible
     */
    private boolean existeNumeroColegiado(int numeroColegiado, int idExcluir) {
        for (Odontologo o : db.getOdontologos().values()) {
            // Excluir el ID especificado (para no validar contra sí mismo en actualizaciones)
            if (o.getId() != idExcluir && o.getNumeroColegiado() == numeroColegiado) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Genera un nuevo ID único para un odontólogo
     * Busca el ID más alto y le suma 1
     * 
     * @return Nuevo ID disponible
     */
    private int generarNuevoIdOdontologo() {
        if (db.getOdontologos().isEmpty()) {
            return 1;
        }
        
        // Encontrar el ID máximo actual
        int maxId = db.getOdontologos().keySet().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        
        return maxId + 1;
    }
    
    /**
     * Recarga los datos desde los archivos JSON
     * Útil cuando se necesita sincronizar con cambios externos
     */
    public void recargarDatos() {
        db.recargarDesdeArchivos();
        System.out.println("📂 Datos de odontólogos recargados desde archivos");
    }
}