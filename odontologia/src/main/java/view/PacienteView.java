package view;

import javax.swing.JOptionPane;

public class PacienteView {

    public void mostrarMenuPaciente() {
        JOptionPane.showMessageDialog(null,
                "MENÚ PACIENTE\n\n" +
                "1. Solicitar cita\n" +
                "2. Reprogramar cita\n" +
                "3. Justificar ausencia\n" +
                "0. Volver"
        );
    }
}
