package view;

import controller.FacturaController;
import javax.swing.*;
import java.awt.*;

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
    cambiarVista(crearPanelPacientes())
);
        btnOdontologos.addActionListener(e ->
                cambiarVista(crearPanelOdontologia())
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
private JPanel crearPanelPacientes() {
    JPanel panelContenedor = new JPanel(new BorderLayout());
    panelContenedor.setBackground(Color.WHITE);
    
    PacienteView pacienteView = new PacienteView();
    
    panelContenedor.add(pacienteView, BorderLayout.CENTER);
    
    return panelContenedor;
}
    // ===== Panel Citas - INTEGRADO CON CitaView COMPLETO =====
    private JPanel crearPanelCitas() {
        // Crear un panel contenedor con BorderLayout
        JPanel panelContenedor = new JPanel(new BorderLayout());
        panelContenedor.setBackground(Color.WHITE);
        
        // Crear una instancia del CitaView completo
        CitaView citaView = new CitaView();
        
        // Agregar el CitaView al centro del contenedor
        panelContenedor.add(citaView, BorderLayout.CENTER);
        
        return panelContenedor;
    }

    // ===== Panel Factura =====
    private JPanel crearPanelFactura() {
        // 1. Crear el contenedor principal
        JPanel panelContenedor = new JPanel(new BorderLayout());
        panelContenedor.setBackground(Color.WHITE);

        // 2. Crear la Vista y el Controlador
        FacturaView facturaView = new FacturaView();
        FacturaController controller = new FacturaController(facturaView);

        // 3. Crear una pequeña barra de búsqueda para probar el controlador
        JPanel barraBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barraBusqueda.setBackground(Color.WHITE);
        
        JLabel lblBuscar = new JLabel("ID Cita/Factura: ");
        JTextField txtBusqueda = new JTextField(10);
        JButton btnBuscar = new JButton("Cargar Datos");

        // Acción del botón: Llama al controlador
        btnBuscar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtBusqueda.getText());
                controller.mostrarFacturaPorIdFactura(id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID numérico válido.");
            }
        });

        barraBusqueda.add(lblBuscar);
        barraBusqueda.add(txtBusqueda);
        barraBusqueda.add(btnBuscar);

        // 4. Organizar en el panel: Buscador arriba, Factura al centro
        panelContenedor.add(barraBusqueda, BorderLayout.NORTH);
        panelContenedor.add(facturaView, BorderLayout.CENTER);

        return panelContenedor;
    }

    // ===== Panel Odontologia ===== 
    private JPanel crearPanelOdontologia() {
        // Crear un panel contenedor con BorderLayout
        JPanel panel = new JPanel(new BorderLayout());
        
        // Crear una instancia del panel Agenda
        OdontologiaView odontologiaView = new OdontologiaView();
        
        // Agregar el panel Agenda al centro
        panel.add(odontologiaView, BorderLayout.CENTER);
        
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