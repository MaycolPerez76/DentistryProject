/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.Database;
import model.Paciente;
import java.util.List;
import java.util.Map;

/**
 * Controlador de Pacientes
 * Actúa como intermediario entre la vista y el modelo Database
 * CON AUTO-GUARDADO
 */
public class PacienteController {
    
    private Database db = Database.getInstance();
    
    /**
     * Agrega un nuevo paciente al sistema
     * @param nombre Nombre completo del paciente
     * @param telefono Número de teléfono
     * @param numeroExpediente Número de expediente único
     * @return true si se agregó exitosamente, false si ya existe el expediente
     */
    public boolean agregarPaciente(String nombre, int telefono, String numeroExpediente) {
        // Validar que no exista un paciente con el mismo número de expediente
        //Busca a los pacientes desde un map (getPacientes) y rescata solo su numero de expediente
        boolean expedienteExiste = db.getPacientes().values().stream()  
                
                .anyMatch(p -> p.getNumeroExpediente().equalsIgnoreCase(numeroExpediente));
        
        if (expedienteExiste) {
            System.err.println("Ya existe un paciente con el expediente: " + numeroExpediente);
            return false;
        }
        
        // Crear el nuevo paciente
        Paciente nuevoPaciente = new Paciente();
        nuevoPaciente.setId(db.generarProximoIdPaciente());
        nuevoPaciente.setNombre(nombre);
        nuevoPaciente.setTelefono(telefono);
        nuevoPaciente.setNumeroExpediente(numeroExpediente);
        
        // Agregar al mapa de pacientes
        db.getPacientes().put(nuevoPaciente.getId(), nuevoPaciente);
        
        // Guardar automáticamente
        db.guardarDatos();
        
        System.out.println("Paciente agregado: " + nombre + " (ID: " + nuevoPaciente.getId() + ")");
        return true;
    }
    
    /**
     * Obtiene todos los pacientes registrados
     * @return Lista de todos los pacientes
     */
    public List<Paciente> obtenerTodosLosPacientes() {
        db.recargarDesdeArchivos();
        return List.copyOf(db.getPacientes().values());
    }
    
    /**
     * Busca un paciente por su ID
     * @param id ID del paciente
     * @return Paciente encontrado o null
     */
    public Paciente obtenerPacientePorId(int id) {
        return db.getPacientes().get(id);
    }
    
    /**
     * Busca un paciente por número de expediente
     * @param numeroExpediente Número de expediente a buscar
     * @return Paciente encontrado o null
     */
    public Paciente obtenerPacientePorExpediente(String numeroExpediente) {
        return db.getPacientes().values().stream()
                .filter(p -> p.getNumeroExpediente().equalsIgnoreCase(numeroExpediente))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Actualiza los datos de un paciente existente
     * @param id ID del paciente a actualizar
     * @param nombre Nuevo nombre
     * @param telefono Nuevo teléfono
     * @param numeroExpediente Nuevo número de expediente
     * @return true si se actualizó exitosamente
     */
    public boolean actualizarPaciente(int id, String nombre, int telefono, String numeroExpediente) {
        Paciente paciente = db.getPacientes().get(id);
        
        if (paciente == null) {
            System.err.println("Paciente no encontrado: ID " + id);
            return false;
        }
        
        // Verificar que el nuevo expediente no esté en uso por otro paciente
        //Busca a los pacientes desde un map (getPacientes) y rescata solo su numero de expediente
        boolean expedienteEnUso = db.getPacientes().values().stream()
                .anyMatch(p -> p.getId() != id && 
                          p.getNumeroExpediente().equalsIgnoreCase(numeroExpediente));
        
        if (expedienteEnUso) {
            System.err.println("El expediente " + numeroExpediente + " ya está en uso");
            return false;
        }
        
        // Actualizar datos
        paciente.setNombre(nombre);
        paciente.setTelefono(telefono);
        paciente.setNumeroExpediente(numeroExpediente);
        
        // Guardar automáticamente
        db.guardarDatos();
        
        System.out.println("Paciente actualizado: " + nombre);
        return true;
    }
    
    /**
     * Elimina un paciente del sistema
     * @param id ID del paciente a eliminar
     * @return true si se eliminó exitosamente
     */
    public boolean eliminarPaciente(int id) {
        //Obtiene su id para validar que exista en un condicional
        Paciente paciente = db.getPacientes().get(id);
        
        if (paciente == null) {
            System.err.println("Paciente no encontrado: ID " + id);
            return false;
        }
        
        // Verificar si tiene citas programadas
        //Obtiene las citas desde un map (getCitas) y extrae los datos para procesarlos
        boolean tieneCitas = db.getCitas().values().stream()
                .anyMatch(c -> c.getPaciente() != null && c.getPaciente().getId() == id);
        
        if (tieneCitas) {
            System.err.println("No se puede eliminar. El paciente tiene citas registradas");
            return false;
        }
        
        // Eliminar paciente
        db.getPacientes().remove(id);
        
        // Guardar automáticamente
        db.guardarDatos();
        
        System.out.println("Paciente eliminado: " + paciente.getNombre());
        return true;
    }
    
    /**
     * Obtiene el total de pacientes registrados
     * @return Cantidad de pacientes
     */
    public int obtenerTotalPacientes() {
        return db.getPacientes().size();
    }
    
    /**
     * Busca pacientes por nombre (búsqueda parcial)
     * @param nombreBusqueda Texto a buscar en el nombre
     * @return Lista de pacientes que coinciden
     */
    public List<Paciente> buscarPacientesPorNombre(String nombreBusqueda) {
        //Busca a los pacientes y luego filtra junto con el nombreBusqueda 
        return db.getPacientes().values().stream()
                .filter(p -> p.getNombre().toLowerCase()
                        .contains(nombreBusqueda.toLowerCase()))
                .toList();
    }
}

    

