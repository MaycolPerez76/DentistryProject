package view;

import controller.OdontologiaController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import model.Odontologo;

/**
 * Vista de Gestión de Odontólogos
 * 
 * FUNCIONALIDAD PRINCIPAL:
 * - Botón destacado "AGREGAR NUEVO ODONTOLOGO" para iniciar el proceso
 * - Formulario modal para capturar datos del odontólogo
 * - Auto-guardado en Database con persistencia en JSON
 * - Visualización de todos los odontólogos registrados
 * - Edición de odontólogos existentes desde la tabla
 * 
 * FLUJO DE DATOS:
 * Vista → Controller → Database → DataPersistence → JSON
 * 
 * @author Sistema de Gestión Odontológica
 * @version 2.0
 */
public class OdontologiaView extends JPanel {
    
    private final OdontologiaController controller;
    
    // Componentes principales
    private JButton btnAgregarNuevoOdontologo;
    private JTable tablaOdontologos;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalOdontologos;
    private JLabel lblEstadoSistema;
    
    // ID del odontólogo seleccionado (para edición)
    private Integer odontologoSeleccionadoId = null;
    
    /**
     * Constructor - Inicializa la vista y carga los datos
     */
    public OdontologiaView() {
        this.controller = new OdontologiaController();
        inicializarComponentes();
        cargarOdontologos();
        actualizarEstadoSistema();
    }
    
    /**
     * Inicializa todos los componentes visuales
     */
    private void inicializarComponentes() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 244, 248));
        setPreferredSize(new Dimension(1200, 800));
        
        // Panel superior: Título y botón principal
        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central: Tabla de odontólogos
        JPanel panelCentral = crearPanelTabla();
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior: Información del sistema
        JPanel panelInferior = crearPanelInferior();
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    /**
     * Crea el panel superior con título y botón principal
     */
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 244, 248));
        panel.setPreferredSize(new Dimension(0, 150));
        
        // Panel de título
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(new Color(41, 128, 185));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel("GESTIÓN DE ODONTÓLOGOS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo, BorderLayout.WEST);
        
        panel.add(panelTitulo, BorderLayout.NORTH);
        
        // Panel de acción principal
        JPanel panelAccion = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelAccion.setBackground(Color.WHITE);
        panelAccion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // BOTÓN PRINCIPAL: AGREGAR NUEVO ODONTÓLOGO
        btnAgregarNuevoOdontologo = new JButton("➕ AGREGAR ODONTÓLOGO");
        btnAgregarNuevoOdontologo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAgregarNuevoOdontologo.setBackground(new Color(46, 204, 113));
        btnAgregarNuevoOdontologo.setForeground(Color.WHITE);
        btnAgregarNuevoOdontologo.setFocusPainted(false);
        btnAgregarNuevoOdontologo.setBorderPainted(false);
        btnAgregarNuevoOdontologo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgregarNuevoOdontologo.setPreferredSize(new Dimension(220, 35));
        btnAgregarNuevoOdontologo.setToolTipText("Haga clic para registrar un nuevo odontólogo en el sistema");
        
        // Efecto hover
        btnAgregarNuevoOdontologo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAgregarNuevoOdontologo.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAgregarNuevoOdontologo.setBackground(new Color(46, 204, 113));
            }
        });
        
        // Acción principal: Mostrar diálogo para agregar odontólogo
        btnAgregarNuevoOdontologo.addActionListener(e -> mostrarDialogoAgregarOdontologo());

        panelAccion.add(btnAgregarNuevoOdontologo);
        
        // Botón secundario: Eliminar
        JButton btnEliminar = new JButton("🗑️ Eliminar");
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEliminar.setBackground(new Color(231, 76, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setBorderPainted(false);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.setPreferredSize(new Dimension(120, 35));
        btnEliminar.addActionListener(e -> eliminarOdontologo());
        
        // Botón secundario: Refrescar
        JButton btnRefrescar = new JButton("🔄 Refrescar Lista");
        btnRefrescar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnRefrescar.setBackground(new Color(52, 152, 219));
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.setBorderPainted(false);
        btnRefrescar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefrescar.setPreferredSize(new Dimension(140, 35));
        btnRefrescar.addActionListener(e -> {
            cargarOdontologos();
            actualizarEstadoSistema();
            JOptionPane.showMessageDialog(this,
                "Lista de odontólogos actualizada correctamente",
                "✅ Lista Actualizada",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        panelAccion.add(btnRefrescar);
        panelAccion.add(btnEliminar);
        
        panel.add(panelAccion, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea el panel con la tabla de odontólogos registrados
     */
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Título del panel
        JLabel lblTituloTabla = new JLabel("📋 ODONTÓLOGOS REGISTRADOS");
        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloTabla.setForeground(new Color(52, 73, 94));
        lblTituloTabla.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(lblTituloTabla, BorderLayout.NORTH);
        
        // Crear modelo de tabla
        String[] columnas = {"ID", "Nombre Completo", "Teléfono", "Número de Colegiado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable directamente
            }
        };
        
        // Crear tabla
        tablaOdontologos = new JTable(modeloTabla);
        tablaOdontologos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaOdontologos.setRowHeight(40);
        tablaOdontologos.setSelectionBackground(new Color(52, 152, 219));
        tablaOdontologos.setSelectionForeground(Color.WHITE);
        tablaOdontologos.setGridColor(new Color(189, 195, 199));
        tablaOdontologos.setShowGrid(true);
        
        // Configurar ancho de columnas
        tablaOdontologos.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        tablaOdontologos.getColumnModel().getColumn(1).setPreferredWidth(250); // Nombre
        tablaOdontologos.getColumnModel().getColumn(2).setPreferredWidth(120); // Teléfono
        tablaOdontologos.getColumnModel().getColumn(3).setPreferredWidth(150); // Número Colegiado
        
        // Centrar contenido de columnas numéricas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tablaOdontologos.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        tablaOdontologos.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Teléfono
        tablaOdontologos.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Número Colegiado
        
        // Personalizar encabezado
        tablaOdontologos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaOdontologos.getTableHeader().setBackground(new Color(52, 73, 94));
        tablaOdontologos.getTableHeader().setForeground(Color.WHITE);
        tablaOdontologos.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        // Double click para editar
        tablaOdontologos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int filaSeleccionada = tablaOdontologos.getSelectedRow();
                    if (filaSeleccionada != -1) {
                        int id = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
                        String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
                        String telefono = String.valueOf(modeloTabla.getValueAt(filaSeleccionada, 2));
                        String numeroColegiado = String.valueOf(modeloTabla.getValueAt(filaSeleccionada, 3));
                        
                        mostrarDialogoEditarOdontologo(id, nombre, telefono, numeroColegiado);
                    }
                }
            }
        });
        
        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(tablaOdontologos);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea el panel inferior con información del sistema
     */
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panel.setPreferredSize(new Dimension(0, 60));
        
        // Panel izquierdo: Total de odontólogos
        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelIzquierdo.setBackground(new Color(236, 240, 241));
        
        lblTotalOdontologos = new JLabel("Total de odontólogos: 0");
        lblTotalOdontologos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalOdontologos.setForeground(new Color(52, 73, 94));
        panelIzquierdo.add(lblTotalOdontologos);
        
        // Panel derecho: Estado del sistema
        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelDerecho.setBackground(new Color(236, 240, 241));
        
        lblEstadoSistema = new JLabel("● Sistema listo");
        lblEstadoSistema.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEstadoSistema.setForeground(new Color(46, 204, 113));
        panelDerecho.add(lblEstadoSistema);
        
        panel.add(panelIzquierdo, BorderLayout.WEST);
        panel.add(panelDerecho, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Muestra el diálogo para agregar un nuevo odontólogo
     */
    private void mostrarDialogoAgregarOdontologo() {
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Odontólogo", true);
        dialogo.setSize(550, 500);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.getContentPane().setBackground(Color.WHITE);
        
        // Panel de título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(46, 204, 113));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitulo = new JLabel("➕ Registrar Nuevo Odontólogo");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        
        dialogo.add(panelTitulo, BorderLayout.NORTH);
        
        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campo: Nombre Completo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblNombre = new JLabel("Nombre Completo:");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelFormulario.add(lblNombre, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField txtNombre = new JTextField(25);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panelFormulario.add(txtNombre, gbc);
        
        // Campo: Teléfono
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelFormulario.add(lblTelefono, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField txtTelefono = new JTextField(25);
        txtTelefono.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTelefono.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panelFormulario.add(txtTelefono, gbc);
        
        // Campo: Número de Colegiado
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel lblColegiado = new JLabel("Número de Colegiado:");
        lblColegiado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelFormulario.add(lblColegiado, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField txtNumeroColegiado = new JTextField(25);
        txtNumeroColegiado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNumeroColegiado.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panelFormulario.add(txtNumeroColegiado, gbc);
        
        // Texto de ayuda
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel lblAyuda = new JLabel("<html><i>Nota: Todos los campos son obligatorios</i></html>");
        lblAyuda.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAyuda.setForeground(new Color(127, 140, 141));
        panelFormulario.add(lblAyuda, gbc);
        
        dialogo.add(panelFormulario, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.setBackground(Color.WHITE);
        
        JButton btnGuardar = new JButton("✅ Guardar");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnGuardar.setBackground(new Color(46, 204, 113));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(200, 45));
        
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String telefonoStr = txtTelefono.getText().trim();
            String numeroColegiadoStr = txtNumeroColegiado.getText().trim();
            
            // Validar campos vacíos
            if (nombre.isEmpty() || telefonoStr.isEmpty() || numeroColegiadoStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo,
                    "⚠️ Todos los campos son obligatorios",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar que el nombre tenga al menos 3 caracteres
            if (nombre.length() < 3) {
                JOptionPane.showMessageDialog(dialogo,
                    "⚠️ El nombre debe tener al menos 3 caracteres",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                int telefono = Integer.parseInt(telefonoStr);
                int numeroColegiado = Integer.parseInt(numeroColegiadoStr);
                
                // Validar números positivos
                if (telefono <= 0 || numeroColegiado <= 0) {
                    JOptionPane.showMessageDialog(dialogo,
                        "⚠️ El teléfono y el número de colegiado deben ser números positivos",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Intentar agregar el odontólogo
                boolean exito = controller.agregarOdontologoNuevo(nombre, telefono, numeroColegiado);
                
                if (exito) {
                    JOptionPane.showMessageDialog(dialogo,
                        "✅ Odontólogo registrado exitosamente\n\n" +
                        "Nombre: " + nombre + "\n" +
                        "Teléfono: " + telefono + "\n" +
                        "Número de Colegiado: " + numeroColegiado,
                        "Registro Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Actualizar tabla y cerrar diálogo
                    cargarOdontologos();
                    actualizarEstadoSistema("Odontólogo agregado exitosamente", new Color(46, 204, 113));
                    dialogo.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialogo,
                        "❌ Error: El número de colegiado " + numeroColegiado + " ya está registrado.\n" +
                        "Por favor, verifique el número e intente nuevamente.",
                        "Error de Duplicación",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo,
                    "⚠️ El teléfono y el número de colegiado deben ser números válidos",
                    "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btnCancelar.setBackground(new Color(149, 165, 166));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(150, 45));
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        
        dialogo.setVisible(true);
    }
    
    /**
     * Muestra el diálogo para editar un odontólogo existente
     */
    private void mostrarDialogoEditarOdontologo(int id, String nombreActual, 
                                                String telefonoActual, String colegiadoActual) {
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                      "Editar Odontólogo", true);
        dialogo.setSize(550, 500);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.getContentPane().setBackground(Color.WHITE);
        
        // Panel de título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(241, 196, 15));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitulo = new JLabel("✏️ Editar Odontólogo #" + id);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        
        dialogo.add(panelTitulo, BorderLayout.NORTH);
        
        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campo: Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblNombre = new JLabel("Nombre Completo:");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelFormulario.add(lblNombre, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField txtNombre = new JTextField(nombreActual, 25);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panelFormulario.add(txtNombre, gbc);
        
        // Campo: Teléfono
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelFormulario.add(lblTelefono, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField txtTelefono = new JTextField(telefonoActual, 25);
        txtTelefono.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTelefono.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panelFormulario.add(txtTelefono, gbc);
        
        // Campo: Número de Colegiado
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel lblColegiado = new JLabel("Número de Colegiado:");
        lblColegiado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelFormulario.add(lblColegiado, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField txtColegiado = new JTextField(colegiadoActual, 25);
        txtColegiado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtColegiado.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panelFormulario.add(txtColegiado, gbc);
        
        // Texto de ayuda
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel lblAyuda = new JLabel("<html><i>Nota: Todos los campos son obligatorios</i></html>");
        lblAyuda.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAyuda.setForeground(new Color(127, 140, 141));
        panelFormulario.add(lblAyuda, gbc);
        
        dialogo.add(panelFormulario, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.setBackground(Color.WHITE);
        
        JButton btnActualizar = new JButton("✅ Actualizar");
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnActualizar.setBackground(new Color(241, 196, 15));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setBorderPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.setPreferredSize(new Dimension(200, 45));
        
        btnActualizar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String telefonoStr = txtTelefono.getText().trim();
            String numeroColegiadoStr = txtColegiado.getText().trim();
            
            // Validar campos vacíos
            if (nombre.isEmpty() || telefonoStr.isEmpty() || numeroColegiadoStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo,
                    "⚠️ Todos los campos son obligatorios",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar que el nombre tenga al menos 3 caracteres
            if (nombre.length() < 3) {
                JOptionPane.showMessageDialog(dialogo,
                    "⚠️ El nombre debe tener al menos 3 caracteres",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                int telefono = Integer.parseInt(telefonoStr);
                int numeroColegiado = Integer.parseInt(numeroColegiadoStr);
                
                // Validar números positivos
                if (telefono <= 0 || numeroColegiado <= 0) {
                    JOptionPane.showMessageDialog(dialogo,
                        "⚠️ El teléfono y el número de colegiado deben ser números positivos",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Intentar actualizar el odontólogo
                boolean exito = controller.actualizarOdontologo(id, nombre, telefono, numeroColegiadoStr);
                
                if (exito) {
                    JOptionPane.showMessageDialog(dialogo,
                        "✅ Odontólogo actualizado exitosamente\n\n" +
                        "ID: " + id + "\n" +
                        "Nombre: " + nombre + "\n" +
                        "Teléfono: " + telefono + "\n" +
                        "Número de Colegiado: " + numeroColegiado,
                        "Actualización Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Actualizar tabla y cerrar diálogo
                    cargarOdontologos();
                    actualizarEstadoSistema("Odontólogo actualizado exitosamente", new Color(241, 196, 15));
                    dialogo.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialogo,
                        "❌ Error: El número de colegiado " + numeroColegiado + " ya está en uso por otro odontólogo.\n" +
                        "Por favor, verifique el número e intente nuevamente.",
                        "Error de Duplicación",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo,
                    "⚠️ El teléfono y el número de colegiado deben ser números válidos",
                    "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btnCancelar.setBackground(new Color(149, 165, 166));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(150, 45));
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        panelBotones.add(btnActualizar);
        panelBotones.add(btnCancelar);
        
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        
        dialogo.setVisible(true);
    }
    
    /**
     * Elimina el odontólogo seleccionado
     */
    private void eliminarOdontologo() {
        int filaSeleccionada = tablaOdontologos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "⚠️ Por favor, seleccione un odontólogo de la tabla para eliminar",
                "Ningún Odontólogo Seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar al odontólogo?\n\n" +
            "ID: " + id + "\n" +
            "Nombre: " + nombre + "\n\n" +
            "⚠️ Esta acción no se puede deshacer.",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean exito = controller.eliminarOdontologo(id);
            
            if (exito) {
                JOptionPane.showMessageDialog(this,
                    "✅ Odontólogo eliminado exitosamente\n\n" +
                    "ID: " + id + "\n" +
                    "Nombre: " + nombre,
                    "Eliminación Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarOdontologos();
                actualizarEstadoSistema("Odontólogo eliminado", new Color(231, 76, 60));
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ No se pudo eliminar el odontólogo.\n\n" +
                    "Posibles causas:\n" +
                    "• El odontólogo tiene citas asignadas\n" +
                    "• El odontólogo no existe en el sistema",
                    "Error de Eliminación",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Carga todos los odontólogos desde la base de datos en la tabla
     */
    private void cargarOdontologos() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        
        // Obtener odontólogos desde el controlador
        List<Odontologo> odontologos = controller.obtenerTodosLosOdontologos();
        
        // Agregar cada odontólogo a la tabla
        for (Odontologo o : odontologos) {
            Object[] fila = {
                o.getId(),
                o.getNombre(),
                o.getTelefono(),
                o.getNumeroColegiado()
            };
            modeloTabla.addRow(fila);
        }
        
        // Actualizar contador
        lblTotalOdontologos.setText("Total de odontólogos: " + odontologos.size());
    }
    
    /**
     * Actualiza el estado del sistema en la interfaz
     */
    private void actualizarEstadoSistema() {
        actualizarEstadoSistema("● Sistema listo", new Color(46, 204, 113));
    }
    
    /**
     * Actualiza el estado del sistema con mensaje y color personalizados
     */
    private void actualizarEstadoSistema(String mensaje, Color color) {
        lblEstadoSistema.setText("● " + mensaje);
        lblEstadoSistema.setForeground(color);
        
        // Restaurar después de 3 segundos
        if (!mensaje.equals("Sistema listo")) {
            Timer timer = new Timer(3000, e -> {
                lblEstadoSistema.setText("● Sistema listo");
                lblEstadoSistema.setForeground(new Color(46, 204, 113));
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
}