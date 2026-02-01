package viewFrame;

import javax.swing.*;
import java.awt.*;

public class FacturaView extends JPanel {

    // Campos visuales (luego el controlador los usará)
    public JTextField txtTitular;
    public JTextField txtExpediente;
    public JTextField txtFecha;
    public JTextField txtHora;
    public JTextField txtTelefono;
    public JTextField txtEstado;
    public JTextField txtTotal;

    public FacturaView() {
        initComponents();
    }

    private void initComponents() {

        setBackground(Color.WHITE);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ================= HEADER =================
        JLabel lblTitulo = new JLabel("Factura");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitulo.setForeground(new Color(33, 33, 33));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.add(lblTitulo, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // ================= CONTENIDO =================
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.WHITE);

        contenido.add(panelTitular());
        contenido.add(Box.createVerticalStrut(15));
        contenido.add(panelCita());
        contenido.add(Box.createVerticalStrut(15));
        contenido.add(panelTotal());

        add(contenido, BorderLayout.CENTER);
    }

    // ================= PANEL TITULAR =================
    private JPanel panelTitular() {

        JPanel panel = crearPanelCard("Datos del Titular");

        JLabel lblTitular = crearLabel("Titular:");
        JLabel lblExpediente = crearLabel("N° Expediente:");

        txtTitular = crearCampo();
        txtExpediente = crearCampo();

        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        panel.add(lblTitular, c);

        c.gridx = 1; c.weightx = 1;
        panel.add(txtTitular, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        panel.add(lblExpediente, c);

        c.gridx = 1; c.weightx = 1;
        panel.add(txtExpediente, c);

        return panel;
    }

    // ================= PANEL CITA =================
    private JPanel panelCita() {

        JPanel panel = crearPanelCard("Información de la Cita");

        JLabel lblFecha = crearLabel("Fecha:");
        JLabel lblHora = crearLabel("Hora:");
        JLabel lblTelefono = crearLabel("Teléfono:");
        JLabel lblEstado = crearLabel("Estado:");

        txtFecha = crearCampo();
        txtHora = crearCampo();
        txtTelefono = crearCampo();
        txtEstado = crearCampo();

        panel.setLayout(new GridLayout(2, 4, 15, 15));

        panel.add(lblFecha);
        panel.add(txtFecha);
        panel.add(lblHora);
        panel.add(txtHora);

        panel.add(lblTelefono);
        panel.add(txtTelefono);
        panel.add(lblEstado);
        panel.add(txtEstado);

        return panel;
    }

    // ================= PANEL TOTAL =================
    private JPanel panelTotal() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(232, 245, 253));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(33, 150, 243)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTotal = new JLabel("TOTAL");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(new Color(33, 150, 243));

        txtTotal = new JTextField();
        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtTotal.setFont(new Font("Segoe UI", Font.BOLD, 32));
        txtTotal.setForeground(new Color(33, 150, 243));
        txtTotal.setBorder(BorderFactory.createEmptyBorder());
        txtTotal.setBackground(new Color(232, 245, 253));

        panel.add(lblTotal, BorderLayout.NORTH);
        panel.add(txtTotal, BorderLayout.CENTER);

        return panel;
    }

    // ================= UTILIDADES DE DISEÑO =================
    private JPanel crearPanelCard(String titulo) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(218, 221, 225)),
                titulo
        ));
        return panel;
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(new Color(90, 90, 90));
        return lbl;
    }

    private JTextField crearCampo() {
        JTextField txt = new JTextField();
        txt.setEditable(false);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBackground(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return txt;
    }
}
