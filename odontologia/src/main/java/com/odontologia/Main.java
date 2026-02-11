package com.odontologia;

import javax.swing.SwingUtilities;
import view.InicioView;
import view.MenuPrincipalView;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // Crear ventana inicio
            InicioView inicio = new InicioView();
            inicio.setVisible(true);

            // Evento del botón iniciar
            inicio.getBtnIniciar().addActionListener(e -> {

                // Cerrar inicio
                inicio.dispose();

                // Abrir menú principal
                MenuPrincipalView menu = new MenuPrincipalView();
                menu.setVisible(true);
            });
        });
    }
}

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
        } while (true);}}
        */
        

