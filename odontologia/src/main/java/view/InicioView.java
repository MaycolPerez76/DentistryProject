package view;

import javax.swing.*;
import java.awt.*;

public class InicioView extends JFrame {

    private JButton btnIniciar;
    private JLabel lblImagen;

    public InicioView() {
        setTitle("Inicio");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // IMAGEN DE FONDO 
        ImageIcon icon = new ImageIcon(
                "src/main/java/com/odontologia/Inicio.png"
        );

        Image img = icon.getImage().getScaledInstance(900, 550, Image.SCALE_SMOOTH);
        lblImagen = new JLabel(new ImageIcon(img));
        lblImagen.setBounds(0, 0, 900, 550);
        lblImagen.setLayout(null);

        // ===== BOTÓN INICIAR =====
        btnIniciar = new JButton("INICIAR");

       // Boton de inicio 
        btnIniciar.setBounds(320,340,260,55);

        // Boton AZUL 
        btnIniciar.setBackground(new Color(33,150,243)); // azul
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setFocusPainted(false);
        btnIniciar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnIniciar.setBorder(BorderFactory.createEmptyBorder());

        lblImagen.add(btnIniciar);
        add(lblImagen);
    }

    public JButton getBtnIniciar() {
        return btnIniciar;
    }
}
