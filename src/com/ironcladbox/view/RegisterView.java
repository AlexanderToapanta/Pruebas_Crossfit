package com.ironcladbox.view;

import com.ironcladbox.controller.AuthController;
import com.ironcladbox.util.UIStyles;
import com.ironcladbox.service.MembershipApiService;
import com.ironcladbox.dto.ApiResponse;
import com.ironcladbox.model.Atleta;
import com.ironcladbox.model.Membresia;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RegisterView extends JFrame {
    private JTextField nombreField;
    private JTextField apellidoField;
    private JTextField emailField;
    private JTextField telefonoField;
    private JPasswordField passwordField;
    private JSpinner pesoSpinner;
    private JSpinner alturaSpinner;
    private JComboBox<String> membresiaCombo;
    private JButton registerButton;
    private JButton backButton;
    private JLabel messageLabel;
    private AuthController authController;
    private MembershipApiService membershipService;
    private List<Membresia> membresias;

    public RegisterView() {
        authController = AuthController.getInstance();
        membershipService = MembershipApiService.getInstance();
        membresias = new java.util.ArrayList<>();
        try {
            ApiResponse resp = membershipService.getAvailable();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    if (e.isJsonObject()) {
                        JsonObject json = e.getAsJsonObject();
                        Membresia m = new Membresia();
                        m.setIdMembresia(json.has("id_membresia") ? json.get("id_membresia").getAsInt() : 0);
                        m.setNombre(json.has("nombre") ? json.get("nombre").getAsString() : "");
                        m.setPrecio(json.has("precio") ? json.get("precio").getAsDouble() : 0);
                        m.setDuracionDias(json.has("duracion_dias") ? json.get("duracion_dias").getAsInt() : 30);
                        m.setActiva(true);
                        membresias.add(m);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initializeUI();
    }

    private void initializeUI() {
        setTitle("IroncladBox CrossFit - Registro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 750);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(UIStyles.PRIMARY_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        JLabel titleLabel = new JLabel("⚡ IRONCLADBOX");
        titleLabel.setFont(UIStyles.FONT_TITLE);
        titleLabel.setForeground(UIStyles.ACCENT_RED);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Crea tu cuenta de atleta");
        subtitleLabel.setFont(UIStyles.FONT_LABEL);
        subtitleLabel.setForeground(UIStyles.TEXT_SECONDARY);
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        gbc.gridwidth = 1;
        int row = 2;

        // Nombre
        addLabeledField(mainPanel, "👤 Nombre:", nombreField = new JTextField(20), gbc, row++);
        // Apellido
        addLabeledField(mainPanel, "👤 Apellido:", apellidoField = new JTextField(20), gbc, row++);
        // Email
        addLabeledField(mainPanel, "📧 Email:", emailField = new JTextField(20), gbc, row++);
        // Teléfono
        addLabeledField(mainPanel, "📱 Teléfono:", telefonoField = new JTextField(20), gbc, row++);
        // Contraseña
        addLabeledField(mainPanel, "🔐 Contraseña:", passwordField = new JPasswordField(20), gbc, row++);

        // Peso
        pesoSpinner = new JSpinner(new SpinnerNumberModel(70.0, 30.0, 200.0, 0.1));
        addLabeledField(mainPanel, "⚖️ Peso (kg):", pesoSpinner, gbc, row++);

        // Altura
        alturaSpinner = new JSpinner(new SpinnerNumberModel(1.70, 1.40, 2.20, 0.01));
        addLabeledField(mainPanel, "📏 Altura (m):", alturaSpinner, gbc, row++);

        // Membresía
        membresiaCombo = new JComboBox<>();
        for (Membresia m : membresias) {
            membresiaCombo.addItem(m.getNombre() + " - $" + m.getPrecio());
        }
        membresiaCombo.setPreferredSize(new Dimension(300, 30));
        addLabeledField(mainPanel, "💳 Membresía:", membresiaCombo, gbc, row++);

        // Message
        messageLabel = new JLabel();
        messageLabel.setForeground(UIStyles.DANGER_RED);
        messageLabel.setFont(UIStyles.FONT_SMALL);
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        mainPanel.add(messageLabel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIStyles.PRIMARY_DARK);

        registerButton = new JButton("REGISTRARSE");
        UIStyles.styleSuccessButton(registerButton);
        registerButton.addActionListener(new RegisterActionListener());
        buttonPanel.add(registerButton);

        backButton = new JButton("Volver al Login");
        UIStyles.styleSecondaryButton(backButton);
        backButton.addActionListener(e -> goBackToLogin());
        buttonPanel.add(backButton);

        gbc.gridy = row;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
        setVisible(true);
    }

    private void addLabeledField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        JLabel lbl = new JLabel(label);
        UIStyles.styleLabel(lbl, true);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        panel.add(lbl, gbc);

        if (field instanceof JTextField) {
            ((JTextField) field).setPreferredSize(new Dimension(300, 35));
            UIStyles.styleTextField((JTextField) field);
        } else if (field instanceof JPasswordField) {
            ((JPasswordField) field).setPreferredSize(new Dimension(300, 35));
            ((JPasswordField) field).setBackground(UIStyles.SECONDARY_DARK);
            ((JPasswordField) field).setForeground(UIStyles.TEXT_PRIMARY);
            ((JPasswordField) field).setCaretColor(UIStyles.ACCENT_RED);
            ((JPasswordField) field).setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 2));
            ((JPasswordField) field).setFont(UIStyles.FONT_LABEL);
        } else if (field instanceof JSpinner) {
            ((JSpinner) field).setPreferredSize(new Dimension(300, 35));
            ((JSpinner) field).setBackground(UIStyles.SECONDARY_DARK);
            ((JSpinner) field).setForeground(UIStyles.TEXT_PRIMARY);
        } else if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setBackground(UIStyles.SECONDARY_DARK);
            ((JComboBox<?>) field).setForeground(UIStyles.TEXT_PRIMARY);
        }

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private class RegisterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String nombre = nombreField.getText().trim();
            String apellido = apellidoField.getText().trim();
            String email = emailField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String password = new String(passwordField.getPassword());
            double peso = (double) pesoSpinner.getValue();
            double altura = (double) alturaSpinner.getValue();

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Completa los campos obligatorios");
                return;
            }

            Atleta atleta = new Atleta(email, password, nombre, apellido, telefono);
            atleta.setPeso(peso);
            atleta.setAltura(altura);

            if (authController.registrarAtleta(atleta)) {
                messageLabel.setForeground(new Color(50, 205, 50));
                messageLabel.setText("Registro exitoso. Inicia sesión ahora.");
                SwingUtilities.invokeLater(() -> {
                    try {
                        Thread.sleep(2000);
                        goBackToLogin();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                });
            } else {
                messageLabel.setForeground(new Color(220, 20, 60));
                messageLabel.setText("Error al registrar. Email puede estar en uso.");
            }
        }
    }

    private void goBackToLogin() {
        dispose();
        new LoginView();
    }
}
