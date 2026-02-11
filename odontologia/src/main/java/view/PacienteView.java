package view;

import controller.PacienteController;
import model.Paciente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Vista de Gestión de Pacientes
 * 
 * FUNCIONALIDAD PRINCIPAL:
 * - Botón destacado "AGREGAR NUEVO PACIENTE" para iniciar el proceso
 * - Formulario modal para capturar datos del paciente
 * - Auto-guardado en Database con persistencia en JSON
 * - Visualización de todos los pacientes registrados
 * - Edición de pacientes existentes desde la tabla
 * 
 * FLUJO DE DATOS:
 * Vista → Controller → Database → DataPersistence → JSON
 * 
 * @author Sistema de Gestión Odontológica
 * @version 2.0
 */
public class PacienteView extends JPanel {
    
    private final PacienteController controller;
    
    // Componentes principales
    private JButton btnAgregarNuevoPaciente;
    private JTable tablaPacientes;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalPacientes;
    
    // ID del paciente seleccionado (para edición)
    private Integer pacienteSeleccionadoId = null;
    
    /**
     * Constructor - Inicializa la vista y carga los datos
     */
    public PacienteView() {
        this.controller = new PacienteController();
        inicializarComponentes();
        cargarPacientes();
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
        
        // Panel central: Tabla de pacientes
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
        
        JLabel lblTitulo = new JLabel("GESTION DE PACIENTES");
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
        
        // BOTÓN PRINCIPAL: AGREGAR NUEVO PACIENTE
        btnAgregarNuevoPaciente = new JButton("AGREGAR PACIENTE");
        btnAgregarNuevoPaciente.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAgregarNuevoPaciente.setBackground(new Color(46, 204, 113));
        btnAgregarNuevoPaciente.setForeground(Color.WHITE);
        btnAgregarNuevoPaciente.setFocusPainted(false);
        btnAgregarNuevoPaciente.setBorderPainted(false);
        btnAgregarNuevoPaciente.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgregarNuevoPaciente.setPreferredSize(new Dimension(200, 30));
        btnAgregarNuevoPaciente.setToolTipText("Haga clic para registrar un nuevo paciente en el sistema");
        
        // Efecto hover
        btnAgregarNuevoPaciente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAgregarNuevoPaciente.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAgregarNuevoPaciente.setBackground(new Color(46, 204, 113));
            }
        });
        
        // Acción principal: Mostrar diálogo para agregar paciente
        btnAgregarNuevoPaciente.addActionListener(e -> mostrarDialogoAgregarPaciente());

        
        panelAccion.add(btnAgregarNuevoPaciente);
        
                JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 11));
        btnEliminar.setBackground(new Color(231, 76, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.addActionListener(e -> eliminarPaciente());
        
        // Botón secundario: Refrescar
        JButton btnRefrescar = new JButton("Refrescar Lista");
        btnRefrescar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnRefrescar.setBackground(new Color(52, 152, 219));
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.setBorderPainted(false);
        btnRefrescar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefrescar.setPreferredSize(new Dimension(120, 30));
        btnRefrescar.addActionListener(e -> {
            cargarPacientes();
            JOptionPane.showMessageDialog(this,
                "Lista de pacientes actualizada",
                "Lista Actualizada",
                JOptionPane.INFORMATION_MESSAGE);
        });
        panelAccion.add(btnRefrescar);
        panelAccion.add(btnEliminar);
        
        panel.add(panelAccion, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea el panel con la tabla de pacientes registrados
     */
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Título del panel
        JLabel lblTituloTabla = new JLabel("PACIENTES REGISTRADOS");
        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloTabla.setForeground(new Color(52, 73, 94));
        lblTituloTabla.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(lblTituloTabla, BorderLayout.NORTH);
        
        // Crear modelo de tabla
        String[] columnas = {"ID", "Nombre Completo", "Telefono", "Número de Expediente"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable directamente
            }
        };
        
        // Crear tabla
        tablaPacientes = new JTable(modeloTabla);
        tablaPacientes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaPacientes.setRowHeight(40); // Altura de filas
        tablaPacientes.setSelectionBackground(new Color(52, 152, 219));
        tablaPacientes.setSelectionForeground(Color.WHITE);
        tablaPacientes.setGridColor(new Color(189, 195, 199));
        tablaPacientes.setShowGrid(true);
        tablaPacientes.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Configurar encabezados
        tablaPacientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaPacientes.getTableHeader().setBackground(new Color(52, 73, 94));
        tablaPacientes.getTableHeader().setForeground(Color.WHITE);
        tablaPacientes.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        // Centrar contenido de celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tablaPacientes.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tablaPacientes.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        // Ajustar ancho de columnas
        tablaPacientes.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaPacientes.getColumnModel().getColumn(1).setPreferredWidth(350);
        tablaPacientes.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaPacientes.getColumnModel().getColumn(3).setPreferredWidth(200);
        
        tablaPacientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Listener para doble clic: editar paciente
        tablaPacientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && tablaPacientes.getSelectedRow() != -1) {
                    editarPacienteSeleccionado();
                }
            }
        });
        
        // Scroll pane con tamaño preferido grande
        JScrollPane scrollPane = new JScrollPane(tablaPacientes);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        scrollPane.setPreferredSize(new Dimension(1100, 500)); // Tamaño grande para mostrar más información
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de información y acciones
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelAcciones.setBackground(Color.WHITE);
        
        
        panel.add(panelAcciones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crea el panel inferior con información del sistema
     */
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
       
        
        JLabel lblSeparador = new JLabel("|");
        lblSeparador.setForeground(new Color(189, 195, 199));
        panel.add(lblSeparador);
        
        JLabel lblInfo = new JLabel("Auto-guardado habilitado • Los cambios se guardan automaticamente");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(127, 140, 141));
        panel.add(lblInfo);
        
        return panel;
    }
    
    /**
     * MÉTODO PRINCIPAL: Muestra el diálogo para agregar un nuevo paciente
     * Este es el punto de entrada para registrar pacientes en el sistema
     */
    private void mostrarDialogoAgregarPaciente() {
        // Crear diálogo modal
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Agregar Nuevo Paciente", true);
        dialogo.setSize(550, 450);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.getContentPane().setBackground(Color.WHITE);
        
        // Panel de título del diálogo
        JPanel panelTituloDialogo = new JPanel();
        panelTituloDialogo.setBackground(new Color(46, 204, 113));
        panelTituloDialogo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTituloDialogo = new JLabel("Registrar Nuevo Paciente");
        lblTituloDialogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTituloDialogo.setForeground(Color.WHITE);
        panelTituloDialogo.add(lblTituloDialogo);
        
        dialogo.add(panelTituloDialogo, BorderLayout.NORTH);
        
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
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setForeground(new Color(52, 73, 94));
        panelFormulario.add(lblNombre, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField txtNombre = new JTextField(25);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtNombre.setToolTipText("Ingrese el nombre completo del paciente");
        panelFormulario.add(txtNombre, gbc);
        
        // Campo: Teléfono
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblTelefono = new JLabel("Telefono:");
        lblTelefono.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTelefono.setForeground(new Color(52, 73, 94));
        panelFormulario.add(lblTelefono, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField txtTelefono = new JTextField(25);
        txtTelefono.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTelefono.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtTelefono.setToolTipText("Ingrese solo numeros (ej: 88881234)");
        panelFormulario.add(txtTelefono, gbc);
        
        // Campo: Número de Expediente
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel lblExpediente = new JLabel("Numero de Expediente:");
        lblExpediente.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblExpediente.setForeground(new Color(52, 73, 94));
        panelFormulario.add(lblExpediente, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField txtExpediente = new JTextField(25);
        txtExpediente.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtExpediente.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtExpediente.setToolTipText("Codigo unico del paciente (ej: EXP-001)");
        panelFormulario.add(txtExpediente, gbc);
        
        // Nota informativa
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel lblNota = new JLabel("<html><i>💡 El numero de expediente debe ser único para cada paciente</i></html>");
        lblNota.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNota.setForeground(new Color(127, 140, 141));
        panelFormulario.add(lblNota, gbc);
        
        dialogo.add(panelFormulario, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(236, 240, 241)));
        
        // Botón: Guardar
        JButton btnGuardar = new JButton("Guardar Paciente");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnGuardar.setBackground(new Color(46, 204, 113));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(200, 45));
        
        btnGuardar.addActionListener(e -> {
            if (validarYGuardarPaciente(txtNombre, txtTelefono, txtExpediente, dialogo)) {
                dialogo.dispose();
            }
        });
        
        // Botón: Cancelar
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btnCancelar.setBackground(new Color(231, 76, 60));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(150, 45));
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        
        // Configurar tecla Enter para guardar
        dialogo.getRootPane().setDefaultButton(btnGuardar);
        
        // Foco inicial en el campo nombre
        dialogo.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent e) {
                txtNombre.requestFocus();
            }
        });
        
        // Mostrar diálogo
        dialogo.setVisible(true);
    }
    
    /**
     * Valida los datos del formulario y guarda el paciente en la base de datos
     * 
     * @param txtNombre Campo de texto con el nombre
     * @param txtTelefono Campo de texto con el teléfono
     * @param txtExpediente Campo de texto con el número de expediente
     * @param dialogo Diálogo padre para cerrar si es exitoso
     * @return true si se guardó exitosamente, false si hubo errores
     */
    private boolean validarYGuardarPaciente(JTextField txtNombre, JTextField txtTelefono, 
                                            JTextField txtExpediente, JDialog dialogo) {
        
        // Validar campo: Nombre
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarError("El nombre es obligatorio", 
                "Por favor ingrese el nombre completo del paciente", txtNombre);
            return false;
        }
        
        if (nombre.length() < 3) {
            mostrarError("Nombre demasiado corto", 
                "El nombre debe tener al menos 3 caracteres", txtNombre);
            return false;
        }
        
        // Validar campo: Teléfono
        String telefonoStr = txtTelefono.getText().trim();
        if (telefonoStr.isEmpty()) {
            mostrarError("El Telefono es obligatorio", 
                "Por favor ingrese el numero de telefono", txtTelefono);
            return false;
        }
        
        int telefono;
        try {
            telefono = Integer.parseInt(telefonoStr);
            if (telefono <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            mostrarError("Telefono Invalido", 
                "El telefono debe ser un numero positivo valido\nEjemplo: 88881234", txtTelefono);
            return false;
        }
        
        // Validar campo: Número de Expediente
        String numeroExpediente = txtExpediente.getText().trim().toUpperCase();
        if (numeroExpediente.isEmpty()) {
            mostrarError("El numero de expediente es obligatorio", 
                "Por favor ingrese un numero de expediente unico", txtExpediente);
            return false;
        }
        
        if (numeroExpediente.length() < 3) {
            mostrarError("Expediente demasiado corto", 
                "El numero de expediente debe tener al menos 3 caracteres", txtExpediente);
            return false;
        }
        
        
        // Intentar guardar en la base de datos
        // El controlador maneja:
        // 1. Verificación de expediente único
        // 2. Generación automática de ID
        // 3. Inserción en Database
        // 4. Persistencia en JSON mediante DataPersistence
        boolean exito = controller.agregarPaciente(nombre, telefono, numeroExpediente);
        
        if (exito) {
            // Actualizar interfaz
            cargarPacientes();
            
            // Mostrar confirmación
            JOptionPane.showMessageDialog(dialogo,
                "Paciente registrado exitosamente!\n\n" +
                "Datos guardados:\n" +
                "   • Nombre: " + nombre + "\n" +
                "   • Teléfono: " + telefono + "\n" +
                "   • Expediente: " + numeroExpediente + "\n\n" +
                "Los datos se han guardado en la base de datos\n" +
                "   y están disponibles para crear citas.",
                "Registro Exitoso",
                JOptionPane.INFORMATION_MESSAGE);
            
            return true;
            
        } else {
            // Error: Expediente duplicado
            
            mostrarError("Expediente duplicado", 
                "Ya existe un paciente con el numero de expediente:\n" + 
                numeroExpediente + "\n\nPor favor use un expediente diferente.", 
                txtExpediente);
            
            return false;
        }
    }
    
    /**
     * Muestra un diálogo de error y enfoca el campo problemático
     */
    private void mostrarError(String titulo, String mensaje, JTextField campo) {
        JOptionPane.showMessageDialog(this,
            "❌ " + mensaje,
            "Error: " + titulo,
            JOptionPane.ERROR_MESSAGE);
        
        if (campo != null) {
            campo.requestFocus();
            campo.selectAll();
        }
    }
     /**
     * Elimina el paciente seleccionado
     */
    private void eliminarPaciente() {
        int filaSeleccionada = tablaPacientes.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un paciente de la tabla",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Esta seguro de eliminar al paciente " + nombre + "?",
            "Confirmar Eliminacion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean exito = controller.eliminarPaciente(id);
            
            if (exito) {
                JOptionPane.showMessageDialog(this,
                    "Paciente eliminado",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                cargarPacientes();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se puede eliminar. El paciente tiene citas registradas",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    /**
     * Edita el paciente seleccionado en la tabla
     */
    private void editarPacienteSeleccionado() {
        int filaSeleccionada = tablaPacientes.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "No hay ningun paciente seleccionado",
                "Informacion",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Obtener datos del paciente
        int id = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombreActual = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        String telefonoActual = modeloTabla.getValueAt(filaSeleccionada, 2).toString();
        String expedienteActual = (String) modeloTabla.getValueAt(filaSeleccionada, 3);
        
        // Mostrar diálogo de edición
        mostrarDialogoEditarPaciente(id, nombreActual, telefonoActual, expedienteActual);
    }
    
    /**
     * Muestra el diálogo para editar un paciente existente
     */
    private void mostrarDialogoEditarPaciente(int id, String nombreActual, 
                                              String telefonoActual, String expedienteActual) {
        
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Editar Paciente", true);
        dialogo.setSize(550, 450);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.getContentPane().setBackground(Color.WHITE);
        
        // Panel de título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(241, 196, 15));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitulo = new JLabel("Editar Paciente #" + id);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        
        dialogo.add(panelTitulo, BorderLayout.NORTH);
        
        // Panel de formulario (similar al de agregar)
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
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        
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
        panelFormulario.add(new JLabel("Telefono:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField txtTelefono = new JTextField(telefonoActual, 25);
        txtTelefono.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTelefono.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panelFormulario.add(txtTelefono, gbc);
        
        // Campo: Expediente
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        panelFormulario.add(new JLabel("Expediente:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField txtExpediente = new JTextField(expedienteActual, 25);
        txtExpediente.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtExpediente.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panelFormulario.add(txtExpediente, gbc);
        
        dialogo.add(panelFormulario, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.setBackground(Color.WHITE);
        
        JButton btnActualizar = new JButton("💾 Actualizar");
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
            String expediente = txtExpediente.getText().trim().toUpperCase();
            
            if (nombre.isEmpty() || telefonoStr.isEmpty() || expediente.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo,
                    "Todos los campos son obligatorios",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                int telefono = Integer.parseInt(telefonoStr);
                
                boolean exito = controller.actualizarPaciente(id, nombre, telefono, expediente);
                
                if (exito) {
                    JOptionPane.showMessageDialog(dialogo,
                        "Paciente actualizado",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                    cargarPacientes();
                    dialogo.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialogo,
                        "Error: El expediente ya esta en uso",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo,
                    "El telefono debe ser un numero valido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnCancelar = new JButton("Cancelar");
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
     * Carga todos los pacientes desde la base de datos en la tabla
     */
    private void cargarPacientes() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        
        // Obtener pacientes desde el controlador
        // El controlador llama a Database que recarga desde JSON si es necesario
        List<Paciente> pacientes = controller.obtenerTodosLosPacientes();
        
        // Agregar cada paciente a la tabla
        for (Paciente p : pacientes) {
            Object[] fila = {
                p.getId(),
                p.getNombre(),
                p.getTelefono(),
                p.getNumeroExpediente()
            };
            modeloTabla.addRow(fila);
        }
        
    }
    
}
