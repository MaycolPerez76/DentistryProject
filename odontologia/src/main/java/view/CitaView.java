package view;

import controller.RecepcionController;
import model.Cita;
import model.Database;
import model.EstadoCita;
import model.Odontologo;
import model.Paciente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Vista para gestionar citas con interfaz gráfica (JPanel)
 * CORREGIDA: Validaciones mejoradas y mensajes descriptivos
 */
public class CitaView extends JPanel {
    
    private RecepcionController controller;
    private Database db;
    
    // Componentes principales
    private JTable tablaCitas;
    private DefaultTableModel modeloTabla;
    private JButton btnCrear, btnReprogramar, btnCancelar, btnConfirmar;
    private JButton btnRegistrarLlegada, btnEvaluarAsistencia, btnActualizar;
    private JComboBox<String> cmbFiltroEstado;
    private JTextField txtBuscarPaciente;
    
    public CitaView() {
        this.controller = new RecepcionController();
        this.db = Database.getInstance();
        
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior - Título y filtros
        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central - Tabla de citas
        JPanel panelCentral = crearPanelTabla();
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior - Botones de acción
        JPanel panelInferior = crearPanelBotones();
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Título
        JLabel lblTitulo = new JLabel("GESTIÓN DE CITAS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        panelFiltros.add(new JLabel("Filtrar por estado:"));
        cmbFiltroEstado = new JComboBox<>(new String[]{
            "Todos", "PENDIENTE", "CONFIRMADA", "ATENDIDA", "CANCELADA", "AUSENTE"
        });
        cmbFiltroEstado.addActionListener(e -> aplicarFiltros());
        panelFiltros.add(cmbFiltroEstado);
        
        panelFiltros.add(Box.createHorizontalStrut(20));
        
        panelFiltros.add(new JLabel("Buscar paciente:"));
        txtBuscarPaciente = new JTextField(15);
        txtBuscarPaciente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                aplicarFiltros();
            }
        });
        panelFiltros.add(txtBuscarPaciente);
        
        btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.addActionListener(e -> cargarDatos());
        panelFiltros.add(btnActualizar);
        
        panel.add(panelFiltros, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Modelo de tabla
        String[] columnas = {"ID", "Paciente", "Odontólogo", "Fecha", "Hora", "Motivo", "Estado", "Llegada"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };
        
        tablaCitas = new JTable(modeloTabla);
        tablaCitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCitas.getTableHeader().setReorderingAllowed(false);
        
        // Ajustar anchos de columnas
        tablaCitas.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        tablaCitas.getColumnModel().getColumn(1).setPreferredWidth(150); // Paciente
        tablaCitas.getColumnModel().getColumn(2).setPreferredWidth(150); // Odontólogo
        tablaCitas.getColumnModel().getColumn(3).setPreferredWidth(100); // Fecha
        tablaCitas.getColumnModel().getColumn(4).setPreferredWidth(80);  // Hora
        tablaCitas.getColumnModel().getColumn(5).setPreferredWidth(200); // Motivo
        tablaCitas.getColumnModel().getColumn(6).setPreferredWidth(100); // Estado
        tablaCitas.getColumnModel().getColumn(7).setPreferredWidth(80);  // Llegada
        
        JScrollPane scrollPane = new JScrollPane(tablaCitas);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnCrear = new JButton("➕ Crear Cita");
        btnCrear.addActionListener(e -> mostrarDialogoCrearCita());
        panel.add(btnCrear);
        
        btnReprogramar = new JButton("📅 Reprogramar");
        btnReprogramar.addActionListener(e -> mostrarDialogoReprogramar());
        panel.add(btnReprogramar);
        
        btnConfirmar = new JButton("✓ Confirmar");
        btnConfirmar.addActionListener(e -> confirmarCita());
        panel.add(btnConfirmar);
        
        btnCancelar = new JButton("✗ Cancelar");
        btnCancelar.addActionListener(e -> cancelarCita());
        panel.add(btnCancelar);
        
        btnRegistrarLlegada = new JButton("⏰ Registrar Llegada");
        btnRegistrarLlegada.addActionListener(e -> mostrarDialogoRegistrarLlegada());
        panel.add(btnRegistrarLlegada);
        
        btnEvaluarAsistencia = new JButton("📋 Evaluar Asistencia");
        btnEvaluarAsistencia.addActionListener(e -> evaluarAsistencia());
        panel.add(btnEvaluarAsistencia);
        
        return panel;
    }
    
    private void cargarDatos() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        
        List<Cita> citas = controller.obtenerTodasLasCitas();
        
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        
        for (Cita cita : citas) {
            Object[] fila = {
                cita.getId(),
                cita.getPaciente() != null ? cita.getPaciente().getNombre() : "N/A",
                cita.getOdontologo() != null ? cita.getOdontologo().getNombre() : "N/A",
                cita.getFecha() != null ? cita.getFecha().format(formatoFecha) : "",
                cita.getHora() != null ? cita.getHora().format(formatoHora) : "",
                cita.getMotivo(),
                cita.getEstado(),
                cita.getHoraLlegadaPaciente() != null ? cita.getHoraLlegadaPaciente().format(formatoHora) : "-"
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void aplicarFiltros() {
        modeloTabla.setRowCount(0);
        
        List<Cita> citas = controller.obtenerTodasLasCitas();
        String filtroEstado = (String) cmbFiltroEstado.getSelectedItem();
        String buscarPaciente = txtBuscarPaciente.getText().toLowerCase().trim();
        
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        
        for (Cita cita : citas) {
            // Filtrar por estado
            if (!filtroEstado.equals("Todos") && !cita.getEstado().toString().equals(filtroEstado)) {
                continue;
            }
            
            // Filtrar por nombre de paciente
            if (!buscarPaciente.isEmpty()) {
                String nombrePaciente = cita.getPaciente() != null ? cita.getPaciente().getNombre().toLowerCase() : "";
                if (!nombrePaciente.contains(buscarPaciente)) {
                    continue;
                }
            }
            
            Object[] fila = {
                cita.getId(),
                cita.getPaciente() != null ? cita.getPaciente().getNombre() : "N/A",
                cita.getOdontologo() != null ? cita.getOdontologo().getNombre() : "N/A",
                cita.getFecha() != null ? cita.getFecha().format(formatoFecha) : "",
                cita.getHora() != null ? cita.getHora().format(formatoHora) : "",
                cita.getMotivo(),
                cita.getEstado(),
                cita.getHoraLlegadaPaciente() != null ? cita.getHoraLlegadaPaciente().format(formatoHora) : "-"
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void mostrarDialogoCrearCita() {
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Crear Nueva Cita", true);
        dialogo.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Paciente
        gbc.gridx = 0; gbc.gridy = 0;
        dialogo.add(new JLabel("Paciente:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cmbPaciente = new JComboBox<>();
        for (Paciente p : db.getPacientes().values()) {
            cmbPaciente.addItem(p.getId() + " - " + p.getNombre());
        }
        dialogo.add(cmbPaciente, gbc);
        
        // Odontólogo
        gbc.gridx = 0; gbc.gridy = 1;
        dialogo.add(new JLabel("Odontólogo:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cmbOdontologo = new JComboBox<>();
        for (Odontologo o : db.getOdontologos().values()) {
            cmbOdontologo.addItem(o.getId() + " - " + o.getNombre());
        }
        dialogo.add(cmbOdontologo, gbc);
        
        // Fecha
        gbc.gridx = 0; gbc.gridy = 2;
        dialogo.add(new JLabel("Fecha (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        JTextField txtFecha = new JTextField(15);
        txtFecha.setText(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dialogo.add(txtFecha, gbc);
        
        // Hora
        gbc.gridx = 0; gbc.gridy = 3;
        dialogo.add(new JLabel("Hora (HH:mm):"), gbc);
        gbc.gridx = 1;
        JTextField txtHora = new JTextField(15);
        txtHora.setText("09:00");
        dialogo.add(txtHora, gbc);
        
        // Motivo
        gbc.gridx = 0; gbc.gridy = 4;
        dialogo.add(new JLabel("Motivo:"), gbc);
        gbc.gridx = 1;
        JTextField txtMotivo = new JTextField(15);
        dialogo.add(txtMotivo, gbc);
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(e -> {
            try {
                // Obtener datos
                String strPaciente = (String) cmbPaciente.getSelectedItem();
                int idPaciente = Integer.parseInt(strPaciente.split(" - ")[0]);
                
                String strOdontologo = (String) cmbOdontologo.getSelectedItem();
                int idOdontologo = Integer.parseInt(strOdontologo.split(" - ")[0]);
                
                LocalDate fecha = LocalDate.parse(txtFecha.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalTime hora = LocalTime.parse(txtHora.getText(), DateTimeFormatter.ofPattern("HH:mm"));
                String motivo = txtMotivo.getText();
                
                // Crear cita usando RecepcionController
                if (controller.crearCita(idPaciente, idOdontologo, fecha, hora, motivo)) {
                    JOptionPane.showMessageDialog(dialogo, "Cita creada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                    dialogo.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialogo, 
                        "No se pudo crear la cita.\nPosibles razones:\n" +
                        "• El odontólogo ya tiene una cita a esa hora\n" +
                        "• La fecha es pasada\n" +
                        "• Datos inválidos", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialogo, "Formato de fecha u hora inválido", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        dialogo.add(panelBotones, gbc);
        
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }
    
    private void mostrarDialogoReprogramar() {
        int filaSeleccionada = tablaCitas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para reprogramar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idCita = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Cita cita = controller.obtenerCitaPorId(idCita);
        
        // VALIDACIÓN MEJORADA
        if (cita == null) {
            JOptionPane.showMessageDialog(this, "La cita seleccionada no existe", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cita.getEstado() == EstadoCita.CANCELADA) {
            JOptionPane.showMessageDialog(this, 
                "No se puede reprogramar una cita CANCELADA.\n" +
                "Debe crear una nueva cita para este paciente.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cita.getEstado() == EstadoCita.ATENDIDA) {
            JOptionPane.showMessageDialog(this, 
                "No se puede reprogramar una cita ATENDIDA.\n" +
                "La cita ya fue completada.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Reprogramar Cita", true);
        dialogo.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialogo.add(new JLabel("Nueva Fecha (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        JTextField txtFecha = new JTextField(15);
        txtFecha.setText(cita.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dialogo.add(txtFecha, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialogo.add(new JLabel("Nueva Hora (HH:mm):"), gbc);
        gbc.gridx = 1;
        JTextField txtHora = new JTextField(15);
        txtHora.setText(cita.getHora().format(DateTimeFormatter.ofPattern("HH:mm")));
        dialogo.add(txtHora, gbc);
        
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(e -> {
            try {
                LocalDate nuevaFecha = LocalDate.parse(txtFecha.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalTime nuevaHora = LocalTime.parse(txtHora.getText(), DateTimeFormatter.ofPattern("HH:mm"));
                
                if (controller.reprogramarCita(idCita, nuevaFecha, nuevaHora)) {
                    JOptionPane.showMessageDialog(dialogo, "Cita reprogramada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                    dialogo.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialogo, 
                        "No se pudo reprogramar la cita.\nPosibles razones:\n" +
                        "• El odontólogo ya tiene una cita a esa hora\n" +
                        "• La nueva fecha es pasada\n" +
                        "• El horario no está disponible", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialogo, "Formato de fecha u hora inválido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        dialogo.add(panelBotones, gbc);
        
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }
    
    private void confirmarCita() {
        int filaSeleccionada = tablaCitas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para confirmar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idCita = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Cita cita = controller.obtenerCitaPorId(idCita);
        
        // VALIDACIONES MEJORADAS
        if (cita == null) {
            JOptionPane.showMessageDialog(this, "La cita seleccionada no existe", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar estado actual
        if (cita.getEstado() == EstadoCita.CONFIRMADA) {
            JOptionPane.showMessageDialog(this, 
                "La cita ya está CONFIRMADA", 
                "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (cita.getEstado() == EstadoCita.CANCELADA) {
            JOptionPane.showMessageDialog(this, 
                "No se puede confirmar una cita CANCELADA.\n" +
                "Una cita cancelada no puede cambiar de estado.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cita.getEstado() == EstadoCita.ATENDIDA) {
            JOptionPane.showMessageDialog(this, 
                "La cita ya fue ATENDIDA.\n" +
                "No es necesario confirmarla.", 
                "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (cita.getEstado() == EstadoCita.AUSENTE) {
            JOptionPane.showMessageDialog(this, 
                "La cita fue marcada como AUSENTE.\n" +
                "No se puede confirmar.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Intentar confirmar
        if (controller.confirmarCita(idCita)) {
            JOptionPane.showMessageDialog(this, 
                "Cita confirmada exitosamente.\n" +
                "Estado cambiado de " + cita.getEstado() + " a CONFIRMADA", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(this, 
                "No se pudo confirmar la cita.\n" +
                "Estado actual: " + cita.getEstado(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelarCita() {
        int filaSeleccionada = tablaCitas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para cancelar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idCita = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Cita cita = controller.obtenerCitaPorId(idCita);
        
        // VALIDACIONES MEJORADAS
        if (cita == null) {
            JOptionPane.showMessageDialog(this, "La cita seleccionada no existe", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cita.getEstado() == EstadoCita.CANCELADA) {
            JOptionPane.showMessageDialog(this, 
                "La cita ya está CANCELADA", 
                "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (cita.getEstado() == EstadoCita.ATENDIDA) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "La cita ya fue ATENDIDA.\n" +
                "¿Está seguro que desea cancelarla de todas formas?\n" +
                "Esto puede afectar los registros.", 
                "Advertencia", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        // Confirmar cancelación
        String mensaje = String.format(
            "¿Está seguro que desea cancelar esta cita?\n\n" +
            "Paciente: %s\n" +
            "Odontólogo: %s\n" +
            "Fecha: %s %s\n" +
            "Estado actual: %s",
            cita.getPaciente().getNombre(),
            cita.getOdontologo().getNombre(),
            cita.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            cita.getHora().format(DateTimeFormatter.ofPattern("HH:mm")),
            cita.getEstado()
        );
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            mensaje, 
            "Confirmar Cancelación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (controller.cancelarCita(idCita)) {
                JOptionPane.showMessageDialog(this, 
                    "Cita cancelada exitosamente.\n" +
                    "El horario ahora está disponible nuevamente.", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo cancelar la cita", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void mostrarDialogoRegistrarLlegada() {
        int filaSeleccionada = tablaCitas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idCita = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Cita cita = controller.obtenerCitaPorId(idCita);
        
        // VALIDACIONES MEJORADAS
        if (cita == null) {
            JOptionPane.showMessageDialog(this, "La cita seleccionada no existe", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cita.getEstado() == EstadoCita.CANCELADA) {
            JOptionPane.showMessageDialog(this, 
                "No se puede registrar llegada de una cita CANCELADA", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cita.getEstado() == EstadoCita.ATENDIDA) {
            JOptionPane.showMessageDialog(this, 
                "La cita ya fue ATENDIDA.\n" +
                "Hora de llegada registrada: " + 
                (cita.getHoraLlegadaPaciente() != null ? 
                    cita.getHoraLlegadaPaciente().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A"), 
                "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (!cita.getFecha().equals(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, 
                "Solo se puede registrar llegada de citas del día actual.\n" +
                "Fecha de la cita: " + cita.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                "Fecha actual: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cita.getHoraLlegadaPaciente() != null) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "Ya existe un registro de llegada: " + 
                cita.getHoraLlegadaPaciente().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n" +
                "¿Desea actualizarlo?",
                "Advertencia",
                JOptionPane.YES_NO_OPTION);
            
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        String horaStr = JOptionPane.showInputDialog(this, 
            "Ingrese la hora de llegada (HH:mm):", 
            LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        
        if (horaStr != null && !horaStr.trim().isEmpty()) {
            try {
                LocalTime horaLlegada = LocalTime.parse(horaStr, DateTimeFormatter.ofPattern("HH:mm"));
                
                if (controller.registrarLlegada(idCita, horaLlegada)) {
                    int retraso = cita.calcularMinutosRetraso();
                    String mensajeRetraso = "";
                    
                    if (retraso > 0) {
                        mensajeRetraso = "\n⚠️ El paciente llegó " + retraso + " minuto(s) tarde";
                    } else if (retraso < 0) {
                        mensajeRetraso = "\n✓ El paciente llegó " + Math.abs(retraso) + " minuto(s) temprano";
                    } else {
                        mensajeRetraso = "\n✓ El paciente llegó puntualmente";
                    }
                    
                    JOptionPane.showMessageDialog(this, 
                        "Llegada registrada exitosamente" + mensajeRetraso, 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "No se pudo registrar la llegada.\n" +
                        "NOTA: Si llegó más de 15 minutos tarde,\n" +
                        "la cita fue automáticamente CANCELADA.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    cargarDatos(); // Actualizar para mostrar el cambio de estado
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Formato de hora inválido. Use HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void evaluarAsistencia() {
        int filaSeleccionada = tablaCitas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idCita = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Cita cita = controller.obtenerCitaPorId(idCita);
        
        // VALIDACIONES MEJORADAS
        if (cita == null) {
            JOptionPane.showMessageDialog(this, "La cita seleccionada no existe", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cita.getEstado() == EstadoCita.CANCELADA) {
            JOptionPane.showMessageDialog(this, 
                "No se puede evaluar asistencia de una cita CANCELADA", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cita.getEstado() == EstadoCita.ATENDIDA) {
            JOptionPane.showMessageDialog(this, 
                "La cita ya fue marcada como ATENDIDA", 
                "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (cita.getEstado() == EstadoCita.AUSENTE) {
            JOptionPane.showMessageDialog(this, 
                "La cita ya fue marcada como AUSENTE", 
                "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Confirmar evaluación
        String mensaje;
        if (cita.getHoraLlegadaPaciente() != null) {
            mensaje = String.format(
                "El paciente registró llegada a las %s\n" +
                "¿Marcar esta cita como ATENDIDA?",
                cita.getHoraLlegadaPaciente().format(DateTimeFormatter.ofPattern("HH:mm"))
            );
        } else {
            mensaje = "No hay registro de llegada del paciente.\n" +
                      "¿Marcar esta cita como AUSENTE?";
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            mensaje, 
            "Evaluar Asistencia", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (controller.evaluarAsistencia(idCita)) {
                String nuevoEstado = cita.getHoraLlegadaPaciente() != null ? "ATENDIDA" : "AUSENTE";
                JOptionPane.showMessageDialog(this, 
                    "Asistencia evaluada exitosamente.\n" +
                    "Estado cambiado a: " + nuevoEstado, 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo evaluar la asistencia", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
