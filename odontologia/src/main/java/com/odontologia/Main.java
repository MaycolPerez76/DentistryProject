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
        

