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
    private JLabel totalAthletes, totalTrainers, totalWods, totalMemberships;

    private static final Color BG = new Color(0x11, 0x11, 0x13);
    private static final Color CARD_BG = new Color(0x1C, 0x1C, 0x1E);
    private static final Color RED = new Color(0xFF, 0x3B, 0x30);
    private static final Color GRAY = new Color(0xB0, 0xB0, 0xB5);
    private static final Color DARK = new Color(0x0A, 0x0A, 0x0C);

    public AdminDashboard() {
        authController = AuthController.getInstance();
        adminController = new AdminController();
        usuarioActual = authController.getUsuarioActual();
        adminController.setOnDataChanged(() -> {
            dispose();
            new AdminDashboard().setVisible(true);
        });
        initializeUI();
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

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        statsPanel.setBackground(BG);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        totalAthletes = statLabel("0");
        totalTrainers = statLabel("0");
        totalWods = statLabel("0");
        totalMemberships = statLabel("0");

        statsPanel.add(statCard("Atletas", totalAthletes, "👥"));
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
                totalTrainers.setText(String.valueOf(s.has("totalTrainers") ? s.get("totalTrainers").getAsInt() : 0));
                totalWods.setText(String.valueOf(s.has("totalWODs") ? s.get("totalWODs").getAsInt() : 0));
                totalMemberships.setText(String.valueOf(s.has("totalMemberships") ? s.get("totalMemberships").getAsInt() : 0));
            }
        } catch (Exception e) {
            totalAthletes.setText("--");
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
    private JPanel createAthletesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        String[] cols = {"ID", "Nombre", "Email", "Telefono"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        loadAthletes(model);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(BG);
        JButton addBtn = actionBtn("+ Agregar Atleta");
        addBtn.addActionListener(e -> showAddAthleteDialog(model));
        JButton editBtn = actionBtn("Editar");
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) showEditAthleteDialog(row, model, table);
        });
        JButton delBtn = actionBtn("Eliminar");
        delBtn.setBackground(new Color(180, 40, 40));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                adminController.eliminarAtleta(id);
                model.removeRow(row);
            }
        });
        btnPanel.add(addBtn); btnPanel.add(editBtn); btnPanel.add(delBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadAthletes(DefaultTableModel model) {
        model.setRowCount(0);
        List<Atleta> list = adminController.obtenerTodosAtletas();
        for (Atleta a : list) {
            model.addRow(new Object[]{a.getIdAtleta(), a.getNombreCompleto(), a.getEmail(), a.getTelefono()});
        }
    }

    private void showAddAthleteDialog(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        Object[] fields = {"Nombre:", nameField, "Apellido:", lastNameField, "Email:", emailField, "Telefono:", phoneField};
        JPanel form = createFormPanel(fields);

        int result = JOptionPane.showConfirmDialog(this, form, "Nuevo Atleta", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                JsonObject body = new JsonObject();
                body.addProperty("nombre", nameField.getText().trim());
                body.addProperty("apellido", lastNameField.getText().trim());
                body.addProperty("email", emailField.getText().trim());
                body.addProperty("fecha_nacimiento", LocalDate.now().minusYears(20).toString());
                body.addProperty("telefono", phoneField.getText().trim());
                AthleteApiService.getInstance().create(body);
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
        Object[] fields = {"Nombre:", nameField, "Apellido:", lastNameField, "Email:", emailField, "Telefono:", phoneField};
        JPanel form = createFormPanel(fields);

        if (JOptionPane.showConfirmDialog(this, form, "Editar Atleta", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            JsonObject body = new JsonObject();
            body.addProperty("nombre", nameField.getText().trim());
            body.addProperty("apellido", lastNameField.getText().trim());
            body.addProperty("email", emailField.getText().trim());
            body.addProperty("telefono", phoneField.getText().trim());
            AthleteApiService.getInstance().update(a.getIdAtleta(), body);
            loadAthletes(model);
        }
    }

    // ============ TAB: ENTRENADORES ============
    private JPanel createTrainersTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        String[] cols = {"ID", "Nombre", "Email", "Especialidad", "Exp. (anos)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        loadTrainers(model);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(BG);
        JButton addBtn = actionBtn("+ Agregar Entrenador");
        addBtn.addActionListener(e -> showAddTrainerDialog(model));
        JButton delBtn = actionBtn("Eliminar");
        delBtn.setBackground(new Color(180, 40, 40));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                adminController.eliminarEntrenador(id);
                model.removeRow(row);
            }
        });
        btnPanel.add(addBtn); btnPanel.add(delBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadTrainers(DefaultTableModel model) {
        model.setRowCount(0);
        for (Entrenador e : adminController.obtenerTodosEntrenadores()) {
            model.addRow(new Object[]{e.getIdEntrenador(), e.getNombreCompleto(), e.getEmail(),
                e.getEspecialidad() != null ? e.getEspecialidad() : "", e.getExperienciaAnios()});
        }
    }

    private void showAddTrainerDialog(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField specField = new JTextField();
        JTextField expField = new JTextField("0");
        Object[] fields = {"Nombre:", nameField, "Apellido:", lastNameField, "Email:", emailField, "Especialidad:", specField, "Experiencia:", expField};
        JPanel form = createFormPanel(fields);

        if (JOptionPane.showConfirmDialog(this, form, "Nuevo Entrenador", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            JsonObject body = new JsonObject();
            body.addProperty("nombre", nameField.getText().trim());
            body.addProperty("apellido", lastNameField.getText().trim());
            body.addProperty("email", emailField.getText().trim());
            body.addProperty("especialidad", specField.getText().trim());
            body.addProperty("anios_experiencia", Integer.parseInt(expField.getText().trim()));
            body.addProperty("fecha_nacimiento", LocalDate.now().minusYears(25).toString());
            TrainerApiService.getInstance().create(body);
            loadTrainers(model);
        }
    }

    // ============ TAB: WODs ============
    private JPanel createWodsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        String[] cols = {"ID", "Titulo", "Fecha", "Tipo", "Nivel"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BG);
        JComboBox<String> monthBox = new JComboBox<>(new String[]{"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"});
        monthBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        JButton loadBtn = actionBtn("Cargar WODs");
        JButton addBtn = actionBtn("+ Nuevo WOD");

        loadBtn.addActionListener(e -> loadWods(model, LocalDate.now().getYear(), monthBox.getSelectedIndex() + 1));
        addBtn.addActionListener(e -> showAddWodDialog(model));
        topPanel.add(new JLabel("Mes:"));
        topPanel.add(monthBox);
        topPanel.add(loadBtn);
        topPanel.add(addBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        loadWods(model, LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        return panel;
    }

    private void loadWods(DefaultTableModel model, int year, int month) {
        model.setRowCount(0);
        try {
            ApiResponse resp = WodApiService.getInstance().getByMonth(year, month);
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    JsonObject w = e.getAsJsonObject();
                    model.addRow(new Object[]{
                        w.has("id_wod") ? w.get("id_wod").getAsInt() : 0,
                        w.has("titulo") ? w.get("titulo").getAsString() : "",
                        w.has("fecha") ? w.get("fecha").getAsString() : "",
                        w.has("tipo") ? w.get("tipo").getAsString() : "",
                        w.has("nivel") ? w.get("nivel").getAsString() : ""
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar WODs: " + ex.getMessage());
        }
    }

    private void showAddWodDialog(DefaultTableModel model) {
        JTextField titleField = new JTextField();
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField descField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"AMRAP","FOR TIME","EMOM","TABATA","STRENGTH","CHIPPER","HERO","BENCHMARK"});
        JComboBox<String> levelBox = new JComboBox<>(new String[]{"Principiante","Intermedio","Avanzado","RX","Todos"});
        Object[] fields = {"Titulo:", titleField, "Fecha:", dateField, "Descripcion:", descField, "Tipo:", typeBox, "Nivel:", levelBox};
        JPanel form = createFormPanel(fields);

        if (JOptionPane.showConfirmDialog(this, form, "Nuevo WOD", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                JsonObject body = new JsonObject();
                body.addProperty("titulo", titleField.getText().trim());
                body.addProperty("fecha", dateField.getText().trim());
                body.addProperty("descripcion", descField.getText().trim());
                body.addProperty("tipo", typeBox.getSelectedItem().toString());
                body.addProperty("nivel", levelBox.getSelectedItem().toString());
                JsonArray horarios = new JsonArray();
                JsonObject h = new JsonObject();
                h.addProperty("hora", "07:00:00");
                h.addProperty("cupo_maximo", 20);
                horarios.add(h);
                body.add("horarios", horarios);
                WodApiService.getInstance().create(body);
                JOptionPane.showMessageDialog(this, "WOD creado exitosamente!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // ============ TAB: MEMBRESIAS ============
    private JPanel createMembershipsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        String[] cols = {"ID", "Nombre", "Precio", "Duracion (dias)", "Activa"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        loadMemberships(model);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(BG);
        JButton addBtn = actionBtn("+ Nueva Membresia");
        addBtn.addActionListener(e -> showAddMembershipDialog(model));
        JButton delBtn = actionBtn("Eliminar");
        delBtn.setBackground(new Color(180, 40, 40));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                adminController.obtenerMembresias().stream().filter(m -> m.getIdMembresia() == id).findFirst()
                    .ifPresent(m -> adminController.actualizarMembresia(new Membresia(m.getNombre(), m.getDescripcion(), m.getPrecio(), m.getDuracionDias(), m.getBeneficios()) {{ setIdMembresia(id); }}));
                loadMemberships(model);
            }
        });
        btnPanel.add(addBtn); btnPanel.add(delBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadMemberships(DefaultTableModel model) {
        model.setRowCount(0);
        for (Membresia m : adminController.obtenerMembresias()) {
            model.addRow(new Object[]{m.getIdMembresia(), m.getNombre(), "$" + m.getPrecio(), m.getDuracionDias(), m.isActiva() ? "Si" : "No"});
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
    private JPanel createExercisesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        String[] cols = {"ID", "Nombre", "Descripcion"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BG);
        JTextField searchField = new JTextField(15);
        JButton searchBtn = actionBtn("Buscar");
        searchBtn.addActionListener(e -> {
            try {
                ApiResponse resp = ExerciseApiService.getInstance().search(searchField.getText().trim());
                model.setRowCount(0);
                if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                    JsonArray arr = resp.data.getAsJsonArray();
                    for (JsonElement el : arr) {
                        JsonObject ex = el.getAsJsonObject();
                        model.addRow(new Object[]{ex.has("id_ejercicio") ? ex.get("id_ejercicio").getAsInt() : 0,
                            ex.has("nombre") ? ex.get("nombre").getAsString() : "",
                            ex.has("descripcion") ? ex.get("descripcion").getAsString() : ""});
                    }
                }
            } catch (Exception ex2) { JOptionPane.showMessageDialog(this, "Error: " + ex2.getMessage()); }
        });
        topPanel.add(new JLabel("Buscar:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        loadExercises(model);
        return panel;
    }

    private void loadExercises(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            ApiResponse resp = ExerciseApiService.getInstance().getAll();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    JsonObject ex = e.getAsJsonObject();
                    model.addRow(new Object[]{ex.has("id_ejercicio") ? ex.get("id_ejercicio").getAsInt() : 0,
                        ex.has("nombre") ? ex.get("nombre").getAsString() : "",
                        ex.has("descripcion") ? ex.get("descripcion").getAsString() : ""});
                }
            }
        } catch (Exception ex) { /* no carga, tabla vacia */ }
    }

    // ============ TAB: CLASES ============
    private JPanel createClassesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        String[] cols = {"ID", "Nombre", "Fecha", "Hora", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        loadClasses(model);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(BG);
        JButton addBtn = actionBtn("+ Nueva Clase");
        addBtn.addActionListener(e -> showAddClassDialog(model));
        JButton delBtn = actionBtn("Cancelar");
        delBtn.setBackground(new Color(180, 40, 40));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                adminController.eliminarClase(id);
                loadClasses(model);
            }
        });
        btnPanel.add(addBtn); btnPanel.add(delBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadClasses(DefaultTableModel model) {
        model.setRowCount(0);
        for (Clase c : adminController.obtenerClases()) {
            model.addRow(new Object[]{c.getIdClase(), c.getNombre(),
                c.getDiaSemana() != null ? c.getDiaSemana() : "",
                c.getHorarioInicio() != null ? c.getHorarioInicio().toString() : "",
                c.isActiva() ? "ACTIVA" : "CANCELADA"});
        }
    }

    private void showAddClassDialog(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField timeField = new JTextField("07:00");
        JTextField capField = new JTextField("20");
        Object[] fields = {"Nombre:", nameField, "Fecha:", dateField, "Hora:", timeField, "Cupo:", capField};
        JPanel form = createFormPanel(fields);

        if (JOptionPane.showConfirmDialog(this, form, "Nueva Clase", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Clase c = new Clase(nameField.getText().trim(), "Clase IroncladBox", 1,
                LocalTime.parse(timeField.getText().trim()), LocalTime.parse(timeField.getText().trim()).plusHours(1),
                dateField.getText().trim(), Integer.parseInt(capField.getText().trim()));
            adminController.crearClase(c);
            loadClasses(model);
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
        currentPwd.setBackground(CARD_BG); currentPwd.setForeground(Color.WHITE); currentPwd.setCaretColor(RED);
        newPwd.setBackground(CARD_BG); newPwd.setForeground(Color.WHITE); newPwd.setCaretColor(RED);
        Object[] fields = {"Contrasena actual:", currentPwd, "Nueva contrasena:", newPwd};
        JPanel form = createFormPanel(fields);
        if (JOptionPane.showConfirmDialog(this, form, "Cambiar Contrasena", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                JsonObject body = new JsonObject();
                body.addProperty("currentPassword", new String(currentPwd.getPassword()));
                body.addProperty("newPassword", new String(newPwd.getPassword()));
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
        table.setRowHeight(28);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setBackground(DARK);
        table.getTableHeader().setForeground(RED);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        return table;
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
                panel.add((JComboBox<?>) rows[i + 1]);
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
}
