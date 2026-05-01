package com.ironcladbox.view;

import com.ironcladbox.controller.AuthController;
import com.ironcladbox.controller.EntrenadorController;
import com.ironcladbox.model.Usuario;
import com.ironcladbox.model.Clase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EntrenadorDashboard extends JFrame {
    private Usuario usuarioActual;
    private AuthController authController;
    private EntrenadorController entrenadorController;
    private JTable clasesTable;

    public EntrenadorDashboard() {
        authController = AuthController.getInstance();
        entrenadorController = new EntrenadorController();
        usuarioActual = authController.getUsuarioActual();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("IroncladBox CrossFit - Dashboard Entrenador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(26, 26, 26));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(40, 40, 40));

        JLabel titleLabel = new JLabel("ENTRENADOR - " + usuarioActual.getNombreCompleto());
        titleLabel.setFont(new Font("Bebas Neue", Font.BOLD, 24));
        titleLabel.setForeground(new Color(230, 126, 34));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(26, 26, 26));
        tabbedPane.setForeground(new Color(230, 126, 34));

        tabbedPane.addTab("Mis Clases", createClassesTab());
        tabbedPane.addTab("Mi Perfil", createProfileTab());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createClassesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(26, 26, 26));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"ID", "Nombre", "Día", "Horario", "Capacidad", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        List<Clase> clases = entrenadorController.obtenerMisClases(usuarioActual.getIdUsuario());
        for (Clase clase : clases) {
            modelo.addRow(new Object[]{
                clase.getIdClase(),
                clase.getNombre(),
                clase.getDiaSemana(),
                clase.getHorarioInicio() + " - " + clase.getHorarioFin(),
                clase.getCapacidadMaxima(),
                clase.isActiva() ? "Activa" : "Inactiva"
            });
        }

        clasesTable = new JTable(modelo);
        clasesTable.setBackground(new Color(40, 40, 40));
        clasesTable.setForeground(new Color(200, 200, 200));
        clasesTable.setGridColor(new Color(60, 60, 60));
        JScrollPane scrollPane = new JScrollPane(clasesTable);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(26, 26, 26));

        JButton attendanceButton = new JButton("Registrar Asistencia");
        attendanceButton.setBackground(new Color(230, 126, 34));
        attendanceButton.setForeground(Color.WHITE);
        attendanceButton.setFocusPainted(false);
        attendanceButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Función en desarrollo"));
        bottomPanel.add(attendanceButton);

        JButton newClassButton = new JButton("Nueva Clase");
        newClassButton.setBackground(new Color(50, 205, 50));
        newClassButton.setForeground(Color.WHITE);
        newClassButton.setFocusPainted(false);
        newClassButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Función en desarrollo"));
        bottomPanel.add(newClassButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createProfileTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(26, 26, 26));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        addInfoRow(panel, "Nombre:", usuarioActual.getNombre(), 0, gbc);
        addInfoRow(panel, "Apellido:", usuarioActual.getApellido(), 1, gbc);
        addInfoRow(panel, "Email:", usuarioActual.getEmail(), 2, gbc);
        addInfoRow(panel, "Teléfono:", usuarioActual.getTelefono(), 3, gbc);

        return panel;
    }

    private void addInfoRow(JPanel panel, String label, String value, int row, GridBagConstraints gbc) {
        JLabel labelComp = new JLabel(label);
        labelComp.setForeground(new Color(230, 126, 34));
        labelComp.setFont(new Font("Montserrat", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(labelComp, gbc);

        JLabel valueComp = new JLabel(value);
        valueComp.setForeground(new Color(200, 200, 200));
        gbc.gridx = 1;
        panel.add(valueComp, gbc);
    }

    private void logout() {
        authController.logout();
        dispose();
        new LoginView();
    }
}
