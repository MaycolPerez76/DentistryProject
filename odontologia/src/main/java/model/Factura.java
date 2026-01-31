package model;

public class Factura {

    private double monto;
    private Cita cita;
    private Paciente paciente;

    // Constructor vacío
    public Factura() {
    }

    // Constructor con parámetros
    public Factura(double monto, Cita cita) {
        this.monto = monto;
        this.cita = cita;
    }

    // Getters y Setters
    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

 public String generar() {
    return "Factura{" +
            ", Titular=" + paciente.getNombre() +
            ", Numero expediente=" + paciente.getNumeroExpediente() +
            ", monto=" + monto +
            ", fecha=" + cita.getFecha() +
            ", hora=" + cita.getHora() +
            ", Estado cita=" + cita.getEstado() +  
            '}';
 }   

}
