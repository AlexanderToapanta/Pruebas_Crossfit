package com.ironcladbox.view;

import com.ironcladbox.controller.AuthController;
import com.ironcladbox.controller.AtletaController;
import com.ironcladbox.model.Usuario;
import com.ironcladbox.model.Clase;
import com.ironcladbox.model.Suscripcion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AtletaDashboard extends JFrame {
    private Usuario usuarioActual;
    private AuthController authController;
    private AtletaController atletaController;
    private JTable clasesTable;
    private JLabel suscripcionLabel;
    private JLabel imcLabel;

    public AtletaDashboard() {
        authController = AuthController.getInstance();
        atletaController = new AtletaController();
        usuarioActual = authController.getUsuarioActual();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("IroncladBox CrossFit - Dashboard Atleta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(26, 26, 26));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(40, 40, 40));

        JLabel titleLabel = new JLabel("IRONCLADBOX - " + usuarioActual.getNombreCompleto());
        titleLabel.setFont(new Font("Bebas Neue", Font.BOLD, 24));
        titleLabel.setForeground(new Color(230, 126, 34));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.setFont(new Font("Montserrat", Font.BOLD, 11));
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(26, 26, 26));
        tabbedPane.setForeground(new Color(230, 126, 34));

        // Tab 1: Información personal
        tabbedPane.addTab("Mi Perfil", createProfileTab());

        // Tab 2: Clases disponibles
        tabbedPane.addTab("Clases Disponibles", createClasesTab());

        // Tab 3: Mi suscripción
        tabbedPane.addTab("Mi Suscripción", createSuscripcionTab());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createProfileTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(26, 26, 26));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        addInfoRow(panel, "Nombre:", usuarioActual.getNombre(), 0, gbc);
        addInfoRow(panel, "Apellido:", usuarioActual.getApellido(), 1, gbc);
        addInfoRow(panel, "Email:", usuarioActual.getEmail(), 2, gbc);
        addInfoRow(panel, "Teléfono:", usuarioActual.getTelefono(), 3, gbc);

        imcLabel = new JLabel("IMC: Calculando...");
        imcLabel.setForeground(new Color(200, 200, 200));
        imcLabel.setFont(new Font("Montserrat", Font.PLAIN, 12));
        gbc.gridy = 4;
        panel.add(imcLabel, gbc);

        JButton updateButton = new JButton("Actualizar Perfil");
        updateButton.setBackground(new Color(230, 126, 34));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        gbc.gridy = 5;
        panel.add(updateButton, gbc);

        return panel;
    }

    private JPanel createClasesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(26, 26, 26));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"ID", "Nombre", "Entrenador", "Día", "Horario", "Capacidad"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        List<Clase> clases = atletaController.obtenerClasesDisponibles();
        for (Clase clase : clases) {
            modelo.addRow(new Object[]{
                clase.getIdClase(),
                clase.getNombre(),
                clase.getNombreEntrenador(),
                clase.getDiaSemana(),
                clase.getHorarioInicio() + " - " + clase.getHorarioFin(),
                clase.getCapacidadMaxima()
            });
        }

        clasesTable = new JTable(modelo);
        clasesTable.setBackground(new Color(40, 40, 40));
        clasesTable.setForeground(new Color(200, 200, 200));
        clasesTable.setGridColor(new Color(60, 60, 60));
        JScrollPane scrollPane = new JScrollPane(clasesTable);
        scrollPane.setBackground(new Color(26, 26, 26));

        panel.add(scrollPane, BorderLayout.CENTER);

        JButton registerButton = new JButton("Registrarse a Clase");
        registerButton.setBackground(new Color(230, 126, 34));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Función en desarrollo"));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(26, 26, 26));
        bottomPanel.add(registerButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSuscripcionTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(26, 26, 26));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        Suscripcion suscripcion = atletaController.obtenerSuscripcionActiva(usuarioActual.getIdUsuario());

        if (suscripcion != null) {
            addInfoRow(panel, "Membresía:", suscripcion.getNombreMembresia(), 0, gbc);
            addInfoRow(panel, "Precio:", "$" + suscripcion.getPrecioMembresia(), 1, gbc);
            addInfoRow(panel, "Inicio:", suscripcion.getFechaInicio().toString(), 2, gbc);
            addInfoRow(panel, "Vencimiento:", suscripcion.getFechaFin().toString(), 3, gbc);
            addInfoRow(panel, "Estado:", suscripcion.isVigente() ? "✓ Vigente" : "✗ Vencida", 4, gbc);
        } else {
            suscripcionLabel = new JLabel("No tienes suscripción activa");
            suscripcionLabel.setForeground(new Color(220, 20, 60));
            suscripcionLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
            gbc.gridy = 0;
            panel.add(suscripcionLabel, gbc);
        }

        JButton renewButton = new JButton("Renovar o Cambiar Membresía");
        renewButton.setBackground(new Color(230, 126, 34));
        renewButton.setForeground(Color.WHITE);
        renewButton.setFocusPainted(false);
        gbc.gridy = 5;
        panel.add(renewButton, gbc);

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
        valueComp.setFont(new Font("Montserrat", Font.PLAIN, 12));
        gbc.gridx = 1;
        panel.add(valueComp, gbc);
    }

    private void logout() {
        authController.logout();
        dispose();
        new LoginView();
    }
}
