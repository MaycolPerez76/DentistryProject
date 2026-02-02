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

    // Métodos del diagrama
    public void solicitarCita() {
        System.out.println("Método solicitarCita no implementado");
    }

    public void solicitarReprogramacion() {
        System.out.println("Método solicitarReprogramacion no implementado");
    }

    public void justificarAusencia() {
        System.out.println("Método justificarAusencia no implementado");
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
