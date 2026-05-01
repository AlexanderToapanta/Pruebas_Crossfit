package com.ironcladbox.view;

import com.ironcladbox.controller.AuthController;
import com.ironcladbox.model.Rol;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;
    private AuthController authController;

    public LoginView() {
        authController = AuthController.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("IroncladBox CrossFit - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(26, 26, 26));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 30, 12, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        JLabel titleLabel = new JLabel("IRONCLADBOX");
        titleLabel.setFont(new Font("Bebas Neue", Font.BOLD, 36));
        titleLabel.setForeground(new Color(230, 126, 34));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Inicia sesión en tu cuenta");
        subtitleLabel.setFont(new Font("Montserrat", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(new Color(230, 126, 34));
        emailLabel.setFont(new Font("Montserrat", Font.BOLD, 12));
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(emailLabel, gbc);

        emailField = new JTextField();
        emailField.setFont(new Font("Montserrat", Font.PLAIN, 12));
        emailField.setPreferredSize(new Dimension(400, 35));
        emailField.setBackground(new Color(45, 45, 45));
        emailField.setForeground(new Color(220, 220, 220));
        emailField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(emailField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setForeground(new Color(230, 126, 34));
        passwordLabel.setFont(new Font("Montserrat", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Montserrat", Font.PLAIN, 12));
        passwordField.setPreferredSize(new Dimension(400, 35));
        passwordField.setBackground(new Color(45, 45, 45));
        passwordField.setForeground(new Color(220, 220, 220));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        mainPanel.add(passwordField, gbc);

        // Message Label
        messageLabel = new JLabel();
        messageLabel.setForeground(new Color(220, 20, 60));
        messageLabel.setFont(new Font("Montserrat", Font.PLAIN, 11));
        gbc.gridy = 6;
        mainPanel.add(messageLabel, gbc);

        // Login Button
        loginButton = new JButton("INICIAR SESIÓN");
        loginButton.setFont(new Font("Montserrat", Font.BOLD, 13));
        loginButton.setBackground(new Color(230, 126, 34));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(400, 40));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(new LoginActionListener());
        gbc.gridy = 7;
        mainPanel.add(loginButton, gbc);

        // Register Button
        registerButton = new JButton("¿Sin cuenta? Regístrate aquí");
        registerButton.setFont(new Font("Montserrat", Font.PLAIN, 11));
        registerButton.setBackground(new Color(50, 50, 50));
        registerButton.setForeground(new Color(230, 126, 34));
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(400, 35));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> openRegisterView());
        gbc.gridy = 8;
        mainPanel.add(registerButton, gbc);

        add(mainPanel);
        setVisible(true);
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Por favor completa todos los campos");
                return;
            }

            if (authController.login(email, password)) {
                Rol rol = authController.getRolActual();
                dispose();
                openDashboard(rol);
            } else {
                messageLabel.setText("Email o contraseña incorrectos");
                passwordField.setText("");
            }
        }
    }

    private void openDashboard(Rol rol) {
        SwingUtilities.invokeLater(() -> {
            switch (rol) {
                case ADMINISTRADOR -> new AdminDashboard();
                case ENTRENADOR -> new EntrenadorDashboard();
                case ATLETA -> new AtletaDashboard();
            }
        });
    }

    private void openRegisterView() {
        dispose();
        new RegisterView();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView());
    }
}
