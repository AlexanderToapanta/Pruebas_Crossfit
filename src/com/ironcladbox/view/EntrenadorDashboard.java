package com.ironcladbox.view;

import com.ironcladbox.controller.AuthController;
import com.ironcladbox.controller.EntrenadorController;
import com.ironcladbox.model.*;
import com.ironcladbox.service.*;
import com.ironcladbox.dto.ApiResponse;
import com.google.gson.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EntrenadorDashboard extends JFrame {
    private Usuario usuarioActual;
    private Entrenador entrenadorActual;
    private AuthController authController;
    private EntrenadorController entrenadorController;
    private JTabbedPane tabbedPane;

    private static final Color BG = new Color(0x11, 0x11, 0x13);
    private static final Color CARD_BG = new Color(0x1C, 0x1C, 0x1E);
    private static final Color RED = new Color(0xFF, 0x3B, 0x30);
    private static final Color GRAY = new Color(0xB0, 0xB0, 0xB5);
    private static final Color DARK = new Color(0x0A, 0x0A, 0x0C);

    public EntrenadorDashboard() {
        authController = AuthController.getInstance();
        entrenadorController = new EntrenadorController();
        entrenadorController.setOnDataChanged(() -> { dispose(); new EntrenadorDashboard().setVisible(true); });
        usuarioActual = authController.getUsuarioActual();
        entrenadorActual = (Entrenador) usuarioActual;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("IroncladBox - Dashboard Entrenador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);

        if (ApiService.getInstance().isOffline()) {
            int pending = ApiService.getInstance().getPendingCount();
            JLabel offlineLabel = new JLabel(pending > 0 ? "  SIN CONEXION - " + pending + " cambios pendientes" : "  SIN CONEXION - Datos en cache", SwingConstants.CENTER);
            offlineLabel.setOpaque(true);
            offlineLabel.setBackground(new Color(200, 120, 0));
            offlineLabel.setForeground(Color.WHITE);
            offlineLabel.setFont(new Font("Arial", Font.BOLD, 12));
            mainPanel.add(offlineLabel, BorderLayout.NORTH);
        }

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG);
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        tabbedPane.addTab("Inicio", createHomeTab());
        tabbedPane.addTab("Mis Clases", createClassesTab());
        tabbedPane.addTab("WODs", createWodsTab());
        tabbedPane.addTab("Mis Atletas", createMyAthletesTab());
        tabbedPane.addTab("Ejercicios", createExercisesTab());
        tabbedPane.addTab("Mi Perfil", createProfileTab());

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(DARK);
        footer.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, RED));
        JLabel userLabel = new JLabel("  Entrenador: " + usuarioActual.getNombreCompleto());
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        footer.add(userLabel, BorderLayout.WEST);
        JButton logoutBtn = new JButton("Cerrar Sesion");
        logoutBtn.setBackground(RED);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 10));
        logoutBtn.addActionListener(e -> { authController.logout(); dispose(); new LoginView(); });
        footer.add(logoutBtn, BorderLayout.EAST);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);
        add(mainPanel);
        setVisible(true);
    }

    private JPanel createHomeTab() { return sectionPanel("Bienvenido, " + usuarioActual.getNombreCompleto(), "Entrenador de IroncladBox"); }

    private JPanel createClassesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        String[] cols = {"ID", "Nombre", "Dia", "Horario", "Capacidad", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = styledTable(model);
        loadClasses(model);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btns.setBackground(BG);
        JButton addBtn = actionBtn("+ Nueva Clase");
        addBtn.addActionListener(e -> { showAddClassDialog(); loadClasses(model); });
        btns.add(addBtn);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void loadClasses(DefaultTableModel model) {
        model.setRowCount(0);
        for (Clase c : entrenadorController.obtenerMisClases(entrenadorActual.getIdEntrenador())) {
            model.addRow(new Object[]{c.getIdClase(), c.getNombre(), c.getDiaSemana(), c.getHorarioInicio() + "-" + c.getHorarioFin(), c.getCapacidadMaxima(), c.isActiva() ? "ACTIVA" : "CANCELADA"});
        }
    }

    private void showAddClassDialog() {
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField timeField = new JTextField("07:00");
        JTextField capField = new JTextField("20");
        Object[] fields = {"Nombre:", nameField, "Descripcion:", descField, "Fecha:", dateField, "Hora:", timeField, "Cupo:", capField};
        JPanel form = createForm(fields);
        if (JOptionPane.showConfirmDialog(this, form, "Nueva Clase", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Clase c = new Clase(nameField.getText(), descField.getText(), entrenadorActual.getIdEntrenador(), LocalTime.parse(timeField.getText()), LocalTime.parse(timeField.getText()).plusHours(1), dateField.getText(), Integer.parseInt(capField.getText()));
                entrenadorController.crearClase(c);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        }
    }

    private JPanel createWodsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        String[] cols = {"ID", "Titulo", "Fecha", "Tipo", "Nivel"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = styledTable(model);
        loadWods(model);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(BG);
        JComboBox<String> monthBox = new JComboBox<>(new String[]{"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"});
        monthBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        JButton loadBtn = actionBtn("Cargar");
        loadBtn.addActionListener(e -> loadWods(model, LocalDate.now().getYear(), monthBox.getSelectedIndex() + 1));
        top.add(new JLabel("Mes:")); top.add(monthBox); top.add(loadBtn);
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadWods(DefaultTableModel model) { loadWods(model, LocalDate.now().getYear(), LocalDate.now().getMonthValue()); }

    private void loadWods(DefaultTableModel model, int y, int m) {
        model.setRowCount(0);
        try {
            ApiResponse resp = WodApiService.getInstance().getByMonth(y, m);
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                for (JsonElement e : resp.data.getAsJsonArray()) {
                    JsonObject w = e.getAsJsonObject();
                    model.addRow(new Object[]{w.has("id_wod")?w.get("id_wod").getAsInt():0, w.has("titulo")?w.get("titulo").getAsString():"", w.has("fecha")?w.get("fecha").getAsString():"", w.has("tipo")?w.get("tipo").getAsString():"", w.has("nivel")?w.get("nivel").getAsString():""});
                }
            }
        } catch (Exception ex) {}
    }

    private JPanel createMyAthletesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        String[] cols = {"ID", "Nombre", "Email"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        try {
            ApiResponse resp = TrainerApiService.getInstance().getMyAthletes();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                for (JsonElement e : resp.data.getAsJsonArray()) {
                    JsonObject a = e.getAsJsonObject();
                    model.addRow(new Object[]{a.has("id_atleta")?a.get("id_atleta").getAsInt():0, (a.has("nombre")?a.get("nombre").getAsString():"")+" "+(a.has("apellido")?a.get("apellido").getAsString():""), a.has("email")?a.get("email").getAsString():""});
                }
            }
        } catch (Exception ex) {}
        JTable table = styledTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createExercisesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        String[] cols = {"ID", "Nombre", "Descripcion"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        try {
            ApiResponse resp = ExerciseApiService.getInstance().getAll();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                for (JsonElement e : resp.data.getAsJsonArray()) {
                    JsonObject ex = e.getAsJsonObject();
                    model.addRow(new Object[]{ex.has("id_ejercicio")?ex.get("id_ejercicio").getAsInt():0, ex.has("nombre")?ex.get("nombre").getAsString():"", ex.has("descripcion")?ex.get("descripcion").getAsString():""});
                }
            }
        } catch (Exception ex) {}
        JTable table = styledTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createProfileTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;
        addRow(panel, "Nombre:", usuarioActual.getNombre(), 0, gbc);
        addRow(panel, "Apellido:", usuarioActual.getApellido(), 1, gbc);
        addRow(panel, "Email:", usuarioActual.getEmail(), 2, gbc);
        addRow(panel, "Especialidad:", entrenadorActual.getEspecialidad() != null ? entrenadorActual.getEspecialidad() : "N/A", 3, gbc);
        addRow(panel, "Experiencia:", entrenadorActual.getExperienciaAnios() + " anos", 4, gbc);
        return panel;
    }

    private void addRow(JPanel p, String label, String val, int row, GridBagConstraints gbc) {
        JLabel l = new JLabel(label); l.setForeground(RED); l.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = row; p.add(l, gbc);
        JLabel v = new JLabel(val); v.setForeground(Color.WHITE); v.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1; p.add(v, gbc);
    }

    private JPanel sectionPanel(String title, String subtitle) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 20));
        t.setForeground(Color.WHITE);
        panel.add(t);
        return panel;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(CARD_BG);
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(0x3A, 0x3A, 0x3C));
        table.setSelectionBackground(RED);
        table.setRowHeight(26);
        table.getTableHeader().setBackground(DARK);
        table.getTableHeader().setForeground(RED);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        return table;
    }

    private JButton actionBtn(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(RED); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 10));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setFocusPainted(false);
        return btn;
    }

    private JPanel createForm(Object[] rows) {
        JPanel panel = new JPanel(new GridLayout(rows.length/2, 2, 8, 6));
        panel.setBackground(BG);
        for (int i = 0; i < rows.length; i += 2) {
            JLabel lbl = new JLabel((String)rows[i]); lbl.setForeground(GRAY); lbl.setFont(new Font("Arial", Font.PLAIN, 11)); panel.add(lbl);
            JTextField tf = (JTextField)rows[i+1];
            tf.setBackground(CARD_BG); tf.setForeground(Color.WHITE); tf.setCaretColor(RED);
            tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0x3A,0x3A,0x3C),1), BorderFactory.createEmptyBorder(4,6,4,6)));
            tf.setFont(new Font("Arial", Font.PLAIN, 11));
            panel.add(tf);
        }
        return panel;
    }
}
