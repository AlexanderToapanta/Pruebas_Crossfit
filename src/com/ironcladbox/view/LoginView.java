package com.ironcladbox.view;

import com.ironcladbox.controller.AuthController;
import com.ironcladbox.model.Rol;
import com.ironcladbox.util.UIStyles;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

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
        setSize(600, 680);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(UIStyles.PRIMARY_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 30, 20, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Panel decorativo para el logo
        JPanel logoPanelContainer = new JPanel();
        logoPanelContainer.setLayout(new BoxLayout(logoPanelContainer, BoxLayout.Y_AXIS));
        logoPanelContainer.setBackground(UIStyles.SECONDARY_DARK);
        logoPanelContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/com/ironcladbox/images/logo.jpeg"));
            Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(img));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoPanelContainer.add(logoLabel);
        } catch (Exception e) {
            // Si no encuentra la imagen, muestra placeholder
            JLabel placeholderLabel = new JLabel("🏋️ LOGO");
            placeholderLabel.setFont(new Font("Arial", Font.BOLD, 30));
            placeholderLabel.setForeground(UIStyles.ACCENT_RED);
            placeholderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoPanelContainer.add(placeholderLabel);
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 30, 10, 30);
        mainPanel.add(logoPanelContainer, gbc);
        gbc.insets = new Insets(20, 30, 20, 30);

        JLabel titleLabel = new JLabel("IRONCLADBOX");
        titleLabel.setFont(UIStyles.FONT_TITLE);
        titleLabel.setForeground(UIStyles.ACCENT_RED);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel subtitleLabel = new JLabel("Inicia sesión en tu cuenta");
        subtitleLabel.setFont(UIStyles.FONT_LABEL);
        subtitleLabel.setForeground(UIStyles.TEXT_SECONDARY);
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 30, 20, 30);
        mainPanel.add(subtitleLabel, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 30, 20, 30);

        // Email
        JLabel emailLabel = new JLabel("📧 Email:");
        UIStyles.styleLabel(emailLabel, true);
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 30, 5, 30);
        mainPanel.add(emailLabel, gbc);

        emailField = new JTextField();
        UIStyles.styleTextField(emailField);
        emailField.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 30, 15, 30);
        mainPanel.add(emailField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("🔐 Contraseña:");
        UIStyles.styleLabel(passwordLabel, true);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 30, 5, 30);
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setBackground(UIStyles.SECONDARY_DARK);
        passwordField.setForeground(UIStyles.TEXT_PRIMARY);
        passwordField.setCaretColor(UIStyles.ACCENT_RED);
        passwordField.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 2));
        passwordField.setFont(UIStyles.FONT_LABEL);
        passwordField.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 30, 15, 30);
        mainPanel.add(passwordField, gbc);

        // Message Label
        messageLabel = new JLabel();
        messageLabel.setForeground(UIStyles.DANGER_RED);
        messageLabel.setFont(UIStyles.FONT_SMALL);
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 30, 15, 30);
        mainPanel.add(messageLabel, gbc);

        // Login Button
        loginButton = new JButton("🔓 INICIAR SESIÓN");
        UIStyles.stylePrimaryButton(loginButton);
        loginButton.setPreferredSize(new Dimension(400, 50));
        loginButton.setFont(new Font("Montserrat", Font.BOLD, 14));
        loginButton.addActionListener(new LoginActionListener());
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 30, 10, 30);
        mainPanel.add(loginButton, gbc);

        // Register Button
        registerButton = new JButton("¿Sin cuenta? Regístrate aquí");
        UIStyles.styleSecondaryButton(registerButton);
        registerButton.setPreferredSize(new Dimension(400, 40));
        registerButton.setFont(new Font("Montserrat", Font.PLAIN, 12));
        registerButton.addActionListener(e -> openRegisterView());
        gbc.gridy = 9;
        gbc.insets = new Insets(5, 30, 20, 30);
        mainPanel.add(registerButton, gbc);

        // Volver al Inicio
        JButton backBtn = new JButton("Volver al Inicio");
        backBtn.setFont(new Font("Montserrat", Font.PLAIN, 12));
        backBtn.setForeground(Color.GRAY);
        backBtn.setBorder(null);
        backBtn.setContentAreaFilled(false);
        backBtn.addActionListener(e -> { dispose(); new LandingView(); });
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 30, 20, 30);
        mainPanel.add(backBtn, gbc);

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

            boolean ok = authController.login(email, password);
            if (ok) {
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
        if (rol == null) {
            messageLabel.setText("Error: No se pudo determinar el rol del usuario");
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            switch (rol) {
                case ADMINISTRADOR -> new AdminDashboard();
                case ENTRENADOR -> new EntrenadorDashboard();
                case ATLETA -> new AtletaDashboard();
                default -> messageLabel.setText("Error: Rol desconocido");
            }
        });
    }

    private void openRegisterView() {
        dispose();
        new RegisterView();
    }

    public static void main(String[] args) {
        java.util.Locale.setDefault(new java.util.Locale("es", "ES"));
        SwingUtilities.invokeLater(() -> new LoginView());
    }
}
