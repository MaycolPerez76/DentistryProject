package view;

import javax.swing.JOptionPane;

public class CitaView {

    public void mostrarMenuCita() {
        JOptionPane.showMessageDialog(null,
                "MENÚ CITAS\n\n" +
                "1. Crear cita\n" +
                "2. Reprogramar cita\n" +
                "3. Cancelar cita\n" +
                "0. Volver"
        );
    }
}
