package com.odontologia;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import viewFrame.Factura;




public class Main {

    public static void main(String[] args) {

        
        
             SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Factura");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Add your JPanel
            frame.setContentPane(new Factura());

            frame.pack();              // Adjust size to components
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
        
        
        
        
        /*
        MenuPrincipalView menu = new MenuPrincipalView();
        PacienteView paciente = new PacienteView();
        OdontologoView odontologo = new OdontologoView();
        CitaView cita = new CitaView();
        FacturaView factura = new FacturaView();

        int opcion;

        do {
            opcion = menu.mostrarMenu();

            switch (opcion) {
                case 1 -> paciente.mostrarMenuPaciente();
                case 2 -> odontologo.mostrarMenuOdontologo();
                case 3 -> cita.mostrarMenuCita();
                case 4 -> factura.mostrarFactura();
                case 0 -> System.exit(0);
                default -> {}
            }
        } while (true);
        */
        
        
        
        
        
        
        
        
        
        
    }
}
