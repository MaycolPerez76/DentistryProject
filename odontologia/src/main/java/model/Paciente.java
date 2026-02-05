package model;


public class Paciente extends Persona {

    private String numeroExpediente;

    public Paciente() {
    }

    // Constructor con parámetros
    public Paciente(int id, String nombre, int telefono, String numeroExpediente) {
        super(id, nombre, telefono);
        this.numeroExpediente = numeroExpediente;
    }

    
    // Getters y Setters
    public String getNumeroExpediente() {
        return numeroExpediente;
    }

    public void setNumeroExpediente(String numeroExpediente) {
        this.numeroExpediente = numeroExpediente;
    }


    @Override
    public String toString() {
        return "Paciente{" +
                "numeroExpediente=" + numeroExpediente +
                ", id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", telefono=" + getTelefono() +
                '}';
    }
}
