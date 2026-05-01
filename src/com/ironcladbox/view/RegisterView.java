package com.ironcladbox.view;

import com.ironcladbox.controller.AuthController;
import com.ironcladbox.dao.IMembresiaDAO;
import com.ironcladbox.dao.MembresiaDAO;
import com.ironcladbox.model.Atleta;
import com.ironcladbox.model.Membresia;

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
    private IMembresiaDAO membresiaDAO;
    private List<Membresia> membresias;

    public RegisterView() {
        authController = AuthController.getInstance();
        membresiaDAO = new MembresiaDAO();
        membresias = membresiaDAO.obtenerActivas();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("IroncladBox CrossFit - Registro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(26, 26, 26));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        JLabel titleLabel = new JLabel("IRONCLADBOX");
        titleLabel.setFont(new Font("Bebas Neue", Font.BOLD, 28));
        titleLabel.setForeground(new Color(230, 126, 34));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Crea tu cuenta");
        subtitleLabel.setFont(new Font("Montserrat", Font.PLAIN, 11));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        gbc.gridwidth = 1;
        int row = 2;

        // Nombre
        addLabeledField(mainPanel, "Nombre:", nombreField = new JTextField(20), gbc, row++);
        // Apellido
        addLabeledField(mainPanel, "Apellido:", apellidoField = new JTextField(20), gbc, row++);
        // Email
        addLabeledField(mainPanel, "Email:", emailField = new JTextField(20), gbc, row++);
        // Teléfono
        addLabeledField(mainPanel, "Teléfono:", telefonoField = new JTextField(20), gbc, row++);
        // Contraseña
        addLabeledField(mainPanel, "Contraseña:", passwordField = new JPasswordField(20), gbc, row++);

        // Peso
        pesoSpinner = new JSpinner(new SpinnerNumberModel(70.0, 30.0, 200.0, 0.1));
        addLabeledField(mainPanel, "Peso (kg):", pesoSpinner, gbc, row++);

        // Altura
        alturaSpinner = new JSpinner(new SpinnerNumberModel(1.70, 1.40, 2.20, 0.01));
        addLabeledField(mainPanel, "Altura (m):", alturaSpinner, gbc, row++);

        // Membresía
        membresiaCombo = new JComboBox<>();
        for (Membresia m : membresias) {
            membresiaCombo.addItem(m.getNombre() + " - $" + m.getPrecio());
        }
        addLabeledField(mainPanel, "Membresía:", membresiaCombo, gbc, row++);

        // Message
        messageLabel = new JLabel();
        messageLabel.setForeground(new Color(220, 20, 60));
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        mainPanel.add(messageLabel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(26, 26, 26));

        registerButton = new JButton("REGISTRARSE");
        registerButton.setFont(new Font("Montserrat", Font.BOLD, 12));
        registerButton.setBackground(new Color(230, 126, 34));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(new RegisterActionListener());
        buttonPanel.add(registerButton);

        backButton = new JButton("Volver al Login");
        backButton.setFont(new Font("Montserrat", Font.PLAIN, 11));
        backButton.setBackground(new Color(50, 50, 50));
        backButton.setForeground(new Color(230, 126, 34));
        backButton.setFocusPainted(false);
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
        lbl.setForeground(new Color(230, 126, 34));
        lbl.setFont(new Font("Montserrat", Font.BOLD, 11));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        panel.add(lbl, gbc);

        if (field instanceof JTextField) {
            ((JTextField) field).setPreferredSize(new Dimension(300, 30));
            ((JTextField) field).setBackground(new Color(45, 45, 45));
            ((JTextField) field).setForeground(new Color(220, 220, 220));
            ((JTextField) field).setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        } else if (field instanceof JPasswordField) {
            ((JPasswordField) field).setPreferredSize(new Dimension(300, 30));
            ((JPasswordField) field).setBackground(new Color(45, 45, 45));
            ((JPasswordField) field).setForeground(new Color(220, 220, 220));
            ((JPasswordField) field).setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        } else if (field instanceof JSpinner) {
            ((JSpinner) field).setPreferredSize(new Dimension(300, 30));
        } else if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setPreferredSize(new Dimension(300, 30));
        }

        gbc.gridx = 1;
        gbc.weightx = 0.8;
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
