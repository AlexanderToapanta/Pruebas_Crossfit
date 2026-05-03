package com.ironcladbox.view;

import com.ironcladbox.controller.AuthController;
import com.ironcladbox.controller.AtletaController;
import com.ironcladbox.util.UIStyles;
import com.ironcladbox.model.Usuario;
import com.ironcladbox.model.Atleta;
import com.ironcladbox.model.Clase;
import com.ironcladbox.model.Suscripcion;
import com.ironcladbox.model.Membresia;
import com.ironcladbox.dao.AtletaDAO;
import com.ironcladbox.dao.IAtletaDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AtletaDashboard extends JFrame {
    private Usuario usuarioActual;
    private Atleta atletaActual;
    private AuthController authController;
    private AtletaController atletaController;
    private IAtletaDAO atletaDAO;
    private JLabel suscripcionLabel;
    private JLabel imcLabel;

    public AtletaDashboard() {
        authController = AuthController.getInstance();
        atletaController = new AtletaController();
        atletaDAO = new AtletaDAO();
        usuarioActual = authController.getUsuarioActual();
        atletaActual = atletaDAO.obtenerPorIdUsuario(usuarioActual.getIdUsuario());
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

        Suscripcion suscripcion = atletaActual != null ?
                atletaController.obtenerSuscripcionActiva(atletaActual.getIdAtleta()) : null;

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
        renewButton.addActionListener(e -> mostrarDialogoRenovacion());
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

    private void mostrarDialogoRenovacion() {
        if (atletaActual == null) {
            JOptionPane.showMessageDialog(this, "Error: No se pudo obtener información del atleta", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Suscripcion suscripcionActual = atletaController.obtenerSuscripcionActiva(atletaActual.getIdAtleta());

        if (suscripcionActual == null) {
            JOptionPane.showMessageDialog(this, "No tienes una suscripción activa para renovar", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Renovar o Cambiar Membresía", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UIStyles.PRIMARY_DARK);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIStyles.PRIMARY_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblActual = new JLabel("Membresía Actual:");
        lblActual.setForeground(UIStyles.ACCENT_RED);
        JLabel txtActual = new JLabel(suscripcionActual.getNombreMembresia() + " - $" + String.format("%.2f", suscripcionActual.getPrecioMembresia()));
        txtActual.setForeground(UIStyles.SUCCESS_GREEN);

        JLabel lblNueva = new JLabel("Nueva Membresía:");
        lblNueva.setForeground(UIStyles.ACCENT_RED);
        List<Membresia> membresias = atletaController.obtenerMembresiasCambio();
        JComboBox<Membresia> comboMembresia = new JComboBox<>(membresias.toArray(new Membresia[0]));

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblActual, gbc);
        gbc.gridx = 1; formPanel.add(txtActual, gbc);
        gbc.gridx = 0; gbc.gridy = y++; formPanel.add(lblNueva, gbc);
        gbc.gridx = 1; formPanel.add(comboMembresia, gbc);

        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.getViewport().setBackground(UIStyles.PRIMARY_DARK);
        dialog.add(scrollForm, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UIStyles.SECONDARY_DARK);

        JButton btnRenovar = new JButton("✓ Renovar");
        UIStyles.styleSuccessButton(btnRenovar);
        btnRenovar.addActionListener(e -> {
            Membresia membresiaSeleccionada = (Membresia) comboMembresia.getSelectedItem();

            if (membresiaSeleccionada == null) {
                JOptionPane.showMessageDialog(dialog, "Selecciona una membresía", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (membresiaSeleccionada.getIdMembresia() == suscripcionActual.getIdMembresia()) {
                JOptionPane.showMessageDialog(dialog, "Selecciona una membresía diferente", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            atletaController.renovarMembresia(atletaActual.getIdAtleta(), membresiaSeleccionada.getIdMembresia());
            JOptionPane.showMessageDialog(dialog, "Membresía renovada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            dispose();
            new AtletaDashboard();
        });

        JButton btnCancelar = new JButton("✗ Cancelar");
        UIStyles.styleDangerButton(btnCancelar);
        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnRenovar);
        buttonPanel.add(btnCancelar);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
