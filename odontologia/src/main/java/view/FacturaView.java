package view;

import javax.swing.JOptionPane;

public class FacturaView {

    public void mostrarFactura() {
        JOptionPane.showMessageDialog(null,
                "FACTURACIÓN\n\n" +
                "Monto de la cita\n" +
                "Generación de factura"
        );
    }
}
