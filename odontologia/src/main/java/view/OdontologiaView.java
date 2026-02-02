package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class OdontologiaView extends JPanel {

    // Componentes principales
    private JTable tablaAgenda;
    private DefaultTableModel modeloTabla;

    private JComboBox<String> comboFecha;
    private JLabel lblOdontologo;

    private JButton btnConfirmar;
    private JButton btnCancelar;

    public OdontologiaView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initEncabezado();
        initTablaAgenda();
        initAcciones();
    }

    // =========================
    // Encabezado
    // =========================
    private void initEncabezado() {
        JPanel panelTop = new JPanel(new GridLayout(2, 2, 10, 10));
        panelTop.setBorder(BorderFactory.createTitledBorder("Agenda del Odontólogo"));
        panelTop.setBackground(Color.WHITE);

        JLabel lblFecha = new JLabel("Fecha:");
        comboFecha = new JComboBox<>();
        // El controlador cargará las fechas disponibles

        JLabel lblDoctorTitulo = new JLabel("Odontólogo:");
        lblOdontologo = new JLabel("No seleccionado");

        panelTop.add(lblFecha);
        panelTop.add(comboFecha);
        panelTop.add(lblDoctorTitulo);
        panelTop.add(lblOdontologo);

        add(panelTop, BorderLayout.NORTH);
    }

    // =========================
    // Tabla de Agenda
    // =========================
    private void initTablaAgenda() {
        String[] columnas = {"Hora", "Estado", "Paciente"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabla solo lectura
            }
        };

        tablaAgenda = new JTable(modeloTabla);
        tablaAgenda.setRowHeight(25);
        tablaAgenda.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tablaAgenda);
        scroll.setBorder(BorderFactory.createTitledBorder("Horarios del día"));

        add(scroll, BorderLayout.CENTER);
    }

    // =========================
    // Botones de Acción
    // =========================
    private void initAcciones() {
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBottom.setBackground(Color.WHITE);

        btnConfirmar = new JButton("Confirmar Cita");
        btnCancelar = new JButton("Cancelar Cita");

        panelBottom.add(btnConfirmar);
        panelBottom.add(btnCancelar);

        add(panelBottom, BorderLayout.SOUTH);
    }

    // =========================
    // Métodos para el Controller
    // =========================

    public JTable getTablaAgenda() {
        return tablaAgenda;
    }

    public DefaultTableModel getModeloTabla() {
        return modeloTabla;
    }

    public JComboBox<String> getComboFecha() {
        return comboFecha;
    }

    public JButton getBtnConfirmar() {
        return btnConfirmar;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }

    public void setNombreOdontologo(String nombre) {
        lblOdontologo.setText(nombre);
    }
}
