package gui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import service.*;

public class LoginFrame extends JFrame {

    // Colors
    private static final Color BG = new Color(246, 245, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color PURPLE = new Color(105, 76, 160);
    private static final Color LIGHT_PURPLE = new Color(239, 234, 248);
    private static final Color TEXT = new Color(45, 42, 55);
    private static final Color MUTED = new Color(135, 130, 145);
    private static final Color BORDER = new Color(230, 226, 237);

    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 25);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JButton btnLogin = new JButton("Login");
    private final JButton btnExit = new JButton("Exit");

    private final AuthService authService;
    private final EmployeeManager employeeManager;
    private final DepartmentManager departmentManager;

    public LoginFrame(AuthService a, EmployeeManager em, DepartmentManager dm) {
        this.authService = a;
        this.employeeManager = em;
        this.departmentManager = dm;
        initComponents();
    }

    private void initComponents() {
        setTitle("Employee Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(createBrandPanel());
        root.add(createFormPanel());
        setContentPane(root);

        getRootPane().setDefaultButton(btnLogin);
    }

    // ================= LEFT BRAND SIDE =================

    private JPanel createBrandPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PURPLE);
        panel.setBorder(new EmptyBorder(150, 40, 40, 40));

        JLabel badge = new JLabel("EMS", SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 22));
        badge.setForeground(PURPLE);
        badge.setOpaque(true);
        badge.setBackground(WHITE);
        badge.setPreferredSize(new Dimension(70, 70));
        badge.setMaximumSize(new Dimension(70, 70));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Employee Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Manage employees, departments and payroll");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(LIGHT_PURPLE);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(badge);
        panel.add(Box.createVerticalStrut(25));
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitle);
        return panel;
    }

    // ================= RIGHT FORM SIDE =================

    private JPanel createFormPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 16),
                new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to continue");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        styleField(txtUsername);
        styleField(txtPassword);

        JPanel userGroup = createFieldGroup("Username", txtUsername);
        JPanel passGroup = createFieldGroup("Password", txtPassword);

        styleButton(btnLogin, true);
        styleButton(btnExit, false);
        btnLogin.addActionListener(e -> handleLogin());
        btnExit.addActionListener(e -> handleExit());

        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 12, 0));
        buttons.setBackground(WHITE);
        buttons.add(btnLogin);
        buttons.add(btnExit);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        card.add(title);
        card.add(Box.createVerticalStrut(7));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(25));
        card.add(userGroup);
        card.add(Box.createVerticalStrut(18));
        card.add(passGroup);
        card.add(Box.createVerticalStrut(25));
        card.add(buttons);

        card.setPreferredSize(new Dimension(340, 380));
        wrapper.add(card);
        return wrapper;
    }

    private JPanel createFieldGroup(String labelText, JComponent input) {
        JPanel group = new JPanel(new BorderLayout(0, 9));
        group.setBackground(WHITE);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT);

        group.add(label, BorderLayout.NORTH);
        group.add(input, BorderLayout.CENTER);
        return group;
    }

    private void styleField(JTextField field) {
        field.setFont(INPUT_FONT);
        field.setForeground(TEXT);
        field.setCaretColor(PURPLE);
        field.setBackground(new Color(250, 249, 252));
        field.setPreferredSize(new Dimension(200, 44));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 10),
                new EmptyBorder(0, 13, 0, 13)
        ));
    }

    private void styleButton(JButton button, boolean primary) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(primary ? WHITE : PURPLE);
        button.setBackground(primary ? PURPLE : LIGHT_PURPLE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                g2.dispose();
                super.paint(g, c);
            }
        });
    }

    // ================= BEHAVIOR =================

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        char[] password = txtPassword.getPassword();

        boolean ok = authService.authenticate(username, password);
        java.util.Arrays.fill(password, '\0');

        if (ok) {
            MainFrame main = new MainFrame(employeeManager, departmentManager, authService);
            main.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    private void handleExit() {
        System.exit(0);
    }

    // ================= ROUNDED BORDER =================

    private static class RoundedBorder extends AbstractBorder {

        private final Color color;
        private final int radius;

        public RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, 2, 2);
        }
    }
}