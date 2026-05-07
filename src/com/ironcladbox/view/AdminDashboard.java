package com.ironcladbox.view;

import com.ironcladbox.controller.AdminController;
import com.ironcladbox.controller.AuthController;
import com.ironcladbox.dao.AtletaDAO;
import com.ironcladbox.dao.EntrenadorDAO;
import com.ironcladbox.dao.IAtletaDAO;
import com.ironcladbox.dao.IEntrenadorDAO;
import com.ironcladbox.dao.IUsuarioDAO;
import com.ironcladbox.dao.UsuarioDAO;
import com.ironcladbox.model.Atleta;
import com.ironcladbox.model.Clase;
import com.ironcladbox.model.Entrenador;
import com.ironcladbox.model.Membresia;
import com.ironcladbox.model.Rol;
import com.ironcladbox.model.Suscripcion;
import com.ironcladbox.model.Usuario;
import com.ironcladbox.util.UIStyles;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class AdminDashboard extends JFrame {

    private interface RowAction {
        void execute(int row);
    }

    private static class ActionColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
        private final JTable table;
        private final RowAction editAction;
        private final RowAction deleteAction;
        private final JPanel panel;
        private int currentRow;

        ActionColumn(JTable table, RowAction editAction, RowAction deleteAction) {
            this.table = table;
            this.editAction = editAction;
            this.deleteAction = deleteAction;

            // Panel único con FlowLayout para centrar botones
            this.panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 3));
            this.panel.setOpaque(true);

            // Botón EDITAR - más pequeño y colores vivos
            JButton editButton = new JButton("✏️ Editar");
            editButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
            editButton.setBackground(new Color(52, 152, 219)); // Azul vibrante
            editButton.setForeground(Color.WHITE);
            editButton.setFocusPainted(false);
            editButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editButton.setPreferredSize(new Dimension(85, 28));

            // Botón ELIMINAR - más pequeño y rojo vibrante
            JButton deleteButton = new JButton("🗑️ Eliminar");
            deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
            deleteButton.setBackground(new Color(231, 76, 60)); // Rojo vibrante
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            deleteButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteButton.setPreferredSize(new Dimension(85, 28));

            // Acciones
            editButton.addActionListener(e -> {
                fireEditingStopped();
                editAction.execute(table.convertRowIndexToModel(currentRow));
            });

            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                deleteAction.execute(table.convertRowIndexToModel(currentRow));
            });

            // Efecto hover
            addHoverEffect(editButton, new Color(52, 152, 219));
            addHoverEffect(deleteButton, new Color(231, 76, 60));

            panel.add(editButton);
            panel.add(deleteButton);
        }

        private void addHoverEffect(JButton button, Color originalColor) {
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(originalColor.brighter());
                    button.setFont(new Font("Segoe UI", Font.BOLD, 12));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(originalColor);
                    button.setFont(new Font("Segoe UI", Font.BOLD, 11));
                }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            panel.setBackground(isSelected ? UIStyles.SECONDARY_DARK : UIStyles.PRIMARY_DARK);
            return panel;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            currentRow = row;
            panel.setBackground(UIStyles.SECONDARY_DARK);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    private Usuario usuarioActual;
    private AuthController authController;
    private AdminController adminController;
    private IUsuarioDAO usuarioDAO;
    private IAtletaDAO atletaDAO;
    private IEntrenadorDAO entrenadorDAO;

    public AdminDashboard() {
        authController = AuthController.getInstance();
        adminController = new AdminController();
        usuarioActual = authController.getUsuarioActual();

        usuarioDAO = new UsuarioDAO();
        atletaDAO = new AtletaDAO();
        entrenadorDAO = new EntrenadorDAO();

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
                BorderFactory.createEmptyBorder(12, 25, 12, 25)));
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
        tabbedPane.addTab("💳 Suscripciones", createSuscripcionesTab());
        tabbedPane.addTab("📋 Membresías", createMembresiaTab());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 15, 0));
        panel.setBackground(UIStyles.SECONDARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(UIStyles.createStatPanel(
                "Atletas registrados",
                String.valueOf(adminController.getTotalAtletas()),
                UIStyles.ACCENT_RED));

        panel.add(UIStyles.createStatPanel(
                "Entrenadores activos",
                String.valueOf(adminController.getTotalEntrenadores()),
                UIStyles.SUCCESS_GREEN));

        panel.add(UIStyles.createStatPanel(
                "Clases disponibles",
                String.valueOf(adminController.getTotalClases()),
                UIStyles.ACCENT_RED));

        panel.add(UIStyles.createStatPanel(
                "Suscripciones activas",
                String.valueOf(adminController.getTotalSuscripciones()),
                UIStyles.ACCENT_RED));

        panel.add(UIStyles.createStatPanel(
                "Membresías activas",
                String.valueOf(adminController.getTotalMembresias()),
                UIStyles.SUCCESS_GREEN));

        return panel;
    }

    private JPanel createAtletasTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.PRIMARY_DARK);

        String[] columnas = { "ID", "Nombre", "Email", "Teléfono", "Peso (kg)", "Altura (m)", "Acciones" };
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
            public boolean isCellEditable(int r, int c) {
                return c == 6;
            }
        };

        JTable table = new JTable(modelo);
        UIStyles.styleTable(table);
        ActionColumn actionColumn = new ActionColumn(table, this::editarAtleta, this::eliminarAtleta);
        table.getColumn("Acciones").setCellRenderer(actionColumn);
        table.getColumn("Acciones").setCellEditor(actionColumn);
        // Ajustar ancho de columnas
        table.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(180); // Email
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Teléfono
        table.getColumnModel().getColumn(4).setPreferredWidth(70); // Peso
        table.getColumnModel().getColumn(5).setPreferredWidth(70); // Altura
        table.getColumnModel().getColumn(6).setPreferredWidth(180); // Acciones ← IMPORTANTE
        table.setRowHeight(48);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UIStyles.PRIMARY_DARK);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(UIStyles.SECONDARY_DARK);

        JButton btnAgregarAtleta = new JButton(" ➕ Agregar Atleta");
        UIStyles.styleSuccessButton(btnAgregarAtleta);
        btnAgregarAtleta.addActionListener(e -> agregarAtleta());
        topPanel.add(btnAgregarAtleta);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void agregarAtleta() {
        JDialog dialog = new JDialog(this, "Agregar Nuevo Atleta", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UIStyles.PRIMARY_DARK);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIStyles.PRIMARY_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Campos del formulario
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setForeground(UIStyles.ACCENT_RED);
        JTextField txtNombre = new JTextField(20);
        UIStyles.styleTextField(txtNombre);

        JLabel lblApellido = new JLabel("Apellido:");
        lblApellido.setForeground(UIStyles.ACCENT_RED);
        JTextField txtApellido = new JTextField(20);
        UIStyles.styleTextField(txtApellido);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setForeground(UIStyles.ACCENT_RED);
        JTextField txtEmail = new JTextField(20);
        UIStyles.styleTextField(txtEmail);

        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setForeground(UIStyles.ACCENT_RED);
        JTextField txtTelefono = new JTextField(20);
        UIStyles.styleTextField(txtTelefono);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setForeground(UIStyles.ACCENT_RED);
        JPasswordField txtPassword = new JPasswordField(20);
        UIStyles.styleTextField(txtPassword);

        JLabel lblPeso = new JLabel("Peso (kg):");
        lblPeso.setForeground(UIStyles.ACCENT_RED);
        JSpinner spinPeso = new JSpinner(new SpinnerNumberModel(70.0, 30.0, 200.0, 0.1));
        spinPeso.setBackground(UIStyles.SECONDARY_DARK);
        spinPeso.setForeground(UIStyles.TEXT_PRIMARY);

        JLabel lblAltura = new JLabel("Altura (m):");
        lblAltura.setForeground(UIStyles.ACCENT_RED);
        JSpinner spinAltura = new JSpinner(new SpinnerNumberModel(1.70, 1.40, 2.20, 0.01));
        spinAltura.setBackground(UIStyles.SECONDARY_DARK);
        spinAltura.setForeground(UIStyles.TEXT_PRIMARY);

        int y = 0;
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblNombre, gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblApellido, gbc);
        gbc.gridx = 1;
        formPanel.add(txtApellido, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblEmail, gbc);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblTelefono, gbc);
        gbc.gridx = 1;
        formPanel.add(txtTelefono, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblPassword, gbc);
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblPeso, gbc);
        gbc.gridx = 1;
        formPanel.add(spinPeso, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblAltura, gbc);
        gbc.gridx = 1;
        formPanel.add(spinAltura, gbc);

        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.getViewport().setBackground(UIStyles.PRIMARY_DARK);
        dialog.add(scrollForm, BorderLayout.CENTER);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UIStyles.SECONDARY_DARK);

        JButton btnGuardar = new JButton("✓ Guardar");
        UIStyles.styleSuccessButton(btnGuardar);
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String email = txtEmail.getText().trim();
                String telefono = txtTelefono.getText().trim();
                String password = new String(txtPassword.getPassword());
                double peso = (double) spinPeso.getValue();
                double altura = (double) spinAltura.getValue();

                if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Completa todos los campos obligatorios", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Crear usuario en la tabla usuarios
                Usuario nuevoUsuario = new Usuario(email, password, nombre, apellido, telefono, Rol.ATLETA);
                usuarioDAO.guardar(nuevoUsuario);

                // Crear atleta en la tabla atletas
                Atleta nuevoAtleta = new Atleta();
                nuevoAtleta.setIdUsuario(nuevoUsuario.getIdUsuario());
                nuevoAtleta.setEmail(email);
                nuevoAtleta.setNombre(nombre);
                nuevoAtleta.setApellido(apellido);
                nuevoAtleta.setTelefono(telefono);
                nuevoAtleta.setPeso(peso);
                nuevoAtleta.setAltura(altura);
                nuevoAtleta.setActivo(true);
                nuevoAtleta.setRol(Rol.ATLETA);

                atletaDAO.guardar(nuevoAtleta);

                JOptionPane.showMessageDialog(dialog, "Atleta agregado exitosamente", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                dispose();
                new AdminDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error al guardar: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
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

    private void editarAtleta(int row) {
        List<Atleta> atletas = adminController.obtenerTodosAtletas();
        if (row >= atletas.size())
            return;

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
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblNombre, gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblApellido, gbc);
        gbc.gridx = 1;
        formPanel.add(txtApellido, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblEmail, gbc);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblTelefono, gbc);
        gbc.gridx = 1;
        formPanel.add(txtTelefono, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblPeso, gbc);
        gbc.gridx = 1;
        formPanel.add(txtPeso, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblAltura, gbc);
        gbc.gridx = 1;
        formPanel.add(txtAltura, gbc);

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
                JOptionPane.showMessageDialog(dialog, "Atleta actualizado exitosamente", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

                // Recargar tabla
                dispose();
                new AdminDashboard();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: Verifica los valores numéricos", "Error",
                        JOptionPane.ERROR_MESSAGE);
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
        if (row >= atletas.size())
            return;

        Atleta atleta = atletas.get(row);
        Object[] opciones = { "Sí", "No" };
        int confirm = JOptionPane.showOptionDialog(this,
                "¿Deseas eliminar al atleta " + atleta.getNombreCompleto() + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (confirm == 0) { // 0 = Sí
            adminController.eliminarAtleta(atleta.getIdAtleta());
            JOptionPane.showMessageDialog(this, "Atleta eliminado exitosamente");
            dispose();
            new AdminDashboard();
        }
    }

    private JPanel createEntrenoresTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.PRIMARY_DARK);

        String[] columnas = { "ID", "Nombre", "Email", "Certificación", "Especialidad", "Experiencia", "Acciones" };
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
            public boolean isCellEditable(int r, int c) {
                return c == 6;
            }
        };

        JTable table = new JTable(modelo);
        UIStyles.styleTable(table);
        ActionColumn actionColumn = new ActionColumn(table, this::editarEntrenador, this::eliminarEntrenador);
        table.getColumn("Acciones").setCellRenderer(actionColumn);
        table.getColumn("Acciones").setCellEditor(actionColumn);
        table.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(180); // Email
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Certificación
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Especialidad
        table.getColumnModel().getColumn(5).setPreferredWidth(80); // Experiencia
        table.getColumnModel().getColumn(6).setPreferredWidth(180); // Acciones
        table.setRowHeight(48);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UIStyles.PRIMARY_DARK);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(UIStyles.SECONDARY_DARK);

        JButton btnAgregarEntrenador = new JButton("➕ Agregar Entrenador");
        UIStyles.styleSuccessButton(btnAgregarEntrenador);
        btnAgregarEntrenador.addActionListener(e -> agregarEntrenador());
        topPanel.add(btnAgregarEntrenador);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void agregarEntrenador() {
        JDialog dialog = new JDialog(this, "Agregar Nuevo Entrenador", true);
        dialog.setSize(500, 650);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UIStyles.PRIMARY_DARK);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIStyles.PRIMARY_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Campos del formulario
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setForeground(UIStyles.ACCENT_RED);
        JTextField txtNombre = new JTextField(20);
        UIStyles.styleTextField(txtNombre);

        JLabel lblApellido = new JLabel("Apellido:");
        lblApellido.setForeground(UIStyles.ACCENT_RED);
        JTextField txtApellido = new JTextField(20);
        UIStyles.styleTextField(txtApellido);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setForeground(UIStyles.ACCENT_RED);
        JTextField txtEmail = new JTextField(20);
        UIStyles.styleTextField(txtEmail);

        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setForeground(UIStyles.ACCENT_RED);
        JTextField txtTelefono = new JTextField(20);
        UIStyles.styleTextField(txtTelefono);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setForeground(UIStyles.ACCENT_RED);
        JPasswordField txtPassword = new JPasswordField(20);
        UIStyles.styleTextField(txtPassword);

        JLabel lblCertificacion = new JLabel("Certificación:");
        lblCertificacion.setForeground(UIStyles.ACCENT_RED);
        JTextField txtCertificacion = new JTextField(20);
        UIStyles.styleTextField(txtCertificacion);

        JLabel lblEspecialidad = new JLabel("Especialidad:");
        lblEspecialidad.setForeground(UIStyles.ACCENT_RED);
        JTextField txtEspecialidad = new JTextField(20);
        UIStyles.styleTextField(txtEspecialidad);

        JLabel lblExperiencia = new JLabel("Experiencia (años):");
        lblExperiencia.setForeground(UIStyles.ACCENT_RED);
        JSpinner spinExperiencia = new JSpinner(new SpinnerNumberModel(1, 0, 50, 1));
        spinExperiencia.setBackground(UIStyles.SECONDARY_DARK);
        spinExperiencia.setForeground(UIStyles.TEXT_PRIMARY);

        int y = 0;
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblNombre, gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblApellido, gbc);
        gbc.gridx = 1;
        formPanel.add(txtApellido, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblEmail, gbc);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblTelefono, gbc);
        gbc.gridx = 1;
        formPanel.add(txtTelefono, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblPassword, gbc);
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblCertificacion, gbc);
        gbc.gridx = 1;
        formPanel.add(txtCertificacion, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblEspecialidad, gbc);
        gbc.gridx = 1;
        formPanel.add(txtEspecialidad, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblExperiencia, gbc);
        gbc.gridx = 1;
        formPanel.add(spinExperiencia, gbc);

        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.getViewport().setBackground(UIStyles.PRIMARY_DARK);
        dialog.add(scrollForm, BorderLayout.CENTER);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UIStyles.SECONDARY_DARK);

        JButton btnGuardar = new JButton("✓ Guardar");
        UIStyles.styleSuccessButton(btnGuardar);
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String email = txtEmail.getText().trim();
                String telefono = txtTelefono.getText().trim();
                String password = new String(txtPassword.getPassword());
                String certificacion = txtCertificacion.getText().trim();
                String especialidad = txtEspecialidad.getText().trim();
                int experiencia = (int) spinExperiencia.getValue();

                if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Completa todos los campos obligatorios", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Crear usuario en la tabla usuarios
                Usuario nuevoUsuario = new Usuario(email, password, nombre, apellido, telefono, Rol.ENTRENADOR);
                usuarioDAO.guardar(nuevoUsuario);

                // Crear entrenador en la tabla entrenadores
                Entrenador nuevoEntrenador = new Entrenador();
                nuevoEntrenador.setIdUsuario(nuevoUsuario.getIdUsuario());
                nuevoEntrenador.setEmail(email);
                nuevoEntrenador.setNombre(nombre);
                nuevoEntrenador.setApellido(apellido);
                nuevoEntrenador.setTelefono(telefono);
                nuevoEntrenador.setCertificacion(certificacion);
                nuevoEntrenador.setEspecialidad(especialidad);
                nuevoEntrenador.setExperienciaAnios(experiencia);
                nuevoEntrenador.setActivo(true);
                nuevoEntrenador.setRol(Rol.ENTRENADOR);

                entrenadorDAO.guardar(nuevoEntrenador);

                JOptionPane.showMessageDialog(dialog, "Entrenador agregado exitosamente", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                dispose();
                new AdminDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error al guardar: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
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

    private void editarEntrenador(int row) {
        List<Entrenador> entrenadores = adminController.obtenerTodosEntrenadores();
        if (row >= entrenadores.size())
            return;

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
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblNombre, gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblApellido, gbc);
        gbc.gridx = 1;
        formPanel.add(txtApellido, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblEmail, gbc);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblCertificacion, gbc);
        gbc.gridx = 1;
        formPanel.add(txtCertificacion, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblEspecialidad, gbc);
        gbc.gridx = 1;
        formPanel.add(txtEspecialidad, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblExperiencia, gbc);
        gbc.gridx = 1;
        formPanel.add(txtExperiencia, gbc);

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
                JOptionPane.showMessageDialog(dialog, "Entrenador actualizado exitosamente", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

                // Recargar tabla
                dispose();
                new AdminDashboard();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: Verifica los valores numéricos", "Error",
                        JOptionPane.ERROR_MESSAGE);
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
        if (row >= entrenadores.size())
            return;

        Entrenador entrenador = entrenadores.get(row);
        Object[] opciones = { "Sí", "No" };
        int confirm = JOptionPane.showOptionDialog(this,
                "¿Deseas eliminar al entrenador " + entrenador.getNombreCompleto() + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (confirm == 0) {
            adminController.eliminarEntrenador(entrenador.getIdEntrenador());
            JOptionPane.showMessageDialog(this, "Entrenador eliminado exitosamente");
            dispose();
            new AdminDashboard();
        }
    }

    private JPanel createClassesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.PRIMARY_DARK);

        String[] columnas = { "ID", "Nombre", "Entrenador", "Día", "Horario", "Capacidad", "Acciones" };
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
            public boolean isCellEditable(int r, int c) {
                return c == 6;
            }
        };

        JTable table = new JTable(modelo);
        UIStyles.styleTable(table);
        table.setFillsViewportHeight(true);

        ActionColumn actionColumn = new ActionColumn(table, this::editarClase, this::eliminarClase);
        table.getColumn("Acciones").setCellRenderer(actionColumn);
        table.getColumn("Acciones").setCellEditor(actionColumn);
        table.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Entrenador
        table.getColumnModel().getColumn(3).setPreferredWidth(90); // Día
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Horario
        table.getColumnModel().getColumn(5).setPreferredWidth(70); // Capacidad
        table.getColumnModel().getColumn(6).setPreferredWidth(180); // Acciones
        table.setRowHeight(48);

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
        String[] dias = { "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo" };
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
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblNombre, gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblDescripcion, gbc);
        gbc.gridx = 1;
        formPanel.add(descripcionScroll, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblEntrenador, gbc);
        gbc.gridx = 1;
        formPanel.add(comboEntrenador, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblDia, gbc);
        gbc.gridx = 1;
        formPanel.add(comboDia, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblHoraInicio, gbc);
        gbc.gridx = 1;
        formPanel.add(txtHoraInicio, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblHoraFin, gbc);
        gbc.gridx = 1;
        formPanel.add(txtHoraFin, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblCapacidad, gbc);
        gbc.gridx = 1;
        formPanel.add(txtCapacidad, gbc);

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
                    JOptionPane.showMessageDialog(dialog, "Debes seleccionar un entrenador", "Error",
                            JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(dialog, "Completa nombre y descripción", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                adminController.crearClase(clase);
                JOptionPane.showMessageDialog(dialog, "Clase creada exitosamente", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                dispose();
                new AdminDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Revisa los datos. Usa formato HH:mm en los horarios y números válidos en capacidad.", "Error",
                        JOptionPane.ERROR_MESSAGE);
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
        if (row >= clases.size())
            return;

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
        String[] dias = { "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo" };
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
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblNombre, gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblDescripcion, gbc);
        gbc.gridx = 1;
        formPanel.add(descripcionScroll, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblEntrenador, gbc);
        gbc.gridx = 1;
        formPanel.add(comboEntrenador, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblDia, gbc);
        gbc.gridx = 1;
        formPanel.add(comboDia, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblHoraInicio, gbc);
        gbc.gridx = 1;
        formPanel.add(txtHoraInicio, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblHoraFin, gbc);
        gbc.gridx = 1;
        formPanel.add(txtHoraFin, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblCapacidad, gbc);
        gbc.gridx = 1;
        formPanel.add(txtCapacidad, gbc);

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
                    JOptionPane.showMessageDialog(dialog, "Selecciona un entrenador", "Error",
                            JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(dialog, "Clase actualizada exitosamente", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                dispose();
                new AdminDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Revisa los datos de la clase. Usa formato HH:mm en los horarios.", "Error",
                        JOptionPane.ERROR_MESSAGE);
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
        if (row >= clases.size())
            return;

        Clase clase = clases.get(row);
        Object[] opciones = { "Sí", "No" };
        int confirm = JOptionPane.showOptionDialog(this,
                "¿Deseas desactivar la clase \"" + clase.getNombre() + "\"?",
                "Confirmar desactivación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (confirm == 0) {
            adminController.eliminarClase(clase.getIdClase());
            JOptionPane.showMessageDialog(this, "Clase desactivada exitosamente");
            dispose();
            new AdminDashboard();
        }
    }

    private JPanel createMembresiaTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.PRIMARY_DARK);

        List<Membresia> membresias = adminController.obtenerMembresias().stream()
                .filter(Membresia::isActiva)
                .toList();

        String[] columnas = { "ID", "Nombre", "Precio ($)", "Duración (días)", "Beneficios", "Acciones" };
        Object[][] datos = new Object[membresias.size()][6];

        int i = 0;
        for (Membresia m : membresias) {
            datos[i][0] = m.getIdMembresia();
            datos[i][1] = m.getNombre();
            datos[i][2] = String.format("$%.2f", m.getPrecio());
            datos[i][3] = m.getDuracionDias();
            datos[i][4] = m.getBeneficios() != null
                    ? m.getBeneficios().substring(0, Math.min(30, m.getBeneficios().length())) + "..."
                    : "Sin beneficios";
            datos[i][5] = "ACCIONES";
            i++;
        }

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            public boolean isCellEditable(int r, int c) {
                return c == 5;
            }
        };

        JTable table = new JTable(modelo);
        UIStyles.styleTable(table);
        ActionColumn actionColumn = new ActionColumn(table, this::editarMembresia, this::eliminarMembresia);
        table.getColumn("Acciones").setCellRenderer(actionColumn);
        table.getColumn("Acciones").setCellEditor(actionColumn);

        table.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(80); // Precio
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Duración
        table.getColumnModel().getColumn(4).setPreferredWidth(200); // Beneficios
        table.getColumnModel().getColumn(5).setPreferredWidth(180); // Acciones
        table.setRowHeight(48);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UIStyles.PRIMARY_DARK);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(UIStyles.SECONDARY_DARK);

        JButton btnNuevaMembresia = new JButton("➕ Nueva Membresía");
        UIStyles.styleSuccessButton(btnNuevaMembresia);
        btnNuevaMembresia.addActionListener(e -> crearNuevaMembresia());
        topPanel.add(btnNuevaMembresia);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void crearNuevaMembresia() {
        JDialog dialog = new JDialog(this, "Nueva Membresía", true);
        dialog.setSize(500, 550);
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
        JTextField txtNombre = new JTextField();
        UIStyles.styleTextField(txtNombre);

        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setForeground(UIStyles.ACCENT_RED);
        JTextArea txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setBackground(UIStyles.SECONDARY_DARK);
        txtDescripcion.setForeground(Color.WHITE);
        txtDescripcion.setFont(new Font("Arial", Font.PLAIN, 11));

        JLabel lblPrecio = new JLabel("Precio ($):");
        lblPrecio.setForeground(UIStyles.ACCENT_RED);
        JSpinner spinPrecio = new JSpinner(new javax.swing.SpinnerNumberModel(29.99, 0, 10000, 0.01));

        JLabel lblDuracion = new JLabel("Duración (días):");
        lblDuracion.setForeground(UIStyles.ACCENT_RED);
        JSpinner spinDuracion = new JSpinner(new javax.swing.SpinnerNumberModel(30, 1, 730, 1));

        JLabel lblBeneficios = new JLabel("Beneficios:");
        lblBeneficios.setForeground(UIStyles.ACCENT_RED);
        JTextArea txtBeneficios = new JTextArea(3, 20);
        txtBeneficios.setLineWrap(true);
        txtBeneficios.setBackground(UIStyles.SECONDARY_DARK);
        txtBeneficios.setForeground(Color.WHITE);
        txtBeneficios.setFont(new Font("Arial", Font.PLAIN, 11));

        int y = 0;
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblNombre, gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblDescripcion, gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(txtDescripcion), gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblPrecio, gbc);
        gbc.gridx = 1;
        formPanel.add(spinPrecio, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblDuracion, gbc);
        gbc.gridx = 1;
        formPanel.add(spinDuracion, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblBeneficios, gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(txtBeneficios), gbc);

        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.getViewport().setBackground(UIStyles.PRIMARY_DARK);
        dialog.add(scrollForm, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UIStyles.SECONDARY_DARK);

        JButton btnGuardar = new JButton("✓ Crear");
        UIStyles.styleSuccessButton(btnGuardar);
        btnGuardar.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "El nombre es requerido", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Double precio = (Double) spinPrecio.getValue();
            Integer duracion = (Integer) spinDuracion.getValue();

            if (precio <= 0) {
                JOptionPane.showMessageDialog(dialog, "El precio debe ser mayor a 0", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Membresia membresia = new Membresia(
                    txtNombre.getText().trim(),
                    txtDescripcion.getText().trim(),
                    precio,
                    duracion,
                    txtBeneficios.getText().trim());

            adminController.crearMembresia(membresia);
            JOptionPane.showMessageDialog(dialog, "Membresía creada exitosamente", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            dispose();
            new AdminDashboard();
        });

        JButton btnCancelar = new JButton("✗ Cancelar");
        UIStyles.styleDangerButton(btnCancelar);
        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void editarMembresia(int row) {
        List<Membresia> membresias = adminController.obtenerMembresias().stream()
                .filter(Membresia::isActiva)
                .toList();
        if (row >= membresias.size())
            return;

        Membresia membresia = membresias.get(row);

        JDialog dialog = new JDialog(this, "Editar Membresía: " + membresia.getNombre(), true);
        dialog.setSize(500, 550);
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
        JTextField txtNombre = new JTextField(membresia.getNombre());
        UIStyles.styleTextField(txtNombre);

        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setForeground(UIStyles.ACCENT_RED);
        JTextArea txtDescripcion = new JTextArea(membresia.getDescripcion() != null ? membresia.getDescripcion() : "",
                3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setBackground(UIStyles.SECONDARY_DARK);
        txtDescripcion.setForeground(Color.WHITE);
        txtDescripcion.setFont(new Font("Arial", Font.PLAIN, 11));

        JLabel lblPrecio = new JLabel("Precio ($):");
        lblPrecio.setForeground(UIStyles.ACCENT_RED);
        JSpinner spinPrecio = new JSpinner(new javax.swing.SpinnerNumberModel(membresia.getPrecio(), 0, 10000, 0.01));

        JLabel lblDuracion = new JLabel("Duración (días):");
        lblDuracion.setForeground(UIStyles.ACCENT_RED);
        JSpinner spinDuracion = new JSpinner(
                new javax.swing.SpinnerNumberModel(membresia.getDuracionDias(), 1, 730, 1));

        JLabel lblBeneficios = new JLabel("Beneficios:");
        lblBeneficios.setForeground(UIStyles.ACCENT_RED);
        JTextArea txtBeneficios = new JTextArea(membresia.getBeneficios() != null ? membresia.getBeneficios() : "", 3,
                20);
        txtBeneficios.setLineWrap(true);
        txtBeneficios.setBackground(UIStyles.SECONDARY_DARK);
        txtBeneficios.setForeground(Color.WHITE);
        txtBeneficios.setFont(new Font("Arial", Font.PLAIN, 11));

        int y = 0;
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblNombre, gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblDescripcion, gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(txtDescripcion), gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblPrecio, gbc);
        gbc.gridx = 1;
        formPanel.add(spinPrecio, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblDuracion, gbc);
        gbc.gridx = 1;
        formPanel.add(spinDuracion, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblBeneficios, gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(txtBeneficios), gbc);

        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.getViewport().setBackground(UIStyles.PRIMARY_DARK);
        dialog.add(scrollForm, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UIStyles.SECONDARY_DARK);

        JButton btnGuardar = new JButton("✓ Guardar");
        UIStyles.styleSuccessButton(btnGuardar);
        btnGuardar.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "El nombre es requerido", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Double precio = (Double) spinPrecio.getValue();
            Integer duracion = (Integer) spinDuracion.getValue();

            if (precio <= 0) {
                JOptionPane.showMessageDialog(dialog, "El precio debe ser mayor a 0", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            membresia.setNombre(txtNombre.getText().trim());
            membresia.setDescripcion(txtDescripcion.getText().trim());
            membresia.setPrecio(precio);
            membresia.setDuracionDias(duracion);
            membresia.setBeneficios(txtBeneficios.getText().trim());

            adminController.actualizarMembresia(membresia);
            JOptionPane.showMessageDialog(dialog, "Membresía actualizada exitosamente", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            dispose();
            new AdminDashboard();
        });

        JButton btnCancelar = new JButton("✗ Cancelar");
        UIStyles.styleDangerButton(btnCancelar);
        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void eliminarMembresia(int row) {
        List<Membresia> membresias = adminController.obtenerMembresias().stream()
                .filter(Membresia::isActiva)
                .toList();
        if (row >= membresias.size())
            return;

        Membresia membresia = membresias.get(row);
        Object[] opciones = { "Sí", "No" };
        int confirm = JOptionPane.showOptionDialog(this,
                "¿Desactivar membresía \"" + membresia.getNombre() + "\"?",
                "Confirmar desactivación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (confirm == 0) {
            membresia.setActiva(false);
            adminController.actualizarMembresia(membresia);
            JOptionPane.showMessageDialog(this, "Membresía desactivada exitosamente");
            dispose();
            new AdminDashboard();
        }
    }

    private void logout() {
        Object[] opciones = { "Sí", "No" };
        int confirm = JOptionPane.showOptionDialog(this,
                "¿Confirmas que deseas cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (confirm == 0) {
            authController.logout();
            dispose();
            new LoginView();
        }
    }

    private JPanel createSuscripcionesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.PRIMARY_DARK);

        String[] columnas = { "Atleta", "Membresía", "Precio ($)", "Fecha Inicio", "Fecha Fin", "Acciones" };
        List<Suscripcion> suscripciones = adminController.obtenerTodasLasSuscripciones();
        Object[][] datos = new Object[suscripciones.size()][6];

        int i = 0;
        for (Suscripcion s : suscripciones) {
            Atleta atleta = adminController.obtenerTodosAtletas().stream()
                    .filter(a -> a.getIdAtleta() == s.getIdAtleta())
                    .findFirst()
                    .orElse(null);

            datos[i][0] = atleta != null ? atleta.getNombreCompleto() : "Desconocido";
            datos[i][1] = s.getNombreMembresia();
            datos[i][2] = String.format("$%.2f", s.getPrecioMembresia());
            datos[i][3] = s.getFechaInicio();
            datos[i][4] = s.getFechaFin();
            datos[i][5] = "ACCIONES";
            i++;
        }

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            public boolean isCellEditable(int r, int c) {
                return c == 5;
            }
        };

        JTable table = new JTable(modelo);
        UIStyles.styleTable(table);
        ActionColumn actionColumn = new ActionColumn(table, this::editarSuscripcion, this::revocarSuscripcion);
        table.getColumn("Acciones").setCellRenderer(actionColumn);
        table.getColumn("Acciones").setCellEditor(actionColumn);
        table.getColumnModel().getColumn(0).setPreferredWidth(150); // Atleta
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Membresía
        table.getColumnModel().getColumn(2).setPreferredWidth(80); // Precio
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Fecha Inicio
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Fecha Fin
        table.getColumnModel().getColumn(5).setPreferredWidth(180); // Acciones
        table.setRowHeight(48);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UIStyles.PRIMARY_DARK);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(UIStyles.SECONDARY_DARK);

        JButton btnNuevaSuscripcion = new JButton("➕ Nueva Suscripción");
        UIStyles.styleSuccessButton(btnNuevaSuscripcion);
        btnNuevaSuscripcion.addActionListener(e -> crearNuevaSuscripcion());
        topPanel.add(btnNuevaSuscripcion);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void crearNuevaSuscripcion() {
        JDialog dialog = new JDialog(this, "Nueva Suscripción", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UIStyles.PRIMARY_DARK);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIStyles.PRIMARY_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblAtleta = new JLabel("Atleta:");
        lblAtleta.setForeground(UIStyles.ACCENT_RED);
        List<Atleta> atletasSinSuscripcion = adminController.obtenerTodosAtletas().stream()
                .filter(a -> adminController.obtenerSuscripcionActivaDeAtleta(a.getIdAtleta()) == null)
                .toList();
        JComboBox<Atleta> comboAtleta = new JComboBox<>(atletasSinSuscripcion.toArray(new Atleta[0]));

        JLabel lblMembresia = new JLabel("Membresía:");
        lblMembresia.setForeground(UIStyles.ACCENT_RED);
        JComboBox<Membresia> comboMembresia = new JComboBox<>(
                adminController.obtenerMembresias().toArray(new Membresia[0]));

        JLabel lblFechaInicio = new JLabel("Fecha Inicio:");
        lblFechaInicio.setForeground(UIStyles.ACCENT_RED);
        JSpinner spinFechaInicio = new JSpinner(new javax.swing.SpinnerDateModel(
                new java.util.Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor editorInicio = new JSpinner.DateEditor(spinFechaInicio, "yyyy-MM-dd");
        spinFechaInicio.setEditor(editorInicio);

        JLabel lblFechaFin = new JLabel("Fecha Fin:");
        lblFechaFin.setForeground(UIStyles.ACCENT_RED);
        JSpinner spinFechaFin = new JSpinner(new javax.swing.SpinnerDateModel(
                new java.util.Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor editorFin = new JSpinner.DateEditor(spinFechaFin, "yyyy-MM-dd");
        spinFechaFin.setEditor(editorFin);

        int y = 0;
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblAtleta, gbc);
        gbc.gridx = 1;
        formPanel.add(comboAtleta, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblMembresia, gbc);
        gbc.gridx = 1;
        formPanel.add(comboMembresia, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblFechaInicio, gbc);
        gbc.gridx = 1;
        formPanel.add(spinFechaInicio, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblFechaFin, gbc);
        gbc.gridx = 1;
        formPanel.add(spinFechaFin, gbc);

        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.getViewport().setBackground(UIStyles.PRIMARY_DARK);
        dialog.add(scrollForm, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UIStyles.SECONDARY_DARK);

        JButton btnGuardar = new JButton("✓ Crear");
        UIStyles.styleSuccessButton(btnGuardar);
        btnGuardar.addActionListener(e -> {
            Atleta atletaSeleccionado = (Atleta) comboAtleta.getSelectedItem();
            Membresia membresiaSeleccionada = (Membresia) comboMembresia.getSelectedItem();

            if (atletaSeleccionado == null || membresiaSeleccionada == null) {
                JOptionPane.showMessageDialog(dialog, "Selecciona atleta y membresía", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            java.util.Date dateInicio = (java.util.Date) spinFechaInicio.getValue();
            java.util.Date dateFin = (java.util.Date) spinFechaFin.getValue();
            LocalDate fechaInicio = dateInicio.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalDate fechaFin = dateFin.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            if (!fechaFin.isAfter(fechaInicio)) {
                JOptionPane.showMessageDialog(dialog, "Fecha fin debe ser posterior a fecha inicio", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                adminController.crearSuscripcion(atletaSeleccionado.getIdAtleta(),
                        membresiaSeleccionada.getIdMembresia(), fechaInicio, fechaFin);
                JOptionPane.showMessageDialog(dialog, "Suscripción creada exitosamente", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                dispose();
                new AdminDashboard();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void editarSuscripcion(int row) {
        List<Suscripcion> suscripciones = adminController.obtenerTodasLasSuscripciones();
        if (row >= suscripciones.size())
            return;

        Suscripcion suscripcion = suscripciones.get(row);
        Atleta atleta = adminController.obtenerTodosAtletas().stream()
                .filter(a -> a.getIdAtleta() == suscripcion.getIdAtleta())
                .findFirst()
                .orElse(null);

        JDialog dialog = new JDialog(this, "Editar Suscripción", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UIStyles.PRIMARY_DARK);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIStyles.PRIMARY_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblAtleta = new JLabel("Atleta:");
        lblAtleta.setForeground(UIStyles.ACCENT_RED);
        JLabel txtAtleta = new JLabel(atleta != null ? atleta.getNombreCompleto() : "Desconocido");
        txtAtleta.setForeground(Color.WHITE);

        JLabel lblMembresia = new JLabel("Membresía:");
        lblMembresia.setForeground(UIStyles.ACCENT_RED);
        JLabel txtMembresia = new JLabel(suscripcion.getNombreMembresia());
        txtMembresia.setForeground(Color.WHITE);

        JLabel lblFechaInicio = new JLabel("Fecha Inicio:");
        lblFechaInicio.setForeground(UIStyles.ACCENT_RED);
        JSpinner spinFechaInicio = new JSpinner(new javax.swing.SpinnerDateModel(
                java.sql.Date.valueOf(suscripcion.getFechaInicio()), null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor editorInicio = new JSpinner.DateEditor(spinFechaInicio, "yyyy-MM-dd");
        spinFechaInicio.setEditor(editorInicio);

        JLabel lblFechaFin = new JLabel("Fecha Fin:");
        lblFechaFin.setForeground(UIStyles.ACCENT_RED);
        JSpinner spinFechaFin = new JSpinner(new javax.swing.SpinnerDateModel(
                java.sql.Date.valueOf(suscripcion.getFechaFin()), null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor editorFin = new JSpinner.DateEditor(spinFechaFin, "yyyy-MM-dd");
        spinFechaFin.setEditor(editorFin);

        int y = 0;
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblAtleta, gbc);
        gbc.gridx = 1;
        formPanel.add(txtAtleta, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblMembresia, gbc);
        gbc.gridx = 1;
        formPanel.add(txtMembresia, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblFechaInicio, gbc);
        gbc.gridx = 1;
        formPanel.add(spinFechaInicio, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        formPanel.add(lblFechaFin, gbc);
        gbc.gridx = 1;
        formPanel.add(spinFechaFin, gbc);

        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.getViewport().setBackground(UIStyles.PRIMARY_DARK);
        dialog.add(scrollForm, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UIStyles.SECONDARY_DARK);

        JButton btnGuardar = new JButton("✓ Guardar");
        UIStyles.styleSuccessButton(btnGuardar);
        btnGuardar.addActionListener(e -> {
            java.util.Date dateInicio = (java.util.Date) spinFechaInicio.getValue();
            java.util.Date dateFin = (java.util.Date) spinFechaFin.getValue();
            LocalDate fechaInicio = dateInicio.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalDate fechaFin = dateFin.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            if (!fechaFin.isAfter(fechaInicio)) {
                JOptionPane.showMessageDialog(dialog, "Fecha fin debe ser posterior a fecha inicio", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                suscripcion.setFechaInicio(fechaInicio);
                suscripcion.setFechaFin(fechaFin);
                adminController.actualizarSuscripcion(suscripcion);
                JOptionPane.showMessageDialog(dialog, "Suscripción actualizada exitosamente", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                dispose();
                new AdminDashboard();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void revocarSuscripcion(int row) {
        List<Suscripcion> suscripciones = adminController.obtenerTodasLasSuscripciones();
        if (row >= suscripciones.size())
            return;

        Suscripcion suscripcion = suscripciones.get(row);
        Atleta atleta = adminController.obtenerTodosAtletas().stream()
                .filter(a -> a.getIdAtleta() == suscripcion.getIdAtleta())
                .findFirst()
                .orElse(null);

        Object[] opciones = { "Sí", "No" };
        int confirm = JOptionPane.showOptionDialog(this,
                "¿Desactivar suscripción para " + (atleta != null ? atleta.getNombreCompleto() : "Desconocido") + "?",
                "Revocar Suscripción",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (confirm == 0) {
            adminController.revocarSuscripcion(suscripcion.getIdSuscripcion());
            JOptionPane.showMessageDialog(this, "Suscripción revocada exitosamente");
            dispose();
            new AdminDashboard();
        }
    }
}
