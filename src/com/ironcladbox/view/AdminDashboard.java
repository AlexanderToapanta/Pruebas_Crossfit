package com.ironcladbox.view;

import com.ironcladbox.controller.AdminController;
import com.ironcladbox.controller.AuthController;
import com.ironcladbox.model.*;
import com.ironcladbox.service.*;
import com.ironcladbox.dto.ApiResponse;
import com.ironcladbox.util.UIStyles;
import com.google.gson.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminDashboard extends JFrame {
    private AdminController adminController;
    private AuthController authController;
    private Usuario usuarioActual;
    private JTabbedPane tabbedPane;
    private JLabel totalAthletes, activeAthletes, totalTrainers, totalWods, totalMemberships;
    private DefaultTableModel athleteModel, trainerModel, membershipModel, exerciseModel, classModel;

    private static final Color BG = new Color(0x11, 0x11, 0x13);
    private static final Color CARD_BG = new Color(0x1C, 0x1C, 0x1E);
    private static final Color RED = new Color(0xFF, 0x3B, 0x30);
    private static final Color GRAY = new Color(0xB0, 0xB0, 0xB5);
    private static final Color DARK = new Color(0x0A, 0x0A, 0x0C);

    public AdminDashboard() {
        authController = AuthController.getInstance();
        adminController = new AdminController();
        usuarioActual = authController.getUsuarioActual();
        adminController.setOnDataChanged(() -> refreshAllTabs());
        new javax.swing.Timer(15000, e -> refreshAllTabs()).start();
        initializeUI();
    }

    private void refreshAllTabs() {
        SwingUtilities.invokeLater(() -> {
            if (athleteModel != null) loadAthletes(athleteModel);
            if (trainerModel != null) loadTrainers(trainerModel);
            refreshWodCalendar();
            if (membershipModel != null) loadMemberships(membershipModel);
            if (exerciseModel != null) loadExercises(exerciseModel);
            if (classModel != null) loadClasses(classModel);
        });
    }

    private void initializeUI() {
        setTitle("IroncladBox - Panel de Administracion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);

        if (ApiService.getInstance().isOffline()) {
            int pending = com.ironcladbox.service.ApiService.getInstance().getPendingCount();
            String text = pending > 0 ? "  SIN CONEXION - " + pending + " cambios pendientes" : "  SIN CONEXION - Datos en cache";
            JLabel offlineLabel = new JLabel(text);
            offlineLabel.setOpaque(true);
            offlineLabel.setBackground(new Color(200, 120, 0));
            offlineLabel.setForeground(Color.WHITE);
            offlineLabel.setFont(new Font("Arial", Font.BOLD, 12));
            offlineLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(offlineLabel, BorderLayout.NORTH);
        }

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG);
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        tabbedPane.addTab("Inicio", createDashboardTab());
        tabbedPane.addTab("Atletas", createAthletesTab());
        tabbedPane.addTab("Entrenadores", createTrainersTab());
        tabbedPane.addTab("WODs", createWodsTab());
        tabbedPane.addTab("Membresias", createMembershipsTab());
        tabbedPane.addTab("Ejercicios", createExercisesTab());
        tabbedPane.addTab("Clases", createClassesTab());
        tabbedPane.addTab("Mi Perfil", createProfileTab());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, RED));
        JLabel userLabel = new JLabel("  Admin: " + (usuarioActual != null ? usuarioActual.getNombreCompleto() : ""));
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        header.add(userLabel, BorderLayout.WEST);
        JButton logoutBtn = new JButton("Cerrar Sesion");
        logoutBtn.setBackground(RED);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 10));
        logoutBtn.addActionListener(e -> logout());
        header.add(logoutBtn, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.SOUTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) refreshStats();
        });
    }

    // ============ TAB: DASHBOARD (INICIO) ============
    private JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 12, 0));
        statsPanel.setBackground(BG);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        totalAthletes = statLabel("0");
        activeAthletes = statLabel("0");
        totalTrainers = statLabel("0");
        totalWods = statLabel("0");
        totalMemberships = statLabel("0");

        statsPanel.add(statCard("Atletas", totalAthletes, "👥"));
        statsPanel.add(statCard("Activos", activeAthletes, "✅"));
        statsPanel.add(statCard("Entrenadores", totalTrainers, "🏅"));
        statsPanel.add(statCard("WODs", totalWods, "🏋️"));
        statsPanel.add(statCard("Membresias", totalMemberships, "💳"));

        panel.add(statsPanel, BorderLayout.NORTH);

        JButton refreshBtn = new JButton("Actualizar Estadisticas");
        refreshBtn.setBackground(RED);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> refreshStats());
        panel.add(refreshBtn, BorderLayout.SOUTH);

        refreshStats();
        return panel;
    }

    private void refreshStats() {
        try {
            ApiResponse resp = MembershipApiService.getInstance().getStats();
            if (resp.isOk() && resp.data != null && resp.data.isJsonObject()) {
                JsonObject s = resp.data.getAsJsonObject();
                totalAthletes.setText(String.valueOf(s.has("totalAthletes") ? s.get("totalAthletes").getAsInt() : 0));
                activeAthletes.setText(String.valueOf(s.has("activeAthletes") ? s.get("activeAthletes").getAsInt() : 0));
                totalTrainers.setText(String.valueOf(s.has("totalTrainers") ? s.get("totalTrainers").getAsInt() : 0));
                totalWods.setText(String.valueOf(s.has("totalWODs") ? s.get("totalWODs").getAsInt() : 0));
                totalMemberships.setText(String.valueOf(s.has("totalMemberships") ? s.get("totalMemberships").getAsInt() : 0));
            }
        } catch (Exception e) {
            totalAthletes.setText("--");
            activeAthletes.setText("--");
            totalTrainers.setText("--");
            totalWods.setText("--");
            totalMemberships.setText("--");
        }
    }

    private JLabel statLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 32));
        label.setForeground(RED);
        return label;
    }

    private JPanel statCard(String title, JLabel value, String icon) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x3A, 0x3A, 0x3C), 1),
            BorderFactory.createEmptyBorder(16, 12, 16, 12)));
        JLabel i = new JLabel(icon);
        i.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        i.setAlignmentX(CENTER_ALIGNMENT);
        card.add(i);
        value.setAlignmentX(CENTER_ALIGNMENT);
        card.add(value);
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 11));
        t.setForeground(GRAY);
        t.setAlignmentX(CENTER_ALIGNMENT);
        card.add(t);
        return card;
    }

        // ============ TAB: ATLETAS ============
    private javax.swing.JTextField athleteSearchField;
    private javax.swing.JCheckBox expiredCheck;
    
    private JPanel createAthletesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        String[] cols = {"ID", "Nombre", "Apellido", "Email", "Telefono", "Membresia", "Vigencia", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        athleteModel = model;
        JTable table = styledTable(model);
        setColumnWidths(table, new int[]{40, 80, 80, 140, 80, 100, 70, 50});
        loadAthletes(model);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG);
        athleteSearchField = new JTextField();
        athleteSearchField.setBackground(CARD_BG);
        athleteSearchField.setForeground(Color.WHITE);
        athleteSearchField.setCaretColor(Color.WHITE);
        athleteSearchField.addActionListener(e -> loadAthletes(model));
        athleteSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void update() { loadAthletes(model); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BG);
        athleteSearchField.setPreferredSize(new Dimension(300, 28));
        searchPanel.add(athleteSearchField);
        expiredCheck = new JCheckBox("Solo vencidas");
        expiredCheck.setBackground(BG);
        expiredCheck.setForeground(Color.WHITE);
        expiredCheck.addActionListener(e -> loadAthletes(model));
        searchPanel.add(expiredCheck);
        topPanel.add(searchPanel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(BG);
        JButton addBtn = actionBtn("+ Agregar Atleta");
        addBtn.addActionListener(e -> showAddAthleteDialog(model));
        JButton editBtn = actionBtn("Editar");
        editBtn.addActionListener(e -> { int row = table.getSelectedRow(); if (row >= 0) showEditAthleteDialog(row, model, table); });
        JButton memberBtn = actionBtn("Asignar Membresia");
        memberBtn.addActionListener(e -> { int row = table.getSelectedRow(); if (row >= 0) showAssignMembershipDialog(table, model, row); });
        JButton statusBtn = actionBtn("Activar/Desactivar");
        statusBtn.addActionListener(e -> { int row = table.getSelectedRow(); if (row >= 0) toggleAthleteStatus(table, model, row); });
        JButton delBtn = actionBtn("Eliminar");
        delBtn.setBackground(new Color(180, 40, 40));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "Eliminar atleta?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                int id = (int) model.getValueAt(row, 0);
                if (adminController.eliminarAtleta(id)) { model.removeRow(row); }
            }
        });
        JButton deactivateBtn = actionBtn("Desactivar Vencidos");
        deactivateBtn.setBackground(new Color(180, 100, 0));
        deactivateBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Desactivar todos los atletas con membresia vencida?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                ApiResponse resp = MembershipApiService.getInstance().deactivateExpired();
                if (resp != null && resp.isOk()) {
                    JOptionPane.showMessageDialog(this, "Atletas vencidos desactivados.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al desactivar: " + (resp != null ? resp.message : ""), "Error", JOptionPane.ERROR_MESSAGE);
                }
                loadAthletes(model);
            }
        });
        btnPanel.add(addBtn); btnPanel.add(editBtn); btnPanel.add(memberBtn); btnPanel.add(statusBtn); btnPanel.add(delBtn); btnPanel.add(deactivateBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(wrapTable(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadAthletes(DefaultTableModel model) {
        model.setRowCount(0);
        String search = athleteSearchField != null ? athleteSearchField.getText().trim().toLowerCase() : "";
        boolean onlyExpired = expiredCheck != null && expiredCheck.isSelected();
        List<Atleta> list = adminController.obtenerTodosAtletas();
        for (Atleta a : list) {
            if (onlyExpired && !"Vencida".equals(a.getVigenciaMembresia())) continue;
            if (!search.isEmpty()) {
                String nombre = (a.getNombre() != null ? a.getNombre() : "").toLowerCase();
                String apellido = (a.getApellido() != null ? a.getApellido() : "").toLowerCase();
                String email = (a.getEmail() != null ? a.getEmail() : "").toLowerCase();
                String membersh = (a.getNombreMembresia() != null ? a.getNombreMembresia() : "").toLowerCase();
                if (!nombre.contains(search) && !apellido.contains(search) && !email.contains(search) && !membersh.contains(search)) {
                    continue;
                }
            }
            model.addRow(new Object[]{a.getIdAtleta(), a.getNombre(), a.getApellido(), a.getEmail(),
                a.getTelefono() != null ? a.getTelefono() : "", 
                a.getNombreMembresia() != null ? a.getNombreMembresia() : "Sin membresia",
                a.getVigenciaMembresia(),
                a.isActivo() ? "Activo" : "Inactivo"});
        }
    }

    private void showAddAthleteDialog(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField fechaField = new JTextField();
        JTextField pesoField = new JTextField();
        JTextField alturaField = new JTextField();
        JTextField dirField = new JTextField();
        JTextField emergField = new JTextField();
        Object[] fields = {"Nombre:", nameField, "Apellido:", lastNameField, "Email:", emailField, "Telefono:", phoneField, "Fecha Nac. (YYYY-MM-DD):", fechaField, "Peso:", pesoField, "Altura:", alturaField, "Direccion:", dirField, "Emergencia:", emergField};
        JPanel form = createFormPanel(fields);

        int result = JOptionPane.showConfirmDialog(this, form, "Nuevo Atleta", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String fechaNac = fechaField.getText().trim();
                if (fechaNac.isEmpty()) fechaNac = LocalDate.now().minusYears(20).toString();
                String[] dateParts = fechaNac.split("-");
                String password = dateParts.length == 3 ? dateParts[2] + dateParts[1] + dateParts[0] : "00000000";
                JsonObject body = new JsonObject();
                body.addProperty("nombre", nameField.getText().trim());
                body.addProperty("apellido", lastNameField.getText().trim());
                body.addProperty("email", emailField.getText().trim());
                body.addProperty("fecha_nacimiento", fechaNac);
                body.addProperty("telefono", phoneField.getText().trim());
                String pesoStr = pesoField.getText().trim();
                if (!pesoStr.isEmpty()) {
                    try { body.addProperty("peso", Double.parseDouble(pesoStr)); } catch (NumberFormatException e) { body.addProperty("peso", (Number) null); }
                }
                String alturaStr = alturaField.getText().trim();
                if (!alturaStr.isEmpty()) {
                    try { body.addProperty("altura", Double.parseDouble(alturaStr)); } catch (NumberFormatException e) { body.addProperty("altura", (Number) null); }
                }
                String dirStr = dirField.getText().trim();
                if (!dirStr.isEmpty()) body.addProperty("direccion", dirStr);
                String emergStr = emergField.getText().trim();
                if (!emergStr.isEmpty()) body.addProperty("contacto_emergencia", emergStr);
                ApiResponse resp = AthleteApiService.getInstance().create(body);
                if (resp != null && resp.isQueued()) {
                    JOptionPane.showMessageDialog(this, "Sin conexion. El atleta se guardara al reconectar. Contrasena: " + password, "Aviso", JOptionPane.WARNING_MESSAGE);
                } else if (resp == null || !resp.isOk()) {
                    JOptionPane.showMessageDialog(this, "Error al crear atleta: " + (resp != null ? resp.message : "Sin respuesta"), "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Atleta creado. Su contrasena es: " + password + " (ddmmaaaa)");
                }
                loadAthletes(model);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void showEditAthleteDialog(int row, DefaultTableModel model, JTable table) {
        Atleta a = adminController.obtenerTodosAtletas().get(row);
        JTextField nameField = new JTextField(a.getNombre());
        JTextField lastNameField = new JTextField(a.getApellido());
        JTextField emailField = new JTextField(a.getEmail());
        JTextField phoneField = new JTextField(a.getTelefono() != null ? a.getTelefono() : "");
        JTextField pesoField = new JTextField(a.getPeso() > 0 ? String.valueOf(a.getPeso()) : "");
        JTextField alturaField = new JTextField(a.getAltura() > 0 ? String.valueOf(a.getAltura()) : "");
        JTextField dirField = new JTextField(a.getDireccion() != null ? a.getDireccion() : "");
        JTextField emergField = new JTextField(a.getContactoEmergencia() != null ? a.getContactoEmergencia() : "");
        JTextField fechaNacField = new JTextField(a.getFechaNacimiento() != null ? a.getFechaNacimiento().toString() : "");
        Object[] fields = {"Nombre:", nameField, "Apellido:", lastNameField, "Email:", emailField, "Telefono:", phoneField, "Peso:", pesoField, "Altura:", alturaField, "Direccion:", dirField, "Emergencia:", emergField, "Fecha Nac. (YYYY-MM-DD):", fechaNacField};
        JPanel form = createFormPanel(fields);

        if (JOptionPane.showConfirmDialog(this, form, "Editar Atleta", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            JsonObject body = new JsonObject();
            body.addProperty("nombre", nameField.getText().trim());
            body.addProperty("apellido", lastNameField.getText().trim());
            body.addProperty("email", emailField.getText().trim());
            body.addProperty("telefono", phoneField.getText().trim());
            String pesoStr = pesoField.getText().trim();
            if (!pesoStr.isEmpty()) {
                try { body.addProperty("peso", Double.parseDouble(pesoStr)); } catch (NumberFormatException e) { body.addProperty("peso", (Number) null); }
            }
            String alturaStr = alturaField.getText().trim();
            if (!alturaStr.isEmpty()) {
                try { body.addProperty("altura", Double.parseDouble(alturaStr)); } catch (NumberFormatException e) { body.addProperty("altura", (Number) null); }
            }
            String dirStr = dirField.getText().trim();
            if (!dirStr.isEmpty()) body.addProperty("direccion", dirStr);
            String emergStr = emergField.getText().trim();
            if (!emergStr.isEmpty()) body.addProperty("contacto_emergencia", emergStr);
            String fechaNacStr = fechaNacField.getText().trim();
            if (!fechaNacStr.isEmpty()) body.addProperty("fecha_nacimiento", fechaNacStr);
            ApiResponse resp = AthleteApiService.getInstance().update(a.getIdAtleta(), body);
            if (resp != null && resp.isQueued()) {
                JOptionPane.showMessageDialog(this, "Sin conexion. Los cambios se guardaran al reconectar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            } else if (resp == null || !resp.isOk()) {
                JOptionPane.showMessageDialog(this, "Error al actualizar: " + (resp != null ? resp.message : "Sin respuesta"), "Error", JOptionPane.ERROR_MESSAGE);
            }
            loadAthletes(model);
        }
    }

    private void showAssignMembershipDialog(JTable table, DefaultTableModel model, int row) {
        int idAtleta = (int) model.getValueAt(row, 0);
        List<Membresia> list = adminController.obtenerMembresias();
        String[] names = list.stream().map(m -> m.getNombre() + " - $" + m.getPrecio() + " / " + m.getDuracionDias() + "d").toArray(String[]::new);
        JComboBox<String> combo = new JComboBox<>(names);
        if (JOptionPane.showConfirmDialog(this, combo, "Asignar Membresia", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Membresia selected = list.get(combo.getSelectedIndex());
            if (adminController.asignarMembresia(idAtleta, selected.getIdMembresia())) {
                loadAthletes(model);
            }
        }
    }

    private void toggleAthleteStatus(JTable table, DefaultTableModel model, int row) {
        int idAtleta = (int) model.getValueAt(row, 0);
        String currentStatus = (String) model.getValueAt(row, 7);
        boolean nuevoEstado = !"Activo".equals(currentStatus);
        if (adminController.toggleEstadoAtleta(idAtleta, nuevoEstado)) {
            loadAthletes(model);
        }
    }

    // ============ TAB: ENTRENADORES ============
    private javax.swing.JTextField trainerSearchField;
    
    private JPanel createTrainersTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        String[] cols = {"ID", "Nombre", "Apellido", "Email", "Especialidad", "Exp.", "Certificaciones", "Telefono", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        trainerModel = model;
        JTable table = styledTable(model);
        setColumnWidths(table, new int[]{40, 100, 100, 160, 120, 50, 160, 100, 60});
        loadTrainers(model);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG);
        trainerSearchField = new JTextField();
        trainerSearchField.setBackground(CARD_BG);
        trainerSearchField.setForeground(Color.WHITE);
        trainerSearchField.setCaretColor(Color.WHITE);
        trainerSearchField.addActionListener(e -> loadTrainers(model));
        trainerSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void update() { loadTrainers(model); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BG);
        trainerSearchField.setPreferredSize(new Dimension(300, 28));
        searchPanel.add(trainerSearchField);
        topPanel.add(searchPanel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(BG);
        JButton addBtn = actionBtn("+ Agregar Entrenador");
        addBtn.addActionListener(e -> showAddTrainerDialog(model));
        JButton editBtn = actionBtn("Editar");
        editBtn.addActionListener(e -> { int row = table.getSelectedRow(); if (row >= 0) showEditTrainerDialog(row, model, table); });
        JButton statusBtn = actionBtn("Activar/Desactivar");
        statusBtn.addActionListener(e -> { int row = table.getSelectedRow(); if (row >= 0) toggleTrainerStatus(table, model, row); });
        JButton delBtn = actionBtn("Eliminar");
        delBtn.setBackground(new Color(180, 40, 40));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "Eliminar entrenador?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                int id = (int) model.getValueAt(row, 0);
                if (adminController.eliminarEntrenador(id)) { model.removeRow(row); }
            }
        });
        btnPanel.add(addBtn); btnPanel.add(editBtn); btnPanel.add(statusBtn); btnPanel.add(delBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(wrapTable(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadTrainers(DefaultTableModel model) {
        model.setRowCount(0);
        String search = trainerSearchField != null ? trainerSearchField.getText().trim().toLowerCase() : "";
        for (Entrenador e : adminController.obtenerTodosEntrenadores()) {
            if (!search.isEmpty()) {
                String nombre = (e.getNombre() != null ? e.getNombre() : "").toLowerCase();
                String apellido = (e.getApellido() != null ? e.getApellido() : "").toLowerCase();
                String email = (e.getEmail() != null ? e.getEmail() : "").toLowerCase();
                String esp = (e.getEspecialidad() != null ? e.getEspecialidad() : "").toLowerCase();
                if (!nombre.contains(search) && !apellido.contains(search) && !email.contains(search) && !esp.contains(search)) {
                    continue;
                }
            }
            model.addRow(new Object[]{e.getIdEntrenador(), e.getNombre(), e.getApellido(), e.getEmail(),
                e.getEspecialidad() != null ? e.getEspecialidad() : "", e.getExperienciaAnios(),
                e.getCertificacion() != null ? e.getCertificacion() : "",
                e.getTelefono() != null ? e.getTelefono() : "",
                e.isActivo() ? "Activo" : "Inactivo"});
        }
    }

    private void showEditTrainerDialog(int row, DefaultTableModel model, JTable table) {
        List<Entrenador> list = adminController.obtenerTodosEntrenadores();
        if (row >= list.size()) return;
        Entrenador e = list.get(row);
        JTextField specField = new JTextField(e.getEspecialidad() != null ? e.getEspecialidad() : "");
        JTextField expField = new JTextField(String.valueOf(e.getExperienciaAnios()));
        JTextField certField = new JTextField(e.getCertificacion() != null ? e.getCertificacion() : "");
        JTextField phoneField = new JTextField(e.getTelefono() != null ? e.getTelefono() : "");
        JTextField bioField = new JTextField(e.getBiografia() != null ? e.getBiografia() : "");
        JTextField dirField = new JTextField(e.getDireccion() != null ? e.getDireccion() : "");
        JTextField fechaNacField = new JTextField(e.getFechaNacimiento() != null ? e.getFechaNacimiento().toString() : "");
        Object[] fields = {"Especialidad:", specField, "Experiencia:", expField, "Certificaciones:", certField, "Telefono:", phoneField, "Biografia:", bioField, "Direccion:", dirField, "Fecha Nac. (YYYY-MM-DD):", fechaNacField};
        JPanel form = createFormPanel(fields);
        if (JOptionPane.showConfirmDialog(this, form, "Editar Entrenador", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            e.setEspecialidad(specField.getText().trim());
            e.setExperienciaAnios(Integer.parseInt(expField.getText().trim()));
            e.setCertificacion(certField.getText().trim());
            e.setTelefono(phoneField.getText().trim());
            e.setBiografia(bioField.getText().trim());
            e.setDireccion(dirField.getText().trim());
            if (!fechaNacField.getText().trim().isEmpty()) {
                try { e.setFechaNacimiento(LocalDate.parse(fechaNacField.getText().trim())); } catch (Exception ignored) {}
            }
            adminController.actualizarEntrenador(e);
            loadTrainers(model);
        }
    }

    private void toggleTrainerStatus(JTable table, DefaultTableModel model, int row) {
        int idEntrenador = (int) model.getValueAt(row, 0);
        String currentStatus = (String) model.getValueAt(row, 8);
        boolean nuevoEstado = !"Activo".equals(currentStatus);
        if (adminController.toggleEstadoEntrenador(idEntrenador, nuevoEstado)) {
            loadTrainers(model);
        }
    }

    private void showAddTrainerDialog(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField specField = new JTextField();
        JTextField expField = new JTextField("0");
        JTextField certField = new JTextField();
        JTextField bioField = new JTextField();
        JTextField phoneField = new JTextField();
        Object[] fields = {"Nombre:", nameField, "Apellido:", lastNameField, "Email:", emailField, "Especialidad:", specField, "Experiencia:", expField, "Certificaciones:", certField, "Biografia:", bioField, "Telefono:", phoneField};
        JPanel form = createFormPanel(fields);

        if (JOptionPane.showConfirmDialog(this, form, "Nuevo Entrenador", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                JsonObject body = new JsonObject();
                body.addProperty("nombre", nameField.getText().trim());
                body.addProperty("apellido", lastNameField.getText().trim());
                body.addProperty("email", emailField.getText().trim());
                body.addProperty("especialidad", specField.getText().trim());
                body.addProperty("anios_experiencia", Integer.parseInt(expField.getText().trim()));
                body.addProperty("certificaciones", certField.getText().trim());
                body.addProperty("biografia", bioField.getText().trim());
                body.addProperty("fecha_nacimiento", LocalDate.now().minusYears(25).toString());
                body.addProperty("telefono", phoneField.getText().trim());
                body.addProperty("direccion", "");
                ApiResponse resp = TrainerApiService.getInstance().create(body);
                if (resp != null && resp.isQueued()) {
                    JOptionPane.showMessageDialog(this, "Sin conexion. El entrenador se guardara al reconectar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                } else if (resp == null || !resp.isOk()) {
                    JOptionPane.showMessageDialog(this, "Error al crear entrenador: " + (resp != null ? resp.message : "Sin respuesta"), "Error", JOptionPane.ERROR_MESSAGE);
                }
                loadTrainers(model);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ============ TAB: WODs ============
    private java.time.LocalDate wodCurrentMonth = java.time.LocalDate.now().withDayOfMonth(1);
    private java.time.LocalDate wodSelectedDay = java.time.LocalDate.now();
    private JPanel wodDetailPanel;
    private JPanel wodCalendarPanel;
    private DefaultTableModel wodScheduleModel;
    
    private JPanel createWodsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navPanel.setBackground(BG);
        JButton prevBtn = new JButton("<");
        prevBtn.addActionListener(e -> { wodCurrentMonth = wodCurrentMonth.minusMonths(1); refreshWodCalendar(); });
        JButton todayBtn = new JButton("Hoy");
        todayBtn.addActionListener(e -> { wodCurrentMonth = java.time.LocalDate.now().withDayOfMonth(1); wodSelectedDay = java.time.LocalDate.now(); refreshWodCalendar(); });
        JButton nextBtn = new JButton(">");
        nextBtn.addActionListener(e -> { wodCurrentMonth = wodCurrentMonth.plusMonths(1); refreshWodCalendar(); });
        JLabel monthLabel = new JLabel();
        monthLabel.setForeground(Color.WHITE);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JButton newWodBtn = actionBtn("+ Nuevo WOD");
        newWodBtn.addActionListener(e -> showAddWodDialog());

        navPanel.add(prevBtn); navPanel.add(monthLabel); navPanel.add(nextBtn); navPanel.add(todayBtn); navPanel.add(newWodBtn);

        wodCalendarPanel = new JPanel() { protected void paintComponent(Graphics g) { g.setColor(BG); g.fillRect(0,0,getWidth(),getHeight()); } };
        wodCalendarPanel.setBackground(BG);
        wodCalendarPanel.setLayout(new BorderLayout());

        wodDetailPanel = new JPanel(new BorderLayout());
        wodDetailPanel.setBackground(BG);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, wodCalendarPanel, wodDetailPanel);
        splitPane.setDividerLocation(350);
        splitPane.setBackground(BG);

        mainPanel.add(navPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        panel.add(mainPanel, BorderLayout.CENTER);
        refreshWodCalendar();
        return panel;
    }

    private void refreshWodCalendar() {
        wodCalendarPanel.removeAll();
        wodDetailPanel.removeAll();

        java.time.YearMonth ym = java.time.YearMonth.from(wodCurrentMonth);
        String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        JLabel ml = null;
        for (java.awt.Component c : ((java.awt.Container)((java.awt.Container)wodCalendarPanel.getParent().getParent().getComponent(0)).getComponent(0)).getComponents()) {
            if (c instanceof JLabel) { ml = (JLabel)c; break; }
        }
        if (ml == null) {
            java.awt.Container nav = (java.awt.Container)((java.awt.Container)wodCalendarPanel.getParent().getParent().getComponent(0));
            for (java.awt.Component c : nav.getComponents()) {
                if (c instanceof JLabel) { ml = (JLabel)c; break; }
            }
        }
        if (ml != null) ml.setText(meses[ym.getMonthValue()-1] + " " + ym.getYear());

        java.time.LocalDate firstDay = ym.atDay(1);
        int startDow = firstDay.getDayOfWeek().getValue() % 7;
        int daysInMonth = ym.lengthOfMonth();

        try {
            ApiResponse resp = WodApiService.getInstance().getByMonth(ym.getYear(), ym.getMonthValue());
            JsonArray arr = (resp != null && resp.isOk() && resp.data != null && resp.data.isJsonArray()) ? resp.data.getAsJsonArray() : new JsonArray();
            java.util.Map<String, JsonObject> wodByDate = new java.util.LinkedHashMap<>();
            for (JsonElement e : arr) { JsonObject w = e.getAsJsonObject(); wodByDate.put(w.get("fecha").getAsString(), w); }

            JPanel grid = new JPanel(new GridLayout(0, 7, 2, 2));
            grid.setBackground(BG);
            String[] dias = {"Lun","Mar","Mie","Jue","Vie","Sab","Dom"};
            for (String d : dias) { JLabel l = new JLabel(d, SwingConstants.CENTER); l.setForeground(GRAY); l.setFont(new Font("Arial", Font.BOLD, 11)); grid.add(l); }

            for (int i = 0; i < startDow; i++) grid.add(new JLabel(""));
            java.time.LocalDate today = java.time.LocalDate.now();
            for (int d = 1; d <= daysInMonth; d++) {
                java.time.LocalDate date = ym.atDay(d);
                String ds = date.toString();
                JsonObject w = wodByDate.get(ds);
                final java.time.LocalDate fdate = date;
                JButton dayBtn = new JButton(d + (w != null ? " ◉" : ""));
                dayBtn.setBackground(date.equals(wodSelectedDay) ? RED : DARK);
                dayBtn.setForeground(date.equals(today) ? RED : Color.WHITE);
                if (date.isBefore(today)) dayBtn.setEnabled(false);
                if (w != null) dayBtn.setToolTipText(w.get("titulo").getAsString());
                dayBtn.addActionListener(e -> { wodSelectedDay = fdate; refreshWodCalendar(); });
                grid.add(dayBtn);
            }
            wodCalendarPanel.add(new JScrollPane(grid), BorderLayout.CENTER);

            JsonObject selWod = wodByDate.get(wodSelectedDay.toString());
            if (selWod != null) {
                showWodDetail(selWod);
            } else {
                wodDetailPanel.add(new JLabel("  Sin WOD programado para este dia", SwingConstants.CENTER) {{ setForeground(GRAY); }}, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            wodCalendarPanel.add(new JLabel("Error al cargar WODs: " + e.getMessage(), SwingConstants.CENTER) {{ setForeground(Color.RED); }}, BorderLayout.CENTER);
        }
        wodCalendarPanel.revalidate(); wodCalendarPanel.repaint();
        wodDetailPanel.revalidate(); wodDetailPanel.repaint();
    }

    private void showWodDetail(JsonObject w) {
        wodDetailPanel.removeAll();
        int idWod = w.has("id_wod") ? w.get("id_wod").getAsInt() : 0;
        String titulo = w.has("titulo") ? w.get("titulo").getAsString() : "";
        String tipo = w.has("tipo") ? w.get("tipo").getAsString() : "";
        String nivel = w.has("nivel") ? w.get("nivel").getAsString() : "";
        String desc = w.has("descripcion") ? w.get("descripcion").getAsString() : "";
        String fecha = w.has("fecha") ? w.get("fecha").getAsString() : wodSelectedDay.toString();

        try {
            ApiResponse detailResp = WodApiService.getInstance().getByDate(fecha);
            if (detailResp != null && detailResp.isOk() && detailResp.data != null && detailResp.data.isJsonObject()) {
                JsonObject detail = detailResp.data.getAsJsonObject();
                if (detail.has("horarios")) w = detail;
            }
        } catch (Exception ignored) {}

        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBackground(BG);
        detailPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BG);

        JLabel titleLabel = new JLabel(titulo);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.LEFT));
        badges.setBackground(BG);
        if (!tipo.isEmpty()) { JLabel tb = new JLabel(tipo); tb.setOpaque(true); tb.setBackground(new Color(60,60,60)); tb.setForeground(Color.WHITE); tb.setFont(new Font("Arial", Font.PLAIN, 10)); tb.setBorder(BorderFactory.createEmptyBorder(2,8,2,8)); badges.add(tb); }
        if (!nivel.isEmpty()) { JLabel nb = new JLabel(nivel); nb.setOpaque(true); nb.setBackground(new Color(180,40,40)); nb.setForeground(Color.WHITE); nb.setFont(new Font("Arial", Font.BOLD, 10)); nb.setBorder(BorderFactory.createEmptyBorder(2,8,2,8)); badges.add(nb); }
        infoPanel.add(badges);
        infoPanel.add(Box.createVerticalStrut(8));

        JTextArea descArea = new JTextArea(desc);
        descArea.setEditable(false); descArea.setLineWrap(true); descArea.setBackground(BG); descArea.setForeground(GRAY);
        descArea.setFont(new Font("Arial", Font.PLAIN, 11));
        infoPanel.add(descArea);
        infoPanel.add(Box.createVerticalStrut(10));

        String[] sCols = {"Hora", "Inscritos", "Entrenador"};
        wodScheduleModel = new DefaultTableModel(sCols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JsonArray horarios = null;
        if (w.has("horarios") && w.get("horarios").isJsonArray()) horarios = w.get("horarios").getAsJsonArray();
        else if (w.has("schedules") && w.get("schedules").isJsonArray()) horarios = w.get("schedules").getAsJsonArray();
        if (horarios != null) {
            for (JsonElement he : horarios) {
                JsonObject h = he.getAsJsonObject();
                String hora = h.has("hora") ? h.get("hora").getAsString().substring(0, 5) : "--:--";
                int inscritos = h.has("inscritos") ? h.get("inscritos").getAsInt() : 0;
                int cupo = h.has("cupo_maximo") ? h.get("cupo_maximo").getAsInt() : 20;
                String entrenador = h.has("entrenador_nombre") ? h.get("entrenador_nombre").getAsString() : "-";
                wodScheduleModel.addRow(new Object[]{hora, inscritos + "/" + cupo, entrenador});
            }
        }
        JTable sTable = styledTable(wodScheduleModel);
        setColumnWidths(sTable, new int[]{80, 120, 200});
        JScrollPane sScroll = new JScrollPane(sTable);
        sScroll.setPreferredSize(new Dimension(400, 120));
        infoPanel.add(new JLabel("Horarios:"));
        infoPanel.add(sScroll);

        detailPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(BG);
        JButton editBtn = actionBtn("Editar WOD");
        editBtn.addActionListener(e -> showEditWodDialog(idWod, titulo, desc, tipo, nivel, fecha));
        JButton delBtn = actionBtn("Eliminar WOD");
        delBtn.setBackground(new Color(180, 40, 40));
        delBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Eliminar WOD '" + titulo + "'?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                ApiResponse resp = WodApiService.getInstance().delete(idWod);
                if (resp != null && !resp.isOk()) JOptionPane.showMessageDialog(this, "Error: " + (resp.message != null ? resp.message : ""), "Error", JOptionPane.ERROR_MESSAGE);
                refreshWodCalendar();
            }
        });
        btnPanel.add(editBtn); btnPanel.add(delBtn);
        detailPanel.add(btnPanel, BorderLayout.SOUTH);

        wodDetailPanel.add(detailPanel, BorderLayout.CENTER);
    }

    private void showEditWodDialog(int idWod, String titulo, String desc, String tipo, String nivel, String fecha) {
        JTextField titField = new JTextField(titulo);
        JTextField descField = new JTextField(desc);
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"AMRAP","FOR TIME","EMOM","TABATA","STRENGTH","CHIPPER","HERO","BENCHMARK"});
        typeBox.setSelectedItem(tipo);
        JComboBox<String> levelBox = new JComboBox<>(new String[]{"PRINCIPIANTE","INTERMEDIO","AVANZADO","RX","SCALED"});
        levelBox.setSelectedItem(nivel);
        Object[] fields = {"Titulo:", titField, "Descripcion:", descField, "Tipo:", typeBox, "Nivel:", levelBox};
        if (JOptionPane.showConfirmDialog(this, createFormPanel(fields), "Editar WOD", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            JsonObject body = new JsonObject();
            body.addProperty("titulo", titField.getText().trim());
            body.addProperty("descripcion", descField.getText().trim());
            body.addProperty("tipo", typeBox.getSelectedItem().toString());
            body.addProperty("nivel", levelBox.getSelectedItem().toString());
            ApiResponse resp = WodApiService.getInstance().update(idWod, body);
            if (resp != null && !resp.isOk()) JOptionPane.showMessageDialog(this, "Error: " + (resp.message != null ? resp.message : ""), "Error", JOptionPane.ERROR_MESSAGE);
            refreshWodCalendar();
        }
    }

    private void showAddWodDialog() {
        JTextField titleField = new JTextField();
        JTextField dateField = new JTextField(wodSelectedDay.toString());
        JTextField descField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"AMRAP","FOR TIME","EMOM","TABATA","STRENGTH","CHIPPER","HERO","BENCHMARK"});
        JComboBox<String> levelBox = new JComboBox<>(new String[]{"PRINCIPIANTE","INTERMEDIO","AVANZADO","RX","SCALED"});

        List<Entrenador> trainers = adminController.obtenerTodosEntrenadores();
        java.util.List<JTextField> horariosHora = new java.util.ArrayList<>();
        java.util.List<JTextField> horariosCupo = new java.util.ArrayList<>();
        java.util.List<JComboBox<String>> horariosEnt = new java.util.ArrayList<>();
        String[] trainerNames = trainers.stream().map(t -> t.getIdEntrenador() + " - " + t.getNombre() + " " + t.getApellido()).toArray(String[]::new);

        addHorarioRow(horariosHora, horariosCupo, horariosEnt, trainerNames);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(new JLabel("Titulo:")); form.add(titleField);
        form.add(new JLabel("Fecha:")); form.add(dateField);
        form.add(new JLabel("Descripcion:")); form.add(descField);
        form.add(new JLabel("Tipo:")); form.add(typeBox);
        form.add(new JLabel("Nivel:")); form.add(levelBox);
        form.add(new JLabel("Horarios:"));
        JPanel hrPanel = createHorariosPanel(horariosHora, horariosCupo, horariosEnt);
        form.add(hrPanel);
        JButton addHr = new JButton("+ Agregar Horario");
        addHr.addActionListener(e -> { addHorarioRow(horariosHora, horariosCupo, horariosEnt, trainerNames); form.remove(form.getComponentCount()-1); form.remove(form.getComponentCount()-1); JPanel hp = createHorariosPanel(horariosHora, horariosCupo, horariosEnt); form.add(hp); form.add(addHr); form.revalidate(); form.repaint(); });
        form.add(addHr);

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setPreferredSize(new Dimension(450, 400));

        if (JOptionPane.showConfirmDialog(this, scrollForm, "Nuevo WOD", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                JsonObject body = new JsonObject();
                body.addProperty("titulo", titleField.getText().trim());
                body.addProperty("fecha", dateField.getText().trim());
                body.addProperty("descripcion", descField.getText().trim());
                body.addProperty("tipo", typeBox.getSelectedItem().toString());
                body.addProperty("nivel", levelBox.getSelectedItem().toString());
                JsonArray horariosArr = new JsonArray();
                for (int i = 0; i < horariosHora.size(); i++) {
                    String hora = horariosHora.get(i).getText().trim();
                    if (hora.isEmpty()) continue;
                    JsonObject h = new JsonObject();
                    h.addProperty("hora", hora + ":00");
                    try { h.addProperty("cupo_maximo", Integer.parseInt(horariosCupo.get(i).getText().trim())); } catch (Exception ex) { h.addProperty("cupo_maximo", 20); }
                    String sel = horariosEnt.get(i).getSelectedItem().toString();
                    h.addProperty("id_entrenador", Integer.parseInt(sel.split(" - ")[0]));
                    horariosArr.add(h);
                }
                body.add("horarios", horariosArr);
                ApiResponse resp = WodApiService.getInstance().create(body);
                if (resp != null && resp.isOk()) {
                    JOptionPane.showMessageDialog(this, "WOD creado exitosamente!");
                    wodSelectedDay = java.time.LocalDate.parse(dateField.getText().trim());
                } else if (resp != null && resp.isQueued()) {
                    JOptionPane.showMessageDialog(this, "Sin conexion. El WOD se guardara al reconectar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + (resp != null ? resp.message : "Sin respuesta"), "Error", JOptionPane.ERROR_MESSAGE);
                }
                refreshWodCalendar();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void addHorarioRow(java.util.List<JTextField> horas, java.util.List<JTextField> cupos, java.util.List<JComboBox<String>> entradores, String[] trainerNames) {
        horas.add(new JTextField("07:00"));
        cupos.add(new JTextField("20"));
        JComboBox<String> cb = new JComboBox<>(trainerNames);
        entradores.add(cb);
    }

    private JPanel createHorariosPanel(java.util.List<JTextField> horas, java.util.List<JTextField> cupos, java.util.List<JComboBox<String>> entradores) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        for (int i = 0; i < horas.size(); i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(new JLabel("Hora:"));
            JTextField hf = horas.get(i); hf.setPreferredSize(new Dimension(60, 25));
            row.add(hf);
            row.add(new JLabel("Cupo:"));
            JTextField cf = cupos.get(i); cf.setPreferredSize(new Dimension(40, 25));
            row.add(cf);
            row.add(entradores.get(i));
            final int idx = i;
            JButton rmBtn = new JButton("X");
            rmBtn.setForeground(Color.RED);
            rmBtn.addActionListener(e -> { horas.remove(idx); cupos.remove(idx); entradores.remove(idx); p.removeAll(); p.add(createHorariosPanel(horas, cupos, entradores)); p.revalidate(); p.repaint(); });
            row.add(rmBtn);
            p.add(row);
        }
        return p;
    }

    // ============ TAB: MEMBRESIAS ============
    private javax.swing.JTextField membershipSearchField;
    
    private JPanel createMembershipsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        String[] cols = {"ID", "Nombre", "Precio", "Duracion", "Descripcion", "Beneficios", "Activa"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        membershipModel = model;
        JTable table = styledTable(model);
        setColumnWidths(table, new int[]{40, 120, 80, 80, 200, 200, 60});
        loadMemberships(model);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG);
        membershipSearchField = new JTextField();
        membershipSearchField.setBackground(CARD_BG);
        membershipSearchField.setForeground(Color.WHITE);
        membershipSearchField.setCaretColor(Color.WHITE);
        membershipSearchField.addActionListener(e -> loadMemberships(model));
        membershipSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void update() { loadMemberships(model); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BG);
        membershipSearchField.setPreferredSize(new java.awt.Dimension(300, 28));
        searchPanel.add(membershipSearchField);
        topPanel.add(searchPanel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(BG);
        JButton addBtn = actionBtn("+ Nueva Membresia");
        addBtn.addActionListener(e -> showAddMembershipDialog(model));
        JButton editBtn = actionBtn("Editar");
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) showEditMembershipDialog(row, model, table);
        });
        JButton delBtn = actionBtn("Eliminar");
        delBtn.setBackground(new Color(180, 40, 40));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "Eliminar membresia?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                int id = (int) model.getValueAt(row, 0);
                ApiResponse resp = MembershipApiService.getInstance().delete(id);
                if (resp != null && !resp.isOk()) {
                    JOptionPane.showMessageDialog(this, "Error: " + (resp.message != null ? resp.message : "No se pudo eliminar"), "Error", JOptionPane.ERROR_MESSAGE);
                }
                loadMemberships(model);
            }
        });
        btnPanel.add(addBtn); btnPanel.add(editBtn); btnPanel.add(delBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(wrapTable(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadMemberships(DefaultTableModel model) {
        model.setRowCount(0);
        String search = membershipSearchField != null ? membershipSearchField.getText().trim().toLowerCase() : "";
        for (Membresia m : adminController.obtenerMembresias()) {
            if (!search.isEmpty()) {
                String nombre = (m.getNombre() != null ? m.getNombre() : "").toLowerCase();
                String desc = (m.getDescripcion() != null ? m.getDescripcion() : "").toLowerCase();
                String precio = String.valueOf(m.getPrecio());
                String duracion = String.valueOf(m.getDuracionDias());
                if (!nombre.contains(search) && !precio.contains(search) && !duracion.contains(search) && !desc.contains(search)) {
                    continue;
                }
            }
            model.addRow(new Object[]{m.getIdMembresia(), m.getNombre(), "$" + m.getPrecio(), m.getDuracionDias() + "d",
                m.getDescripcion() != null ? (m.getDescripcion().length() > 30 ? m.getDescripcion().substring(0, 30) + "..." : m.getDescripcion()) : "",
                m.getBeneficios() != null ? (m.getBeneficios().length() > 40 ? m.getBeneficios().substring(0, 40) + "..." : m.getBeneficios()) : "",
                m.isActiva() ? "Si" : "No"});
        }
    }

    private void showEditMembershipDialog(int row, DefaultTableModel model, JTable table) {
        List<Membresia> list = adminController.obtenerMembresias();
        if (row >= list.size()) return;
        Membresia m = list.get(row);
        JTextField nameField = new JTextField(m.getNombre());
        JTextField priceField = new JTextField(String.valueOf(m.getPrecio()));
        JTextField daysField = new JTextField(String.valueOf(m.getDuracionDias()));
        JTextField descField = new JTextField(m.getDescripcion() != null ? m.getDescripcion() : "");
        JTextField benefitsField = new JTextField(m.getBeneficios() != null ? m.getBeneficios() : "");
        JCheckBox activeCheck = new JCheckBox("Activa", m.isActiva());
        activeCheck.setBackground(BG);
        activeCheck.setForeground(Color.WHITE);
        Object[] fields = {"Nombre:", nameField, "Precio:", priceField, "Duracion (dias):", daysField, "Descripcion:", descField, "Beneficios:", benefitsField, "", activeCheck};
        JPanel form = createFormPanel(fields);
        if (JOptionPane.showConfirmDialog(this, form, "Editar Membresia", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            JsonObject body = new JsonObject();
            body.addProperty("nombre", nameField.getText().trim());
            body.addProperty("precio", Double.parseDouble(priceField.getText().trim()));
            body.addProperty("duracion_dias", Integer.parseInt(daysField.getText().trim()));
            body.addProperty("descripcion", descField.getText().trim());
            body.addProperty("beneficios", benefitsField.getText().trim());
            body.addProperty("estado", activeCheck.isSelected());
            ApiResponse resp = MembershipApiService.getInstance().update(m.getIdMembresia(), body);
            if (resp == null || (!resp.isOk() && !resp.isQueued())) {
                JOptionPane.showMessageDialog(this, "Error al actualizar: " + (resp != null ? resp.message : "Sin respuesta"), "Error", JOptionPane.ERROR_MESSAGE);
            }
            loadMemberships(model);
        }
    }

    private void showAddMembershipDialog(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField("25.00");
        JTextField daysField = new JTextField("30");
        JTextField descField = new JTextField();
        JTextField benefitsField = new JTextField();
        Object[] fields = {"Nombre:", nameField, "Precio:", priceField, "Duracion (dias):", daysField, "Descripcion:", descField, "Beneficios:", benefitsField};
        JPanel form = createFormPanel(fields);

        if (JOptionPane.showConfirmDialog(this, form, "Nueva Membresia", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Membresia m = new Membresia(nameField.getText().trim(), descField.getText().trim(),
                Double.parseDouble(priceField.getText().trim()), Integer.parseInt(daysField.getText().trim()),
                benefitsField.getText().trim());
            adminController.crearMembresia(m);
            loadMemberships(model);
        }
    }

    // ============ TAB: EJERCICIOS ============
    private javax.swing.JTextField exerciseSearchField;
    
    private JPanel createExercisesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        String[] cols = {"ID", "Nombre", "Descripcion", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        exerciseModel = model;
        JTable table = styledTable(model);
        setColumnWidths(table, new int[]{40, 150, 350, 80});
        loadExercises(model);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG);
        exerciseSearchField = new JTextField();
        exerciseSearchField.setBackground(CARD_BG); exerciseSearchField.setForeground(Color.WHITE); exerciseSearchField.setCaretColor(Color.WHITE);
        exerciseSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void update() { loadExercises(model); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BG);
        exerciseSearchField.setPreferredSize(new Dimension(300, 28));
        searchPanel.add(exerciseSearchField);
        topPanel.add(searchPanel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(BG);
        JButton addBtn = actionBtn("+ Nuevo Ejercicio");
        addBtn.addActionListener(e -> showAddExerciseDialog(model));
        JButton editBtn = actionBtn("Editar");
        editBtn.addActionListener(e -> { int row = table.getSelectedRow(); if (row >= 0) showEditExerciseDialog(row, model); });
        JButton deactBtn = actionBtn("Desactivar");
        deactBtn.setBackground(new Color(200, 120, 0));
        deactBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "Desactivar este ejercicio?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                int id = (int) model.getValueAt(row, 0);
                ExerciseApiService.getInstance().delete(id);
                loadExercises(model);
            }
        });
        JButton reactBtn = actionBtn("Reactivar");
        reactBtn.setBackground(new Color(40, 160, 40));
        reactBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "Reactivar este ejercicio?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                int id = (int) model.getValueAt(row, 0);
                ExerciseApiService.getInstance().reactivate(id);
                loadExercises(model);
            }
        });
        btnPanel.add(addBtn); btnPanel.add(editBtn); btnPanel.add(deactBtn); btnPanel.add(reactBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(wrapTable(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadExercises(DefaultTableModel model) {
        model.setRowCount(0);
        String search = exerciseSearchField != null ? exerciseSearchField.getText().trim().toLowerCase() : "";
        try {
            ApiResponse resp = ExerciseApiService.getInstance().getAll();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                for (JsonElement el : resp.data.getAsJsonArray()) {
                    JsonObject ex = el.getAsJsonObject();
                    String nombre = ex.has("nombre") ? ex.get("nombre").getAsString() : "";
                    String desc = ex.has("descripcion") && !ex.get("descripcion").isJsonNull() ? ex.get("descripcion").getAsString() : "";
                    boolean activo = !ex.has("activo") || ex.get("activo").isJsonNull() || ex.get("activo").getAsBoolean();
                    if (!search.isEmpty()) {
                        if (!nombre.toLowerCase().contains(search) && !desc.toLowerCase().contains(search)) continue;
                    }
                    model.addRow(new Object[]{ex.has("id_ejercicio") ? ex.get("id_ejercicio").getAsInt() : 0,
                        nombre, desc.length() > 50 ? desc.substring(0,50)+"..." : desc,
                        activo ? "Activo" : "Inactivo"});
                }
            }
        } catch (Exception e) { /* ignore */ }
    }

    private void showAddExerciseDialog(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        Object[] fields = {"Nombre:", nameField, "Descripcion:", descField};
        if (JOptionPane.showConfirmDialog(this, createFormPanel(fields), "Nuevo Ejercicio", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            JsonObject body = new JsonObject();
            body.addProperty("nombre", nameField.getText().trim());
            body.addProperty("descripcion", descField.getText().trim());
            ExerciseApiService.getInstance().create(body);
            loadExercises(model);
        }
    }

    private void showEditExerciseDialog(int row, DefaultTableModel model) {
        try {
            int id = (int) model.getValueAt(row, 0);
            ApiResponse resp = ExerciseApiService.getInstance().getById(id);
            if (!resp.isOk() || resp.data == null || !resp.data.isJsonObject()) return;
            JsonObject ex = resp.data.getAsJsonObject();
            JTextField nameField = new JTextField(ex.has("nombre") ? ex.get("nombre").getAsString() : "");
            JTextField descField = new JTextField(ex.has("descripcion") && !ex.get("descripcion").isJsonNull() ? ex.get("descripcion").getAsString() : "");
            Object[] fields = {"Nombre:", nameField, "Descripcion:", descField};
            if (JOptionPane.showConfirmDialog(this, createFormPanel(fields), "Editar Ejercicio", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                JsonObject body = new JsonObject();
                body.addProperty("nombre", nameField.getText().trim());
                body.addProperty("descripcion", descField.getText().trim());
                ExerciseApiService.getInstance().update(id, body);
                loadExercises(model);
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    // ============ TAB: CLASES ============
    private javax.swing.JTextField classSearchField;
    
    private JPanel createClassesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        String[] cols = {"ID", "Nombre", "Descripcion", "Fecha", "Hora", "Entrenador", "Cupo", "Inscritos", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        classModel = model;
        JTable table = styledTable(model);
        setColumnWidths(table, new int[]{40, 120, 200, 80, 60, 140, 50, 60, 60});
        loadClasses(model);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG);
        classSearchField = new JTextField();
        classSearchField.setBackground(CARD_BG); classSearchField.setForeground(Color.WHITE); classSearchField.setCaretColor(Color.WHITE);
        classSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void update() { loadClasses(model); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BG);
        classSearchField.setPreferredSize(new Dimension(300, 28));
        searchPanel.add(classSearchField);
        topPanel.add(searchPanel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(BG);
        JButton addBtn = actionBtn("+ Nueva Clase");
        addBtn.addActionListener(e -> showAddClassDialog(model));
        JButton editBtn = actionBtn("Editar");
        editBtn.addActionListener(e -> { int row = table.getSelectedRow(); if (row >= 0) showEditClassDialog(row, model); });
        JButton cancelBtn = actionBtn("Cancelar");
        cancelBtn.setBackground(new Color(200, 120, 0));
        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "Cancelar esta clase? Pasara a estado CANCELADA.", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                int id = (int) model.getValueAt(row, 0);
                if (adminController.eliminarClase(id)) loadClasses(model);
            }
        });
        JButton reactBtn = actionBtn("Reactivar");
        reactBtn.setBackground(new Color(40, 160, 40));
        reactBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "Reactivar esta clase?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                int id = (int) model.getValueAt(row, 0);
                JsonObject reactBody = new JsonObject();
                reactBody.addProperty("estado", "ACTIVA");
                ApiResponse resp = ClassApiService.getInstance().update(id, reactBody);
                if (resp != null && resp.isOk()) loadClasses(model);
                else JOptionPane.showMessageDialog(this, "Error al reactivar: " + (resp != null ? resp.message : ""), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton permDelBtn = actionBtn("Eliminar Permanente");
        permDelBtn.setBackground(new Color(180, 40, 40));
        permDelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "Eliminar esta clase PERMANENTEMENTE?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                int id = (int) model.getValueAt(row, 0);
                ApiResponse resp = ClassApiService.getInstance().deletePermanently(id);
                if (resp != null && resp.isOk()) loadClasses(model);
                else JOptionPane.showMessageDialog(this, "Error: " + (resp != null ? resp.message : ""), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnPanel.add(addBtn); btnPanel.add(editBtn); btnPanel.add(cancelBtn); btnPanel.add(reactBtn); btnPanel.add(permDelBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(wrapTable(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadClasses(DefaultTableModel model) {
        model.setRowCount(0);
        String search = classSearchField != null ? classSearchField.getText().trim().toLowerCase() : "";
        for (Clase c : adminController.obtenerClases()) {
            if (!search.isEmpty()) {
                String nombre = (c.getNombre() != null ? c.getNombre() : "").toLowerCase();
                String trainer = (c.getNombreEntrenador() != null ? c.getNombreEntrenador() : "").toLowerCase();
                if (!nombre.contains(search) && !trainer.contains(search)) continue;
            }
            model.addRow(new Object[]{c.getIdClase(), c.getNombre(),
                c.getDescripcion() != null ? (c.getDescripcion().length() > 30 ? c.getDescripcion().substring(0,30)+"..." : c.getDescripcion()) : "",
                c.getDiaSemana() != null ? c.getDiaSemana() : "",
                c.getHorarioInicio() != null ? c.getHorarioInicio().toString() : "",
                c.getNombreEntrenador() != null ? c.getNombreEntrenador() : (c.getIdEntrenador() > 0 ? "ID: "+c.getIdEntrenador() : "-"),
                c.getCapacidadMaxima(), getInscritosForClass(c.getIdClase()),
                c.isActiva() ? "ACTIVA" : "CANCELADA"});
        }
    }

    private int getInscritosForClass(int idClase) {
        try {
            ApiResponse resp = ClassApiService.getInstance().getEnrolledStudents(idClase);
            if (resp != null && resp.isOk() && resp.data != null && resp.data.isJsonArray()) return resp.data.getAsJsonArray().size();
        } catch (Exception ignored) {}
        return 0;
    }

    private void showAddClassDialog(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField timeField = new JTextField("07:00");
        JTextField capField = new JTextField("20");
        List<Entrenador> trainers = adminController.obtenerTodosEntrenadores();
        String[] trainerNames = trainers.stream().map(t -> t.getIdEntrenador() + " - " + t.getNombre() + " " + t.getApellido()).toArray(String[]::new);
        JComboBox<String> entBox = new JComboBox<>(trainerNames);
        Object[] fields = {"Nombre:", nameField, "Descripcion:", descField, "Fecha:", dateField, "Hora:", timeField, "Cupo:", capField, "Entrenador:", entBox};
        JPanel form = createFormPanel(fields);

        if (JOptionPane.showConfirmDialog(this, form, "Nueva Clase", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            int entId = Integer.parseInt(trainerNames[entBox.getSelectedIndex()].split(" - ")[0]);
            Clase c = new Clase(nameField.getText().trim(), descField.getText().trim(), entId,
                LocalTime.parse(timeField.getText().trim()), LocalTime.parse(timeField.getText().trim()).plusHours(1),
                dateField.getText().trim(), Integer.parseInt(capField.getText().trim()));
            adminController.crearClase(c);
            loadClasses(model);
        }
    }

    private void showEditClassDialog(int row, DefaultTableModel model) {
        List<Clase> list = adminController.obtenerClases();
        if (row >= list.size()) return;
        Clase c = list.get(row);
        JTextField nameField = new JTextField(c.getNombre());
        JTextField descField = new JTextField(c.getDescripcion() != null ? c.getDescripcion() : "");
        JTextField dateField = new JTextField(c.getDiaSemana() != null ? c.getDiaSemana() : LocalDate.now().toString());
        JTextField timeField = new JTextField(c.getHorarioInicio() != null ? c.getHorarioInicio().toString() : "07:00");
        JTextField capField = new JTextField(String.valueOf(c.getCapacidadMaxima()));
        List<Entrenador> trainers = adminController.obtenerTodosEntrenadores();
        String[] trainerNames = trainers.stream().map(t -> t.getIdEntrenador() + " - " + t.getNombre() + " " + t.getApellido()).toArray(String[]::new);
        JComboBox<String> entBox = new JComboBox<>(trainerNames);
        for (int i = 0; i < trainers.size(); i++) { if (trainers.get(i).getIdEntrenador() == c.getIdEntrenador()) { entBox.setSelectedIndex(i); break; } }
        Object[] fields = {"Nombre:", nameField, "Descripcion:", descField, "Fecha:", dateField, "Hora:", timeField, "Cupo:", capField, "Entrenador:", entBox};
        if (JOptionPane.showConfirmDialog(this, createFormPanel(fields), "Editar Clase", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            int entId = Integer.parseInt(trainerNames[entBox.getSelectedIndex()].split(" - ")[0]);
            JsonObject body = new JsonObject();
            body.addProperty("nombre", nameField.getText().trim());
            body.addProperty("descripcion", descField.getText().trim());
            body.addProperty("fecha", dateField.getText().trim());
            body.addProperty("hora", timeField.getText().trim() + ":00");
            body.addProperty("cupo_maximo", Integer.parseInt(capField.getText().trim()));
            body.addProperty("id_entrenador", entId);
            ApiResponse resp = ClassApiService.getInstance().update(c.getIdClase(), body);
            if (resp != null && resp.isOk()) loadClasses(model);
            else JOptionPane.showMessageDialog(this, "Error: " + (resp != null ? resp.message : ""), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============ TAB: MI PERFIL ============
    private JPanel createProfileTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.anchor = GridBagConstraints.WEST;

        addPRow(fields, "Nombre:", usuarioActual.getNombre(), 0, gbc);
        addPRow(fields, "Apellido:", usuarioActual.getApellido(), 1, gbc);
        addPRow(fields, "Email:", usuarioActual.getEmail(), 2, gbc);
        addPRow(fields, "Rol:", "ADMINISTRADOR", 3, gbc);

        JButton changePwdBtn = new JButton("Cambiar Contrasena");
        changePwdBtn.setBackground(RED); changePwdBtn.setForeground(Color.WHITE);
        changePwdBtn.setFont(new Font("Arial", Font.BOLD, 11));
        changePwdBtn.setAlignmentX(CENTER_ALIGNMENT);
        changePwdBtn.addActionListener(e -> showChangePasswordDialog());
        changePwdBtn.setMaximumSize(new Dimension(200, 35));

        panel.add(fields);
        panel.add(Box.createVerticalStrut(20));
        panel.add(changePwdBtn);
        return panel;
    }

    private void showChangePasswordDialog() {
        JPasswordField currentPwd = new JPasswordField();
        JPasswordField newPwd = new JPasswordField();
        JPasswordField confirmPwd = new JPasswordField();
        currentPwd.setBackground(CARD_BG); currentPwd.setForeground(Color.WHITE); currentPwd.setCaretColor(RED);
        newPwd.setBackground(CARD_BG); newPwd.setForeground(Color.WHITE); newPwd.setCaretColor(RED);
        confirmPwd.setBackground(CARD_BG); confirmPwd.setForeground(Color.WHITE); confirmPwd.setCaretColor(RED);
        Object[] fields = {"Contrasena actual:", currentPwd, "Nueva contrasena:", newPwd, "Confirmar nueva:", confirmPwd};
        JPanel form = createFormPanel(fields);
        if (JOptionPane.showConfirmDialog(this, form, "Cambiar Contrasena", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (!new String(newPwd.getPassword()).equals(new String(confirmPwd.getPassword()))) {
                JOptionPane.showMessageDialog(this, "Las contrasenas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                ApiResponse resp = AuthApiService.getInstance().changePassword(
                    new String(currentPwd.getPassword()), new String(newPwd.getPassword()));
                JOptionPane.showMessageDialog(this, resp.isOk() ? "Contrasena actualizada!" : resp.message);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // ============ UTILIDADES ============
    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(CARD_BG);
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(0x3A, 0x3A, 0x3C));
        table.setSelectionBackground(RED);
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(32);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setBackground(DARK);
        table.getTableHeader().setForeground(RED);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        table.getTableHeader().setReorderingAllowed(false);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : new Color(0x25, 0x25, 0x28));
                }
                if (c instanceof JLabel) {
                    JLabel l = (JLabel) c;
                    l.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                }
                return c;
            }
        });
        return table;
    }

    private void setColumnWidths(JTable table, int[] widths) {
        for (int i = 0; i < Math.min(widths.length, table.getColumnCount()); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private JButton actionBtn(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(RED);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 10));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setFocusPainted(false);
        return btn;
    }

    private JPanel createFormPanel(Object[] rows) {
        JPanel panel = new JPanel(new GridLayout(rows.length / 2, 2, 8, 6));
        panel.setBackground(BG);
        for (int i = 0; i < rows.length; i += 2) {
            JLabel lbl = new JLabel((String) rows[i]);
            lbl.setForeground(GRAY);
            lbl.setFont(new Font("Arial", Font.PLAIN, 11));
            panel.add(lbl);
            if (rows[i + 1] instanceof JTextField) {
                JTextField tf = (JTextField) rows[i + 1];
                tf.setBackground(CARD_BG);
                tf.setForeground(Color.WHITE);
                tf.setCaretColor(RED);
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x3A, 0x3A, 0x3C), 1),
                    BorderFactory.createEmptyBorder(4, 6, 4, 6)));
                tf.setFont(new Font("Arial", Font.PLAIN, 11));
                panel.add(tf);
            } else if (rows[i + 1] instanceof JComboBox) {
                JComboBox<?> cb = (JComboBox<?>) rows[i + 1];
                cb.setBackground(CARD_BG);
                cb.setForeground(Color.WHITE);
                cb.setFont(new Font("Arial", Font.PLAIN, 11));
                panel.add(cb);
            } else if (rows[i + 1] instanceof JCheckBox) {
                JCheckBox chk = (JCheckBox) rows[i + 1];
                chk.setBackground(BG);
                chk.setForeground(Color.WHITE);
                panel.add(chk);
            } else {
                panel.add((java.awt.Component) rows[i + 1]);
            }
        }
        return panel;
    }

    private void addPRow(JPanel p, String label, String val, int row, GridBagConstraints gbc) {
        JLabel l = new JLabel(label); l.setForeground(RED); l.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = row; p.add(l, gbc);
        JLabel v = new JLabel(val); v.setForeground(Color.WHITE); v.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1; p.add(v, gbc);
    }

    private void logout() {
        authController.logout();
        dispose();
        new LoginView();
    }

    private JScrollPane wrapTable(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(800, 280));
        return sp;
    }
}
