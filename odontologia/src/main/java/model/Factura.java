package model;

public class Factura {
    private int id;
    private double monto;
    private Cita cita;
    private Paciente paciente; // Se inicializará desde la cita

    // Constructor vacío
    public Factura() {
    }

    // Constructor con parámetros
    public Factura(int id, double monto, Cita cita) {
        this.id = id;
        this.monto = monto;
        this.cita = cita;
        this.paciente = cita.getPaciente(); // Inicializar paciente desde la cita
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
        if (cita != null && cita.getPaciente() != null) {
            this.paciente = cita.getPaciente();
        }
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    
    
    public String generar() {
        return "Factura{" +
                "id=" + id +
                ", Titular=" + (paciente != null ? paciente.getNombre() : "N/A") +
                ", Numero expediente=" + (paciente != null ? paciente.getNumeroExpediente() : "N/A") +
                ", monto=" + monto +
                ", fecha=" + (cita != null ? cita.getFecha() : "N/A") +
                ", hora=" + (cita != null ? cita.getHora() : "N/A") +
                ", Estado cita=" + (cita != null ? cita.getEstado() : "N/A") +
                '}';
    }
}