package controller;

import javax.swing.JOptionPane;
import model.*;
import view.FacturaView;

/**
 * Controlador de Factura
 * Busca facturas por ID de CITA
 */
public class FacturaController {
    private Database db = Database.getInstance();
    private FacturaView view;

    public FacturaController(FacturaView view) {
        this.view = view;
    }
    

    public void mostrarFacturaPorIdCita(int idCita) {
        // Usar el nuevo método getFacturaPorIdCita()
        Factura factura = db.getFacturaPorIdCita(idCita);
        
        if (factura != null) {
            cargarDatosEnVista(factura);
            System.out.println("Factura encontrada para cita " + idCita + " -> Factura #" + factura.getId());
        } else {
            limpiarVista();
            System.err.println("No se encontró factura para la cita " + idCita);
            JOptionPane.showMessageDialog(view, 
                "No se encontró factura para la cita #" + idCita,
                "Factura no encontrada", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
 public void mostrarFacturaPorId(int idCita) {  // Cambiar el nombre del parámetro para claridad
    // Buscar la factura por ID de CITA, no ID de factura
    Factura factura = db.getFacturaPorIdCita(idCita);
    
    if (factura != null) {
        cargarDatosEnVista(factura);
    } else {
        limpiarVista();
        JOptionPane.showMessageDialog(view, 
            "No se encontró factura para la cita #" + idCita,
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
    
  
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
            
            System.out.println(" Factura cargada en vista:");
            System.out.println("   - Paciente: " + paciente.getNombre());
            System.out.println("   - Fecha: " + cita.getFecha());
            System.out.println("   - Estado: " + cita.getEstado());
            System.out.println("   - Monto: $" + factura.getMonto());
            
        } catch (Exception e) {
            limpiarVista();
            System.err.println("Error al cargar factura: " + e.getMessage());
            e.printStackTrace();
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
    
    public boolean actualizarMontoPorIdCita(int idCita, double nuevoMonto) {
        Factura factura = db.getFacturaPorIdCita(idCita);
        
        if (factura != null) {
            factura.setMonto(nuevoMonto);
            db.guardarDatos();
            System.out.println("Monto de factura actualizado: $" + nuevoMonto);
            
            // Recargar la vista si está mostrando esta factura
            if (view.txtTitular.getText() != null && !view.txtTitular.getText().isEmpty()) {
                cargarDatosEnVista(factura);
            }
            
            return true;
        }
        
        System.err.println("No se encontró factura para actualizar monto");
        return false;
    }
}
