package com.ironcladbox.view;

import com.ironcladbox.controller.AuthController;
import com.ironcladbox.controller.AdminController;
import com.ironcladbox.util.UIStyles;
import com.ironcladbox.model.Usuario;
import com.ironcladbox.model.Atleta;
import com.ironcladbox.model.Entrenador;
import com.ironcladbox.model.Clase;

import javax.swing.*;
import javax.swing.AbstractCellEditor;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.util.List;

public class AdminDashboard extends JFrame {
    private interface RowAction {
        void execute(int row);
    }

    private static class ActionColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
        private final JTable table;
        private final RowAction editAction;
        private final RowAction deleteAction;
        private final JPanel renderPanel;
        private final JPanel editorPanel;
        private int currentRow;

        ActionColumn(JTable table, RowAction editAction, RowAction deleteAction) {
            this.table = table;
            this.editAction = editAction;
            this.deleteAction = deleteAction;
            this.renderPanel = createPanel(false);
            this.editorPanel = createPanel(true);

            renderPanel.add(createButton("✏️", UIStyles.ACCENT_RED, false));
            renderPanel.add(createButton("🗑️", UIStyles.DANGER_RED, false));

            JButton editButton = createButton("✏️", UIStyles.ACCENT_RED, true);
            JButton deleteButton = createButton("🗑️", UIStyles.DANGER_RED, true);

            editButton.addActionListener(e -> {
                stopCellEditing();
                editAction.execute(table.convertRowIndexToModel(currentRow));
            });

            deleteButton.addActionListener(e -> {
                stopCellEditing();
                deleteAction.execute(table.convertRowIndexToModel(currentRow));
            });
            editorPanel.add(editButton);
            editorPanel.add(deleteButton);
        }

        private JPanel createPanel(boolean editing) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);
            panel.setBackground(editing ? UIStyles.SECONDARY_DARK : UIStyles.PRIMARY_DARK);
            return panel;
        }

        private JButton createButton(String text, Color background, boolean enabled) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setBackground(background);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            button.setFocusPainted(false);
            button.setEnabled(enabled);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return button;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            renderPanel.setBackground(isSelected ? UIStyles.SECONDARY_DARK : UIStyles.PRIMARY_DARK);
            return renderPanel;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            editorPanel.setBackground(UIStyles.SECONDARY_DARK);
            return editorPanel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    private Usuario usuarioActual;
    private AuthController authController;
    private AdminController adminController;

    public AdminDashboard() {
        authController = AuthController.getInstance();
        adminController = new AdminController();
        usuarioActual = authController.getUsuarioActual();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("IroncladBox CrossFit - Panel de Administración");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIStyles.PRIMARY_DARK);

        // ================= HEADER =================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIStyles.SECONDARY_DARK);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, UIStyles.ACCENT_RED));
        
        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftHeaderPanel.setBackground(UIStyles.SECONDARY_DARK);
        
        JPanel logoPanel = UIStyles.createLogoPanel(60, 60);
        logoPanel.setBackground(UIStyles.SECONDARY_DARK);
        leftHeaderPanel.add(logoPanel);
        
        JLabel titleLabel = new JLabel("ADMINISTRADOR - " + usuarioActual.getNombreCompleto());
        titleLabel.setFont(UIStyles.FONT_TITLE);
        titleLabel.setForeground(UIStyles.ACCENT_RED);
        leftHeaderPanel.add(titleLabel);
        
        topPanel.add(leftHeaderPanel, BorderLayout.WEST);
        
        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightHeaderPanel.setBackground(UIStyles.SECONDARY_DARK);
        
        JButton logoutButton = new JButton("🚪 CERRAR SESIÓN");
        logoutButton.setBackground(UIStyles.DANGER_RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Montserrat", Font.BOLD, 13));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(UIStyles.ACCENT_RED_LIGHT);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(UIStyles.DANGER_RED);
            }
        });

        logoutButton.addActionListener(e -> logout());
        rightHeaderPanel.add(logoutButton);
        
        topPanel.add(rightHeaderPanel, BorderLayout.EAST);

        // ================= STATS =================
        JPanel statsPanel = createStatsPanel();

        // 🔥 SOLUCIÓN: contenedor vertical
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(UIStyles.SECONDARY_DARK);

        topContainer.add(topPanel);
        topContainer.add(statsPanel);

        mainPanel.add(topContainer, BorderLayout.NORTH);

        // ================= TABS =================
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(UIStyles.PRIMARY_DARK);
        tabbedPane.setForeground(UIStyles.ACCENT_RED);

        tabbedPane.addTab("👥 Atletas", createAtletasTab());
        tabbedPane.addTab("🏋️ Entrenadores", createEntrenoresTab());
        tabbedPane.addTab("📚 Clases", createClassesTab());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(UIStyles.SECONDARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(UIStyles.createStatPanel(
            "Atletas registrados",
            String.valueOf(adminController.getTotalAtletas()),
            UIStyles.ACCENT_RED
        ));

        panel.add(UIStyles.createStatPanel(
            "Entrenadores activos",
            String.valueOf(adminController.getTotalEntrenadores()),
            UIStyles.SUCCESS_GREEN
        ));

        panel.add(UIStyles.createStatPanel(
            "Clases disponibles",
            String.valueOf(adminController.getTotalClases()),
            UIStyles.ACCENT_RED
        ));

        return panel;
    }

    private JPanel createAtletasTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.PRIMARY_DARK);

        String[] columnas = {"ID", "Nombre", "Email", "Teléfono", "Peso (kg)", "Altura (m)", "Acciones"};
        Object[][] datos = new Object[adminController.obtenerTodosAtletas().size()][7];

        int i = 0;
        for (Atleta a : adminController.obtenerTodosAtletas()) {
            datos[i][0] = a.getIdAtleta();
            datos[i][1] = a.getNombreCompleto();
            datos[i][2] = a.getEmail();
            datos[i][3] = a.getTelefono();
            datos[i][4] = a.getPeso();
            datos[i][5] = a.getAltura();
            datos[i][6] = "ACCIONES";
            i++;
        }

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            public boolean isCellEditable(int r, int c) { return c == 6; }
        };

        JTable table = new JTable(modelo);
        UIStyles.styleTable(table);
        table.setRowHeight(35);
        ActionColumn actionColumn = new ActionColumn(table, this::editarAtleta, this::eliminarAtleta);
        table.getColumn("Acciones").setCellRenderer(actionColumn);
        table.getColumn("Acciones").setCellEditor(actionColumn);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UIStyles.PRIMARY_DARK);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void editarAtleta(int row) {
        List<Atleta> atletas = adminController.obtenerTodosAtletas();
        if (row >= atletas.size()) return;
        
        Atleta atleta = atletas.get(row);
        
        JDialog dialog = new JDialog(this, "Editar Atleta: " + atleta.getNombreCompleto(), true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UIStyles.PRIMARY_DARK);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIStyles.PRIMARY_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Campos de edición
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setForeground(UIStyles.ACCENT_RED);
        JTextField txtNombre = new JTextField(atleta.getNombre(), 20);
        UIStyles.styleTextField(txtNombre);
        
        JLabel lblApellido = new JLabel("Apellido:");
        lblApellido.setForeground(UIStyles.ACCENT_RED);
        JTextField txtApellido = new JTextField(atleta.getApellido(), 20);
        UIStyles.styleTextField(txtApellido);
        
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setForeground(UIStyles.ACCENT_RED);
        JTextField txtEmail = new JTextField(atleta.getEmail(), 20);
        UIStyles.styleTextField(txtEmail);
        
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setForeground(UIStyles.ACCENT_RED);
        JTextField txtTelefono = new JTextField(atleta.getTelefono(), 20);
        UIStyles.styleTextField(txtTelefono);
        
        JLabel lblPeso = new JLabel("Peso (kg):");
        lblPeso.setForeground(UIStyles.ACCENT_RED);
        JTextField txtPeso = new JTextField(String.valueOf(atleta.getPeso()), 20);
        UIStyles.styleTextField(txtPeso);
        
        JLabel lblAltura = new JLabel("Altura (m):");
        lblAltura.setForeground(UIStyles.ACCENT_RED);
        JTextField txtAltura = new JTextField(String.valueOf(atleta.getAltura()), 20);
        UIStyles.styleTextField(txtAltura);
        
        // Agregar campos al formulario
        int y = 0;
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblNombre, gbc);
        gbc.gridx = 1; formPanel.add(txtNombre, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblApellido, gbc);
        gbc.gridx = 1; formPanel.add(txtApellido, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblEmail, gbc);
        gbc.gridx = 1; formPanel.add(txtEmail, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblTelefono, gbc);
        gbc.gridx = 1; formPanel.add(txtTelefono, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblPeso, gbc);
        gbc.gridx = 1; formPanel.add(txtPeso, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblAltura, gbc);
        gbc.gridx = 1; formPanel.add(txtAltura, gbc);
        
        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.getViewport().setBackground(UIStyles.PRIMARY_DARK);
        dialog.add(scrollForm, BorderLayout.CENTER);
        
        // Botones de acción
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UIStyles.SECONDARY_DARK);
        
        JButton btnGuardar = new JButton("✓ Guardar");
        UIStyles.styleSuccessButton(btnGuardar);
        btnGuardar.addActionListener(e -> {
            try {
                atleta.setNombre(txtNombre.getText());
                atleta.setApellido(txtApellido.getText());
                atleta.setEmail(txtEmail.getText());
                atleta.setTelefono(txtTelefono.getText());
                atleta.setPeso(Double.parseDouble(txtPeso.getText()));
                atleta.setAltura(Double.parseDouble(txtAltura.getText()));
                
                adminController.actualizarAtleta(atleta);
                JOptionPane.showMessageDialog(dialog, "Atleta actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                
                // Recargar tabla
                dispose();
                new AdminDashboard();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: Verifica los valores numéricos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnCancelar = new JButton("✗ Cancelar");
        UIStyles.styleDangerButton(btnCancelar);
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private void eliminarAtleta(int row) {
        List<Atleta> atletas = adminController.obtenerTodosAtletas();
        if (row >= atletas.size()) return;
        
        Atleta atleta = atletas.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Deseas eliminar al atleta " + atleta.getNombreCompleto() + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            adminController.eliminarAtleta(atleta.getIdAtleta());
            JOptionPane.showMessageDialog(this, "Atleta eliminado exitosamente");
            dispose();
            new AdminDashboard();
        }
    }

    private JPanel createEntrenoresTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.PRIMARY_DARK);

        String[] columnas = {"ID", "Nombre", "Email", "Certificación", "Especialidad", "Experiencia", "Acciones"};
        Object[][] datos = new Object[adminController.obtenerTodosEntrenadores().size()][7];

        int i = 0;
        for (Entrenador e : adminController.obtenerTodosEntrenadores()) {
            datos[i][0] = e.getIdEntrenador();
            datos[i][1] = e.getNombreCompleto();
            datos[i][2] = e.getEmail();
            datos[i][3] = e.getCertificacion();
            datos[i][4] = e.getEspecialidad();
            datos[i][5] = e.getExperienciaAnios() + " años";
            datos[i][6] = "ACCIONES";
            i++;
        }

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            public boolean isCellEditable(int r, int c) { return c == 6; }
        };

        JTable table = new JTable(modelo);
        UIStyles.styleTable(table);
        table.setRowHeight(35);
        ActionColumn actionColumn = new ActionColumn(table, this::editarEntrenador, this::eliminarEntrenador);
        table.getColumn("Acciones").setCellRenderer(actionColumn);
        table.getColumn("Acciones").setCellEditor(actionColumn);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UIStyles.PRIMARY_DARK);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void editarEntrenador(int row) {
        List<Entrenador> entrenadores = adminController.obtenerTodosEntrenadores();
        if (row >= entrenadores.size()) return;
        
        Entrenador entrenador = entrenadores.get(row);
        
        JDialog dialog = new JDialog(this, "Editar Entrenador: " + entrenador.getNombreCompleto(), true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UIStyles.PRIMARY_DARK);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIStyles.PRIMARY_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Campos de edición
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setForeground(UIStyles.ACCENT_RED);
        JTextField txtNombre = new JTextField(entrenador.getNombre(), 20);
        UIStyles.styleTextField(txtNombre);
        
        JLabel lblApellido = new JLabel("Apellido:");
        lblApellido.setForeground(UIStyles.ACCENT_RED);
        JTextField txtApellido = new JTextField(entrenador.getApellido(), 20);
        UIStyles.styleTextField(txtApellido);
        
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setForeground(UIStyles.ACCENT_RED);
        JTextField txtEmail = new JTextField(entrenador.getEmail(), 20);
        UIStyles.styleTextField(txtEmail);
        
        JLabel lblCertificacion = new JLabel("Certificación:");
        lblCertificacion.setForeground(UIStyles.ACCENT_RED);
        JTextField txtCertificacion = new JTextField(entrenador.getCertificacion(), 20);
        UIStyles.styleTextField(txtCertificacion);
        
        JLabel lblEspecialidad = new JLabel("Especialidad:");
        lblEspecialidad.setForeground(UIStyles.ACCENT_RED);
        JTextField txtEspecialidad = new JTextField(entrenador.getEspecialidad(), 20);
        UIStyles.styleTextField(txtEspecialidad);
        
        JLabel lblExperiencia = new JLabel("Experiencia (años):");
        lblExperiencia.setForeground(UIStyles.ACCENT_RED);
        JTextField txtExperiencia = new JTextField(String.valueOf(entrenador.getExperienciaAnios()), 20);
        UIStyles.styleTextField(txtExperiencia);
        
        // Agregar campos al formulario
        int y = 0;
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblNombre, gbc);
        gbc.gridx = 1; formPanel.add(txtNombre, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblApellido, gbc);
        gbc.gridx = 1; formPanel.add(txtApellido, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblEmail, gbc);
        gbc.gridx = 1; formPanel.add(txtEmail, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblCertificacion, gbc);
        gbc.gridx = 1; formPanel.add(txtCertificacion, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblEspecialidad, gbc);
        gbc.gridx = 1; formPanel.add(txtEspecialidad, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblExperiencia, gbc);
        gbc.gridx = 1; formPanel.add(txtExperiencia, gbc);
        
        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.getViewport().setBackground(UIStyles.PRIMARY_DARK);
        dialog.add(scrollForm, BorderLayout.CENTER);
        
        // Botones de acción
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UIStyles.SECONDARY_DARK);
        
        JButton btnGuardar = new JButton("✓ Guardar");
        UIStyles.styleSuccessButton(btnGuardar);
        btnGuardar.addActionListener(e -> {
            try {
                entrenador.setNombre(txtNombre.getText());
                entrenador.setApellido(txtApellido.getText());
                entrenador.setEmail(txtEmail.getText());
                entrenador.setCertificacion(txtCertificacion.getText());
                entrenador.setEspecialidad(txtEspecialidad.getText());
                entrenador.setExperienciaAnios(Integer.parseInt(txtExperiencia.getText()));
                
                adminController.actualizarEntrenador(entrenador);
                JOptionPane.showMessageDialog(dialog, "Entrenador actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                
                // Recargar tabla
                dispose();
                new AdminDashboard();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: Verifica los valores numéricos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnCancelar = new JButton("✗ Cancelar");
        UIStyles.styleDangerButton(btnCancelar);
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private void eliminarEntrenador(int row) {
        List<Entrenador> entrenadores = adminController.obtenerTodosEntrenadores();
        if (row >= entrenadores.size()) return;
        
        Entrenador entrenador = entrenadores.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Deseas eliminar al entrenador " + entrenador.getNombreCompleto() + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            adminController.eliminarEntrenador(entrenador.getIdEntrenador());
            JOptionPane.showMessageDialog(this, "Entrenador eliminado exitosamente");
            dispose();
            new AdminDashboard();
        }
    }

    private JPanel createClassesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.PRIMARY_DARK);

        String[] columnas = {"ID", "Nombre", "Entrenador", "Día", "Horario", "Capacidad", "Acciones"};
        Object[][] datos = new Object[adminController.obtenerClases().size()][7];

        int i = 0;
        for (Clase c : adminController.obtenerClases()) {
            datos[i][0] = c.getIdClase();
            datos[i][1] = c.getNombre();
            datos[i][2] = c.getNombreEntrenador();
            datos[i][3] = c.getDiaSemana();
            datos[i][4] = c.getHorarioInicio() + " - " + c.getHorarioFin();
            datos[i][5] = c.getCapacidadMaxima();
            datos[i][6] = "ACCIONES";
            i++;
        }

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            public boolean isCellEditable(int r, int c) { return c == 6; }
        };

        JTable table = new JTable(modelo);
        UIStyles.styleTable(table);
        table.setRowHeight(35);
        table.setFillsViewportHeight(true);

        ActionColumn actionColumn = new ActionColumn(table, this::editarClase, this::eliminarClase);
        table.getColumn("Acciones").setCellRenderer(actionColumn);
        table.getColumn("Acciones").setCellEditor(actionColumn);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UIStyles.PRIMARY_DARK);
        scroll.setBackground(UIStyles.PRIMARY_DARK);
        scroll.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR));

        panel.add(scroll, BorderLayout.CENTER);

        JButton btn = new JButton("➕ Nueva Clase");
        UIStyles.styleSuccessButton(btn);
        btn.addActionListener(e -> nuevaClase());

        JPanel bottom = new JPanel();
        bottom.setBackground(UIStyles.PRIMARY_DARK);
        bottom.add(btn);

        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void nuevaClase() {
        JDialog dialog = new JDialog(this, "Nueva Clase", true);
        dialog.setSize(560, 560);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UIStyles.PRIMARY_DARK);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIStyles.PRIMARY_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setForeground(UIStyles.ACCENT_RED);
        JTextField txtNombre = new JTextField(20);
        UIStyles.styleTextField(txtNombre);

        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setForeground(UIStyles.ACCENT_RED);
        JTextArea txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setBackground(UIStyles.SECONDARY_DARK);
        txtDescripcion.setForeground(UIStyles.TEXT_PRIMARY);
        txtDescripcion.setCaretColor(UIStyles.ACCENT_RED);
        txtDescripcion.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 2));
        JScrollPane descripcionScroll = new JScrollPane(txtDescripcion);
        descripcionScroll.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 2));

        JLabel lblEntrenador = new JLabel("Entrenador:");
        lblEntrenador.setForeground(UIStyles.ACCENT_RED);
        List<Entrenador> entrenadores = adminController.obtenerTodosEntrenadores();
        JComboBox<Entrenador> comboEntrenador = new JComboBox<>(entrenadores.toArray(new Entrenador[0]));
        comboEntrenador.setBackground(UIStyles.SECONDARY_DARK);
        comboEntrenador.setForeground(UIStyles.TEXT_PRIMARY);
        comboEntrenador.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 2));
        comboEntrenador.setFont(UIStyles.FONT_LABEL);
        comboEntrenador.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value == null ? "" : value.getNombreCompleto());
            label.setOpaque(true);
            label.setFont(UIStyles.FONT_LABEL);
            label.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            label.setForeground(isSelected ? UIStyles.PRIMARY_DARK : UIStyles.TEXT_PRIMARY);
            label.setBackground(isSelected ? UIStyles.ACCENT_RED : UIStyles.SECONDARY_DARK);
            return label;
        });

        JLabel lblDia = new JLabel("Día:");
        lblDia.setForeground(UIStyles.ACCENT_RED);
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        JComboBox<String> comboDia = new JComboBox<>(dias);
        comboDia.setBackground(UIStyles.SECONDARY_DARK);
        comboDia.setForeground(UIStyles.TEXT_PRIMARY);
        comboDia.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 2));

        JLabel lblHoraInicio = new JLabel("Hora inicio (HH:mm):");
        lblHoraInicio.setForeground(UIStyles.ACCENT_RED);
        JTextField txtHoraInicio = new JTextField(20);
        UIStyles.styleTextField(txtHoraInicio);

        JLabel lblHoraFin = new JLabel("Hora fin (HH:mm):");
        lblHoraFin.setForeground(UIStyles.ACCENT_RED);
        JTextField txtHoraFin = new JTextField(20);
        UIStyles.styleTextField(txtHoraFin);

        JLabel lblCapacidad = new JLabel("Capacidad máxima:");
        lblCapacidad.setForeground(UIStyles.ACCENT_RED);
        JTextField txtCapacidad = new JTextField(20);
        UIStyles.styleTextField(txtCapacidad);

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblNombre, gbc);
        gbc.gridx = 1; formPanel.add(txtNombre, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblDescripcion, gbc);
        gbc.gridx = 1; formPanel.add(descripcionScroll, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblEntrenador, gbc);
        gbc.gridx = 1; formPanel.add(comboEntrenador, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblDia, gbc);
        gbc.gridx = 1; formPanel.add(comboDia, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblHoraInicio, gbc);
        gbc.gridx = 1; formPanel.add(txtHoraInicio, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblHoraFin, gbc);
        gbc.gridx = 1; formPanel.add(txtHoraFin, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblCapacidad, gbc);
        gbc.gridx = 1; formPanel.add(txtCapacidad, gbc);

        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.getViewport().setBackground(UIStyles.PRIMARY_DARK);
        dialog.add(scrollForm, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UIStyles.SECONDARY_DARK);

        JButton btnGuardar = new JButton("✓ Guardar");
        UIStyles.styleSuccessButton(btnGuardar);
        btnGuardar.addActionListener(e -> {
            try {
                Entrenador entrenadorSeleccionado = (Entrenador) comboEntrenador.getSelectedItem();
                if (entrenadorSeleccionado == null) {
                    JOptionPane.showMessageDialog(dialog, "Debes seleccionar un entrenador", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Clase clase = new Clase();
                clase.setNombre(txtNombre.getText().trim());
                clase.setDescripcion(txtDescripcion.getText().trim());
                clase.setIdEntrenador(entrenadorSeleccionado.getIdEntrenador());
                clase.setNombreEntrenador(entrenadorSeleccionado.getNombreCompleto());
                clase.setDiaSemana((String) comboDia.getSelectedItem());
                clase.setHorarioInicio(LocalTime.parse(txtHoraInicio.getText().trim()));
                clase.setHorarioFin(LocalTime.parse(txtHoraFin.getText().trim()));
                clase.setCapacidadMaxima(Integer.parseInt(txtCapacidad.getText().trim()));
                clase.setActiva(true);

                if (clase.getNombre().isEmpty() || clase.getDescripcion().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Completa nombre y descripción", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                adminController.crearClase(clase);
                JOptionPane.showMessageDialog(dialog, "Clase creada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                dispose();
                new AdminDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Revisa los datos. Usa formato HH:mm en los horarios y números válidos en capacidad.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancelar = new JButton("✗ Cancelar");
        UIStyles.styleDangerButton(btnCancelar);
        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void editarClase(int row) {
        List<Clase> clases = adminController.obtenerClases();
        if (row >= clases.size()) return;

        Clase clase = clases.get(row);

        JDialog dialog = new JDialog(this, "Editar Clase: " + clase.getNombre(), true);
        dialog.setSize(560, 560);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UIStyles.PRIMARY_DARK);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIStyles.PRIMARY_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setForeground(UIStyles.ACCENT_RED);
        JTextField txtNombre = new JTextField(clase.getNombre(), 20);
        UIStyles.styleTextField(txtNombre);

        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setForeground(UIStyles.ACCENT_RED);
        JTextArea txtDescripcion = new JTextArea(clase.getDescripcion(), 4, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setBackground(UIStyles.SECONDARY_DARK);
        txtDescripcion.setForeground(UIStyles.TEXT_PRIMARY);
        txtDescripcion.setCaretColor(UIStyles.ACCENT_RED);
        txtDescripcion.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 2));
        JScrollPane descripcionScroll = new JScrollPane(txtDescripcion);
        descripcionScroll.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 2));

        JLabel lblEntrenador = new JLabel("Entrenador:");
        lblEntrenador.setForeground(UIStyles.ACCENT_RED);
        List<Entrenador> entrenadores = adminController.obtenerTodosEntrenadores();
        JComboBox<Entrenador> comboEntrenador = new JComboBox<>(entrenadores.toArray(new Entrenador[0]));
        comboEntrenador.setBackground(UIStyles.SECONDARY_DARK);
        comboEntrenador.setForeground(UIStyles.TEXT_PRIMARY);
        comboEntrenador.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 2));
        comboEntrenador.setFont(UIStyles.FONT_LABEL);
        comboEntrenador.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value == null ? "" : value.getNombreCompleto());
            label.setOpaque(true);
            label.setFont(UIStyles.FONT_LABEL);
            label.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            label.setForeground(isSelected ? UIStyles.PRIMARY_DARK : UIStyles.TEXT_PRIMARY);
            label.setBackground(isSelected ? UIStyles.ACCENT_RED : UIStyles.SECONDARY_DARK);
            return label;
        });
        for (int index = 0; index < entrenadores.size(); index++) {
            if (entrenadores.get(index).getNombreCompleto().equals(clase.getNombreEntrenador())) {
                comboEntrenador.setSelectedIndex(index);
                break;
            }
        }

        JLabel lblDia = new JLabel("Día:");
        lblDia.setForeground(UIStyles.ACCENT_RED);
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        JComboBox<String> comboDia = new JComboBox<>(dias);
        comboDia.setSelectedItem(clase.getDiaSemana());
        comboDia.setBackground(UIStyles.SECONDARY_DARK);
        comboDia.setForeground(UIStyles.TEXT_PRIMARY);
        comboDia.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 2));

        JLabel lblHoraInicio = new JLabel("Hora inicio (HH:mm):");
        lblHoraInicio.setForeground(UIStyles.ACCENT_RED);
        JTextField txtHoraInicio = new JTextField(clase.getHorarioInicio().toString(), 20);
        UIStyles.styleTextField(txtHoraInicio);

        JLabel lblHoraFin = new JLabel("Hora fin (HH:mm):");
        lblHoraFin.setForeground(UIStyles.ACCENT_RED);
        JTextField txtHoraFin = new JTextField(clase.getHorarioFin().toString(), 20);
        UIStyles.styleTextField(txtHoraFin);

        JLabel lblCapacidad = new JLabel("Capacidad máxima:");
        lblCapacidad.setForeground(UIStyles.ACCENT_RED);
        JTextField txtCapacidad = new JTextField(String.valueOf(clase.getCapacidadMaxima()), 20);
        UIStyles.styleTextField(txtCapacidad);

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblNombre, gbc);
        gbc.gridx = 1; formPanel.add(txtNombre, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblDescripcion, gbc);
        gbc.gridx = 1; formPanel.add(descripcionScroll, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblEntrenador, gbc);
        gbc.gridx = 1; formPanel.add(comboEntrenador, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblDia, gbc);
        gbc.gridx = 1; formPanel.add(comboDia, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblHoraInicio, gbc);
        gbc.gridx = 1; formPanel.add(txtHoraInicio, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblHoraFin, gbc);
        gbc.gridx = 1; formPanel.add(txtHoraFin, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblCapacidad, gbc);
        gbc.gridx = 1; formPanel.add(txtCapacidad, gbc);

        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.getViewport().setBackground(UIStyles.PRIMARY_DARK);
        dialog.add(scrollForm, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UIStyles.SECONDARY_DARK);

        JButton btnGuardar = new JButton("✓ Guardar");
        UIStyles.styleSuccessButton(btnGuardar);
        btnGuardar.addActionListener(e -> {
            try {
                Entrenador entrenadorSeleccionado = (Entrenador) comboEntrenador.getSelectedItem();
                if (entrenadorSeleccionado == null) {
                    JOptionPane.showMessageDialog(dialog, "Selecciona un entrenador", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                clase.setNombre(txtNombre.getText());
                clase.setDescripcion(txtDescripcion.getText());
                clase.setIdEntrenador(entrenadorSeleccionado.getIdEntrenador());
                clase.setNombreEntrenador(entrenadorSeleccionado.getNombreCompleto());
                clase.setDiaSemana((String) comboDia.getSelectedItem());
                clase.setHorarioInicio(LocalTime.parse(txtHoraInicio.getText().trim()));
                clase.setHorarioFin(LocalTime.parse(txtHoraFin.getText().trim()));
                clase.setCapacidadMaxima(Integer.parseInt(txtCapacidad.getText().trim()));

                adminController.actualizarClase(clase);
                JOptionPane.showMessageDialog(dialog, "Clase actualizada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                dispose();
                new AdminDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Revisa los datos de la clase. Usa formato HH:mm en los horarios.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancelar = new JButton("✗ Cancelar");
        UIStyles.styleDangerButton(btnCancelar);
        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void eliminarClase(int row) {
        List<Clase> clases = adminController.obtenerClases();
        if (row >= clases.size()) return;

        Clase clase = clases.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Deseas desactivar la clase \"" + clase.getNombre() + "\"?",
                "Confirmar desactivación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            adminController.eliminarClase(clase.getIdClase());
            JOptionPane.showMessageDialog(this, "Clase desactivada exitosamente");
            dispose();
            new AdminDashboard();
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Confirmas que deseas cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            authController.logout();
            dispose();
            new LoginView();
        }
    }
}