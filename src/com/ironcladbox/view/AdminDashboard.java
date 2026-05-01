package com.ironcladbox.view;

import com.ironcladbox.controller.AuthController;
import com.ironcladbox.controller.AdminController;
import com.ironcladbox.model.Usuario;
import com.ironcladbox.model.Atleta;
import com.ironcladbox.model.Entrenador;
import com.ironcladbox.model.Clase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private Usuario usuarioActual;
    private AuthController authController;
    private AdminController adminController;
    private JLabel statsPanel;

    public AdminDashboard() {
        authController = AuthController.getInstance();
        adminController = new AdminController();
        usuarioActual = authController.getUsuarioActual();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("IroncladBox CrossFit - Panel de Administración");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(26, 26, 26));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(40, 40, 40));

        JLabel titleLabel = new JLabel("ADMINISTRADOR - " + usuarioActual.getNombreCompleto());
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

        // Stats Panel
        mainPanel.add(createStatsPanel(), BorderLayout.BEFORE_FIRST_LINE);

        // Content Panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(26, 26, 26));
        tabbedPane.setForeground(new Color(230, 126, 34));

        tabbedPane.addTab("Atletas", createAtletasTab());
        tabbedPane.addTab("Entrenadores", createEntrenoresTab());
        tabbedPane.addTab("Clases", createClassesTab());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 15));
        panel.setBackground(new Color(40, 40, 40));

        JLabel atletasLbl = new JLabel("Atletas: " + adminController.getTotalAtletas());
        atletasLbl.setFont(new Font("Montserrat", Font.BOLD, 13));
        atletasLbl.setForeground(new Color(230, 126, 34));
        panel.add(atletasLbl);

        JLabel entrenadoresLbl = new JLabel("Entrenadores: " + adminController.getTotalEntrenadores());
        entrenadoresLbl.setFont(new Font("Montserrat", Font.BOLD, 13));
        entrenadoresLbl.setForeground(new Color(230, 126, 34));
        panel.add(entrenadoresLbl);

        JLabel clasesLbl = new JLabel("Clases: " + adminController.getTotalClases());
        clasesLbl.setFont(new Font("Montserrat", Font.BOLD, 13));
        clasesLbl.setForeground(new Color(230, 126, 34));
        panel.add(clasesLbl);

        return panel;
    }

    private JPanel createAtletasTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(26, 26, 26));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"ID", "Nombre", "Email", "Teléfono", "Peso (kg)", "Altura (m)"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        List<Atleta> atletas = adminController.obtenerTodosAtletas();
        for (Atleta atleta : atletas) {
            modelo.addRow(new Object[]{
                atleta.getIdAtleta(),
                atleta.getNombreCompleto(),
                atleta.getEmail(),
                atleta.getTelefono(),
                atleta.getPeso(),
                atleta.getAltura()
            });
        }

        JTable table = new JTable(modelo);
        table.setBackground(new Color(40, 40, 40));
        table.setForeground(new Color(200, 200, 200));
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEntrenoresTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(26, 26, 26));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"ID", "Nombre", "Email", "Certificación", "Especialidad", "Experiencia"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        List<Entrenador> entrenadores = adminController.obtenerTodosEntrenadores();
        for (Entrenador entrenador : entrenadores) {
            modelo.addRow(new Object[]{
                entrenador.getIdEntrenador(),
                entrenador.getNombreCompleto(),
                entrenador.getEmail(),
                entrenador.getCertificacion(),
                entrenador.getEspecialidad(),
                entrenador.getExperienciaAnios() + " años"
            });
        }

        JTable table = new JTable(modelo);
        table.setBackground(new Color(40, 40, 40));
        table.setForeground(new Color(200, 200, 200));
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createClassesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(26, 26, 26));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"ID", "Nombre", "Entrenador", "Día", "Horario", "Capacidad"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        List<Clase> clases = adminController.obtenerClases();
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

        JTable table = new JTable(modelo);
        table.setBackground(new Color(40, 40, 40));
        table.setForeground(new Color(200, 200, 200));
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(26, 26, 26));
        JButton newClassButton = new JButton("Nueva Clase");
        newClassButton.setBackground(new Color(50, 205, 50));
        newClassButton.setForeground(Color.WHITE);
        newClassButton.setFocusPainted(false);
        newClassButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Función en desarrollo"));
        bottomPanel.add(newClassButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void logout() {
        authController.logout();
        dispose();
        new LoginView();
    }
}
