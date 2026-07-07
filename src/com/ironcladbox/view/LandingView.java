package com.ironcladbox.view;

import com.ironcladbox.service.*;
import com.ironcladbox.dto.ApiResponse;
import com.ironcladbox.util.UIStyles;
import com.ironcladbox.config.ApiConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import javax.swing.*;
import javax.swing.border.*;

public class LandingView extends JFrame {
    private static final Color BG = new Color(0x11, 0x11, 0x13);
    private static final Color CARD_BG = new Color(0x1C, 0x1C, 0x1E);
    private static final Color RED = new Color(0xFF, 0x3B, 0x30);
    private static final Color GRAY = new Color(0xB0, 0xB0, 0xB5);
    private static final Color DARK_GRAY = new Color(0x3A, 0x3A, 0x3C);
    private static final Font BEBAS = new Font("Arial", Font.BOLD, 28);
    private static final Font MONT_BOLD = new Font("Arial", Font.BOLD, 12);
    private static final Font MONT = new Font("Arial", Font.PLAIN, 11);

    private JPanel contentPanel;

    public LandingView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("IroncladBox CrossFit - Quito, Ecuador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 720);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createNavbar(), BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG);
        contentPanel.add(createHero());
        contentPanel.add(createAboutSection());
        contentPanel.add(createClassesSection());
        contentPanel.add(createTrainersSection());
        contentPanel.add(createMembershipsSection());
        contentPanel.add(createContactSection());
        contentPanel.add(createFooter());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BG);
        add(mainPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    // ========== NAVBAR ==========
    private JPanel createNavbar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(new Color(0x0A, 0x0A, 0x0C));
        nav.setBorder(new MatteBorder(0, 0, 2, 0, RED));
        nav.setPreferredSize(new Dimension(850, 50));

        // Logo + brand
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        logoPanel.setBackground(nav.getBackground());
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/com/ironcladbox/images/logo.jpeg"));
            Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            logoPanel.add(new JLabel(new ImageIcon(img)));
        } catch (Exception e) {
            JLabel l = new JLabel("IB");
            l.setFont(new Font("Arial", Font.BOLD, 16));
            l.setForeground(RED);
            logoPanel.add(l);
        }
        JLabel brand = new JLabel("IRONCLADBOX");
        brand.setFont(new Font("Arial", Font.BOLD, 18));
        brand.setForeground(Color.WHITE);
        logoPanel.add(brand);
        nav.add(logoPanel, BorderLayout.WEST);

        // Links
        JPanel links = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        links.setBackground(nav.getBackground());
        String[] items = {"Nosotros", "Clases", "Entrenadores", "Membresias", "Contacto"};
        int[] targets = {1, 2, 3, 4, 5};
        for (int i = 0; i < items.length; i++) {
            final int idx = targets[i];
            JButton btn = new JButton(items[i]);
            btn.setForeground(GRAY);
            btn.setFont(MONT);
            btn.setBorder(null);
            btn.setContentAreaFilled(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> scrollTo(idx));
            links.add(btn);
        }
        JButton loginBtn = new JButton("Iniciar Sesion");
        loginBtn.setBackground(RED);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 11));
        loginBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
        loginBtn.setContentAreaFilled(true);
        loginBtn.setOpaque(true);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> { dispose(); new LoginView(); });
        links.add(loginBtn);
        nav.add(links, BorderLayout.EAST);

        return nav;
    }

    // ========== HERO ==========
    private JPanel createHero() {
        JPanel hero = new JPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBackground(new Color(0x0D, 0x0D, 0x0F));
        hero.setBorder(new EmptyBorder(90, 30, 70, 30));
        hero.setPreferredSize(new Dimension(850, 380));
        hero.setMaximumSize(new Dimension(850, 380));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/com/ironcladbox/images/logo.jpeg"));
            Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(img));
            logoLabel.setAlignmentX(CENTER_ALIGNMENT);
            hero.add(logoLabel);
        } catch (Exception e) {
            JLabel place = new JLabel("LOGO");
            place.setFont(new Font("Arial", Font.BOLD, 30));
            place.setForeground(RED);
            place.setAlignmentX(CENTER_ALIGNMENT);
            hero.add(place);
        }
        hero.add(Box.createVerticalStrut(16));

        JLabel sub = new JLabel("BIENVENIDO A");
        sub.setFont(new Font("Arial", Font.BOLD, 14));
        sub.setForeground(GRAY);
        sub.setAlignmentX(CENTER_ALIGNMENT);
        hero.add(sub);

        JLabel title = new JLabel("IRONCLADBOX");
        title.setFont(new Font("Arial", Font.BOLD, 60));
        title.setForeground(RED);
        title.setAlignmentX(CENTER_ALIGNMENT);
        hero.add(title);

        JLabel desc = new JLabel("FORJANDO ATLETAS EN EL CORAZON DE QUITO");
        desc.setFont(new Font("Arial", Font.BOLD, 11));
        desc.setForeground(GRAY);
        desc.setAlignmentX(CENTER_ALIGNMENT);
        hero.add(desc);
        hero.add(Box.createVerticalStrut(24));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btns.setBackground(hero.getBackground());
        JButton startBtn = styledButton("COMENZAR AHORA", RED, Color.WHITE);
        startBtn.addActionListener(e -> scrollTo(4));
        JButton aboutBtn = styledButton("CONOCE MAS", DARK_GRAY, Color.WHITE);
        aboutBtn.addActionListener(e -> scrollTo(1));
        btns.add(startBtn);
        btns.add(aboutBtn);
        hero.add(btns);

        return hero;
    }

    // ========== ABOUT ==========
    private JPanel createAboutSection() {
        JPanel panel = sectionWrapper();
        panel.add(sectionTitle("SOBRE NOSOTROS"));
        panel.add(sectionUnderline());
        panel.add(Box.createVerticalStrut(10));

        JLabel heading = new JLabel("EL MEJOR CROSSFIT DE QUITO");
        heading.setFont(new Font("Arial", Font.BOLD, 13));
        heading.setForeground(RED);
        heading.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(heading);
        panel.add(Box.createVerticalStrut(10));

        String text = "<html><div style='text-align:center;width:650px;'>"
            + "IroncladBox es mas que un gimnasio, es una comunidad dedicada a transformar vidas "
            + "a traves del fitness funcional. Ubicados en el corazon de Quito, ofrecemos un "
            + "ambiente de entrenamiento de clase mundial con coaches certificados y programacion de primer nivel.<br><br>"
            + "Nuestras instalaciones cuentan con el mejor equipo y un ambiente motivador donde "
            + "atletas de todos los niveles pueden alcanzar sus objetivos."
            + "</div></html>";
        JLabel aboutText = new JLabel(text);
        aboutText.setFont(new Font("Arial", Font.PLAIN, 11));
        aboutText.setForeground(GRAY);
        aboutText.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(aboutText);
        panel.add(Box.createVerticalStrut(16));

        // Feature cards
        JPanel features = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        features.setBackground(BG);
        features.add(featureCard("\uD83C\uDFCB", "Equipo Premium", "Instalaciones de primer nivel"));
        features.add(featureCard("\uD83D\uDC65", "Comunidad", "Ambiente motivador y familiar"));
        features.add(featureCard("\uD83C\uDFC5", "Coaches Certificados", "Entrenadores experimentados"));
        features.add(featureCard("\uD83D\uDCC8", "Progreso", "Resultados garantizados"));
        panel.add(features);

        // Stats
        panel.add(Box.createVerticalStrut(16));
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        stats.setBackground(BG);
        try {
            ApiResponse resp = MembershipApiService.getInstance().getStats();
            if (resp.isOk() && resp.data != null && resp.data.isJsonObject()) {
                JsonObject s = resp.data.getAsJsonObject();
                stats.add(statCard("Atletas", s.has("totalAthletes") ? s.get("totalAthletes").getAsInt() : 60));
                stats.add(statCard("Coaches", s.has("totalTrainers") ? s.get("totalTrainers").getAsInt() : 2));
                stats.add(statCard("Clases", s.has("totalWODs") ? s.get("totalWODs").getAsInt() : 50));
                stats.add(statCard("Membresias", s.has("totalMemberships") ? s.get("totalMemberships").getAsInt() : 4));
            }
        } catch (Exception ex) {
            stats.add(statCard("Atletas", 60));
            stats.add(statCard("Coaches", 2));
            stats.add(statCard("Clases", 50));
            stats.add(statCard("Membresias", 4));
        }
        panel.add(stats);
        return panel;
    }

    // ========== CLASSES ==========
    private JPanel createClassesSection() {
        JPanel panel = sectionWrapper();
        panel.add(sectionTitle("NUESTRAS CLASES"));
        panel.add(sectionUnderline());
        panel.add(Box.createVerticalStrut(10));
        JLabel sub = new JLabel("Clases disenadas para todos los niveles");
        sub.setFont(MONT);
        sub.setForeground(GRAY);
        sub.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(sub);

        JPanel grid = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        grid.setBackground(BG);
        try {
            ApiResponse resp = ClassApiService.getInstance().getAvailable();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (int i = 0; i < Math.min(arr.size(), 6); i++) {
                    JsonObject c = arr.get(i).getAsJsonObject();
                    grid.add(classCard(c));
                }
            }
        } catch (Exception ex) {
            grid.add(placeholderCard("Cargando clases..."));
        }
        panel.add(grid);
        return panel;
    }

    // ========== TRAINERS ==========
    private JPanel createTrainersSection() {
        JPanel panel = sectionWrapper();
        panel.add(sectionTitle("NUESTRO EQUIPO"));
        panel.add(sectionUnderline());
        panel.add(Box.createVerticalStrut(10));
        JLabel sub = new JLabel("Coaches certificados y apasionados");
        sub.setFont(MONT);
        sub.setForeground(GRAY);
        sub.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(sub);

        JPanel grid = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        grid.setBackground(BG);
        try {
            ApiResponse resp = TrainerApiService.getInstance().getPublicTrainers();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (int i = 0; i < Math.min(arr.size(), 4); i++) {
                    JsonObject t = arr.get(i).getAsJsonObject();
                    grid.add(trainerCard(t));
                }
            }
        } catch (Exception ex) {
            grid.add(placeholderCard("Cargando entrenadores..."));
        }
        panel.add(grid);
        return panel;
    }

    // ========== MEMBERSHIPS ==========
    private JPanel createMembershipsSection() {
        JPanel panel = sectionWrapper();
        panel.add(sectionTitle("MEMBRESIAS"));
        panel.add(sectionUnderline());
        panel.add(Box.createVerticalStrut(10));
        JLabel sub = new JLabel("Elige el plan que mejor se adapte a ti");
        sub.setFont(MONT);
        sub.setForeground(GRAY);
        sub.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(sub);

        JPanel grid = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        grid.setBackground(BG);
        try {
            ApiResponse resp = MembershipApiService.getInstance().getAvailable();
            if (resp.isOk() && resp.data != null && resp.data.isJsonArray()) {
                JsonArray arr = resp.data.getAsJsonArray();
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject m = arr.get(i).getAsJsonObject();
                    grid.add(membershipCard(m));
                }
            }
        } catch (Exception ex) {
            grid.add(placeholderCard("Cargando membresias..."));
        }
        panel.add(grid);
        return panel;
    }

    // ========== CONTACT ==========
    private JPanel createContactSection() {
        JPanel panel = sectionWrapper();
        panel.add(sectionTitle("CONTACTANOS"));
        panel.add(sectionUnderline());
        panel.add(Box.createVerticalStrut(10));
        JLabel sub = new JLabel("Estamos aqui para ayudarte a comenzar tu journey");
        sub.setFont(MONT);
        sub.setForeground(GRAY);
        sub.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(sub);
        panel.add(Box.createVerticalStrut(16));

        JPanel contactContent = new JPanel(new BorderLayout(20, 0));
        contactContent.setBackground(BG);

        // Left: info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BG);
        infoPanel.add(infoRow("\uD83D\uDCCD", "Ubicacion", "Quito, Ecuador"));
        infoPanel.add(infoRow("\uD83D\uDCDE", "Telefono", "+593 99 666 6672"));
        infoPanel.add(infoRow("\u2709\uFE0F", "Email", "info@ironcladbox.com"));
        infoPanel.add(infoRow("\uD83D\uDD52", "Horarios", "Lun-Vie: 06:00-21:00 | Sab: 08:00-14:00"));
        JPanel social = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        social.setBackground(BG);
        for (String s : new String[]{"FB", "IG", "WA", "YT"}) {
            JButton sb = new JButton(s);
            sb.setFont(new Font("Arial", Font.BOLD, 9));
            sb.setForeground(RED);
            sb.setBackground(CARD_BG);
            sb.setBorder(new LineBorder(DARK_GRAY, 1));
            sb.setCursor(new Cursor(Cursor.HAND_CURSOR));
            social.add(sb);
        }
        infoPanel.add(social);
        contactContent.add(infoPanel, BorderLayout.WEST);

        // Right: form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BG);
        formPanel.setBorder(new EmptyBorder(0, 20, 0, 0));
        JLabel formTitle = new JLabel("Envíanos un mensaje");
        formTitle.setFont(new Font("Arial", Font.BOLD, 13));
        formTitle.setForeground(Color.WHITE);
        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(10));

        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextArea msgArea = new JTextArea(4, 20);
        msgArea.setLineWrap(true);
        JScrollPane msgScroll = new JScrollPane(msgArea);
        formPanel.add(styledField(nameField, "Nombre completo"));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(styledField(emailField, "Email"));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(styledField(phoneField, "Telefono"));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(styledFieldScroll(msgScroll));
        formPanel.add(Box.createVerticalStrut(10));

        JButton sendBtn = styledButton("ENVIAR MENSAJE", RED, Color.WHITE);
        sendBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        sendBtn.addActionListener(e -> {
            String n = nameField.getText().trim();
            String em = emailField.getText().trim();
            String msg = msgArea.getText().trim();
            if (n.isEmpty() || em.isEmpty() || msg.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completa nombre, email y mensaje", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                JsonObject body = new JsonObject();
                body.addProperty("name", n);
                body.addProperty("email", em);
                body.addProperty("phone", phoneField.getText().trim());
                body.addProperty("message", msg);
                body.addProperty("website", "");
                ApiService.getInstance().post(ApiConfig.CONTACT, body);
                JOptionPane.showMessageDialog(this, "Mensaje enviado exitosamente!", "Exito", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText(""); emailField.setText(""); phoneField.setText(""); msgArea.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(sendBtn);

        contactContent.add(formPanel, BorderLayout.CENTER);
        panel.add(contactContent);

        // Google Maps link
        panel.add(Box.createVerticalStrut(10));
        JButton mapBtn = new JButton("Ver en Google Maps");
        mapBtn.setFont(new Font("Arial", Font.PLAIN, 10));
        mapBtn.setForeground(RED);
        mapBtn.setBackground(CARD_BG);
        mapBtn.setBorder(new LineBorder(DARK_GRAY, 1));
        mapBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mapBtn.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://maps.app.goo.gl/pRFAxbzsCvii74r8A"));
            } catch (Exception ex) {}
        });
        mapBtn.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(mapBtn);

        return panel;
    }

    // ========== FOOTER ==========
    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(new Color(0x0A, 0x0A, 0x0C));
        footer.setBorder(new MatteBorder(2, 0, 0, 0, RED));

        JPanel footerContent = new JPanel(new GridLayout(1, 4, 20, 0));
        footerContent.setBackground(footer.getBackground());
        footerContent.setBorder(new EmptyBorder(20, 40, 20, 40));

        footerContent.add(footerCol("IRONCLADBOX", "Forjando atletas desde 2024", null));
        footerContent.add(footerCol("Enlaces", null, new String[]{"Inicio", "Clases", "Membresias", "Contacto"}));
        footerContent.add(footerCol("Horarios", null, new String[]{"Lun-Vie: 06:00 - 21:00", "Sabado: 08:00 - 14:00", "Domingo: Cerrado"}));
        footerContent.add(footerCol("Siguenos", null, new String[]{"Facebook", "Instagram", "WhatsApp", "YouTube"}));

        footer.add(footerContent);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(footer.getBackground());
        JLabel copy = new JLabel("(c) 2026 IroncladBox CrossFit. Todos los derechos reservados.");
        copy.setFont(new Font("Arial", Font.PLAIN, 10));
        copy.setForeground(GRAY);
        bottom.add(copy);
        footer.add(bottom);

        return footer;
    }

    private JPanel footerCol(String title, String desc, String[] items) {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(new Color(0x0A, 0x0A, 0x0C));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 12));
        t.setForeground(Color.WHITE);
        col.add(t);
        col.add(Box.createVerticalStrut(6));
        if (desc != null) {
            JLabel d = new JLabel(desc);
            d.setFont(MONT);
            d.setForeground(GRAY);
            col.add(d);
        }
        if (items != null) {
            for (String item : items) {
                JLabel li = new JLabel(item);
                li.setFont(MONT);
                li.setForeground(GRAY);
                col.add(li);
            }
        }
        return col;
    }

    // ========== HELPER COMPONENTS ==========
    private JPanel sectionWrapper() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(30, 40, 30, 40));
        return p;
    }

    private JLabel sectionTitle(String text) {
        JLabel t = new JLabel(text);
        t.setFont(new Font("Arial", Font.BOLD, 22));
        t.setForeground(Color.WHITE);
        t.setAlignmentX(CENTER_ALIGNMENT);
        return t;
    }

    private JPanel sectionUnderline() {
        JPanel line = new JPanel();
        line.setBackground(RED);
        line.setMaximumSize(new Dimension(50, 3));
        line.setPreferredSize(new Dimension(50, 3));
        line.setAlignmentX(CENTER_ALIGNMENT);
        return line;
    }

    private JPanel infoRow(String icon, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row.setBackground(BG);
        row.add(new JLabel(icon));
        JLabel l = new JLabel("<html><b>" + label + "</b><br>" + value + "</html>");
        l.setFont(MONT);
        l.setForeground(GRAY);
        row.add(l);
        return row;
    }

    private JTextField styledField(JTextField field, String placeholder) {
        field.setBackground(CARD_BG);
        field.setForeground(Color.WHITE);
        field.setCaretColor(RED);
        field.setBorder(new CompoundBorder(new LineBorder(DARK_GRAY, 1), new EmptyBorder(8, 10, 8, 10)));
        field.setFont(MONT);
        return field;
    }

    private JScrollPane styledFieldScroll(JScrollPane scroll) {
        scroll.getViewport().getView().setBackground(CARD_BG);
        scroll.getViewport().getView().setForeground(Color.WHITE);
        ((JTextArea)scroll.getViewport().getView()).setCaretColor(RED);
        scroll.setBorder(new LineBorder(DARK_GRAY, 1));
        ((JTextArea)scroll.getViewport().getView()).setFont(MONT);
        return scroll;
    }

    private JPanel featureCard(String icon, String title, String desc) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(new LineBorder(DARK_GRAY, 1), new EmptyBorder(12, 16, 12, 16)));
        card.setPreferredSize(new Dimension(140, 80));

        JLabel i = new JLabel(icon);
        i.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        i.setAlignmentX(CENTER_ALIGNMENT);
        card.add(i);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 11));
        t.setForeground(Color.WHITE);
        t.setAlignmentX(CENTER_ALIGNMENT);
        card.add(t);

        JLabel d = new JLabel(desc);
        d.setFont(new Font("Arial", Font.PLAIN, 9));
        d.setForeground(GRAY);
        d.setAlignmentX(CENTER_ALIGNMENT);
        card.add(d);

        return card;
    }

    private JPanel statCard(String label, int value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(new LineBorder(DARK_GRAY, 1), new EmptyBorder(10, 18, 10, 18)));

        JLabel v = new JLabel(String.valueOf(value));
        v.setFont(new Font("Arial", Font.BOLD, 26));
        v.setForeground(RED);
        v.setAlignmentX(CENTER_ALIGNMENT);
        card.add(v);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Arial", Font.PLAIN, 9));
        l.setForeground(GRAY);
        l.setAlignmentX(CENTER_ALIGNMENT);
        card.add(l);

        return card;
    }

    private JPanel classCard(JsonObject c) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(new LineBorder(DARK_GRAY, 1), new EmptyBorder(12, 14, 12, 14)));
        card.setPreferredSize(new Dimension(200, 80));

        String name = c.has("nombre") ? c.get("nombre").getAsString() : "Clase";
        JLabel n = new JLabel(name);
        n.setFont(new Font("Arial", Font.BOLD, 12));
        n.setForeground(Color.WHITE);
        n.setAlignmentX(CENTER_ALIGNMENT);
        card.add(n);

        if (c.has("hora")) {
            String hora = c.get("hora").getAsString().substring(0, Math.min(5, c.get("hora").getAsString().length()));
            JLabel h = new JLabel(hora);
            h.setFont(new Font("Arial", Font.BOLD, 11));
            h.setForeground(RED);
            h.setAlignmentX(CENTER_ALIGNMENT);
            card.add(h);
        }
        if (c.has("entrenador_nombre")) {
            JLabel tr = new JLabel(c.get("entrenador_nombre").getAsString());
            tr.setFont(MONT);
            tr.setForeground(GRAY);
            tr.setAlignmentX(CENTER_ALIGNMENT);
            card.add(tr);
        }
        return card;
    }

    private JPanel trainerCard(JsonObject t) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(new LineBorder(DARK_GRAY, 1), new EmptyBorder(12, 14, 12, 14)));
        card.setPreferredSize(new Dimension(170, 90));

        JLabel avatar = new JLabel("\uD83D\uDC64");
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        avatar.setAlignmentX(CENTER_ALIGNMENT);
        card.add(avatar);

        String fullName = (t.has("nombre") ? t.get("nombre").getAsString() : "") + " " +
                         (t.has("apellido") ? t.get("apellido").getAsString() : "");
        JLabel n = new JLabel(fullName.trim());
        n.setFont(new Font("Arial", Font.BOLD, 12));
        n.setForeground(Color.WHITE);
        n.setAlignmentX(CENTER_ALIGNMENT);
        card.add(n);

        if (t.has("especialidad")) {
            JLabel s = new JLabel(t.get("especialidad").getAsString());
            s.setFont(MONT);
            s.setForeground(RED);
            s.setAlignmentX(CENTER_ALIGNMENT);
            card.add(s);
        }
        return card;
    }

    private JPanel membershipCard(JsonObject m) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(new LineBorder(RED, 1), new EmptyBorder(16, 20, 16, 20)));
        card.setPreferredSize(new Dimension(180, 150));

        JLabel n = new JLabel(m.has("nombre") ? m.get("nombre").getAsString() : "");
        n.setFont(new Font("Arial", Font.BOLD, 14));
        n.setForeground(Color.WHITE);
        n.setAlignmentX(CENTER_ALIGNMENT);
        card.add(n);

        JLabel p = new JLabel(m.has("precio") ? "$" + m.get("precio").getAsDouble() : "");
        p.setFont(new Font("Arial", Font.BOLD, 22));
        p.setForeground(RED);
        p.setAlignmentX(CENTER_ALIGNMENT);
        card.add(p);

        JLabel d = new JLabel(m.has("duracion_dias") ? m.get("duracion_dias").getAsInt() + " dias" : "");
        d.setFont(MONT);
        d.setForeground(GRAY);
        d.setAlignmentX(CENTER_ALIGNMENT);
        card.add(d);

        if (m.has("beneficios")) {
            String[] benefits = m.get("beneficios").getAsString().split(",");
            for (String b : benefits) {
                if (b.trim().isEmpty()) continue;
                JLabel bl = new JLabel("\u2714 " + b.trim());
                bl.setFont(new Font("Arial", Font.PLAIN, 9));
                bl.setForeground(GRAY);
                bl.setAlignmentX(CENTER_ALIGNMENT);
                card.add(bl);
            }
        }
        return card;
    }

    private JPanel placeholderCard(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setBackground(BG);
        JLabel l = new JLabel(text);
        l.setFont(MONT);
        l.setForeground(GRAY);
        p.add(l);
        return p;
    }

    private JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setBorder(new EmptyBorder(9, 22, 9, 22));
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void scrollTo(int section) {
        JScrollPane sp = findScrollPane(getContentPane());
        if (sp == null) return;
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

    private JScrollPane findScrollPane(Container c) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JScrollPane) return (JScrollPane) comp;
            if (comp instanceof Container) {
                JScrollPane found = findScrollPane((Container) comp);
                if (found != null) return found;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        java.util.Locale.setDefault(new java.util.Locale("es", "ES"));
        SwingUtilities.invokeLater(() -> new LandingView());
    }
}
