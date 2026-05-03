package com.ironcladbox.view;

import com.ironcladbox.controller.AuthController;
import com.ironcladbox.controller.AtletaController;
import com.ironcladbox.util.UIStyles;
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
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIStyles.PRIMARY_DARK);

        // Header
        JButton logoutButton = new JButton("🚪 Cerrar Sesión");
        UIStyles.styleDangerButton(logoutButton);
        logoutButton.addActionListener(e -> logout());
        
        JPanel headerPanel = UIStyles.createHeaderPanel("💪 ATLETA - " + usuarioActual.getNombreCompleto(), logoutButton);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(UIStyles.PRIMARY_DARK);
        tabbedPane.setForeground(UIStyles.ACCENT_RED);

        // Tab 1: Información personal
        tabbedPane.addTab("👤 Mi Perfil", createProfileTab());

        // Tab 2: Clases disponibles
        tabbedPane.addTab("📚 Clases Disponibles", createClasesTab());

        // Tab 3: Mi suscripción
        tabbedPane.addTab("💳 Mi Suscripción", createSuscripcionTab());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
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

        imcLabel = new JLabel("IMC: Calculando...");
        imcLabel.setForeground(UIStyles.ACCENT_RED);
        imcLabel.setFont(UIStyles.FONT_LABEL);
        gbc.gridy = 4;
        panel.add(imcLabel, gbc);

        JButton updateButton = new JButton("✏️ Actualizar Perfil");
        UIStyles.stylePrimaryButton(updateButton);
        gbc.gridy = 5;
        panel.add(updateButton, gbc);

        return panel;
    }

    private JPanel createClasesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.PRIMARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"ID", "Nombre", "Entrenador", "Día", "Horario", "Capacidad"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

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

        JTable clasesTable = new JTable(modelo);
        UIStyles.styleTable(clasesTable);
        
        JScrollPane scrollPane = new JScrollPane(clasesTable);
        scrollPane.setBackground(UIStyles.SECONDARY_DARK);
        scrollPane.getViewport().setBackground(UIStyles.SECONDARY_DARK);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton registerButton = new JButton("🎯 Registrarse a Clase");
        UIStyles.stylePrimaryButton(registerButton);
        registerButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Función en desarrollo"));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(UIStyles.PRIMARY_DARK);
        bottomPanel.add(registerButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSuscripcionTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIStyles.PRIMARY_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.anchor = GridBagConstraints.WEST;

        Suscripcion suscripcion = atletaController.obtenerSuscripcionActiva(usuarioActual.getIdUsuario());

        if (suscripcion != null) {
            addInfoRow(panel, "💳 Membresía:", suscripcion.getNombreMembresia(), 0, gbc);
            addInfoRow(panel, "💰 Precio:", "$" + suscripcion.getPrecioMembresia(), 1, gbc);
            addInfoRow(panel, "📅 Inicio:", suscripcion.getFechaInicio().toString(), 2, gbc);
            addInfoRow(panel, "📅 Vencimiento:", suscripcion.getFechaFin().toString(), 3, gbc);
            
            String estado = suscripcion.isVigente() ? "✓ Vigente" : "✗ Vencida";
            Color estadoColor = suscripcion.isVigente() ? UIStyles.SUCCESS_GREEN : UIStyles.DANGER_RED;
            addInfoRowWithColor(panel, "Estado:", estado, estadoColor, 4, gbc);
        } else {
            suscripcionLabel = new JLabel("❌ No tienes suscripción activa");
            suscripcionLabel.setForeground(UIStyles.DANGER_RED);
            suscripcionLabel.setFont(UIStyles.FONT_SUBTITLE);
            gbc.gridy = 0;
            panel.add(suscripcionLabel, gbc);
        }

        JButton renewButton = new JButton("🔄 Renovar o Cambiar Membresía");
        UIStyles.styleSuccessButton(renewButton);
        gbc.gridy = 5;
        panel.add(renewButton, gbc);

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
    
    private void addInfoRowWithColor(JPanel panel, String label, String value, Color valueColor, int row, GridBagConstraints gbc) {
        JLabel labelComp = new JLabel(label);
        UIStyles.styleLabel(labelComp, true);
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(labelComp, gbc);

        JLabel valueComp = new JLabel(value);
        valueComp.setForeground(valueColor);
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
