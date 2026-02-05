package view;

import controller.PacienteController;
import model.Paciente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista de Gestión de Pacientes
 * Incluye formulario para agregar pacientes y tabla para visualizarlos
 */
public class PacienteView extends JPanel {
    
    private final PacienteController controller;
    
    // Componentes del formulario
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtNumeroExpediente;
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnActualizar;
    
    // Tabla de pacientes
    private JTable tablaPacientes;
    private DefaultTableModel modeloTabla;
    
    // ID del paciente seleccionado (para edición)
    private Integer pacienteSeleccionadoId = null;
    
    public PacienteView() {
        this.controller = new PacienteController();
        inicializarComponentes();
        cargarPacientes();
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior: Título
        JPanel panelTitulo = crearPanelTitulo();
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central: Formulario
        JPanel panelFormulario = crearPanelFormulario();
        add(panelFormulario, BorderLayout.CENTER);
        
        // Panel inferior: Tabla de pacientes
        JPanel panelTabla = crearPanelTabla();
        add(panelTabla, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(52, 152, 219));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        JLabel lblTitulo = new JLabel("👤 GESTIÓN DE PACIENTES");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo);
        
        return panel;
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "Datos del Paciente",
            0,
            0,
            new Font("Arial", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblNombre = new JLabel("Nombre Completo:");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblNombre, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtNombre = new JTextField(20);
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(txtNombre, gbc);
        
        // Teléfono
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblTelefono, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtTelefono = new JTextField(20);
        txtTelefono.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(txtTelefono, gbc);
        
        // Número de Expediente
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel lblExpediente = new JLabel("Número de Expediente:");
        lblExpediente.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblExpediente, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtNumeroExpediente = new JTextField(20);
        txtNumeroExpediente.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(txtNumeroExpediente, gbc);
        
        // Panel de botones
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        JPanel panelBotones = crearPanelBotones();
        panel.add(panelBotones, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(Color.WHITE);
        
        // Botón Guardar
        btnGuardar = new JButton("💾 Guardar Paciente");
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(46, 204, 113));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarPaciente());
        panel.add(btnGuardar);
        
        // Botón Actualizar
        btnActualizar = new JButton("✏️ Actualizar");
        btnActualizar.setFont(new Font("Arial", Font.BOLD, 13));
        btnActualizar.setBackground(new Color(241, 196, 15));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.setEnabled(false);
        btnActualizar.addActionListener(e -> actualizarPaciente());
        panel.add(btnActualizar);
        
        // Botón Limpiar
        btnLimpiar = new JButton("🗑️ Limpiar");
        btnLimpiar.setFont(new Font("Arial", Font.BOLD, 13));
        btnLimpiar.setBackground(new Color(149, 165, 166));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "Pacientes Registrados",
            0,
            0,
            new Font("Arial", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 300));
        
        // Crear modelo de tabla
        String[] columnas = {"ID", "Nombre", "Teléfono", "Número de Expediente"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };
        
        // Crear tabla
        tablaPacientes = new JTable(modeloTabla);
        tablaPacientes.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaPacientes.setRowHeight(25);
        tablaPacientes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaPacientes.getTableHeader().setBackground(new Color(52, 152, 219));
        tablaPacientes.getTableHeader().setForeground(Color.WHITE);
        tablaPacientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Listener para selección de fila
        tablaPacientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPacientes.getSelectedRow() != -1) {
                cargarPacienteEnFormulario();
            }
        });
        
        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(tablaPacientes);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones de tabla
        JPanel panelBotonesTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelBotonesTabla.setBackground(Color.WHITE);
        
        JButton btnRefrescar = new JButton("🔄 Refrescar");
        btnRefrescar.setFont(new Font("Arial", Font.BOLD, 11));
        btnRefrescar.setBackground(new Color(52, 152, 219));
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefrescar.addActionListener(e -> cargarPacientes());
        panelBotonesTabla.add(btnRefrescar);
        
        JButton btnEliminar = new JButton("❌ Eliminar");
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 11));
        btnEliminar.setBackground(new Color(231, 76, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.addActionListener(e -> eliminarPaciente());
        panelBotonesTabla.add(btnEliminar);
        
        panel.add(panelBotonesTabla, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Guarda un nuevo paciente
     */
    private void guardarPaciente() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }
        
        String nombre = txtNombre.getText().trim();
        int telefono;
        try {
            telefono = Integer.parseInt(txtTelefono.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "El teléfono debe ser un número válido",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        String numeroExpediente = txtNumeroExpediente.getText().trim().toUpperCase();
        
        // Intentar guardar
        boolean exito = controller.agregarPaciente(nombre, telefono, numeroExpediente);
        
        if (exito) {
            JOptionPane.showMessageDialog(this,
                "✅ Paciente guardado exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarPacientes();
        } else {
            JOptionPane.showMessageDialog(this,
                "❌ Ya existe un paciente con ese número de expediente",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Actualiza un paciente existente
     */
    private void actualizarPaciente() {
        if (pacienteSeleccionadoId == null) {
            JOptionPane.showMessageDialog(this,
                "No hay un paciente seleccionado para actualizar",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!validarCampos()) {
            return;
        }
        
        String nombre = txtNombre.getText().trim();
        int telefono;
        try {
            telefono = Integer.parseInt(txtTelefono.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "El teléfono debe ser un número válido",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        String numeroExpediente = txtNumeroExpediente.getText().trim().toUpperCase();
        
        boolean exito = controller.actualizarPaciente(
            pacienteSeleccionadoId, 
            nombre, 
            telefono, 
            numeroExpediente
        );
        
        if (exito) {
            JOptionPane.showMessageDialog(this,
                "✅ Paciente actualizado exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarPacientes();
        } else {
            JOptionPane.showMessageDialog(this,
                "❌ Error al actualizar el paciente",
                "Error",
                JOptionPane.ERROR_MESSAGE);
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
            "¿Está seguro de eliminar al paciente " + nombre + "?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean exito = controller.eliminarPaciente(id);
            
            if (exito) {
                JOptionPane.showMessageDialog(this,
                    "✅ Paciente eliminado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarPacientes();
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ No se puede eliminar. El paciente tiene citas registradas",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Carga los datos del paciente seleccionado en el formulario
     */
    private void cargarPacienteEnFormulario() {
        int filaSeleccionada = tablaPacientes.getSelectedRow();
        
        if (filaSeleccionada != -1) {
            pacienteSeleccionadoId = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
            String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
            String telefono = modeloTabla.getValueAt(filaSeleccionada, 2).toString();
            String numeroExpediente = (String) modeloTabla.getValueAt(filaSeleccionada, 3);
            
            txtNombre.setText(nombre);
            txtTelefono.setText(telefono);
            txtNumeroExpediente.setText(numeroExpediente);
            
            // Habilitar botón actualizar y deshabilitar guardar
            btnActualizar.setEnabled(true);
            btnGuardar.setEnabled(false);
        }
    }
    
    /**
     * Carga todos los pacientes en la tabla
     */
    private void cargarPacientes() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        
        List<Paciente> pacientes = controller.obtenerTodosLosPacientes();
        
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
    
    /**
     * Limpia el formulario y resetea el estado
     */
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtNumeroExpediente.setText("");
        pacienteSeleccionadoId = null;
        
        btnGuardar.setEnabled(true);
        btnActualizar.setEnabled(false);
        
        tablaPacientes.clearSelection();
    }
    
    /**
     * Valida que los campos no estén vacíos
     */
    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre es obligatorio",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }
        
        if (txtTelefono.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El teléfono es obligatorio",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtTelefono.requestFocus();
            return false;
        }
        
        if (txtNumeroExpediente.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El número de expediente es obligatorio",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtNumeroExpediente.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Método público para mostrar el menú (compatibilidad con versión anterior)
     */
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




   