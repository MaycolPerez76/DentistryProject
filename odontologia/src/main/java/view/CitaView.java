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
 * Vista para gestionar citas con interfaz gráfica (JPanel) CORREGIDA:
 * Validaciones mejoradas y mensajes descriptivos
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
    private JButton btnAsignarMonto;

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
        JLabel lblTitulo = new JLabel("GESTION DE CITAS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panelFiltros.add(new JLabel("Filtrar por estado:"));
        cmbFiltroEstado = new JComboBox<>(new String[]{
            "Todos", "PENDIENTE", "CONFIRMADA", "CANCELADA",});
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

        btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarDatos());
        panelFiltros.add(btnActualizar);

        panel.add(panelFiltros, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());

        // Modelo de tabla
        String[] columnas = {"ID", "Paciente", "Odontólogo", "Fecha", "Hora", "Motivo", "Estado"};
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

        JScrollPane scrollPane = new JScrollPane(tablaCitas);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnCrear = new JButton("Crear Cita");
        btnCrear.addActionListener(e -> mostrarDialogoCrearCita());
        panel.add(btnCrear);

        btnReprogramar = new JButton("Reprogramar");
        btnReprogramar.addActionListener(e -> mostrarDialogoReprogramar());
        panel.add(btnReprogramar);

        btnConfirmar = new JButton("Confirmar");
        btnConfirmar.addActionListener(e -> confirmarCita());
        panel.add(btnConfirmar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> cancelarCita());
        panel.add(btnCancelar);

        // ⭐ NUEVO BOTÓN - ELIMINAR
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.addActionListener(e -> eliminarCita());
        panel.add(btnEliminar);

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
        //Filtracion del estado de busqueda (PENDIENTE, CANCELADA, CONFIRMADA)
        String filtroEstado = (String) cmbFiltroEstado.getSelectedItem();
        String buscarPaciente = txtBuscarPaciente.getText().toLowerCase().trim();

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

        for (Cita cita : citas) {
            // Filtrar por estado (se compara por defecto por seguridad)
            if (!filtroEstado.equals("Todos") && !cita.getEstado().toString().equals(filtroEstado)) {
                continue;
            }

            // Filtrar por nombre de paciente
            if (!buscarPaciente.isEmpty()) {
                //Obtiene el nombre del paciente, en caso que este vacio entonces no retornara nada.
                String nombrePaciente = cita.getPaciente() != null ? cita.getPaciente().getNombre().toLowerCase() : "";
                //Si no esta el nombre entonces lo deja pasar sin retornar nada.
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
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialogo.add(new JLabel("Paciente:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cmbPaciente = new JComboBox<>();
        for (Paciente p : db.getPacientes().values()) {
            cmbPaciente.addItem(p.getId() + " - " + p.getNombre());
        }
        dialogo.add(cmbPaciente, gbc);

        // Odontólogo
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialogo.add(new JLabel("Odontólogo:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cmbOdontologo = new JComboBox<>();
        for (Odontologo o : db.getOdontologos().values()) {
            cmbOdontologo.addItem(o.getId() + " - " + o.getNombre());
        }
        dialogo.add(cmbOdontologo, gbc);

        // Fecha
        gbc.gridx = 0;
        gbc.gridy = 2;
        dialogo.add(new JLabel("Fecha (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        JTextField txtFecha = new JTextField(15);
        txtFecha.setText(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dialogo.add(txtFecha, gbc);

        // Hora
        gbc.gridx = 0;
        gbc.gridy = 3;
        dialogo.add(new JLabel("Hora (HH:mm):"), gbc);
        gbc.gridx = 1;
        JTextField txtHora = new JTextField(15);
        txtHora.setText("09:00");
        dialogo.add(txtHora, gbc);

        // Motivo
        gbc.gridx = 0;
        gbc.gridy = 4;
        dialogo.add(new JLabel("Motivo:"), gbc);
        gbc.gridx = 1;
        JTextField txtMotivo = new JTextField(15);
        dialogo.add(txtMotivo, gbc);

        // Monto (NUEVO)
        gbc.gridx = 0;
        gbc.gridy = 6;
        dialogo.add(new JLabel("Monto $:"), gbc);
        gbc.gridx = 1;
        JTextField txtMonto = new JTextField(15);
        txtMonto.setText("0.00");
        dialogo.add(txtMonto, gbc);

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

                LocalDate fecha = LocalDate.parse(txtFecha.getText(),
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalTime hora = LocalTime.parse(txtHora.getText(),
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                String motivo = txtMotivo.getText();

                // OBTENER MONTO
                double monto = 0.0;
                try {
                    monto = Double.parseDouble(txtMonto.getText().trim());
                    if (monto < 0) {
                        JOptionPane.showMessageDialog(dialogo,
                                "El monto no puede ser negativo",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialogo,
                            "Formato de monto inválido. Se usará $0.00",
                            "Advertencia",
                            JOptionPane.WARNING_MESSAGE);
                    monto = 0.0;
                }

                // CREAR CITA CON MONTO
                if (controller.crearCitaConMonto(idPaciente, idOdontologo, fecha, hora, motivo, monto)) {
                    JOptionPane.showMessageDialog(dialogo,
                            String.format(
                                    "Cita creada exitosamente\n\n"
                                    + "Factura generada automáticamente\n"
                                    + "Monto: $%.2f",
                                    monto
                            ),
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                    dialogo.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialogo,
                            "No se pudo crear la cita.\nPosibles razones:\n"
                            + "• El odontólogo ya tiene una cita a esa hora\n"
                            + "• La fecha es pasada\n"
                            + "• Datos inválidos",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialogo,
                        "Formato de fecha u hora inválido",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialogo.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        dialogo.add(panelBotones, gbc);

        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    private void mostrarDialogoReprogramar() {
        int filaSeleccionada = tablaCitas.getSelectedRow();
        //No ha seleccionado nada
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para reprogramar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        //Obtiene el primer valor de la columna, en este caso el id.
        int idCita = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Cita cita = controller.obtenerCitaPorId(idCita);

        if (cita == null) {
            JOptionPane.showMessageDialog(this, "La cita seleccionada no existe", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            JOptionPane.showMessageDialog(this,
                    "No se puede reprogramar una cita CANCELADA.\n"
                    + "Debe crear una nueva cita para este paciente.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Reprogramar Cita", true);
        dialogo.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialogo.add(new JLabel("Nueva Fecha (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        JTextField txtFecha = new JTextField(15);
        txtFecha.setText(cita.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dialogo.add(txtFecha, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
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
                            "No se pudo reprogramar la cita.\nPosibles razones:\n"
                            + "• El odontólogo ya tiene una cita a esa hora\n"
                            + "• La nueva fecha es pasada\n"
                            + "• El horario no está disponible",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialogo, "Formato de fecha u hora inválido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialogo.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
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
                    "No se puede confirmar una cita CANCELADA.\n"
                    + "Una cita cancelada no puede cambiar de estado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Intentar confirmar
        if (controller.confirmarCita(idCita)) {
            JOptionPane.showMessageDialog(this,
                    "Cita confirmada exitosamente.\n"
                    ,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo confirmar la cita.\n"
                    + "Estado actual: " + cita.getEstado(),
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

        // Confirmar cancelación
        String mensaje = String.format(
                "¿Está seguro que desea cancelar esta cita?\n\n"
                + "Paciente: %s\n"
                + "Odontólogo: %s\n"
                + "Fecha: %s %s\n"
                + "Estado actual: %s",
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
                        "Cita cancelada exitosamente.\n"
                        + "El horario ahora está disponible nuevamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo cancelar la cita", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarCita() {
        int filaSeleccionada = tablaCitas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una cita para eliminar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCita = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Cita cita = controller.obtenerCitaPorId(idCita);

        // Validaciones importantes
        if (cita == null) {
            JOptionPane.showMessageDialog(this,
                    "La cita seleccionada no existe",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener información de factura asociada
        model.Factura factura = controller.obtenerFacturaPorCita(idCita);
        String infoFactura = "";
        if (factura != null) {
            infoFactura = String.format(
                    "\nFactura asociada #%d\nMonto: $%.2f\n",
                    factura.getId(),
                    factura.getMonto()
            );
        }

        // Mensaje de confirmación con TODA la información
        String mensaje = String.format(
                "ELIMINACIÓN PERMANENTE️\n\n"
                + "ESTA ACCIÓN NO SE PUEDE DESHACER\n\n"
                + "Información de la cita a eliminar:\n"
                + "• ID: %d\n"
                + "• Paciente: %s\n"
                + "• Odontólogo: %s\n"
                + "• Fecha: %s\n"
                + "• Hora: %s\n"
                + "• Motivo: %s\n"
                + "• Estado: %s\n"
                + "%s"
                + "\n¿Está absolutamente seguro que desea ELIMINAR PERMANENTEMENTE esta cita?\n\n"
                + "ADVERTENCIA: Se eliminarán:\n"
                + " La cita\n"
                + " La factura asociada (si existe)\n"
                + " Los registros del horario\n"
                + " Todos los datos relacionados",
                cita.getId(),
                cita.getPaciente().getNombre(),
                cita.getOdontologo().getNombre(),
                cita.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                cita.getHora().format(DateTimeFormatter.ofPattern("HH:mm")),
                cita.getMotivo(),
                cita.getEstado(),
                infoFactura
        );

        // Panel personalizado con checkbox de confirmación
        JCheckBox confirmCheck = new JCheckBox("He leído la advertencia y deseo continuar");
        Object[] options = {"ELIMINAR", "CANCELAR"};

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JLabel("<html><body style='width: 350px'>" + mensaje.replace("\n", "<br>") + "</body></html>"), BorderLayout.CENTER);
        panel.add(confirmCheck, BorderLayout.SOUTH);

        int opcion = JOptionPane.showOptionDialog(
                this,
                panel,
                "CONFIRMAR ELIMINACIÓN PERMANENTE",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1] // Por defecto seleccionar CANCELAR
        );

        // Solo proceder si seleccionó ELIMINAR y marcó el checkbox
        if (opcion == 0 && confirmCheck.isSelected()) {
            try {
                // Usar el método de eliminación completa
                if (controller.eliminarCitaCompletamente(idCita)) {
                    JOptionPane.showMessageDialog(this,
                            String.format(
                                    " ELIMINACIÓN EXITOSA\n\n"
                                    + "Cita #%d eliminada completamente:\n"
                                    + "• Cita removida de la base de datos\n"
                                    + "• Factura eliminada (si existía)\n"
                                    + "• Horario liberado\n"
                                    + "• Cambios guardados en disco\n\n"
                                    + "La tabla se actualizará automáticamente",
                                    idCita
                            ),
                            "Eliminación Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Recargar datos frescos desde disco
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(this,
                            " No se pudo eliminar la cita.\n"
                            + "Puede que ya haya sido eliminada o haya un error en el sistema.",
                            "Error de Eliminación",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        " Error inesperado al eliminar:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else if (opcion == 0 && !confirmCheck.isSelected()) {
            JOptionPane.showMessageDialog(this,
                    "Debe marcar la casilla de confirmación para proceder con la eliminación.",
                    "Confirmación Requerida",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void mostrarDialogoAsignarMonto() {
        int filaSeleccionada = tablaCitas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una cita para asignar monto",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCita = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Cita cita = controller.obtenerCitaPorId(idCita);

        if (cita == null) {
            JOptionPane.showMessageDialog(this,
                    "La cita seleccionada no existe",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar estado de la cita
        if (cita.getEstado() == EstadoCita.CANCELADA) {
            JOptionPane.showMessageDialog(this,
                    "No se puede asignar monto a una cita CANCELADA",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener factura actual
        model.Factura facturaActual = controller.obtenerFacturaPorCita(idCita);
        String montoActual = facturaActual != null
                ? String.format("%.2f", facturaActual.getMonto()) : "0.00";

        // Mostrar información de la cita
        String mensaje = String.format(
                "Cita #%d\n"
                + "Paciente: %s\n"
                + "Odontólogo: %s\n"
                + "Fecha: %s\n"
                + "Estado: %s\n\n"
                + "Monto actual: $%s\n\n"
                + "Ingrese el nuevo monto:",
                cita.getId(),
                cita.getPaciente().getNombre(),
                cita.getOdontologo().getNombre(),
                cita.getFecha().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                cita.getEstado(),
                montoActual
        );

        String inputMonto = JOptionPane.showInputDialog(this, mensaje, montoActual);

        if (inputMonto != null && !inputMonto.trim().isEmpty()) {
            try {
                double nuevoMonto = Double.parseDouble(inputMonto.trim());

                if (nuevoMonto < 0) {
                    JOptionPane.showMessageDialog(this,
                            "El monto no puede ser negativo",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Actualizar monto en la factura
                if (controller.actualizarMontoFactura(idCita, nuevoMonto)) {
                    JOptionPane.showMessageDialog(this,
                            String.format(
                                    "Monto actualizado exitosamente\n\n"
                                    + "Monto anterior: $%.2f\n"
                                    + "Monto nuevo: $%.2f\n\n"
                                    + "Factura guardada automáticamente",
                                    Double.parseDouble(montoActual),
                                    nuevoMonto
                            ),
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos(); // Actualizar tabla
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo actualizar el monto.\n"
                            + "Verifique que la factura exista.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Formato de monto inválido.\n"
                        + "Use números decimales (ej: 150.00 o 150)",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
