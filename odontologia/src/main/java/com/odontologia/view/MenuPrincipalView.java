package com.redodontologia.view;

import javax.swing.JOptionPane;

public class MenuPrincipalView {

    public int mostrarMenu() {
        try {
            String opcion = JOptionPane.showInputDialog(
                    "SISTEMA RED ODONTOLÓGICA\n\n" +
                    "1. Paciente\n" +
                    "2. Odontólogo\n" +
                    "3. Citas\n" +
                    "4. Facturación\n" +
                    "5. Información\n" +
                    "0. Salir\n\n" +
                    "Seleccione una opción:"
            );

            if (opcion == null) return 0;
            return Integer.parseInt(opcion);

        } catch (Exception e) {
            return -1;
        }
    }
}
