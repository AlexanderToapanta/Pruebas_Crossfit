package com.ironcladbox.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Clase centralizada para estilos UI consistentes
 */
public class UIStyles {
    
    // Paleta de colores mejorada
    public static final Color PRIMARY_DARK = new Color(15, 15, 15);
    public static final Color SECONDARY_DARK = new Color(30, 30, 35);
    public static final Color ACCENT_RED = new Color(220, 20, 60);
    public static final Color ACCENT_RED_LIGHT = new Color(255, 65, 100);
    public static final Color ACCENT_BLUE = new Color(100, 100, 110);
    public static final Color SUCCESS_GREEN = new Color(60, 160, 80);
    public static final Color DANGER_RED = new Color(220, 20, 60);
    public static final Color TEXT_PRIMARY = new Color(240, 240, 245);
    public static final Color TEXT_SECONDARY = new Color(180, 180, 190);
    public static final Color BORDER_COLOR = new Color(60, 20, 30);
    public static final Color HOVER_COLOR = new Color(50, 15, 25);
    
    // Fuentes
    public static final Font FONT_TITLE = new Font("Bebas Neue", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("Montserrat", Font.BOLD, 18);
    public static final Font FONT_BUTTON = new Font("Montserrat", Font.BOLD, 12);
    public static final Font FONT_LABEL = new Font("Montserrat", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Montserrat", Font.PLAIN, 11);
    
    /**
     * Estiliza un botón primario (acción principal)
     */
    public static void stylePrimaryButton(JButton button) {
        button.setBackground(ACCENT_RED);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));
        addButtonHoverEffect(button, ACCENT_RED);
    }
    
    /**
     * Estiliza un botón secundario
     */
    public static void styleSecondaryButton(JButton button) {
        button.setBackground(SECONDARY_DARK);
        button.setForeground(ACCENT_RED);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(ACCENT_RED, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));
    }
    
    /**
     * Estiliza un botón de peligro (logout, delete)
     */
    public static void styleDangerButton(JButton button) {
        button.setBackground(DANGER_RED);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButtonHoverEffect(button, DANGER_RED);
    }
    
    /**
     * Estiliza un botón de éxito (guardar, confirmar)
     */
    public static void styleSuccessButton(JButton button) {
        button.setBackground(SUCCESS_GREEN);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButtonHoverEffect(button, SUCCESS_GREEN);
    }
    
    /**
     * Añade efecto de hover a un botón
     */
    private static void addButtonHoverEffect(JButton button, Color originalColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(brightenColor(originalColor, 30));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
    }
    
    /**
     * Aplica estilos a un JTable
     */
    public static void styleTable(JTable table) {
        table.setBackground(SECONDARY_DARK);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(ACCENT_RED);
        table.setSelectionForeground(PRIMARY_DARK);
        table.setRowHeight(28);
        table.getTableHeader().setBackground(new Color(40, 40, 50));
        table.getTableHeader().setForeground(ACCENT_RED);
        table.getTableHeader().setFont(FONT_BUTTON);
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setReorderingAllowed(false);
    }
    
    /**
     * Estiliza un JLabel
     */
    public static void styleLabel(JLabel label, boolean isPrimary) {
        label.setFont(isPrimary ? FONT_LABEL : FONT_SMALL);
        label.setForeground(isPrimary ? ACCENT_RED : TEXT_SECONDARY);
    }
    
    /**
     * Estiliza un campo de entrada de texto
     */
    public static void styleTextField(JTextField field) {
        field.setBackground(SECONDARY_DARK);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT_RED);
        field.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));
        field.setFont(FONT_LABEL);
        field.setPreferredSize(new Dimension(300, 35));
    }
    
    /**
     * Estiliza un panel con borde redondeado
     */
    public static JPanel createStyledPanel(Color backgroundColor) {
        JPanel panel = new JPanel();
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return panel;
    }
    
    /**
     * Aclara un color
     */
    private static Color brightenColor(Color color, int amount) {
        return new Color(
            Math.min(255, color.getRed() + amount),
            Math.min(255, color.getGreen() + amount),
            Math.min(255, color.getBlue() + amount)
        );
    }
    
    /**
     * Crea un panel de encabezado estilizado
     */
    public static JPanel createHeaderPanel(String title, JComponent... rightComponents) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SECONDARY_DARK);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, ACCENT_RED));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(ACCENT_RED);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        if (rightComponents.length > 0) {
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            rightPanel.setBackground(SECONDARY_DARK);
            for (JComponent comp : rightComponents) {
                rightPanel.add(comp);
            }
            headerPanel.add(rightPanel, BorderLayout.EAST);
        }
        
        return headerPanel;
    }
    
    /**
     * Crea un panel de estadísticas estilizado
     */
    public static JPanel createStatPanel(String label, String value, Color accentColor) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(SECONDARY_DARK);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Montserrat", Font.BOLD, 24));
        valueLabel.setForeground(accentColor);
        panel.add(valueLabel);
        
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(FONT_LABEL);
        labelLabel.setForeground(TEXT_SECONDARY);
        panel.add(labelLabel);
        
        return panel;
    }
    
    /**
     * Carga y escala una imagen
     */
    public static ImageIcon loadScaledImage(String imagePath, int width, int height) {
        try {
            File file = new File(imagePath);
            if (!file.exists()) {
                return null;
            }
            BufferedImage image = ImageIO.read(file);
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + imagePath);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Crea un panel con logo del gimnasio
     */
    public static JPanel createLogoPanel(int width, int height) {
        JPanel panel = new JPanel();
        panel.setBackground(PRIMARY_DARK);
        
        ImageIcon logo = loadScaledImage("resources/images/logo.png", width, height);
        if (logo != null && logo.getIconWidth() > 0) {
            JLabel logoLabel = new JLabel(logo);
            panel.add(logoLabel);
        }
        
        return panel;
    }
}
