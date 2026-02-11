
package view;

import javax.swing.*;
import java.awt.*;

public class InformacionView extends JPanel {

    private JLabel titulo;
    private JTextArea informacion;

    public InformacionView() {
        iniciarComponentes();
    }

    private void iniciarComponentes() {

        setLayout(new BorderLayout());
        setBackground(new Color(236, 242, 245));

        // Título
        titulo = new JLabel("Información del Sistema", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));

        // Área de texto (puedes cambiar el contenido luego)
        informacion = new JTextArea();
        informacion.setEditable(false);
        informacion.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        informacion.setLineWrap(true);
        informacion.setWrapStyleWord(true);
        informacion.setBackground(new Color(236, 242, 245));

        informacion.setText("""
Sistema Red Odontológica

Aplicación para la gestión de citas médicas odontológicas, implementado por Maycol Perez,
Alexa Miranda y Eitel Jimenez. Trabajo finalizado el dia 23/02/2026, analizado por
nuestro docente encargado en la revision y evaluacion del programa.

Con el fin de la creacion de un sistema funcional y adaptable, tanto para
el cliente como para el desarrollador. Para mas consultas o problemas puede
comunicarse al correo "sistemaRedOdontologia2026.gmial.com"                 

Módulos disponibles:
• Pacientes
• Odontólogos
• Citas
• Facturación
                            
 FuncionalidadesQu
 • Creacion y muestra del paciente
 • Creacion y muestra del odontologo
 • Creacion y muestra de la cita odontologica
 • Creacion y muestra de factura de la cita correspondida al paciente                                                     


""");

        JScrollPane scroll = new JScrollPane(informacion);
        scroll.setBorder(null);

        add(titulo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }
}


