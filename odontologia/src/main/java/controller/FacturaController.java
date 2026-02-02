package controller;

import javax.swing.JOptionPane;
import model.*;
import view.FacturaView;

public class FacturaController {
    private Database db = Database.getInstance();
    private FacturaView view;

    public FacturaController(FacturaView view) {
        this.view = view;
    }
    
    /**
     * Método principal para mostrar una factura en la vista por su ID
     */
    public void mostrarFacturaPorId(int idFactura) {
        // Buscar la factura por ID
        Factura factura = db.getFacturaById(idFactura);
        
        if (factura != null) {
            cargarDatosEnVista(factura);
        } else {
            limpiarVista();
            JOptionPane.showMessageDialog(view, 
                "Factura #" + idFactura + " no encontrada.",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Carga los datos de una factura en la vista
     */
    private void cargarDatosEnVista(Factura factura) {
        try {
            // Verificar que todos los datos necesarios estén presentes
            if (factura.getCita() == null || factura.getPaciente() == null) {
                throw new IllegalArgumentException("Datos incompletos en la factura");
            }
            
            Cita cita = factura.getCita();
            Paciente paciente = factura.getPaciente();
            
            // Cargar datos del paciente
            view.txtTitular.setText(paciente.getNombre());
            
            // Expediente: usar número de expediente si existe, sino ID
            String expediente = (paciente.getNumeroExpediente() != null && !paciente.getNumeroExpediente().isEmpty())
                    ? paciente.getNumeroExpediente()
                    : "Pac-" + paciente.getId();
            view.txtExpediente.setText(expediente);
            
            // Cargar datos de la cita
            view.txtFecha.setText(cita.getFecha() != null ? cita.getFecha().toString() : "N/A");
            view.txtHora.setText(cita.getHora() != null ? cita.getHora().toString() : "N/A");
            view.txtTelefono.setText(String.valueOf(paciente.getTelefono()));
            view.txtEstado.setText(cita.getEstado() != null ? cita.getEstado().toString() : "N/A");
            
            // Cargar total - IMPLEMENTACIÓN DEL CONDICIONAL
            if (cita.getEstado() == EstadoCita.CONFIRMADA) {
                // Si la cita está CONFIRMADA, mostrar el monto total
                view.txtTotal.setText(String.format("$%.2f", factura.getMonto()));
            } else {
                // Si la cita NO está confirmada (PENDIENTE, CANCELADA, etc.), mostrar $0.00
                view.txtTotal.setText("$0.00");
            }
            
        } catch (Exception e) {
            limpiarVista();
            JOptionPane.showMessageDialog(view,
                "Error al cargar la factura: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Limpia todos los campos de la vista
     */
    public void limpiarVista() {
        view.txtTitular.setText("");
        view.txtExpediente.setText("");
        view.txtFecha.setText("");
        view.txtHora.setText("");
        view.txtTelefono.setText("");
        view.txtEstado.setText("");
        view.txtTotal.setText("$0.00");
    }
}