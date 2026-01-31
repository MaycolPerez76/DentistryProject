package view;

import javax.swing.JOptionPane;

public class OdontologoView {

    public void mostrarMenuOdontologo() {
        JOptionPane.showMessageDialog(null,
                "MENÚ ODONTÓLOGO\n\n" +
                "1. Ver agenda\n" +
                "2. Confirmar cita\n" +
                "3. Cancelar cita\n" +
                "0. Volver"
        );
    }
}
