package com.ironcladbox.view;

import com.ironcladbox.controller.AuthController;
import com.ironcladbox.controller.AtletaController;
import com.ironcladbox.model.*;
import com.ironcladbox.service.*;
import com.ironcladbox.dto.ApiResponse;
import com.ironcladbox.util.ValidationUtils;
import com.google.gson.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AtletaDashboard extends JFrame {
    private Usuario usuarioActual;
    private Atleta atletaActual;
    private AuthController authController;
    private AtletaController atletaController;
    private JTabbedPane tabbedPane;

    private static final Color BG = new Color(0x11, 0x11, 0x13);
    private static final Color CARD_BG = new Color(0x1C, 0x1C, 0x1E);
    private static final Color RED = new Color(0xFF, 0x3B, 0x30);
    private static final Color GREEN = new Color(0x06, 0xD6, 0xA0);
    private static final Color ORANGE = new Color(0xFF, 0xA5, 0x00);
    private static final Color GRAY = new Color(0xB0, 0xB0, 0xB5);
    private static final Color DARK = new Color(0x0A, 0x0A, 0x0C);

    private JPanel dashboardStatsPanel;
    private JPanel upcomingWodsPanel;
    private DefaultTableModel classAvailableModel;
    private DefaultTableModel classMyModel;
    private JTabbedPane classesSubTabs;
    private DefaultTableModel wodMonthModel;
    private JTable wodMonthTable;
    private JLabel wodMonthLabel;
    private JPanel wodSchedulesPanel;
    private int currentWodYear = LocalDate.now().getYear();
    private int currentWodMonth = LocalDate.now().getMonthValue();
    private DefaultTableModel progressModel;
    private JTable progressTable;
    private DefaultTableModel exerciseModel;
    private JPanel membershipPanel;
    private JPanel rachaStatsPanel;
    private DefaultTableModel rachaHistModel;
    private DefaultTableModel myWodModel;
    private JTable myWodTable;
    private JPanel progressStatsPanel;

    public AtletaDashboard() {
        authController = AuthController.getInstance();
        atletaController = new AtletaController();
        atletaController.setOnDataChanged(() -> refreshAllTabs());
        new javax.swing.Timer(15000, e -> refreshAllTabs());
        usuarioActual = authController.getUsuarioActual();
        atletaActual = usuarioActual instanceof Atleta ? (Atleta) usuarioActual : new Atleta();
        if (!(usuarioActual instanceof Atleta)) {
            atletaActual.setIdUsuario(usuarioActual.getIdUsuario());
            atletaActual.setNombre(usuarioActual.getNombre());
            atletaActual.setApellido(usuarioActual.getApellido());
            atletaActual.setEmail(usuarioActual.getEmail());
        }
        initializeUI();
    }

    private void refreshAllTabs() {
        SwingUtilities.invokeLater(() -> {
            loadDashboardStats();
            loadClassesData();
            loadWodMonthData();
            loadMyWodData();
            loadRachaData();
            loadMembershipData();
            loadProgressData();
            loadExercisesData();
        });
    }

    private JPanel buildStatCard(String icon, String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, RED),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLbl.setForeground(Color.WHITE);
        JLabel valLbl = new JLabel(value, SwingConstants.RIGHT);
        valLbl.setFont(new Font("Arial", Font.BOLD, 26));
        valLbl.setForeground(Color.WHITE);
        JLabel lblLbl = new JLabel(label);
        lblLbl.setFont(new Font("Arial", Font.PLAIN, 10));
        lblLbl.setForeground(new Color(255, 255, 255, 160));
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(color);
        top.add(iconLbl, BorderLayout.WEST);
        top.add(valLbl, BorderLayout.EAST);
        card.add(top, BorderLayout.CENTER);
        card.add(lblLbl, BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(180, 80));
        return card;
    }

    private void initializeUI() {
        setTitle("IroncladBox - Dashboard Atleta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);

        if (ApiService.getInstance().isOffline()) {
            int pending = ApiService.getInstance().getPendingCount();
            JLabel off = new JLabel(pending > 0 ? "  SIN CONEXION - " + pending + " cambios pendientes" : "  SIN CONEXION - Datos en cache", SwingConstants.CENTER);
            off.setOpaque(true); off.setBackground(new Color(200, 120, 0)); off.setForeground(Color.WHITE);
            off.setFont(new Font("Arial", Font.BOLD, 12));
            mainPanel.add(off, BorderLayout.NORTH);
        }

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG); tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        tabbedPane.addTab("Inicio", buildHomeTab());
        tabbedPane.addTab("Mi Perfil", buildProfileTab());
        tabbedPane.addTab("Clases", buildClassesTab());
        tabbedPane.addTab("WODs", buildWodsTab());
        tabbedPane.addTab("Racha", buildRachaTab());
        tabbedPane.addTab("Membresia", buildMembershipTab());
        tabbedPane.addTab("Progreso", buildProgressTab());
        tabbedPane.addTab("Ejercicios", buildExercisesTab());

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(DARK);
        footer.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, RED));
        JLabel userLabel = new JLabel("  Atleta: " + usuarioActual.getNombreCompleto());
        userLabel.setForeground(Color.WHITE); userLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        footer.add(userLabel, BorderLayout.WEST);
        JButton logoutBtn = new JButton("Cerrar Sesion");
        logoutBtn.setBackground(RED); logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 10));
        logoutBtn.addActionListener(e -> { authController.logout(); dispose(); new LoginView(); });
        footer.add(logoutBtn, BorderLayout.EAST);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);
        add(mainPanel);
        setVisible(true);
    }

    // ==================== TAB: INICIO ====================
    private JPanel buildHomeTab() {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBackground(BG);
        wrap.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel welcome = new JLabel("Bienvenido, " + usuarioActual.getNombreCompleto());
        welcome.setFont(new Font("Arial", Font.BOLD, 22));
        welcome.setForeground(Color.WHITE);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(welcome);
        wrap.add(Box.createVerticalStrut(20));

        dashboardStatsPanel = new JPanel();
        dashboardStatsPanel.setBackground(BG);
        dashboardStatsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dashboardStatsPanel.setLayout(new GridLayout(1, 4, 10, 0));
        wrap.add(dashboardStatsPanel);
        wrap.add(Box.createVerticalStrut(25));

        JLabel upcomingTitle = new JLabel("Proximos WODs");
        upcomingTitle.setFont(new Font("Arial", Font.BOLD, 16));
        upcomingTitle.setForeground(Color.WHITE);
        upcomingTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(upcomingTitle);
        wrap.add(Box.createVerticalStrut(10));

        upcomingWodsPanel = new JPanel(new BorderLayout());
        upcomingWodsPanel.setBackground(BG);
        upcomingWodsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(upcomingWodsPanel);

        JButton gotoWods = new JButton("Ver Calendario WOD");
        gotoWods.setBackground(RED); gotoWods.setForeground(Color.WHITE);
        gotoWods.setFont(new Font("Arial", Font.BOLD, 12));
        gotoWods.setAlignmentX(Component.LEFT_ALIGNMENT);
        gotoWods.setMaximumSize(new Dimension(200, 35));
        gotoWods.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        wrap.add(Box.createVerticalStrut(12));
        wrap.add(gotoWods);

        loadDashboardStats();
        return wrap;
    }

    private void loadDashboardStats() {
        if (dashboardStatsPanel == null || upcomingWodsPanel == null) return;
        dashboardStatsPanel.removeAll();
        upcomingWodsPanel.removeAll();

        try {
            JsonObject racha = atletaController.obtenerRacha();
            int rachaActual = getJsonInt(racha, "racha_actual");
            int rachaMaxima = getJsonInt(racha, "racha_maxima");
            int totalAsistencias = getJsonInt(racha, "total_asistencias");

            List<JsonObject> schedules = atletaController.obtenerMisHorariosWOD();
            String hoy = LocalDate.now().toString();
            long inscritos = schedules.stream().filter(s -> s.has("fecha") && s.get("fecha").getAsString().compareTo(hoy) >= 0).count();

            dashboardStatsPanel.add(buildStatCard("\uD83D\uDCC5", "WODs Inscritos", String.valueOf(inscritos), new Color(0x35, 0x39, 0x4A)));
            dashboardStatsPanel.add(buildStatCard("\uD83D\uDD25", "Dias de Racha", String.valueOf(rachaActual), RED));
            dashboardStatsPanel.add(buildStatCard("\uD83C\uDFC6", "Record de Racha", String.valueOf(rachaMaxima), new Color(0x35, 0x39, 0x4A)));
            dashboardStatsPanel.add(buildStatCard("\uD83C\uDFCB", "Total Asistencias", String.valueOf(totalAsistencias), new Color(0x35, 0x39, 0x4A)));

            JPanel list = new JPanel();
            list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
            list.setBackground(BG);
            boolean has = false;
            for (JsonObject s : schedules) {
                if (!s.has("fecha")) continue;
                String fs = s.get("fecha").getAsString().substring(0, 10);
                if (fs.compareTo(hoy) < 0) continue;
                has = true;
                LocalDate d = LocalDate.parse(fs);
                String dia = d.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es"));
                dia = dia.substring(0, 1).toUpperCase() + dia.substring(1);
                String hora = s.has("hora") ? s.get("hora").getAsString().substring(0, 5) : "";
                String titulo = s.has("titulo") ? s.get("titulo").getAsString() : "WOD";
                String ent = s.has("entrenador_nombre") ? s.get("entrenador_nombre").getAsString() : "Sin asignar";
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(CARD_BG);
                row.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, GREEN), BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                JLabel info = new JLabel("<html><b>" + titulo + "</b><br><span style='color:#888;'>" + dia + " - " + hora + " | " + ent + "</span></html>");
                info.setForeground(Color.WHITE); info.setFont(new Font("Arial", Font.PLAIN, 11));
                JLabel badge = new JLabel("PROXIMO"); badge.setForeground(GREEN); badge.setFont(new Font("Arial", Font.BOLD, 10));
                row.add(info, BorderLayout.CENTER); row.add(badge, BorderLayout.EAST);
                list.add(row); list.add(Box.createVerticalStrut(6));
            }
            if (!has) {
                JLabel e = new JLabel("  No tienes WODs proximos. Ve al calendario!");
                e.setForeground(GRAY); e.setFont(new Font("Arial", Font.PLAIN, 12));
                list.add(e);
            }
            upcomingWodsPanel.add(new JScrollPane(list), BorderLayout.CENTER);
        } catch (Exception ex) {
            JLabel err = new JLabel("Error: " + ex.getMessage());
            err.setForeground(RED); upcomingWodsPanel.add(err);
        }
        dashboardStatsPanel.revalidate(); dashboardStatsPanel.repaint();
        upcomingWodsPanel.revalidate(); upcomingWodsPanel.repaint();
    }

    // ==================== TAB: PERFIL ====================
    private JPanel buildProfileTab() {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(BG);
        outer.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));

        JLabel fotoLbl = new JLabel("", SwingConstants.CENTER);
        fotoLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            String url = atletaController.getFotoPerfil();
            if (url != null && !url.isEmpty()) {
                BufferedImage img = ImageIO.read(new URL(url));
                if (img != null) fotoLbl.setIcon(new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
            }
        } catch (Exception ignored) {}
        outer.add(fotoLbl);
        outer.add(Box.createVerticalStrut(15));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20); gbc.anchor = GridBagConstraints.WEST;
        double imc = atletaActual.getPeso() > 0 && atletaActual.getAltura() > 0 ? atletaActual.getPeso() / (atletaActual.getAltura() * atletaActual.getAltura()) : 0;
        String imcStr = imc > 0 ? String.format("%.1f (%s)", imc, imc < 18.5 ? "Bajo" : imc < 25 ? "Normal" : imc < 30 ? "Sobrepeso" : "Obeso") : "N/A";
        addLabelValue(fields, "Nombre:", usuarioActual.getNombre(), 0, gbc);
        addLabelValue(fields, "Apellido:", usuarioActual.getApellido(), 1, gbc);
        addLabelValue(fields, "Email:", usuarioActual.getEmail(), 2, gbc);
        addLabelValue(fields, "Telefono:", usuarioActual.getTelefono() != null ? usuarioActual.getTelefono() : "N/A", 3, gbc);
        addLabelValue(fields, "Peso:", atletaActual.getPeso() > 0 ? atletaActual.getPeso() + " kg" : "N/A", 4, gbc);
        addLabelValue(fields, "Altura:", atletaActual.getAltura() > 0 ? atletaActual.getAltura() + " m" : "N/A", 5, gbc);
        addLabelValue(fields, "IMC:", imcStr, 6, gbc);

        JButton chPwd = new JButton("Cambiar Contrasena");
        chPwd.setBackground(RED); chPwd.setForeground(Color.WHITE);
        chPwd.setFont(new Font("Arial", Font.BOLD, 11));
        chPwd.setAlignmentX(Component.CENTER_ALIGNMENT);
        chPwd.setMaximumSize(new Dimension(200, 35));
        chPwd.addActionListener(e -> showChangePasswordDialog());

        outer.add(fields);
        outer.add(Box.createVerticalStrut(15));
        outer.add(chPwd);
        return outer;
    }

    private void showChangePasswordDialog() {
        JPasswordField cp = new JPasswordField(); cp.setBackground(CARD_BG); cp.setForeground(Color.WHITE);
        JPasswordField np = new JPasswordField(); np.setBackground(CARD_BG); np.setForeground(Color.WHITE);
        JPasswordField cf = new JPasswordField(); cf.setBackground(CARD_BG); cf.setForeground(Color.WHITE);
        JPanel dlg = new JPanel();
        dlg.setBackground(BG); dlg.setLayout(new BoxLayout(dlg, BoxLayout.Y_AXIS));
        dlg.add(new JLabel("Contrasena actual:")); dlg.add(cp); dlg.add(Box.createVerticalStrut(5));
        dlg.add(new JLabel("Nueva:")); dlg.add(np); dlg.add(Box.createVerticalStrut(5));
        dlg.add(new JLabel("Confirmar:")); dlg.add(cf);
        if (JOptionPane.showConfirmDialog(this, dlg, "Cambiar Contrasena", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String cur = new String(cp.getPassword());
            String nw = new String(np.getPassword());
            String cnf = new String(cf.getPassword());
            if (cur.isEmpty()) { JOptionPane.showMessageDialog(this, "La contrasena actual no puede estar vacia", "Error", JOptionPane.ERROR_MESSAGE); return; }
            String err = ValidationUtils.validatePassword(nw);
            if (err != null) { JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE); return; }
            if (!nw.equals(cnf)) { JOptionPane.showMessageDialog(this, "Las contrasenas no coinciden", "Error", JOptionPane.ERROR_MESSAGE); return; }
            try {
                ApiResponse r = AuthApiService.getInstance().changePassword(cur, nw);
                JOptionPane.showMessageDialog(this, r.isOk() ? "Contrasena actualizada!" : r.message);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        }
    }

    // ==================== TAB: CLASES ====================
    private JPanel buildClassesTab() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        classesSubTabs = new JTabbedPane();
        classesSubTabs.setBackground(BG); classesSubTabs.setForeground(Color.WHITE);
        classesSubTabs.setFont(new Font("Arial", Font.BOLD, 11));

        JPanel availP = new JPanel(new BorderLayout());
        availP.setBackground(BG);
        classAvailableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Fecha", "Horario", "Entrenador", "Cupo", "Estado"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable availT = styledTable(classAvailableModel);
        availP.add(new JScrollPane(availT), BorderLayout.CENTER);
        JPanel availBtns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        availBtns.setBackground(BG);
        JButton enrollBtn = new JButton("Inscribirse");
        enrollBtn.setBackground(GREEN); enrollBtn.setForeground(Color.BLACK);
        enrollBtn.setFont(new Font("Arial", Font.BOLD, 11));
        enrollBtn.addActionListener(e -> {
            int r = availT.getSelectedRow();
            if (r >= 0) {
                int id = (int) classAvailableModel.getValueAt(r, 0);
                handleEnrollClass(id);
            } else JOptionPane.showMessageDialog(this, "Selecciona una clase primero");
        });
        availBtns.add(enrollBtn);
        availP.add(availBtns, BorderLayout.SOUTH);

        JPanel myP = new JPanel(new BorderLayout());
        myP.setBackground(BG);
        classMyModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Fecha", "Horario", "Entrenador", "Estado"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable myT = styledTable(classMyModel);
        myP.add(new JScrollPane(myT), BorderLayout.CENTER);
        JPanel myBtns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        myBtns.setBackground(BG);
        JButton cancelBtn = new JButton("Cancelar Inscripcion");
        cancelBtn.setBackground(RED); cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 11));
        cancelBtn.addActionListener(e -> {
            int r = myT.getSelectedRow();
            if (r >= 0) {
                int id = (int) classMyModel.getValueAt(r, 0);
                handleUnenrollClass(id);
            } else JOptionPane.showMessageDialog(this, "Selecciona una clase primero");
        });
        myBtns.add(cancelBtn);
        myP.add(myBtns, BorderLayout.SOUTH);

        classesSubTabs.addTab("Disponibles", availP);
        classesSubTabs.addTab("Mis Clases", myP);
        outer.add(classesSubTabs, BorderLayout.CENTER);
        loadClassesData();
        return outer;
    }

    private void handleEnrollClass(int idClase) {
        if (atletaController.inscribirClase(idClase)) {
            JOptionPane.showMessageDialog(this, "Inscripcion exitosa!");
            loadClassesData();
        } else JOptionPane.showMessageDialog(this, "Error al inscribirse. Verifica tu membresia.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void handleUnenrollClass(int idClase) {
        int c = JOptionPane.showConfirmDialog(this, "Cancelar inscripcion?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            if (atletaController.cancelarInscripcionClase(idClase)) {
                JOptionPane.showMessageDialog(this, "Inscripcion cancelada!");
                loadClassesData();
            } else JOptionPane.showMessageDialog(this, "Error al cancelar", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadClassesData() {
        if (classAvailableModel == null || classMyModel == null) return;
        classAvailableModel.setRowCount(0);
        classMyModel.setRowCount(0);
        for (Clase c : atletaController.obtenerClasesDisponibles()) {
            String f = c.getFecha() != null ? c.getFecha().toString() : "N/A";
            String h = c.getHorarioInicio() != null ? c.getHorarioInicio().toString() : "";
            classAvailableModel.addRow(new Object[]{c.getIdClase(), c.getNombre(), f, h, c.getNombreEntrenador() != null ? c.getNombreEntrenador() : "N/A", c.getInscritos() + "/" + c.getCapacidadMaxima(), c.isActiva() ? "DISPONIBLE" : "CANCELADA"});
        }
        for (Clase c : atletaController.obtenerMisClases()) {
            String f = c.getFecha() != null ? c.getFecha().toString() : "N/A";
            String h = c.getHorarioInicio() != null ? c.getHorarioInicio().toString() : "";
            classMyModel.addRow(new Object[]{c.getIdClase(), c.getNombre(), f, h, c.getNombreEntrenador() != null ? c.getNombreEntrenador() : "N/A", c.isActiva() ? "INSCRITO" : "CANCELADA"});
        }
    }

    // ==================== TAB: WODs ====================
    private JPanel buildWodsTab() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTabbedPane wodSubTabs = new JTabbedPane();
        wodSubTabs.setBackground(BG); wodSubTabs.setForeground(Color.WHITE);
        wodSubTabs.setFont(new Font("Arial", Font.BOLD, 11));

        JPanel calPanel = new JPanel(new BorderLayout());
        calPanel.setBackground(BG);
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(BG);
        JButton prev = new JButton("<");
        prev.setBackground(CARD_BG); prev.setForeground(Color.WHITE); prev.setFont(new Font("Arial", Font.BOLD, 14));
        prev.addActionListener(e -> { if (currentWodMonth == 1) { currentWodMonth = 12; currentWodYear--; } else currentWodMonth--; loadWodMonthData(); });
        JButton next = new JButton(">");
        next.setBackground(CARD_BG); next.setForeground(Color.WHITE); next.setFont(new Font("Arial", Font.BOLD, 14));
        next.addActionListener(e -> { if (currentWodMonth == 12) { currentWodMonth = 1; currentWodYear++; } else currentWodMonth++; loadWodMonthData(); });
        wodMonthLabel = new JLabel();
        wodMonthLabel.setFont(new Font("Arial", Font.BOLD, 16)); wodMonthLabel.setForeground(Color.WHITE);
        header.add(prev); header.add(wodMonthLabel); header.add(next);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setBackground(BG); split.setDividerSize(4); split.setResizeWeight(0.35);
        wodMonthModel = new DefaultTableModel(new String[]{"ID", "Fecha", "WOD", "Tipo", "Nivel"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        wodMonthTable = styledTable(wodMonthModel);
        wodMonthTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel topP = new JPanel(new BorderLayout());
        topP.setBackground(BG);
        JLabel topLbl = new JLabel("  WODs del mes - Selecciona uno para ver horarios");
        topLbl.setForeground(GRAY); topLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        topP.add(topLbl, BorderLayout.NORTH);
        topP.add(new JScrollPane(wodMonthTable), BorderLayout.CENTER);
        wodSchedulesPanel = new JPanel(new BorderLayout());
        wodSchedulesPanel.setBackground(BG);
        wodSchedulesPanel.add(new JLabel("  Selecciona un WOD para ver sus horarios", SwingConstants.CENTER) {{
            setForeground(GRAY); setFont(new Font("Arial", Font.PLAIN, 12));
        }}, BorderLayout.CENTER);
        split.setTopComponent(topP);
        split.setBottomComponent(wodSchedulesPanel);
        wodMonthTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = wodMonthTable.getSelectedRow();
                if (row >= 0) { int idWod = (int) wodMonthModel.getValueAt(row, 0); loadWodSchedules(idWod); }
            }
        });
        calPanel.add(header, BorderLayout.NORTH);
        calPanel.add(split, BorderLayout.CENTER);

        JPanel myWodPanel = new JPanel(new BorderLayout());
        myWodPanel.setBackground(BG);
        JLabel myWodInfo = new JLabel("  Tus proximos WODs inscritos. Puedes marcar asistencia el dia del WOD.");
        myWodInfo.setForeground(GRAY); myWodInfo.setFont(new Font("Arial", Font.PLAIN, 11));
        myWodPanel.add(myWodInfo, BorderLayout.NORTH);
        myWodModel = new DefaultTableModel(new String[]{"ID_Insc", "Fecha", "WOD", "Horario", "Entrenador", "Estado", ""}, 0) {
            public boolean isCellEditable(int r, int c) { return c == 6; }
        };
        JTable myWodTableLocal = styledTable(myWodModel);
        myWodTableLocal.getColumn("").setMaxWidth(140);
        myWodTable = myWodTableLocal;
        myWodTableLocal.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                int row = myWodTableLocal.rowAtPoint(evt.getPoint());
                int col = myWodTableLocal.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 6) {
                    int idInsc = (int) myWodModel.getValueAt(row, 0);
                    String estado = (String) myWodModel.getValueAt(row, 5);
                    if ("HOY".equals(estado)) {
                        handleMarcarAsistencia(idInsc);
                    } else if ("INSCRITO".equals(estado)) {
                        handleCancelWodInscripcion(idInsc);
                    }
                }
            }
        });
        myWodPanel.add(new JScrollPane(myWodTableLocal), BorderLayout.CENTER);

        wodSubTabs.addTab("Calendario", calPanel);
        wodSubTabs.addTab("Mis WODs", myWodPanel);

        wodSubTabs.addChangeListener(e -> {
            if (wodSubTabs.getSelectedIndex() == 1) loadMyWodData();
        });

        outer.add(wodSubTabs, BorderLayout.CENTER);
        loadWodMonthData();
        return outer;
    }

    private void loadMyWodData() {
        if (myWodModel == null) return;
        myWodModel.setRowCount(0);
        List<JsonObject> schedules = atletaController.obtenerMisHorariosWOD();
        LocalDate hoy = LocalDate.now();
        for (JsonObject s : schedules) {
            if (!s.has("fecha")) continue;
            String fs = s.get("fecha").getAsString().substring(0, 10);
            LocalDate fd = LocalDate.parse(fs);
            if (fd.isBefore(hoy)) continue;
            int idInsc = getJsonInt(s, "id_inscripcion");
            String titulo = s.has("titulo") ? s.get("titulo").getAsString() : "";
            String hora = s.has("hora") ? s.get("hora").getAsString().substring(0, 5) : "";
            String ent = s.has("entrenador_nombre") ? s.get("entrenador_nombre").getAsString() : "Sin asignar";
            boolean asistio = s.has("asistio") && !s.get("asistio").isJsonNull() && s.get("asistio").getAsBoolean();
            String estado = asistio ? "ASISTIDO" : (fd.equals(hoy) ? "HOY" : "INSCRITO");
            myWodModel.addRow(new Object[]{idInsc, fs, titulo, hora, ent, estado, ""});
        }
    }

    private void loadWodMonthData() {
        if (wodMonthLabel == null || wodMonthModel == null) return;
        String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        wodMonthLabel.setText(meses[currentWodMonth - 1] + " " + currentWodYear);
        wodMonthModel.setRowCount(0);
        for (JsonObject w : atletaController.obtenerWODsPorMes(currentWodYear, currentWodMonth)) {
            wodMonthModel.addRow(new Object[]{
                getJsonInt(w, "id_wod"),
                w.has("fecha") ? w.get("fecha").getAsString().substring(0, 10) : "",
                w.has("titulo") ? w.get("titulo").getAsString() : "",
                w.has("tipo") ? w.get("tipo").getAsString() : "",
                w.has("nivel") ? w.get("nivel").getAsString() : ""
            });
        }
        if (wodSchedulesPanel != null) {
            wodSchedulesPanel.removeAll();
            wodSchedulesPanel.add(new JLabel("  Selecciona un WOD para ver sus horarios", SwingConstants.CENTER) {{
                setForeground(GRAY); setFont(new Font("Arial", Font.PLAIN, 12));
            }}, BorderLayout.CENTER);
            wodSchedulesPanel.revalidate(); wodSchedulesPanel.repaint();
        }
    }

    private void loadWodSchedules(int idWod) {
        if (wodSchedulesPanel == null) return;
        wodSchedulesPanel.removeAll();
        List<JsonObject> horarios = atletaController.obtenerHorariosWOD(idWod);
        List<JsonObject> misHorarios = atletaController.obtenerMisHorariosWOD();

        JPanel cards = new JPanel();
        cards.setLayout(new BoxLayout(cards, BoxLayout.Y_AXIS));
        cards.setBackground(BG);

        for (JsonObject h : horarios) {
            int idH = getJsonInt(h, "id_horario");
            String hora = h.has("hora") ? h.get("hora").getAsString().substring(0, 5) : "";
            int ins = getJsonInt(h, "inscritos");
            int cupo = getJsonInt(h, "cupo_maximo");
            String ent = h.has("entrenador_nombre") ? h.get("entrenador_nombre").getAsString() : "Sin asignar";
            String estado = h.has("estado") ? h.get("estado").getAsString() : "ACTIVO";
            boolean yaInscrito = misHorarios.stream().anyMatch(m -> getJsonInt(m, "id_horario") == idH);

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 20), 1), BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

            JPanel info = new JPanel(new GridLayout(3, 1));
            info.setBackground(CARD_BG);
            JLabel hLbl = new JLabel(hora + "  |  " + ent);
            hLbl.setForeground(Color.WHITE); hLbl.setFont(new Font("Arial", Font.BOLD, 13));
            JLabel cLbl = new JLabel(ins + "/" + cupo + " inscritos");
            cLbl.setForeground(GRAY); cLbl.setFont(new Font("Arial", Font.PLAIN, 11));
            JLabel eLbl = new JLabel(yaInscrito ? "YA INSCRITO" : "DISPONIBLE");
            eLbl.setForeground(yaInscrito ? ORANGE : GREEN); eLbl.setFont(new Font("Arial", Font.BOLD, 10));
            info.add(hLbl); info.add(cLbl); info.add(eLbl);
            card.add(info, BorderLayout.CENTER);

            JPanel acts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            acts.setBackground(CARD_BG);
            if (yaInscrito) {
                JButton unenroll = new JButton("Cancelar");
                unenroll.setBackground(RED); unenroll.setForeground(Color.WHITE);
                unenroll.setFont(new Font("Arial", Font.BOLD, 10));
                unenroll.addActionListener(ev -> handleUnenrollWod(idH, idWod));
                acts.add(unenroll);
            } else if ("ACTIVO".equalsIgnoreCase(estado)) {
                JButton enroll = new JButton("Inscribirme");
                enroll.setBackground(GREEN); enroll.setForeground(Color.BLACK);
                enroll.setFont(new Font("Arial", Font.BOLD, 10));
                enroll.addActionListener(ev -> handleEnrollWod(idH, idWod));
                acts.add(enroll);
            }
            card.add(acts, BorderLayout.EAST);
            cards.add(card);
            cards.add(Box.createVerticalStrut(6));
        }

        wodSchedulesPanel.add(new JScrollPane(cards), BorderLayout.CENTER);
        wodSchedulesPanel.revalidate(); wodSchedulesPanel.repaint();
    }

    private void handleEnrollWod(int idHorario, int idWod) {
        if (atletaController.inscribirHorarioWOD(idHorario)) {
            JOptionPane.showMessageDialog(this, "Inscripcion exitosa!");
            loadWodSchedules(idWod);
        } else JOptionPane.showMessageDialog(this, "Error al inscribirse. Solo un horario por dia.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void handleUnenrollWod(int idHorario, int idWod) {
        int c = JOptionPane.showConfirmDialog(this, "Cancelar inscripcion a este horario?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION && atletaController.cancelarInscripcionWOD(idHorario)) {
            JOptionPane.showMessageDialog(this, "Inscripcion cancelada!");
            loadWodSchedules(idWod);
            loadMyWodData();
        }
    }

    private void handleMarcarAsistencia(int idInscripcion) {
        if (atletaController.marcarAsistencia(idInscripcion)) {
            JOptionPane.showMessageDialog(this, "Asistencia marcada! Sigue con la racha!");
        } else {
            JOptionPane.showMessageDialog(this, "Error al marcar asistencia", "Error", JOptionPane.ERROR_MESSAGE);
        }
        loadMyWodData();
        loadDashboardStats();
        loadRachaData();
    }

    private void handleCancelWodInscripcion(int idInscripcion) {
        int c = JOptionPane.showConfirmDialog(this, "Cancelar esta inscripcion?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            List<JsonObject> schedules = atletaController.obtenerMisHorariosWOD();
            for (JsonObject s : schedules) {
                if (getJsonInt(s, "id_inscripcion") == idInscripcion) {
                    int idH = getJsonInt(s, "id_horario");
                    if (idH > 0 && atletaController.cancelarInscripcionWOD(idH)) {
                        JOptionPane.showMessageDialog(this, "Inscripcion cancelada!");
                    }
                    break;
                }
            }
            loadMyWodData();
            loadDashboardStats();
        }
    }

    // ==================== TAB: RACHA ====================
    private JPanel buildRachaTab() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        rachaStatsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        rachaStatsPanel.setBackground(BG);
        rachaStatsPanel.setPreferredSize(new Dimension(700, 100));
        outer.add(rachaStatsPanel, BorderLayout.NORTH);

        rachaHistModel = new DefaultTableModel(new String[]{"Fecha", "WOD", "Tipo", "Horario", "Entrenador"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable histT = styledTable(rachaHistModel);
        JPanel bot = new JPanel(new BorderLayout());
        bot.setBackground(BG);
        JLabel t = new JLabel("  Historial de Asistencias");
        t.setForeground(Color.WHITE); t.setFont(new Font("Arial", Font.BOLD, 14));
        bot.add(t, BorderLayout.NORTH);
        bot.add(new JScrollPane(histT), BorderLayout.CENTER);
        outer.add(bot, BorderLayout.CENTER);

        loadRachaData();
        return outer;
    }

    private void loadRachaData() {
        if (rachaStatsPanel == null || rachaHistModel == null) return;
        rachaStatsPanel.removeAll();
        rachaHistModel.setRowCount(0);

        JsonObject racha = atletaController.obtenerRacha();
        int ra = getJsonInt(racha, "racha_actual");
        int rm = getJsonInt(racha, "racha_maxima");
        int am = getJsonInt(racha, "asistencias_mes");
        rachaStatsPanel.add(buildStatCard("\uD83D\uDD25", "Dias Actuales", String.valueOf(ra), RED));
        rachaStatsPanel.add(buildStatCard("\uD83C\uDFC6", "Record Personal", String.valueOf(rm), new Color(0x35, 0x39, 0x4A)));
        rachaStatsPanel.add(buildStatCard("\uD83D\uDCC5", "Este Mes", String.valueOf(am), new Color(0x35, 0x39, 0x4A)));

        for (JsonObject item : atletaController.obtenerHistorialAsistencias()) {
            String f = item.has("fecha_asistencia") ? item.get("fecha_asistencia").getAsString().substring(0, 10) : "";
            String wt = item.has("wod_titulo") ? item.get("wod_titulo").getAsString() : "N/A";
            String tp = item.has("tipo_wod") ? item.get("tipo_wod").getAsString() : "--";
            String h = item.has("hora") ? item.get("hora").getAsString().substring(0, 5) : "--";
            String en = item.has("entrenador_nombre") ? item.get("entrenador_nombre").getAsString() : "N/A";
            rachaHistModel.addRow(new Object[]{f, wt, tp, h, en});
        }
        rachaStatsPanel.revalidate(); rachaStatsPanel.repaint();
    }

    // ==================== TAB: MEMBRESIA ====================
    private JPanel buildMembershipTab() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        membershipPanel = new JPanel();
        membershipPanel.setLayout(new BoxLayout(membershipPanel, BoxLayout.Y_AXIS));
        membershipPanel.setBackground(BG);
        outer.add(membershipPanel, BorderLayout.CENTER);
        loadMembershipData();
        return outer;
    }

    private void loadMembershipData() {
        if (membershipPanel == null) return;
        membershipPanel.removeAll();

        Suscripcion s = atletaController.obtenerSuscripcionActiva(atletaActual.getIdAtleta());

        JLabel title = new JLabel("MI MEMBRESIA");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        membershipPanel.add(title);
        membershipPanel.add(Box.createVerticalStrut(15));

        if (s != null && s.isActiva()) {
            String estado = s.isVigente() ? "ACTIVA" : "VENCIDA";
            Color ec = s.isVigente() ? GREEN : RED;
            addRowMem(membershipPanel, "Plan:", s.getNombreMembresia() != null ? s.getNombreMembresia() : "N/A");
            addRowMem(membershipPanel, "Precio:", s.getPrecioMembresia() > 0 ? "$" + String.format("%.2f", s.getPrecioMembresia()) : "N/A");
            addRowMem(membershipPanel, "Inicio:", s.getFechaInicio() != null ? s.getFechaInicio().toString() : "N/A");
            addRowMem(membershipPanel, "Fin:", s.getFechaFin() != null ? s.getFechaFin().toString() : "N/A");
            JPanel erow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            erow.setBackground(BG);
            JLabel el = new JLabel("Estado: ");
            el.setForeground(Color.WHITE); el.setFont(new Font("Arial", Font.BOLD, 12));
            JLabel ev = new JLabel(estado);
            ev.setForeground(ec); ev.setFont(new Font("Arial", Font.BOLD, 14));
            erow.add(el); erow.add(ev);
            membershipPanel.add(erow);
        } else {
            JLabel empty = new JLabel("No tienes membresia activa");
            empty.setForeground(GRAY); empty.setFont(new Font("Arial", Font.PLAIN, 14));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            membershipPanel.add(empty);
        }

        membershipPanel.add(Box.createVerticalStrut(20));
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btns.setBackground(BG);
        JButton change = new JButton("Cambiar Membresia");
        change.setBackground(RED); change.setForeground(Color.WHITE); change.setFont(new Font("Arial", Font.BOLD, 11));
        change.addActionListener(e -> showRenewDialog());
        btns.add(change);
        JButton cancel = new JButton("Cancelar Membresia");
        cancel.setBackground(new Color(100, 100, 100)); cancel.setForeground(Color.WHITE); cancel.setFont(new Font("Arial", Font.BOLD, 11));
        cancel.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Al cancelar tu membresia perderas el acceso a la plataforma. Continuar?", "Cancelar Membresia", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (c == JOptionPane.YES_OPTION) {
                if (atletaController.cancelarMembresia()) {
                    JOptionPane.showMessageDialog(this, "Membresia cancelada. Al cerrar sesion perderas el acceso.");
                    loadMembershipData();
                } else JOptionPane.showMessageDialog(this, "Error al cancelar membresia", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btns.add(cancel);
        membershipPanel.add(btns);
        membershipPanel.revalidate(); membershipPanel.repaint();
    }

    private void showRenewDialog() {
        List<Membresia> list = atletaController.obtenerMembresiasCambio();
        if (list.isEmpty()) { JOptionPane.showMessageDialog(this, "No hay membresias disponibles"); return; }
        JComboBox<String> combo = new JComboBox<>(list.stream().map(m -> m.getNombre() + " - $" + String.format("%.2f", m.getPrecio())).toArray(String[]::new));
        if (JOptionPane.showConfirmDialog(this, combo, "Selecciona membresia", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Membresia sel = list.get(combo.getSelectedIndex());
            if (atletaController.renovarMembresia(atletaActual.getIdAtleta(), sel.getIdMembresia())) {
                JOptionPane.showMessageDialog(this, "Membresia actualizada!");
                loadMembershipData();
            } else JOptionPane.showMessageDialog(this, "Error al actualizar membresia", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== TAB: PROGRESO ====================
    private JPanel buildProgressTab() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        progressStatsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        progressStatsPanel.setBackground(BG);
        progressStatsPanel.setPreferredSize(new Dimension(700, 100));
        outer.add(progressStatsPanel, BorderLayout.NORTH);

        progressModel = new DefaultTableModel(new String[]{"Ejercicio", "Descripcion", "Marca Maxima", "Actualizado", ""}, 0) {
            public boolean isCellEditable(int r, int c) { return c == 4; }
        };
        progressTable = styledTable(progressModel);
        progressTable.getColumn("").setMaxWidth(120);
        progressTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                int row = progressTable.rowAtPoint(evt.getPoint());
                int col = progressTable.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 4) {
                    String nombre = (String) progressModel.getValueAt(row, 0);
                    String marcaStr = (String) progressModel.getValueAt(row, 2);
                    double marcaActual = 0;
                    if (marcaStr != null && !marcaStr.equals("Sin registrar")) {
                        try { marcaActual = Double.parseDouble(marcaStr.replace(" lb", "")); } catch (NumberFormatException ignored) {}
                    }
                    int idEj = 0;
                    List<JsonObject> ejercicios = atletaController.obtenerEjerciciosConProgreso();
                    for (JsonObject ej : ejercicios) {
                        if (nombre.equals(ej.has("nombre") ? ej.get("nombre").getAsString() : "")) {
                            idEj = getJsonInt(ej, "id_ejercicio"); break;
                        }
                    }
                    showProgressMarkDialog(idEj, nombre, marcaActual);
                }
            }
        });
        outer.add(new JScrollPane(progressTable), BorderLayout.CENTER);
        loadProgressData();
        return outer;
    }

    private void showProgressMarkDialog(int idEjercicio, String nombreEjercicio, double marcaActual) {
        JPanel dlg = new JPanel();
        dlg.setBackground(BG);
        dlg.setLayout(new BoxLayout(dlg, BoxLayout.Y_AXIS));
        dlg.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel icon = new JLabel("\uD83C\uDFCB", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        dlg.add(icon);
        dlg.add(Box.createVerticalStrut(10));

        String title = marcaActual > 0 ? "Actualizar Marca" : "Registrar Primera Marca";
        JLabel tLbl = new JLabel(title);
        tLbl.setFont(new Font("Arial", Font.BOLD, 16));
        tLbl.setForeground(Color.WHITE);
        tLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        dlg.add(tLbl);
        dlg.add(Box.createVerticalStrut(5));

        JLabel nLbl = new JLabel(nombreEjercicio);
        nLbl.setFont(new Font("Arial", Font.BOLD, 13));
        nLbl.setForeground(RED);
        nLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        dlg.add(nLbl);

        if (marcaActual > 0) {
            JLabel cur = new JLabel("Marca actual: " + String.format("%.1f lb", marcaActual));
            cur.setForeground(GRAY); cur.setFont(new Font("Arial", Font.PLAIN, 11));
            cur.setAlignmentX(Component.CENTER_ALIGNMENT);
            dlg.add(cur);
        }
        dlg.add(Box.createVerticalStrut(15));

        JLabel inpLbl = new JLabel("Marca Maxima (lb):");
        inpLbl.setForeground(Color.WHITE); inpLbl.setFont(new Font("Arial", Font.BOLD, 12));
        inpLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        dlg.add(inpLbl);

        JTextField input = new JTextField(marcaActual > 0 ? String.format("%.1f", marcaActual) : "", 10);
        input.setBackground(CARD_BG); input.setForeground(Color.WHITE);
        input.setCaretColor(RED); input.setFont(new Font("Arial", Font.PLAIN, 14));
        input.setMaximumSize(new Dimension(200, 30));
        input.setAlignmentX(Component.CENTER_ALIGNMENT);
        dlg.add(input);

        int result = JOptionPane.showConfirmDialog(this, dlg, "Registrar Marca", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double marca = Double.parseDouble(input.getText().trim());
                if (marca <= 0) { JOptionPane.showMessageDialog(this, "Ingresa una marca valida mayor a 0", "Error", JOptionPane.ERROR_MESSAGE); return; }
                JsonObject resp = atletaController.actualizarMarca(idEjercicio, marca);
                String msg = "Marca registrada exitosamente!";
                String tipo = resp.has("tipo") ? resp.get("tipo").getAsString() : "";
                String motivacion = resp.has("motivacion") ? resp.get("motivacion").getAsString() : "";
                if ("mejora".equals(tipo)) msg = "Nuevo record personal! " + (motivacion.isEmpty() ? "Sigue mejorando!" : motivacion);
                else if ("descenso".equals(tipo)) msg = (motivacion.isEmpty() ? "No te desanimes, sigue entrenando!" : motivacion);
                else if ("primera_vez".equals(tipo)) msg = "Primera marca registrada! " + (motivacion.isEmpty() ? "A superarse!" : motivacion);
                JOptionPane.showMessageDialog(this, msg);
                loadProgressData();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Ingresa un numero valido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadProgressData() {
        if (progressModel == null || progressStatsPanel == null) return;
        progressModel.setRowCount(0);
        progressStatsPanel.removeAll();
        try {
            JsonObject stats = atletaController.obtenerEstadisticasProgreso();
            int total = getJsonInt(stats, "total_ejercicios");
            double promVal = stats.has("promedio_marcas") && !stats.get("promedio_marcas").isJsonNull() ? stats.get("promedio_marcas").getAsDouble() : 0;
            double maxVal = stats.has("marca_mas_alta") && !stats.get("marca_mas_alta").isJsonNull() ? stats.get("marca_mas_alta").getAsDouble() : 0;
            progressStatsPanel.add(buildStatCard("\uD83C\uDFCB", "Ejercicios", String.valueOf(total), new Color(0x35, 0x39, 0x4A)));
            progressStatsPanel.add(buildStatCard("\uD83D\uDCC8", "Promedio", String.format("%.1f lb", promVal), new Color(0x35, 0x39, 0x4A)));
            progressStatsPanel.add(buildStatCard("\uD83C\uDFC6", "Marca Mas Alta", String.format("%.1f lb", maxVal), RED));
            progressStatsPanel.revalidate(); progressStatsPanel.repaint();

            for (JsonObject ej : atletaController.obtenerEjerciciosConProgreso()) {
                String nombre = ej.has("nombre") ? ej.get("nombre").getAsString() : "";
                String desc = ej.has("descripcion") ? ej.get("descripcion").getAsString() : "";
                double marcaActual = ej.has("marca_maxima") && !ej.get("marca_maxima").isJsonNull() ? ej.get("marca_maxima").getAsDouble() : 0;
                String marca = marcaActual > 0 ? String.format("%.1f lb", marcaActual) : "Sin registrar";
                String fecha = ej.has("fecha_actualizacion") && !ej.get("fecha_actualizacion").isJsonNull()
                    ? ej.get("fecha_actualizacion").getAsString().substring(0, 10) : "-";
                String btnLabel = marcaActual > 0 ? "Actualizar" : "Registrar";
                progressModel.addRow(new Object[]{nombre, desc, marca, fecha, btnLabel});
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // ==================== TAB: EJERCICIOS ====================
    private JPanel buildExercisesTab() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        exerciseModel = new DefaultTableModel(new String[]{"Imagen", "Nombre", "Descripcion"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
            public Class<?> getColumnClass(int c) { return c == 0 ? ImageIcon.class : String.class; }
        };
        JTable exTable = new JTable(exerciseModel);
        exTable.setBackground(CARD_BG); exTable.setForeground(Color.WHITE);
        exTable.setGridColor(new Color(0x3A, 0x3A, 0x3C));
        exTable.setSelectionBackground(RED);
        exTable.setRowHeight(60);
        exTable.getTableHeader().setBackground(DARK); exTable.getTableHeader().setForeground(RED);
        exTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        exTable.setFont(new Font("Arial", Font.PLAIN, 11));
        outer.add(new JScrollPane(exTable), BorderLayout.CENTER);

        loadExercisesData();
        return outer;
    }

    private void loadExercisesData() {
        if (exerciseModel == null) return;
        exerciseModel.setRowCount(0);
        for (JsonObject ex : atletaController.obtenerEjercicios()) {
            String nombre = ex.has("nombre") ? ex.get("nombre").getAsString() : "";
            String desc = ex.has("descripcion") ? ex.get("descripcion").getAsString() : "";
            ImageIcon icon = null;
            if (ex.has("imagen_url") && !ex.get("imagen_url").isJsonNull()) {
                try {
                    BufferedImage img = ImageIO.read(new URL(ex.get("imagen_url").getAsString()));
                    if (img != null) icon = new ImageIcon(img.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                } catch (Exception ignored) {}
            }
            exerciseModel.addRow(new Object[]{icon, nombre, desc});
        }
    }

    // ==================== HELPERS ====================
    private int getJsonInt(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsInt() : 0;
    }

    private void addLabelValue(JPanel p, String label, String val, int row, GridBagConstraints gbc) {
        JLabel l = new JLabel(label); l.setForeground(RED); l.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = row; p.add(l, gbc);
        JLabel v = new JLabel(val); v.setForeground(Color.WHITE); v.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1; p.add(v, gbc);
    }

    private void addRowMem(JPanel p, String label, String val) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setBackground(BG);
        JLabel l = new JLabel(label + " ");
        l.setForeground(RED); l.setFont(new Font("Arial", Font.BOLD, 12));
        JLabel v = new JLabel(val);
        v.setForeground(Color.WHITE); v.setFont(new Font("Arial", Font.PLAIN, 12));
        row.add(l); row.add(v);
        p.add(row);
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(CARD_BG); table.setForeground(Color.WHITE);
        table.setGridColor(new Color(0x3A, 0x3A, 0x3C)); table.setSelectionBackground(RED);
        table.setRowHeight(26);
        table.getTableHeader().setBackground(DARK); table.getTableHeader().setForeground(RED);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        return table;
    }
}
