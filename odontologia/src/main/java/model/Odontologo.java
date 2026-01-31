package model;


public class Odontologo extends Persona {

    private String especialidad;
    private int numeroColegiado;

    public Odontologo() {
    }


    public Odontologo(int id, String nombre, int telefono, String especialidad, int numeroColegiado) {
        super(id, nombre, telefono);
        this.especialidad = especialidad;
        this.numeroColegiado = numeroColegiado;
    }

    // Getters y Setters
    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public int getNumeroColegiado() {
        return numeroColegiado;
    }

    public void setNumeroColegiado(int numeroColegiado) {
        this.numeroColegiado = numeroColegiado;
    }

    // Métodos del diagrama
    public void enviarRecordatorio() {
        System.out.println("Método enviarRecordatorio no implementado");
    }

    @Override
    public String toString() {
        return "Odontologo{" +
                "especialidad='" + especialidad + '\'' +
                ", numeroColegiado=" + numeroColegiado +
                ", id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", telefono=" + getTelefono() +
                '}';
    }
}
