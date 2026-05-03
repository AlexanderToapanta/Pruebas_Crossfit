package com.ironcladbox.view;

import com.ironcladbox.controller.AuthController;
import com.ironcladbox.controller.EntrenadorController;
import com.ironcladbox.util.UIStyles;
import com.ironcladbox.model.Usuario;
import com.ironcladbox.model.Clase;
import com.ironcladbox.model.Entrenador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.util.List;

public class EntrenadorDashboard extends JFrame {
    private Usuario usuarioActual;
    private AuthController authController;
    private EntrenadorController entrenadorController;

    public EntrenadorDashboard() {
        authController = AuthController.getInstance();
        entrenadorController = new EntrenadorController();
        usuarioActual = authController.getUsuarioActual();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("IroncladBox CrossFit - Dashboard Entrenador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 750);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIStyles.PRIMARY_DARK);

        // Header
        JButton logoutButton = new JButton("🚪 Cerrar Sesión");
        UIStyles.styleDangerButton(logoutButton);
        logoutButton.addActionListener(e -> logout());
        
        JPanel headerPanel = UIStyles.createHeaderPanel("🏋️ ENTRENADOR - " + usuarioActual.getNombreCompleto(), logoutButton);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(UIStyles.PRIMARY_DARK);
        tabbedPane.setForeground(UIStyles.ACCENT_RED);

        // Panel de clases que se recargará dinámicamente
        JPanel classesPanel = new JPanel(new BorderLayout());
        classesPanel.setBackground(UIStyles.PRIMARY_DARK);
        
        tabbedPane.addTab("📚 Mis Clases", classesPanel);
        tabbedPane.addTab("👤 Mi Perfil", createProfileTab());

        // Listener para recargar clases cuando se abre la pestaña
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                classesPanel.removeAll();
                classesPanel.add(createClassesTab(), BorderLayout.CENTER);
                classesPanel.revalidate();
                classesPanel.repaint();
            }
        });

        // Cargar las clases la primera vez
        classesPanel.add(createClassesTab(), BorderLayout.CENTER);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createClassesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.PRIMARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"ID", "Nombre", "Día", "Horario", "Capacidad", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Cargar las clases actuales del entrenador
        Entrenador entrenadorActual = (Entrenador) usuarioActual;
        List<Clase> clases = entrenadorController.obtenerMisClases(entrenadorActual.getIdEntrenador());
        for (Clase clase : clases) {
            modelo.addRow(new Object[]{
                clase.getIdClase(),
                clase.getNombre(),
                clase.getDiaSemana(),
                clase.getHorarioInicio() + " - " + clase.getHorarioFin(),
                clase.getCapacidadMaxima(),
                clase.isActiva() ? "✓ Activa" : "✗ Inactiva"
            });
        }

        JTable clasesTable = new JTable(modelo);
        UIStyles.styleTable(clasesTable);
        
        JScrollPane scrollPane = new JScrollPane(clasesTable);
        scrollPane.setBackground(UIStyles.PRIMARY_DARK);
        scrollPane.getViewport().setBackground(UIStyles.PRIMARY_DARK);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(UIStyles.PRIMARY_DARK);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton attendanceButton = new JButton("📋 Registrar Asistencia");
        UIStyles.stylePrimaryButton(attendanceButton);
        attendanceButton.addActionListener(e -> {
            if (clasesTable.getSelectedRow() >= 0) {
                int idClase = (int) modelo.getValueAt(clasesTable.getSelectedRow(), 0);
                registrarAsistencia(idClase);
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una clase de la tabla", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        bottomPanel.add(attendanceButton);

        JButton newClassButton = new JButton("➕ Nueva Clase");
        UIStyles.styleSuccessButton(newClassButton);
        newClassButton.addActionListener(e -> nuevaClase());
        bottomPanel.add(newClassButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void registrarAsistencia(int idClase) {
        JOptionPane.showMessageDialog(this, "Registro de asistencia para clase " + idClase + " en desarrollo", "Información", JOptionPane.INFORMATION_MESSAGE);
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
                Entrenador entrenadorActual = (Entrenador) usuarioActual;
                
                Clase clase = new Clase();
                clase.setNombre(txtNombre.getText().trim());
                clase.setDescripcion(txtDescripcion.getText().trim());
                clase.setIdEntrenador(entrenadorActual.getIdEntrenador());
                clase.setNombreEntrenador(entrenadorActual.getNombreCompleto());
                clase.setDiaSemana((String) comboDia.getSelectedItem());
                clase.setHorarioInicio(LocalTime.parse(txtHoraInicio.getText().trim()));
                clase.setHorarioFin(LocalTime.parse(txtHoraFin.getText().trim()));
                clase.setCapacidadMaxima(Integer.parseInt(txtCapacidad.getText().trim()));
                clase.setActiva(true);

                if (clase.getNombre().isEmpty() || clase.getDescripcion().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Completa nombre y descripción", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                entrenadorController.crearClase(clase);
                JOptionPane.showMessageDialog(dialog, "Clase creada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                
                // Recargar el dashboard
                dispose();
                new EntrenadorDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Revisa los datos. Usa formato HH:mm en los horarios.", "Error", JOptionPane.ERROR_MESSAGE);
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

    private JPanel createProfileTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIStyles.PRIMARY_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.anchor = GridBagConstraints.WEST;

        addInfoRow(panel, "👤 Nombre:", usuarioActual.getNombre(), 0, gbc);
        addInfoRow(panel, "👤 Apellido:", usuarioActual.getApellido(), 1, gbc);
        addInfoRow(panel, "📧 Email:", usuarioActual.getEmail(), 2, gbc);
        addInfoRow(panel, "📱 Teléfono:", usuarioActual.getTelefono() != null ? usuarioActual.getTelefono() : "N/A", 3, gbc);

        return panel;
    }

    private void addInfoRow(JPanel panel, String label, String value, int row, GridBagConstraints gbc) {
        JLabel labelComp = new JLabel(label);
        UIStyles.styleLabel(labelComp, true);
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(labelComp, gbc);

        JLabel valueComp = new JLabel(value);
        valueComp.setForeground(UIStyles.TEXT_PRIMARY);
        valueComp.setFont(UIStyles.FONT_LABEL);
        gbc.gridx = 1;
        panel.add(valueComp, gbc);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Confirmas que deseas cerrar sesión?", 
            "Cerrar Sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            authController.logout();
            dispose();
            new LoginView();
        }
    }
}
