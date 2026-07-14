package com.ironcladbox.view;

import com.ironcladbox.controller.AuthController;
import com.ironcladbox.controller.EntrenadorController;
import com.ironcladbox.model.*;
import com.ironcladbox.service.*;
import com.ironcladbox.dto.ApiResponse;
import com.ironcladbox.util.ValidationUtils;
import com.google.gson.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;

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
    private static final Color GREEN = new Color(0x34, 0xC7, 0x59);
    private static final Color BLUE = new Color(0x4A, 0x90, 0xD9);
    private static final Color ORANGE = new Color(0xFF, 0x95, 0x00);

    private YearMonth currentMonth = YearMonth.now();
    private JPanel calendarGrid;
    private JLabel monthLabel;
    private DefaultTableModel athleteModel, exerciseModel;
    private JTextArea wodDetailArea;
    private JsonObject selectedWodJson;

    public EntrenadorDashboard() {
        authController = AuthController.getInstance();
        entrenadorController = new EntrenadorController();
        entrenadorController.setOnDataChanged(() -> refreshAllTabs());
        new javax.swing.Timer(15000, e -> refreshAllTabs()).start();
        usuarioActual = authController.getUsuarioActual();
        entrenadorActual = (Entrenador) usuarioActual;
        initializeUI();
    }

    private void refreshAllTabs() {
        SwingUtilities.invokeLater(() -> {
            if (athleteModel != null) loadMyAthletes(athleteModel);
            if (exerciseModel != null) loadExercises(exerciseModel);
            if (calendarGrid != null) buildCalendar();
        });
    }

    // ============================================================
    // MAIN UI
    // ============================================================
    private void initializeUI() {
        setTitle("IroncladBox - Dashboard Entrenador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setMinimumSize(new Dimension(800, 550));
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

    // ============================================================
    // TAB 1: INICIO (DASHBOARD)
    // ============================================================
    private JPanel createHomeTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Bienvenido, " + usuarioActual.getNombreCompleto(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));

        JLabel subtitle = new JLabel("Entrenador de IroncladBox", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(GRAY);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(30));

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        statsPanel.setBackground(BG);
        statsPanel.setMaximumSize(new Dimension(900, 120));
        statsPanel.setAlignmentX(CENTER_ALIGNMENT);

        try {
            ApiResponse wodsResp = TrainerApiService.getInstance().getMyWods();
            ApiResponse athletesResp = TrainerApiService.getInstance().getMyAthletes();
            int totalWods = 0, totalAthletes = 0, upcomingWods = 0;
            LocalDate today = LocalDate.now();

            if (wodsResp.isOk() && wodsResp.data.isJsonArray()) {
                for (JsonElement e : wodsResp.data.getAsJsonArray()) {
                    totalWods++;
                    JsonObject w = e.getAsJsonObject();
                    String fecha = w.has("fecha") ? w.get("fecha").getAsString() : "";
                    if (!fecha.isEmpty()) {
                        try { if (!LocalDate.parse(fecha).isBefore(today)) upcomingWods++; } catch (Exception ex) {}
                    }
                }
            }
            if (athletesResp.isOk() && athletesResp.data.isJsonArray()) {
                totalAthletes = athletesResp.data.getAsJsonArray().size();
            }

            statsPanel.add(createStatCard("WODs Totales", String.valueOf(totalWods), ORANGE, "\uD83D\uDCAA"));
            statsPanel.add(createStatCard("WODs Proximos", String.valueOf(upcomingWods), GREEN, "\uD83D\uDCC5"));
            statsPanel.add(createStatCard("Mis Atletas", String.valueOf(totalAthletes), BLUE, "\uD83D\uDC65"));
        } catch (Exception ex) {
            statsPanel.add(createStatCard("WODs Totales", "—", ORANGE, "\uD83D\uDCAA"));
            statsPanel.add(createStatCard("WODs Proximos", "—", GREEN, "\uD83D\uDCC5"));
            statsPanel.add(createStatCard("Mis Atletas", "—", BLUE, "\uD83D\uDC65"));
        }

        panel.add(statsPanel);
        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color, String emoji) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x3A, 0x3A, 0x3C), 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        JLabel emojiLbl = new JLabel(emoji); emojiLbl.setFont(new Font("Arial", Font.PLAIN, 28));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2; gbc.insets = new Insets(0, 0, 0, 16);
        card.add(emojiLbl, gbc);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Arial", Font.BOLD, 28));
        valueLbl.setForeground(color);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 1; gbc.insets = new Insets(0, 0, 0, 0);
        card.add(valueLbl, gbc);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLbl.setForeground(GRAY);
        gbc.gridy = 1;
        card.add(titleLbl, gbc);

        return card;
    }

    // ============================================================
    // TAB 2: MIS CLASES
    // ============================================================
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
                String nm = nameField.getText();
                String fe = dateField.getText();
                String ho = timeField.getText();
                String ca = capField.getText();
                String err = ValidationUtils.validateMinLength(nm, 3, "El nombre");
                if (err != null) { JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE); return; }
                err = ValidationUtils.validateDateNotPast(fe, "La fecha");
                if (err != null) { JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE); return; }
                err = ValidationUtils.validateTimeRange(ho, "La hora");
                if (err != null) { JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE); return; }
                err = ValidationUtils.validatePositiveInteger(ca, "El cupo");
                if (err != null) { JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE); return; }
                Clase c = new Clase(nm, descField.getText(), entrenadorActual.getIdEntrenador(), LocalTime.parse(ho), LocalTime.parse(ho).plusHours(1), fe, Integer.parseInt(ca));
                entrenadorController.crearClase(c);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        }
    }

    // ============================================================
    // TAB 3: WODS (CALENDAR + CRUD)
    // ============================================================
    private JPanel createWodsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setBackground(BG);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nav.setBackground(BG);
        JButton prevBtn = new JButton("<");
        styleNavBtn(prevBtn);
        prevBtn.addActionListener(e -> { currentMonth = currentMonth.minusMonths(1); buildCalendar(); });

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setForeground(Color.WHITE);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton nextBtn = new JButton(">");
        styleNavBtn(nextBtn);
        nextBtn.addActionListener(e -> { currentMonth = currentMonth.plusMonths(1); buildCalendar(); });

        JButton todayBtn = actionBtn("Hoy");
        todayBtn.addActionListener(e -> { currentMonth = YearMonth.now(); buildCalendar(); });

        nav.add(prevBtn);
        nav.add(monthLabel);
        nav.add(nextBtn);
        nav.add(todayBtn);
        calendarPanel.add(nav, BorderLayout.NORTH);

        calendarGrid = new JPanel();
        calendarGrid.setBackground(BG);
        calendarPanel.add(new JScrollPane(calendarGrid), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(BG);
        rightPanel.setPreferredSize(new Dimension(320, 0));

        JPanel wodBtns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wodBtns.setBackground(BG);
        JButton createWodBtn = actionBtn("+ Crear WOD");
        createWodBtn.addActionListener(e -> { showCreateWodDialog(); buildCalendar(); });
        wodBtns.add(createWodBtn);

        wodDetailArea = new JTextArea("Selecciona un WOD en el calendario para ver detalles.");
        wodDetailArea.setEditable(false);
        wodDetailArea.setBackground(CARD_BG);
        wodDetailArea.setForeground(Color.WHITE);
        wodDetailArea.setFont(new Font("Arial", Font.PLAIN, 12));
        wodDetailArea.setLineWrap(true);
        wodDetailArea.setWrapStyleWord(true);
        JScrollPane detailScroll = new JScrollPane(wodDetailArea);
        detailScroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel wodActionBtns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wodActionBtns.setBackground(BG);
        JButton editWodBtn = actionBtn("Editar WOD");
        editWodBtn.addActionListener(e -> { if (selectedWodJson != null) showEditWodDialog(); });
        JButton deleteWodBtn = new JButton("Eliminar WOD");
        deleteWodBtn.setBackground(new Color(0xC0, 0x39, 0x2B));
        deleteWodBtn.setForeground(Color.WHITE);
        deleteWodBtn.setFont(new Font("Arial", Font.BOLD, 10));
        deleteWodBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        deleteWodBtn.setFocusPainted(false);
        deleteWodBtn.addActionListener(e -> { if (selectedWodJson != null) showDeleteWodConfirm(); });
        wodActionBtns.add(editWodBtn);
        wodActionBtns.add(deleteWodBtn);

        rightPanel.add(wodBtns, BorderLayout.NORTH);
        rightPanel.add(detailScroll, BorderLayout.CENTER);
        rightPanel.add(wodActionBtns, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, calendarPanel, rightPanel);
        splitPane.setDividerLocation(700);
        splitPane.setBackground(BG);
        panel.add(splitPane, BorderLayout.CENTER);

        buildCalendar();
        return panel;
    }

    private void buildCalendar() {
        monthLabel.setText(currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es")) + " " + currentMonth.getYear());
        calendarGrid.removeAll();
        calendarGrid.setLayout(new GridLayout(0, 7, 2, 2));

        String[] diasSemana = {"Dom", "Lun", "Mar", "Mie", "Jue", "Vie", "Sab"};
        for (String d : diasSemana) {
            JLabel header = new JLabel(d, SwingConstants.CENTER);
            header.setOpaque(true);
            header.setBackground(DARK);
            header.setForeground(RED);
            header.setFont(new Font("Arial", Font.BOLD, 10));
            calendarGrid.add(header);
        }

        LocalDate firstDay = currentMonth.atDay(1);
        int startDow = firstDay.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        JsonObject wodsByDay = new JsonObject();
        try {
            ApiResponse resp = WodApiService.getInstance().getByMonth(currentMonth.getYear(), currentMonth.getMonthValue());
            if (resp.isOk() && resp.data.isJsonArray()) {
                for (JsonElement e : resp.data.getAsJsonArray()) {
                    JsonObject w = e.getAsJsonObject();
                    String fecha = w.has("fecha") ? w.get("fecha").getAsString() : "";
                    if (!fecha.isEmpty()) {
                        if (!wodsByDay.has(fecha)) wodsByDay.add(fecha, new JsonArray());
                        wodsByDay.getAsJsonArray(fecha).add(w);
                    }
                }
            }
        } catch (Exception ex) {}

        for (int i = 0; i < startDow; i++) calendarGrid.add(new JLabel(""));

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            String fechaStr = date.toString();
            JPanel cell = new JPanel(new BorderLayout());
            cell.setBackground(date.equals(today) ? new Color(0x2A, 0x1A, 0x1A) : CARD_BG);
            cell.setBorder(BorderFactory.createLineBorder(new Color(0x3A, 0x3A, 0x3C)));
            cell.setToolTipText(fechaStr);

            JLabel dayLbl = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            dayLbl.setFont(new Font("Arial", Font.BOLD, 12));
            dayLbl.setForeground(Color.WHITE);
            cell.add(dayLbl, BorderLayout.NORTH);

            if (wodsByDay.has(fechaStr)) {
                JsonArray wods = wodsByDay.getAsJsonArray(fechaStr);
                JLabel countLbl = new JLabel(wods.size() + " WOD", SwingConstants.CENTER);
                countLbl.setFont(new Font("Arial", Font.PLAIN, 9));
                countLbl.setForeground(RED);
                cell.add(countLbl, BorderLayout.CENTER);
                cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                cell.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        showWodDetail(wods);
                    }
                });
            }

            calendarGrid.add(cell);
        }

        calendarGrid.revalidate();
        calendarGrid.repaint();
    }

    private void showWodDetail(JsonArray wods) {
        StringBuilder sb = new StringBuilder();
        for (JsonElement e : wods) {
            JsonObject w = e.getAsJsonObject();
            if (sb.length() > 0) sb.append("\n" + "═".repeat(40) + "\n\n");
            sb.append("TITULO: ").append(w.has("titulo") ? w.get("titulo").getAsString() : "N/A").append("\n");
            sb.append("Tipo: ").append(w.has("tipo") ? w.get("tipo").getAsString() : "N/A").append("\n");
            sb.append("Nivel: ").append(w.has("nivel") ? w.get("nivel").getAsString() : "N/A").append("\n");
            sb.append("Fecha: ").append(w.has("fecha") ? w.get("fecha").getAsString() : "N/A").append("\n\n");
            sb.append("DESCRIPCION:\n").append(w.has("descripcion") ? w.get("descripcion").getAsString() : "N/A").append("\n");
            if (w.has("horarios") && w.get("horarios").isJsonArray()) {
                sb.append("\nHORARIOS:\n");
                for (JsonElement h : w.getAsJsonArray("horarios")) {
                    JsonObject horario = h.getAsJsonObject();
                    sb.append("  • ").append(horario.has("hora") ? horario.get("hora").getAsString() : "?");
                    sb.append(" | Cupo: ").append(horario.has("cupo_maximo") ? horario.get("cupo_maximo").getAsInt() : 0);
                    int inscritos = horario.has("inscritos") ? horario.get("inscritos").getAsInt() : 0;
                    sb.append(" | Inscritos: ").append(inscritos).append("\n");
                }
            }
            selectedWodJson = w;
        }
        wodDetailArea.setText(sb.toString());
        wodDetailArea.setCaretPosition(0);
    }

    private void showCreateWodDialog() {
        JTextField tituloField = new JTextField();
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setBackground(CARD_BG); descArea.setForeground(Color.WHITE); descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        JTextField fechaField = new JTextField(LocalDate.now().toString());
        JComboBox<String> tipoBox = new JComboBox<>(new String[]{"AMRAP", "FOR TIME", "EMOM", "TABATA", "STRENGTH", "CHIPPER", "HERO", "BENCHMARK"});
        JComboBox<String> nivelBox = new JComboBox<>(new String[]{"Principiante", "Intermedio", "Avanzado", "Todos"});

        DefaultTableModel schedModel = new DefaultTableModel(new String[]{"Hora", "Cupo"}, 0);
        JTable schedTable = styledTable(schedModel);
        schedTable.setPreferredScrollableViewportSize(new Dimension(300, 80));
        JScrollPane schedScroll = new JScrollPane(schedTable);

        JTextField horaField = new JTextField("07:00", 5);
        JTextField cupoField = new JTextField("12", 5);
        JButton addSchedBtn = actionBtn("+ Horario");
        addSchedBtn.addActionListener(e -> {
            schedModel.addRow(new Object[]{horaField.getText(), Integer.parseInt(cupoField.getText())});
        });

        JPanel schedForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        schedForm.setBackground(BG);
        schedForm.add(new JLabel("Hora:")); schedForm.add(horaField);
        schedForm.add(new JLabel("Cupo:")); schedForm.add(cupoField);
        schedForm.add(addSchedBtn);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BG);
        form.add(createLabeledField("Titulo:", tituloField));
        form.add(createLabeledField("Descripcion:", descScroll));
        form.add(createLabeledField("Fecha:", fechaField));
        form.add(createLabeledField("Tipo:", tipoBox));
        form.add(createLabeledField("Nivel:", nivelBox));
        form.add(Box.createVerticalStrut(8));
        form.add(new JLabel("Horarios:"));
        form.add(schedForm);
        form.add(schedScroll);

        if (JOptionPane.showConfirmDialog(this, form, "Crear WOD", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String tit = tituloField.getText();
                String fec = fechaField.getText();
                String err = ValidationUtils.validateMinLength(tit, 3, "El titulo");
                if (err != null) { JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE); return; }
                err = ValidationUtils.validateDateNotPast(fec, "La fecha");
                if (err != null) { JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE); return; }

                JsonObject body = new JsonObject();
                body.addProperty("fecha", fec);
                body.addProperty("titulo", tit);
                body.addProperty("descripcion", descArea.getText());
                body.addProperty("tipo", (String) tipoBox.getSelectedItem());
                body.addProperty("nivel", (String) nivelBox.getSelectedItem());
                JsonArray horarios = new JsonArray();
                for (int i = 0; i < schedModel.getRowCount(); i++) {
                    String hora = (String) schedModel.getValueAt(i, 0);
                    err = ValidationUtils.validateTimeRange(hora, "La hora");
                    if (err != null) { JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE); return; }
                    int cupo = (int) schedModel.getValueAt(i, 1);
                    err = ValidationUtils.validatePositiveInteger(String.valueOf(cupo), "El cupo");
                    if (err != null) { JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE); return; }
                    JsonObject h = new JsonObject();
                    h.addProperty("hora", hora);
                    h.addProperty("cupo_maximo", cupo);
                    h.addProperty("id_entrenador", entrenadorActual.getIdEntrenador());
                    horarios.add(h);
                }
                body.add("horarios", horarios);

                ApiResponse resp = WodApiService.getInstance().create(body);
                if (resp.isOk()) {
                    JOptionPane.showMessageDialog(this, "WOD creado exitosamente!");
                    buildCalendar();
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + resp.message, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        }
    }

    private void showEditWodDialog() {
        if (selectedWodJson == null) return;
        JsonObject w = selectedWodJson;
        JTextField tituloField = new JTextField(w.has("titulo") ? w.get("titulo").getAsString() : "");
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setBackground(CARD_BG); descArea.setForeground(Color.WHITE); descArea.setLineWrap(true);
        descArea.setText(w.has("descripcion") ? w.get("descripcion").getAsString() : "");
        JScrollPane descScroll = new JScrollPane(descArea);
        JComboBox<String> tipoBox = new JComboBox<>(new String[]{"AMRAP", "FOR TIME", "EMOM", "TABATA", "STRENGTH", "CHIPPER", "HERO", "BENCHMARK"});
        String tipo = w.has("tipo") ? w.get("tipo").getAsString() : "AMRAP";
        for (int i = 0; i < tipoBox.getItemCount(); i++) { if (tipoBox.getItemAt(i).equals(tipo)) { tipoBox.setSelectedIndex(i); break; } }
        JComboBox<String> nivelBox = new JComboBox<>(new String[]{"Principiante", "Intermedio", "Avanzado", "Todos"});
        String nivel = w.has("nivel") ? w.get("nivel").getAsString() : "Intermedio";
        for (int i = 0; i < nivelBox.getItemCount(); i++) { if (nivelBox.getItemAt(i).equals(nivel)) { nivelBox.setSelectedIndex(i); break; } }

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BG);
        form.add(createLabeledField("Titulo:", tituloField));
        form.add(createLabeledField("Descripcion:", descScroll));
        form.add(createLabeledField("Tipo:", tipoBox));
        form.add(createLabeledField("Nivel:", nivelBox));

        if (JOptionPane.showConfirmDialog(this, form, "Editar WOD", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                JsonObject body = new JsonObject();
                body.addProperty("titulo", tituloField.getText());
                body.addProperty("descripcion", descArea.getText());
                body.addProperty("tipo", (String) tipoBox.getSelectedItem());
                body.addProperty("nivel", (String) nivelBox.getSelectedItem());
                int idWod = w.has("id_wod") ? w.get("id_wod").getAsInt() : 0;
                ApiResponse resp = WodApiService.getInstance().update(idWod, body);
                if (resp.isOk()) {
                    JOptionPane.showMessageDialog(this, "WOD actualizado!");
                    buildCalendar();
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + resp.message, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        }
    }

    private void showDeleteWodConfirm() {
        if (selectedWodJson == null) return;
        int idWod = selectedWodJson.has("id_wod") ? selectedWodJson.get("id_wod").getAsInt() : 0;
        String titulo = selectedWodJson.has("titulo") ? selectedWodJson.get("titulo").getAsString() : "este WOD";
        if (JOptionPane.showConfirmDialog(this, "Eliminar \"" + titulo + "\"?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                ApiResponse resp = WodApiService.getInstance().delete(idWod);
                if (resp.isOk()) {
                    JOptionPane.showMessageDialog(this, "WOD eliminado!");
                    wodDetailArea.setText("Selecciona un WOD en el calendario para ver detalles.");
                    selectedWodJson = null;
                    buildCalendar();
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + resp.message, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        }
    }

    // ============================================================
    // TAB 4: MIS ATLETAS
    // ============================================================
    private JPanel createMyAthletesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        String[] cols = {"ID", "Nombre", "Email", "Total Inscripciones", "Ultimo WOD"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        athleteModel = model;
        loadMyAthletes(model);
        JTable table = styledTable(model);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(BG);
        JTextField filterField = new JTextField(15);
        filterField.setBackground(CARD_BG); filterField.setForeground(Color.WHITE); filterField.setCaretColor(RED);
        JButton filterBtn = actionBtn("Buscar");
        filterBtn.addActionListener(e -> {
            String filter = filterField.getText().toLowerCase();
            model.setRowCount(0);
            loadMyAthletesFiltered(model, filter);
        });
        JButton refreshBtn = actionBtn("Actualizar");
        refreshBtn.addActionListener(e -> loadMyAthletes(model));
        top.add(new JLabel("Buscar:"));
        top.add(filterField);
        top.add(filterBtn);
        top.add(refreshBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadMyAthletes(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            ApiResponse resp = TrainerApiService.getInstance().getMyAthletes();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                for (JsonElement e : resp.data.getAsJsonArray()) {
                    JsonObject a = e.getAsJsonObject();
                    String nombre = (a.has("nombre") ? a.get("nombre").getAsString() : "") + " " + (a.has("apellido") ? a.get("apellido").getAsString() : "");
                    String email = a.has("email") ? a.get("email").getAsString() : "";
                    int insc = a.has("total_inscripciones") ? a.get("total_inscripciones").getAsInt() : (a.has("inscripciones_count") ? a.get("inscripciones_count").getAsInt() : 0);
                    String ultWod = a.has("ultimo_wod") ? a.get("ultimo_wod").getAsString() : (a.has("fecha_ultimo_wod") ? a.get("fecha_ultimo_wod").getAsString() : "N/A");
                    model.addRow(new Object[]{a.has("id_atleta") ? a.get("id_atleta").getAsInt() : 0, nombre, email, insc, ultWod});
                }
            }
        } catch (Exception ex) {}
    }

    private void loadMyAthletesFiltered(DefaultTableModel model, String filter) {
        try {
            ApiResponse resp = TrainerApiService.getInstance().getMyAthletes();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                for (JsonElement e : resp.data.getAsJsonArray()) {
                    JsonObject a = e.getAsJsonObject();
                    String nombre = (a.has("nombre") ? a.get("nombre").getAsString() : "") + " " + (a.has("apellido") ? a.get("apellido").getAsString() : "");
                    String email = a.has("email") ? a.get("email").getAsString() : "";
                    if (filter.isEmpty() || nombre.toLowerCase().contains(filter) || email.toLowerCase().contains(filter)) {
                        int insc = a.has("total_inscripciones") ? a.get("total_inscripciones").getAsInt() : (a.has("inscripciones_count") ? a.get("inscripciones_count").getAsInt() : 0);
                        String ultWod = a.has("ultimo_wod") ? a.get("ultimo_wod").getAsString() : (a.has("fecha_ultimo_wod") ? a.get("fecha_ultimo_wod").getAsString() : "N/A");
                        model.addRow(new Object[]{a.has("id_atleta") ? a.get("id_atleta").getAsInt() : 0, nombre, email, insc, ultWod});
                    }
                }
            }
        } catch (Exception ex) {}
    }

    // ============================================================
    // TAB 5: EJERCICIOS (CRUD)
    // ============================================================
    private JPanel createExercisesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        String[] cols = {"ID", "Nombre", "Descripcion", "Activo"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        exerciseModel = model;
        JTable table = styledTable(model);
        loadExercises(model);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { if (e.isPopupTrigger()) showExercisePopup(e, table, model); }
            public void mouseReleased(MouseEvent e) { if (e.isPopupTrigger()) showExercisePopup(e, table, model); }
        });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btns.setBackground(BG);
        JButton addBtn = actionBtn("+ Nuevo Ejercicio");
        addBtn.addActionListener(e -> { showExerciseDialog(null); loadExercises(model); });
        JButton editBtn = actionBtn("Editar");
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) { showExerciseDialog(row); loadExercises(model); }
        });
        JButton toggleBtn = new JButton("Activar/Desactivar");
        toggleBtn.setBackground(ORANGE);
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setFont(new Font("Arial", Font.BOLD, 10));
        toggleBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        toggleBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) toggleExercise(row, model);
        });
        JButton refreshBtn = actionBtn("Actualizar");
        refreshBtn.addActionListener(e -> loadExercises(model));
        btns.add(addBtn);
        btns.add(editBtn);
        btns.add(toggleBtn);
        btns.add(refreshBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void loadExercises(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            ApiResponse resp = ExerciseApiService.getInstance().getAll();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                for (JsonElement e : resp.data.getAsJsonArray()) {
                    JsonObject ex = e.getAsJsonObject();
                    String desc = ex.has("descripcion") ? ex.get("descripcion").getAsString() : "";
                    if (desc.length() > 60) desc = desc.substring(0, 57) + "...";
                    boolean activo = !ex.has("activo") || ex.get("activo").getAsBoolean();
                    model.addRow(new Object[]{
                        ex.has("id_ejercicio") ? ex.get("id_ejercicio").getAsInt() : 0,
                        ex.has("nombre") ? ex.get("nombre").getAsString() : "",
                        desc,
                        activo ? "ACTIVO" : "INACTIVO"
                    });
                }
            }
        } catch (Exception ex) {}
    }

    private void showExercisePopup(MouseEvent e, JTable table, DefaultTableModel model) {
        int row = table.rowAtPoint(e.getPoint());
        table.setRowSelectionInterval(row, row);
        JPopupMenu popup = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Editar");
        editItem.addActionListener(ev -> { showExerciseDialog(row); loadExercises(model); });
        JMenuItem toggleItem = new JMenuItem("Activar/Desactivar");
        toggleItem.addActionListener(ev -> toggleExercise(row, model));
        popup.add(editItem);
        popup.add(toggleItem);
        popup.show(table, e.getX(), e.getY());
    }

    private void showExerciseDialog(Integer editRow) {
        JTextField nameField = new JTextField();
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setBackground(CARD_BG); descArea.setForeground(Color.WHITE); descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        int exId = 0;

        if (editRow != null && exerciseModel != null && editRow < exerciseModel.getRowCount()) {
            nameField.setText((String) exerciseModel.getValueAt(editRow, 1));
            try {
                ApiResponse resp = ExerciseApiService.getInstance().getAll();
                if (resp.isOk() && resp.data.isJsonArray()) {
                    JsonArray arr = resp.data.getAsJsonArray();
                    if (editRow < arr.size()) {
                        JsonObject ex = arr.get(editRow).getAsJsonObject();
                        descArea.setText(ex.has("descripcion") ? ex.get("descripcion").getAsString() : "");
                        exId = ex.has("id_ejercicio") ? ex.get("id_ejercicio").getAsInt() : (int) exerciseModel.getValueAt(editRow, 0);
                    }
                }
            } catch (Exception ex) {}
        }

        Object[] fields = {"Nombre:", nameField, "Descripcion:", descScroll};
        JPanel form = createForm(fields);
        String title = editRow != null ? "Editar Ejercicio" : "Nuevo Ejercicio";
        if (JOptionPane.showConfirmDialog(this, form, title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String nm = nameField.getText();
                String err = ValidationUtils.validateMinLength(nm, 3, "El nombre");
                if (err != null) { JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE); return; }
                JsonObject body = new JsonObject();
                body.addProperty("nombre", nm);
                body.addProperty("descripcion", descArea.getText());
                ApiResponse resp;
                if (editRow != null) {
                    resp = ExerciseApiService.getInstance().update(exId, body);
                } else {
                    resp = ExerciseApiService.getInstance().create(body);
                }
                if (!resp.isOk()) JOptionPane.showMessageDialog(this, "Error: " + resp.message, "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        }
    }

    private void toggleExercise(int row, DefaultTableModel model) {
        if (row < 0) return;
        try {
            ApiResponse resp = ExerciseApiService.getInstance().getAll();
            if (resp.isOk() && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                if (row < arr.size()) {
                    JsonObject ex = arr.get(row).getAsJsonObject();
                    int id = ex.has("id_ejercicio") ? ex.get("id_ejercicio").getAsInt() : (int) model.getValueAt(row, 0);
                    boolean activo = !ex.has("activo") || ex.get("activo").getAsBoolean();
                    ApiResponse result;
                    if (activo) {
                        result = ExerciseApiService.getInstance().delete(id);
                    } else {
                        result = ExerciseApiService.getInstance().reactivate(id);
                    }
                    if (!result.isOk()) JOptionPane.showMessageDialog(this, "Error: " + result.message, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {}
    }

    // ============================================================
    // TAB 6: MI PERFIL
    // ============================================================
    private JPanel createProfileTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel title = new JLabel("Mi Perfil", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); gbc.anchor = GridBagConstraints.WEST;
        addRow(fields, "Nombre:", usuarioActual.getNombre(), 0, gbc);
        addRow(fields, "Apellido:", usuarioActual.getApellido(), 1, gbc);
        addRow(fields, "Email:", usuarioActual.getEmail(), 2, gbc);
        addRow(fields, "Especialidad:", entrenadorActual.getEspecialidad() != null ? entrenadorActual.getEspecialidad() : "N/A", 3, gbc);
        addRow(fields, "Experiencia:", entrenadorActual.getExperienciaAnios() + " anios", 4, gbc);
        String telefono = usuarioActual.getTelefono() != null && !usuarioActual.getTelefono().isEmpty() ? usuarioActual.getTelefono() : "N/A";
        addRow(fields, "Telefono:", telefono, 5, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(BG);
        btnPanel.setAlignmentX(CENTER_ALIGNMENT);

        JButton changePwdBtn = new JButton("Cambiar Contrasena");
        changePwdBtn.setBackground(RED); changePwdBtn.setForeground(Color.WHITE);
        changePwdBtn.setFont(new Font("Arial", Font.BOLD, 11));
        changePwdBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        changePwdBtn.setFocusPainted(false);
        changePwdBtn.addActionListener(e -> showChangePwd());

        JButton uploadPhotoBtn = new JButton("Subir Foto");
        uploadPhotoBtn.setBackground(BLUE); uploadPhotoBtn.setForeground(Color.WHITE);
        uploadPhotoBtn.setFont(new Font("Arial", Font.BOLD, 11));
        uploadPhotoBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        uploadPhotoBtn.setFocusPainted(false);
        uploadPhotoBtn.addActionListener(e -> uploadProfilePhoto());

        btnPanel.add(changePwdBtn);
        btnPanel.add(uploadPhotoBtn);

        panel.add(fields);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btnPanel);
        return panel;
    }

    private void uploadProfilePhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar foto de perfil");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imagenes", "jpg", "jpeg", "png", "gif", "webp"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file.length() > 5 * 1024 * 1024) {
                JOptionPane.showMessageDialog(this, "La imagen no debe exceder 5MB", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                JsonObject body = new JsonObject();
                body.addProperty("filePath", file.getAbsolutePath());
                ApiResponse resp = ApiService.getInstance().post(ApiService.getInstance().getBaseUrl() + "/api/auth/upload-profile-image", body);
                JOptionPane.showMessageDialog(this, resp.isOk() ? "Foto actualizada!" : "Error: " + resp.message);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        }
    }

    private void showChangePwd() {
        JPasswordField cp = new JPasswordField(); cp.setBackground(CARD_BG); cp.setForeground(Color.WHITE);
        JPasswordField np = new JPasswordField(); np.setBackground(CARD_BG); np.setForeground(Color.WHITE);
        JPasswordField cf = new JPasswordField(); cf.setBackground(CARD_BG); cf.setForeground(Color.WHITE);
        Object[] fields = {"Contrasena actual:", cp, "Nueva contrasena:", np, "Confirmar nueva:", cf};
        JPanel form = createForm(fields);
        if (JOptionPane.showConfirmDialog(this, form, "Cambiar Contrasena", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String cur = new String(cp.getPassword());
            String nw = new String(np.getPassword());
            String cnf = new String(cf.getPassword());
            if (cur.isEmpty()) { JOptionPane.showMessageDialog(this, "La contrasena actual no puede estar vacia", "Error", JOptionPane.ERROR_MESSAGE); return; }
            String err = ValidationUtils.validatePassword(nw);
            if (err != null) { JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE); return; }
            if (!nw.equals(cnf)) { JOptionPane.showMessageDialog(this, "Las contrasenas no coinciden", "Error", JOptionPane.ERROR_MESSAGE); return; }
            try {
                ApiResponse resp = AuthApiService.getInstance().changePassword(cur, nw);
                JOptionPane.showMessageDialog(this, resp.isOk() ? "Contrasena actualizada!" : resp.message);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        }
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private void addRow(JPanel p, String label, String val, int row, GridBagConstraints gbc) {
        JLabel l = new JLabel(label); l.setForeground(RED); l.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = row; p.add(l, gbc);
        JLabel v = new JLabel(val); v.setForeground(Color.WHITE); v.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1; p.add(v, gbc);
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

    private void styleNavBtn(JButton btn) {
        btn.setBackground(CARD_BG); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btn.setFocusPainted(false);
    }

    private JPanel createForm(Object[] rows) {
        JPanel panel = new JPanel(new GridLayout(rows.length / 2, 2, 8, 6));
        panel.setBackground(BG);
        for (int i = 0; i < rows.length; i += 2) {
            JLabel lbl = new JLabel((String) rows[i]);
            lbl.setForeground(GRAY);
            lbl.setFont(new Font("Arial", Font.PLAIN, 11));
            panel.add(lbl);
            Component comp = (Component) rows[i + 1];
            if (comp instanceof JTextField) {
                JTextField tf = (JTextField) comp;
                tf.setBackground(CARD_BG); tf.setForeground(Color.WHITE); tf.setCaretColor(RED);
                tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0x3A, 0x3A, 0x3C), 1), BorderFactory.createEmptyBorder(4, 6, 4, 6)));
                tf.setFont(new Font("Arial", Font.PLAIN, 11));
            }
            if (comp instanceof JScrollPane) {
                ((JScrollPane) comp).setBorder(BorderFactory.createLineBorder(new Color(0x3A, 0x3A, 0x3C), 1));
            }
            panel.add(comp);
        }
        return panel;
    }

    private JPanel createLabeledField(String label, Component field) {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(BG);
        JLabel lbl = new JLabel(label);
        lbl.setForeground(GRAY); lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        p.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        return p;
    }
}
