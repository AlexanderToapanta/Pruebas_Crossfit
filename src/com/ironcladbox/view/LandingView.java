package com.ironcladbox.view;

import com.ironcladbox.service.ClassApiService;
import com.ironcladbox.service.TrainerApiService;
import com.ironcladbox.service.MembershipApiService;
import com.ironcladbox.service.ApiService;
import com.ironcladbox.dto.ApiResponse;
import com.ironcladbox.util.UIStyles;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ironcladbox.config.ApiConfig;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import javax.swing.*;
import javax.swing.border.*;

public class LandingView extends JFrame {

    public LandingView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("IroncladBox CrossFit - Quito, Ecuador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(UIStyles.PRIMARY_DARK);

        wrapper.add(createHeroSection());
        wrapper.add(createAboutSection());
        wrapper.add(createTrainersSection());
        wrapper.add(createClassesSection());
        wrapper.add(createMembershipsSection());
        wrapper.add(createContactSection());
        wrapper.add(createFooter());

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        add(scrollPane);
        setVisible(true);
    }

    private JPanel createHeroSection() {
        JPanel hero = new JPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBackground(new Color(20, 20, 22));
        hero.setBorder(new EmptyBorder(50, 30, 40, 30));
        hero.setMaximumSize(new Dimension(800, 350));
        hero.setPreferredSize(new Dimension(800, 350));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/com/ironcladbox/images/logo.jpeg"));
            Image img = icon.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(img));
            logoLabel.setAlignmentX(CENTER_ALIGNMENT);
            hero.add(logoLabel);
        } catch (Exception e) {
            JLabel place = new JLabel("LOGO");
            place.setFont(new Font("Montserrat", Font.BOLD, 30));
            place.setForeground(UIStyles.ACCENT_RED);
            place.setAlignmentX(CENTER_ALIGNMENT);
            hero.add(place);
        }

        hero.add(Box.createVerticalStrut(10));

        JLabel subtitle = new JLabel("BIENVENIDO A");
        subtitle.setFont(new Font("Montserrat", Font.BOLD, 14));
        subtitle.setForeground(UIStyles.TEXT_SECONDARY);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        hero.add(subtitle);

        JLabel title = new JLabel("IRONCLADBOX");
        title.setFont(new Font("Bebas Neue", Font.BOLD, 52));
        title.setForeground(UIStyles.ACCENT_RED);
        title.setAlignmentX(CENTER_ALIGNMENT);
        hero.add(title);

        JLabel desc = new JLabel("FORJANDO ATLETAS EN EL CORAZON DE QUITO");
        desc.setFont(new Font("Montserrat", Font.BOLD, 12));
        desc.setForeground(UIStyles.TEXT_SECONDARY);
        desc.setAlignmentX(CENTER_ALIGNMENT);
        hero.add(desc);

        hero.add(Box.createVerticalStrut(20));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setBackground(new Color(20, 20, 22));
        JButton startBtn = new JButton("  COMENZAR AHORA  ");
        UIStyles.stylePrimaryButton(startBtn);
        startBtn.addActionListener(e -> scrollTo(4));
        JButton aboutBtn = new JButton("  CONOCE MAS  ");
        UIStyles.styleSecondaryButton(aboutBtn);
        aboutBtn.addActionListener(e -> scrollTo(1));
        btnPanel.add(startBtn);
        btnPanel.add(aboutBtn);
        hero.add(btnPanel);

        return hero;
    }

    private JPanel createAboutSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIStyles.PRIMARY_DARK);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel sectionTitle = new JLabel("SOBRE NOSOTROS");
        sectionTitle.setFont(new Font("Bebas Neue", Font.BOLD, 28));
        sectionTitle.setForeground(Color.WHITE);
        sectionTitle.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(sectionTitle);

        panel.add(createUnderline());

        JLabel heading = new JLabel("EL MEJOR CROSSFIT DE QUITO");
        heading.setFont(new Font("Montserrat", Font.BOLD, 14));
        heading.setForeground(UIStyles.ACCENT_RED);
        heading.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(heading);

        panel.add(Box.createVerticalStrut(10));

        String aboutText = "<html><div style='text-align:center;width:600px;'>"
            + "IroncladBox es mas que un gimnasio, es una comunidad dedicada a transformar vidas "
            + "a traves del fitness funcional. Ubicados en el corazon de Quito, ofrecemos un "
            + "ambiente de entrenamiento de clase mundial con coaches certificados y programacion "
            + "de primer nivel.</div></html>";
        JLabel aboutLabel = new JLabel(aboutText);
        aboutLabel.setFont(new Font("Montserrat", Font.PLAIN, 12));
        aboutLabel.setForeground(UIStyles.TEXT_SECONDARY);
        aboutLabel.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(aboutLabel);

        panel.add(Box.createVerticalStrut(15));

        JPanel features = new JPanel(new GridLayout(1, 3, 15, 0));
        features.setBackground(UIStyles.PRIMARY_DARK);
        features.add(createFeatureCard("Equipo Premium", "Instalaciones de primer nivel"));
        features.add(createFeatureCard("Comunidad", "Ambiente motivador y familiar"));
        features.add(createFeatureCard("Coaches Certificados", "Entrenadores experimentados"));
        features.setMinimumSize(new Dimension(700, 80));
        features.setPreferredSize(new Dimension(700, 80));
        features.setMaximumSize(new Dimension(700, 80));
        panel.add(features);

        return panel;
    }

    private JPanel createFeatureCard(String title, String desc) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIStyles.SECONDARY_DARK);
        card.setBorder(new CompoundBorder(new LineBorder(UIStyles.BORDER_COLOR, 1), new EmptyBorder(10, 10, 10, 10)));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Montserrat", Font.BOLD, 12));
        t.setForeground(Color.WHITE);
        t.setAlignmentX(CENTER_ALIGNMENT);
        card.add(t);

        JLabel d = new JLabel(desc);
        d.setFont(new Font("Montserrat", Font.PLAIN, 10));
        d.setForeground(UIStyles.TEXT_SECONDARY);
        d.setAlignmentX(CENTER_ALIGNMENT);
        card.add(d);

        return card;
    }

    private JPanel createTrainersSection() {
        JPanel panel = sectionPanel("ENTRENADORES");
        JPanel cards = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        cards.setBackground(UIStyles.PRIMARY_DARK);

        try {
            ApiResponse resp = TrainerApiService.getInstance().getPublicTrainers();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    JsonObject t = e.getAsJsonObject();
                    JPanel card = new JPanel();
                    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                    card.setBackground(UIStyles.SECONDARY_DARK);
                    card.setBorder(new CompoundBorder(new LineBorder(UIStyles.BORDER_COLOR, 1), new EmptyBorder(12, 15, 12, 15)));
                    card.setPreferredSize(new Dimension(180, 100));

                    String name = (t.has("nombre") ? t.get("nombre").getAsString() : "") + " " +
                                  (t.has("apellido") ? t.get("apellido").getAsString() : "");
                    JLabel n = new JLabel(name.trim());
                    n.setFont(new Font("Montserrat", Font.BOLD, 13));
                    n.setForeground(Color.WHITE);
                    n.setAlignmentX(CENTER_ALIGNMENT);
                    card.add(n);

                    if (t.has("especialidad")) {
                        JLabel s = new JLabel(t.get("especialidad").getAsString());
                        s.setFont(new Font("Montserrat", Font.PLAIN, 10));
                        s.setForeground(UIStyles.ACCENT_RED);
                        s.setAlignmentX(CENTER_ALIGNMENT);
                        card.add(s);
                    }
                    cards.add(card);
                }
            }
        } catch (Exception ex) {
            JLabel err = new JLabel("Cargando entrenadores...");
            err.setForeground(UIStyles.TEXT_SECONDARY);
            cards.add(err);
        }
        panel.add(cards);
        return panel;
    }

    private JPanel createClassesSection() {
        JPanel panel = sectionPanel("CLASES");
        JPanel cards = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        cards.setBackground(UIStyles.PRIMARY_DARK);

        try {
            ApiResponse resp = ClassApiService.getInstance().getAvailable();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                int count = 0;
                for (JsonElement e : arr) {
                    if (count++ >= 6) break;
                    JsonObject c = e.getAsJsonObject();
                    JPanel card = new JPanel();
                    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                    card.setBackground(UIStyles.SECONDARY_DARK);
                    card.setBorder(new CompoundBorder(new LineBorder(UIStyles.BORDER_COLOR, 1), new EmptyBorder(12, 15, 12, 15)));
                    card.setPreferredSize(new Dimension(200, 100));

                    JLabel n = new JLabel(c.has("nombre") ? c.get("nombre").getAsString() : "Clase");
                    n.setFont(new Font("Montserrat", Font.BOLD, 13));
                    n.setForeground(Color.WHITE);
                    n.setAlignmentX(CENTER_ALIGNMENT);
                    card.add(n);

                    if (c.has("hora")) {
                        String hora = c.get("hora").getAsString();
                        if (hora.length() >= 5) hora = hora.substring(0, 5);
                        JLabel h = new JLabel(hora + (c.has("fecha") ? " | " + c.get("fecha").getAsString() : ""));
                        h.setFont(new Font("Montserrat", Font.PLAIN, 10));
                        h.setForeground(UIStyles.ACCENT_RED);
                        h.setAlignmentX(CENTER_ALIGNMENT);
                        card.add(h);
                    }
                    if (c.has("entrenador_nombre")) {
                        JLabel tr = new JLabel("Trainer: " + c.get("entrenador_nombre").getAsString());
                        tr.setFont(new Font("Montserrat", Font.PLAIN, 10));
                        tr.setForeground(UIStyles.TEXT_SECONDARY);
                        tr.setAlignmentX(CENTER_ALIGNMENT);
                        card.add(tr);
                    }
                    cards.add(card);
                }
            }
        } catch (Exception ex) {
            JLabel err = new JLabel("Cargando clases...");
            err.setForeground(UIStyles.TEXT_SECONDARY);
            cards.add(err);
        }
        panel.add(cards);
        return panel;
    }

    private JPanel createMembershipsSection() {
        JPanel panel = sectionPanel("MEMBRESIAS");
        JPanel cards = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        cards.setBackground(UIStyles.PRIMARY_DARK);

        try {
            ApiResponse resp = MembershipApiService.getInstance().getAvailable();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (JsonElement e : arr) {
                    JsonObject m = e.getAsJsonObject();
                    JPanel card = new JPanel();
                    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                    card.setBackground(UIStyles.SECONDARY_DARK);
                    card.setBorder(new CompoundBorder(new LineBorder(UIStyles.ACCENT_RED, 1), new EmptyBorder(15, 18, 15, 18)));
                    card.setPreferredSize(new Dimension(180, 140));

                    JLabel n = new JLabel(m.has("nombre") ? m.get("nombre").getAsString() : "");
                    n.setFont(new Font("Montserrat", Font.BOLD, 15));
                    n.setForeground(Color.WHITE);
                    n.setAlignmentX(CENTER_ALIGNMENT);
                    card.add(n);

                    JLabel p = new JLabel(m.has("precio") ? "$" + m.get("precio").getAsDouble() + " USD" : "");
                    p.setFont(new Font("Montserrat", Font.BOLD, 20));
                    p.setForeground(UIStyles.ACCENT_RED);
                    p.setAlignmentX(CENTER_ALIGNMENT);
                    card.add(p);

                    JLabel d = new JLabel(m.has("duracion_dias") ? m.get("duracion_dias").getAsInt() + " dias" : "");
                    d.setFont(new Font("Montserrat", Font.PLAIN, 10));
                    d.setForeground(UIStyles.TEXT_SECONDARY);
                    d.setAlignmentX(CENTER_ALIGNMENT);
                    card.add(d);

                    if (m.has("beneficios")) {
                        String[] benefits = m.get("beneficios").getAsString().split(",");
                        for (String b : benefits) {
                            if (b.trim().isEmpty()) continue;
                            JLabel bl = new JLabel("  " + b.trim());
                            bl.setFont(new Font("Montserrat", Font.PLAIN, 9));
                            bl.setForeground(UIStyles.TEXT_SECONDARY);
                            bl.setAlignmentX(CENTER_ALIGNMENT);
                            card.add(bl);
                        }
                    }
                    cards.add(card);
                }
            }
        } catch (Exception ex) {
            JLabel err = new JLabel("Cargando membresias...");
            err.setForeground(UIStyles.TEXT_SECONDARY);
            cards.add(err);
        }
        panel.add(cards);
        return panel;
    }

    private JPanel createContactSection() {
        JPanel panel = sectionPanel("CONTACTO");
        panel.setMaximumSize(new Dimension(800, 250));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIStyles.PRIMARY_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JTextArea msgArea = new JTextArea(4, 30);
        msgArea.setLineWrap(true);
        JScrollPane msgScroll = new JScrollPane(msgArea);
        msgScroll.setPreferredSize(new Dimension(350, 80));

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        form.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        form.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Mensaje:"), gbc);
        gbc.gridx = 1;
        form.add(msgScroll, gbc);

        JButton sendBtn = new JButton("  ENVIAR MENSAJE  ");
        UIStyles.stylePrimaryButton(sendBtn);
        sendBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String msg = msgArea.getText().trim();
            if (name.isEmpty() || email.isEmpty() || msg.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completa todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                JsonObject body = new JsonObject();
                body.addProperty("name", name);
                body.addProperty("email", email);
                body.addProperty("message", msg);
                body.addProperty("website", "");
                ApiService.getInstance().post(ApiConfig.CONTACT, body);
                JOptionPane.showMessageDialog(this, "Mensaje enviado exitosamente!", "Exito", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText(""); emailField.setText(""); msgArea.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al enviar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(sendBtn, gbc);

        panel.add(form);
        return panel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(new Color(10, 10, 12));
        footer.setBorder(new EmptyBorder(20, 30, 20, 30));

        footer.add(createRow("  Horario: Lunes-Viernes 5AM-9PM | Sabados 7AM-2PM | Domingos 8AM-12PM  "));
        footer.add(Box.createVerticalStrut(5));
        footer.add(createRow("  Ubicacion: Quito, Ecuador  "));
        footer.add(Box.createVerticalStrut(8));

        JButton loginBtn = new JButton("  INICIAR SESION  ");
        UIStyles.stylePrimaryButton(loginBtn);
        loginBtn.setAlignmentX(CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> {
            dispose();
            new LoginView();
        });
        footer.add(loginBtn);

        footer.add(Box.createVerticalStrut(8));
        JLabel copy = new JLabel("IroncladBox CrossFit - 2026");
        copy.setFont(new Font("Montserrat", Font.PLAIN, 10));
        copy.setForeground(UIStyles.TEXT_SECONDARY);
        copy.setAlignmentX(CENTER_ALIGNMENT);
        footer.add(copy);

        return footer;
    }

    private JPanel createRow(String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setBackground(new Color(10, 10, 12));
        JLabel label = new JLabel(text);
        label.setFont(new Font("Montserrat", Font.PLAIN, 11));
        label.setForeground(UIStyles.TEXT_SECONDARY);
        row.add(label);
        return row;
    }

    private JPanel sectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIStyles.PRIMARY_DARK);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Bebas Neue", Font.BOLD, 28));
        t.setForeground(Color.WHITE);
        t.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(t);
        panel.add(createUnderline());
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private JPanel createUnderline() {
        JPanel line = new JPanel();
        line.setBackground(UIStyles.ACCENT_RED);
        line.setMaximumSize(new Dimension(60, 3));
        line.setPreferredSize(new Dimension(60, 3));
        line.setAlignmentX(CENTER_ALIGNMENT);
        return line;
    }

    private void scrollTo(int section) {
        JScrollPane sp = (JScrollPane) getContentPane().getComponent(0);
        JViewport vp = sp.getViewport();
        Component comp = vp.getView();
        if (comp instanceof JPanel) {
            JPanel wrapper = (JPanel) comp;
            if (section < wrapper.getComponentCount()) {
                Rectangle rect = wrapper.getComponent(section).getBounds();
                vp.setViewPosition(new Point(0, rect.y));
            }
        }
    }

    public static void main(String[] args) {
        java.util.Locale.setDefault(new java.util.Locale("es", "ES"));
        SwingUtilities.invokeLater(() -> new LandingView());
    }
}
