package com.redodontologia.model;


public class Paciente extends Persona {

    private int numeroExpediente;

    public Paciente() {
    }

    // Constructor con parámetros
    public Paciente(int id, String nombre, int telefono, int numeroExpediente) {
        super(id, nombre, telefono);
        this.numeroExpediente = numeroExpediente;
    }

    
    // Getters y Setters
    public int getNumeroExpediente() {
        return numeroExpediente;
    }

    public void setNumeroExpediente(int numeroExpediente) {
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
