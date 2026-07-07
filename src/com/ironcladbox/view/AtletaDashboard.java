package com.ironcladbox.view;

import com.ironcladbox.controller.AuthController;
import com.ironcladbox.controller.AtletaController;
import com.ironcladbox.model.*;
import com.ironcladbox.service.*;
import com.ironcladbox.dto.ApiResponse;
import com.google.gson.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AtletaDashboard extends JFrame {
    private Usuario usuarioActual;
    private Atleta atletaActual;
    private AuthController authController;
    private AtletaController atletaController;
    private JTabbedPane tabbedPane;
    private DefaultTableModel classModel, wodModel, membershipModel, progressModel;

    private static final Color BG = new Color(0x11, 0x11, 0x13);
    private static final Color CARD_BG = new Color(0x1C, 0x1C, 0x1E);
    private static final Color RED = new Color(0xFF, 0x3B, 0x30);
    private static final Color GRAY = new Color(0xB0, 0xB0, 0xB5);
    private static final Color DARK = new Color(0x0A, 0x0A, 0x0C);

    public AtletaDashboard() {
        authController = AuthController.getInstance();
        atletaController = new AtletaController();
        atletaController.setOnDataChanged(() -> refreshAllTabs());
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
        // Las tablas del atleta se recargan al cambiar de pestaña o al recibir eventos del socket
    }

    private void initializeUI() {
        setTitle("IroncladBox - Dashboard Atleta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setMinimumSize(new Dimension(650, 450));
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

        JTabbedPane tp = new JTabbedPane();
        tp.setBackground(BG); tp.setForeground(Color.WHITE);
        tp.setFont(new Font("Arial", Font.BOLD, 12));
        tp.addTab("Inicio", createHomeTab());
        tp.addTab("Mi Perfil", createProfileTab());
        tp.addTab("Clases", createClassesTab());
        tp.addTab("WODs", createWodsTab());
        tp.addTab("Membresia", createMembershipTab());
        tp.addTab("Progreso", createProgressTab());
        tp.addTab("Ejercicios", createExercisesTab());
        this.tabbedPane = tp;

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

    private JPanel createHomeTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);
        JLabel welcome = new JLabel("Bienvenido, " + usuarioActual.getNombreCompleto(), SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 22));
        welcome.setForeground(Color.WHITE);
        panel.add(welcome);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(BG);
        JButton viewClassesBtn = new JButton("Ver Clases Disponibles");
        viewClassesBtn.setBackground(RED); viewClassesBtn.setForeground(Color.WHITE);
        viewClassesBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        bottom.add(viewClassesBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(panel, BorderLayout.CENTER);
        wrapper.add(bottom, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel createWodsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        String[] cols = {"ID", "Titulo", "Fecha", "Tipo", "Nivel"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        try {
            ApiResponse resp = WodApiService.getInstance().getByMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                for (JsonElement e : resp.data.getAsJsonArray()) {
                    JsonObject w = e.getAsJsonObject();
                    model.addRow(new Object[]{w.has("id_wod")?w.get("id_wod").getAsInt():0, w.has("titulo")?w.get("titulo").getAsString():"", w.has("fecha")?w.get("fecha").getAsString():"", w.has("tipo")?w.get("tipo").getAsString():"", w.has("nivel")?w.get("nivel").getAsString():""});
                }
            }
        } catch (Exception ex) {}
        JTable table = styledTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createProgressTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        String[] cols = {"Ejercicio", "Marca Maxima"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        try {
            ProgressApiService ps = ProgressApiService.getInstance();
            ApiResponse resp = ps.getEjerciciosConProgreso();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                for (JsonElement e : resp.data.getAsJsonArray()) {
                    JsonObject p = e.getAsJsonObject();
                    model.addRow(new Object[]{p.has("nombre")?p.get("nombre").getAsString():"", p.has("marca_maxima")?p.get("marca_maxima").getAsDouble()+" lb":""});
                }
            }
        } catch (Exception ex) {}
        JTable table = styledTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(BG);
        JButton refreshBtn = new JButton("Actualizar Progreso");
        refreshBtn.setBackground(RED); refreshBtn.setForeground(Color.WHITE);
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createProfileTab() {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(BG);
        outer.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); gbc.anchor = GridBagConstraints.WEST;

        double imc = atletaActual.getPeso() > 0 && atletaActual.getAltura() > 0
            ? atletaActual.getPeso() / (atletaActual.getAltura() * atletaActual.getAltura()) : 0;
        String imcStr = imc > 0 ? String.format("%.1f (%s)", imc, imc < 18.5 ? "Bajo" : imc < 25 ? "Normal" : imc < 30 ? "Sobrepeso" : "Obeso") : "N/A";

        addPRow(panel, "Nombre:", usuarioActual.getNombre(), 0, gbc);
        addPRow(panel, "Apellido:", usuarioActual.getApellido(), 1, gbc);
        addPRow(panel, "Email:", usuarioActual.getEmail(), 2, gbc);
        addPRow(panel, "Telefono:", usuarioActual.getTelefono() != null ? usuarioActual.getTelefono() : "N/A", 3, gbc);
        addPRow(panel, "Peso:", atletaActual.getPeso() > 0 ? atletaActual.getPeso() + " kg" : "N/A", 4, gbc);
        addPRow(panel, "Altura:", atletaActual.getAltura() > 0 ? atletaActual.getAltura() + " m" : "N/A", 5, gbc);
        addPRow(panel, "IMC:", imcStr, 6, gbc);

        JButton changePwdBtn = new JButton("Cambiar Contrasena");
        changePwdBtn.setBackground(RED); changePwdBtn.setForeground(Color.WHITE);
        changePwdBtn.setFont(new Font("Arial", Font.BOLD, 11));
        changePwdBtn.setAlignmentX(CENTER_ALIGNMENT);
        changePwdBtn.setMaximumSize(new Dimension(200, 35));
        changePwdBtn.addActionListener(e -> {
            JPasswordField cp = new JPasswordField(); cp.setBackground(CARD_BG); cp.setForeground(Color.WHITE);
            JPasswordField np = new JPasswordField(); np.setBackground(CARD_BG); np.setForeground(Color.WHITE);
            if (JOptionPane.showConfirmDialog(this, new JPanel() {{ setBackground(BG); add(new JLabel("Contrasena actual:")); add(cp); add(new JLabel("Nueva:")); add(np); }}, "Cambiar Contrasena", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    ApiResponse resp = AuthApiService.getInstance().changePassword(new String(cp.getPassword()), new String(np.getPassword()));
                    JOptionPane.showMessageDialog(this, resp.isOk() ? "Contrasena actualizada!" : resp.message);
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
            }
        });

        outer.add(panel);
        outer.add(Box.createVerticalStrut(15));
        outer.add(changePwdBtn);
        return outer;
    }

    private JPanel createClassesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        String[] cols = {"ID", "Nombre", "Horario", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        for (Clase c : atletaController.obtenerClasesDisponibles()) {
            model.addRow(new Object[]{c.getIdClase(), c.getNombre(), c.getHorarioInicio() != null ? c.getHorarioInicio().toString() : "", c.isActiva() ? "DISPONIBLE" : "CANCELADA"});
        }
        JTable table = styledTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMembershipTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); gbc.fill = GridBagConstraints.HORIZONTAL;

        Suscripcion s = atletaController.obtenerSuscripcionActiva(atletaActual.getIdAtleta());
        int row = 0;
        if (s != null && s.isActiva()) {
            JLabel title = new JLabel("MI MEMBRESIA", SwingConstants.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 16)); title.setForeground(Color.WHITE);
            gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
            panel.add(title, gbc);
            gbc.gridwidth = 1;

            addPRow(panel, "Plan:", s.getNombreMembresia() != null ? s.getNombreMembresia() : "Membresia", row++, gbc);
            addPRow(panel, "Precio:", s.getPrecioMembresia() > 0 ? "$" + s.getPrecioMembresia() : "N/A", row++, gbc);
            addPRow(panel, "Inicio:", s.getFechaInicio() != null ? s.getFechaInicio().toString() : "N/A", row++, gbc);
            addPRow(panel, "Fin:", s.getFechaFin() != null ? s.getFechaFin().toString() : "N/A", row++, gbc);
            addPRow(panel, "Estado:", s.isVigente() ? "VIGENTE" : "VENCIDA", row++, gbc);
        } else {
            JLabel empty = new JLabel("No tienes membresia activa", SwingConstants.CENTER);
            empty.setForeground(GRAY); empty.setFont(new Font("Arial", Font.PLAIN, 13));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            panel.add(empty, gbc);
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(BG);
        JButton renewBtn = new JButton("Renovar Membresia");
        renewBtn.setBackground(RED); renewBtn.setForeground(Color.WHITE);
        renewBtn.setFont(new Font("Arial", Font.BOLD, 11));
        renewBtn.addActionListener(e -> showRenewDialog());
        btnPanel.add(renewBtn);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        return panel;
    }

    private void showRenewDialog() {
        List<Membresia> list = atletaController.obtenerMembresiasCambio();
        if (list.isEmpty()) { JOptionPane.showMessageDialog(this, "No hay membresias disponibles"); return; }
        JComboBox<String> combo = new JComboBox<>(list.stream().map(m -> m.getNombre() + " - $" + m.getPrecio()).toArray(String[]::new));
        if (JOptionPane.showConfirmDialog(this, combo, "Selecciona membresia", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Membresia selected = list.get(combo.getSelectedIndex());
            atletaController.renovarMembresia(atletaActual.getIdAtleta(), selected.getIdMembresia());
            JOptionPane.showMessageDialog(this, "Membresia renovada! Debes esperar activacion del admin.");
        }
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
        } catch (Exception ignored) {}
        JTable table = styledTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void addPRow(JPanel p, String label, String val, int row, GridBagConstraints gbc) {
        JLabel l = new JLabel(label); l.setForeground(RED); l.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = row; p.add(l, gbc);
        JLabel v = new JLabel(val); v.setForeground(Color.WHITE); v.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1; p.add(v, gbc);
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
