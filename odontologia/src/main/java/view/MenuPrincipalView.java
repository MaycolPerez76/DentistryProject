package view;

import javax.swing.*;
import java.awt.*;
import viewFrame.FacturaView;

public class MenuPrincipalView extends JFrame {

    private JPanel contentPanel;

    public MenuPrincipalView() {
        setTitle("Red Odontológica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== Panel izquierdo (MENÚ) =====
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(new Color(30, 90, 160));
        menuPanel.setLayout(new GridLayout(6, 1, 0, 15));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(40, 15, 40, 15));

        JButton btnPacientes = crearBoton("Pacientes");
        JButton btnOdontologos = crearBoton("Odontólogos");
        JButton btnCitas = crearBoton("Citas");
        JButton btnFacturas = crearBoton("Facturas");
        JButton btnInformacion = crearBoton("Información");
        JButton btnSalir = crearBoton("Salir");

        menuPanel.add(btnPacientes);
        menuPanel.add(btnOdontologos);
        menuPanel.add(btnCitas);
        menuPanel.add(btnFacturas);
        menuPanel.add(btnInformacion);
        menuPanel.add(btnSalir);

        // ===== Panel derecho (CONTENIDO) =====
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(menuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // ===== Vista inicial (TÍTULO PRINCIPAL) =====
        cambiarVista(crearPanelInicio());

        // ===== Acciones =====
        btnPacientes.addActionListener(e ->
                cambiarVista(crearPanel("Gestión de Pacientes"))
        );

        btnOdontologos.addActionListener(e ->
                cambiarVista(crearPanel("Gestión de Odontólogos"))
        );

        btnCitas.addActionListener(e ->
                cambiarVista(crearPanelCitas())
        );

        btnFacturas.addActionListener(e ->
                cambiarVista(crearPanelFactura())
        );

        btnInformacion.addActionListener(e ->
                cambiarVista(crearPanelInformacion())
        );

        btnSalir.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    // ===== Botón uniforme =====
    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0, 120, 215));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(160, 45));
        return btn;
    }

    // ===== Cambio de vista =====
    private void cambiarVista(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ===== Pantalla inicial =====
    private JPanel crearPanelInicio() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titulo = new JLabel("Sistema de Gestión Odontológica", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));

        JLabel subtitulo = new JLabel(
                "Seleccione una opción del menú para comenzar",
                SwingConstants.CENTER
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        panel.add(titulo, BorderLayout.CENTER);
        panel.add(subtitulo, BorderLayout.SOUTH);

        return panel;
    }

    // ===== Panel genérico =====
    private JPanel crearPanel(String tituloTexto) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titulo = new JLabel(tituloTexto, SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));

        panel.add(titulo, BorderLayout.NORTH);

        return panel;
    }

    // ===== Panel Citas =====
    private JPanel crearPanelCitas() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titulo = new JLabel("Gestión de Citas", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        panel.add(titulo, BorderLayout.NORTH);

        JPanel opciones = new JPanel(new GridLayout(3, 1, 0, 15));
        opciones.setBorder(BorderFactory.createEmptyBorder(60, 200, 60, 200));

        opciones.add(new JButton("Crear cita"));
        opciones.add(new JButton("Reprogramar cita"));
        opciones.add(new JButton("Enviar recordatorio"));

        panel.add(opciones, BorderLayout.CENTER);

        return panel;
    }

    // ===== Panel Factura ===== (MODIFICADO)
    private JPanel crearPanelFactura() {
        // Crear un panel contenedor con BorderLayout
        JPanel panel = new JPanel(new BorderLayout());
        
        // Crear una instancia del panel Factura
        FacturaView facturaPanel = new FacturaView();
        
        // Agregar el panel Factura al centro
        panel.add(facturaPanel, BorderLayout.CENTER);
        
        return panel;
    }

    // ===== Panel Información =====
    private JPanel crearPanelInformacion() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titulo = new JLabel("Información del Sistema", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        panel.add(titulo, BorderLayout.NORTH);

        JTextArea info = new JTextArea("""
        Sistema de Gestión Odontológica

        • Administración de pacientes
        • Control de citas
        • Recordatorios y reprogramación
        • Facturación odontológica
        """);

        info.setEditable(false);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(info, BorderLayout.CENTER);

        return panel;
    }
}