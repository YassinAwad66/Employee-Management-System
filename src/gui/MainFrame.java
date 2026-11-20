package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatLightLaf;

public class MainFrame extends JFrame {

    // ================= COLORS =================

    private final Color PURPLE = new Color(111, 78, 165);
    private final Color DARK_PURPLE = new Color(43, 34, 61);
    private final Color SIDEBAR_HOVER = new Color(67, 53, 91);
    private final Color GREEN = new Color(75, 160, 125);

    private final Color BACKGROUND = new Color(247, 246, 250);
    private final Color WHITE = Color.WHITE;
    private final Color TEXT = new Color(43, 40, 52);
    private final Color MUTED = new Color(145, 140, 155);
    private final Color BORDER = new Color(235, 232, 240);

    // ================= COMPONENTS =================

    private JPanel mainPanel;
    private CardLayout cardLayout;

    private JLabel pageTitle;
    private JLabel pageSubtitle;

    private final List<JButton> navigationButtons = new ArrayList<>();

    // ================= CONSTRUCTOR =================

    public MainFrame() {

        setTitle("Employee Management System");
        setSize(1250, 780);
        setMinimumSize(new Dimension(1050, 680));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Sidebar
        add(createSidebar(), BorderLayout.WEST);

        // Main Content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND);

        contentPanel.add(createHeader(), BorderLayout.NORTH);

        // Pages
        cardLayout = new CardLayout();

        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND);

        mainPanel.add(createDashboard(), "Dashboard");
        mainPanel.add(new EmployeeFormPanel(), "Add Employee");
        mainPanel.add(new EmployeeTablePanel(), "Employees");

        contentPanel.add(mainPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        showPage("Dashboard");

    }

    // ================= SIDEBAR =================

    private JPanel createSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setBackground(DARK_PURPLE);

        sidebar.setLayout(new BoxLayout(
                sidebar, BoxLayout.Y_AXIS
        ));

        sidebar.setBorder(
                new EmptyBorder(32, 20, 25, 20)
        );

        // Logo
        JLabel logo = new JLabel("ems.");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 34));
        logo.setForeground(WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("EMPLOYEE MANAGEMENT");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subtitle.setForeground(new Color(185, 175, 200));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(3));
        sidebar.add(subtitle);

        sidebar.add(Box.createVerticalStrut(55));

        // Menu
        addSectionLabel(sidebar, "MAIN MENU");

        sidebar.add(createNavButton("Dashboard", "Dashboard"));
        sidebar.add(Box.createVerticalStrut(7));

        sidebar.add(createNavButton("Employees", "Employees"));
        sidebar.add(Box.createVerticalStrut(7));

        sidebar.add(createNavButton("Add Employee", "Add Employee"));

        sidebar.add(Box.createVerticalStrut(35));

        // Other
        addSectionLabel(sidebar, "PREFERENCES");

        sidebar.add(createNavButton("Reports", null));
        sidebar.add(Box.createVerticalStrut(7));

        sidebar.add(createNavButton("Settings", null));

        sidebar.add(Box.createVerticalGlue());

        // Sidebar Footer
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(
                footer, BoxLayout.Y_AXIS
        ));

        footer.setBackground(DARK_PURPLE);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(85, 72, 105));
        separator.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 1)
        );

        JLabel version = new JLabel("EMS  |  Version 1.0");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        version.setForeground(new Color(175, 165, 190));

        footer.add(separator);
        footer.add(Box.createVerticalStrut(17));
        footer.add(version);

        sidebar.add(footer);

        return sidebar;
    }

    // ================= SECTION LABEL =================

    private void addSectionLabel(JPanel panel, String text) {

        JLabel label = new JLabel(text);

        label.setFont(new Font(
                "Segoe UI", Font.BOLD, 10
        ));

        label.setForeground(new Color(165, 150, 185));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(14));
    }

    // ================= NAVIGATION BUTTON =================

    private JButton createNavButton(
            String text,
            String screen) {

        JButton button = new JButton();

        button.setLayout(new BorderLayout());

        JLabel indicator = new JLabel(" ");
        indicator.setPreferredSize(new Dimension(4, 0));

        JLabel label = new JLabel(text);

        label.setFont(new Font(
                "Segoe UI", Font.PLAIN, 14
        ));

        label.setForeground(new Color(220, 215, 230));

        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.setBorder(
                new EmptyBorder(0, 15, 0, 10)
        );

        inner.add(label, BorderLayout.CENTER);

        button.add(indicator, BorderLayout.WEST);
        button.add(inner, BorderLayout.CENTER);

        button.setBackground(DARK_PURPLE);

        button.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 46)
        );

        button.setPreferredSize(
                new Dimension(205, 46)
        );

        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        navigationButtons.add(button);

        button.putClientProperty("screen", screen);

        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                if (!Boolean.TRUE.equals(
                        button.getClientProperty("active"))) {

                    button.setBackground(SIDEBAR_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {

                if (!Boolean.TRUE.equals(
                        button.getClientProperty("active"))) {

                    button.setBackground(DARK_PURPLE);
                }
            }
        });

        button.addActionListener(e -> {

            if (screen != null) {

                showPage(screen);

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "This section is coming soon!",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        return button;
    }

    // ================= SHOW PAGE =================

    private void showPage(String screen) {

        cardLayout.show(mainPanel, screen);

        switch (screen) {

            case "Dashboard":
                pageTitle.setText("Dashboard");
                pageSubtitle.setText(
                        "Overview of your employee management system"
                );
                break;

            case "Employees":
                pageTitle.setText("Employees");
                pageSubtitle.setText(
                        "View and manage your employees"
                );
                break;

            case "Add Employee":
                pageTitle.setText("Add Employee");
                pageSubtitle.setText(
                        "Create a new employee record"
                );
                break;
        }

        for (JButton button : navigationButtons) {

            boolean selected = screen.equals(
                    button.getClientProperty("screen")
            );

            button.putClientProperty("active", selected);

            button.setBackground(
                    selected ? SIDEBAR_HOVER : DARK_PURPLE
            );

            if (button.getComponentCount() > 0) {

    Component indicator = button.getComponent(0);

    if (indicator instanceof JLabel) {

        JLabel indicatorLabel = (JLabel) indicator;

        indicatorLabel.setBackground(GREEN);
        indicatorLabel.setOpaque(selected);
    }
}
            button.repaint();
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // ================= HEADER =================

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(WHITE);

        header.setBorder(
                new EmptyBorder(19, 35, 19, 35)
        );

        // Page Information
        JPanel titlePanel = new JPanel();

        titlePanel.setLayout(new BoxLayout(
                titlePanel, BoxLayout.Y_AXIS
        ));

        titlePanel.setBackground(WHITE);

        pageTitle = new JLabel("Dashboard");

        pageTitle.setFont(new Font(
                "Segoe UI", Font.BOLD, 20
        ));

        pageTitle.setForeground(TEXT);

        pageSubtitle = new JLabel(
                "Overview of your employee management system"
        );

        pageSubtitle.setFont(new Font(
                "Segoe UI", Font.PLAIN, 12
        ));

        pageSubtitle.setForeground(MUTED);

        titlePanel.add(pageTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(pageSubtitle);

        // Admin Profile
        JPanel adminPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 3)
        );

        adminPanel.setBackground(WHITE);

        JPanel avatar = new JPanel(new GridBagLayout());

        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setBackground(LIGHT_PURPLE());

        JLabel avatarLetter = new JLabel("A");

        avatarLetter.setFont(new Font(
                "Segoe UI", Font.BOLD, 16
        ));

        avatarLetter.setForeground(PURPLE);

        avatar.add(avatarLetter);

        JPanel adminInfo = new JPanel();

        adminInfo.setLayout(new BoxLayout(
                adminInfo, BoxLayout.Y_AXIS
        ));

        adminInfo.setBackground(WHITE);

        JLabel adminName = new JLabel("Administrator");

        adminName.setFont(new Font(
                "Segoe UI", Font.BOLD, 13
        ));

        adminName.setForeground(TEXT);

        JLabel adminRole = new JLabel("System Admin");

        adminRole.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11
        ));

        adminRole.setForeground(MUTED);

        adminInfo.add(adminName);
        adminInfo.add(Box.createVerticalStrut(4));
        adminInfo.add(adminRole);

        adminPanel.add(adminInfo);
        adminPanel.add(avatar);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(adminPanel, BorderLayout.EAST);

        return header;
    }

    private Color LIGHT_PURPLE() {
        return new Color(239, 233, 248);
    }

    // ================= DASHBOARD =================

    private JPanel createDashboard() {

        JPanel dashboard = new JPanel(
                new BorderLayout(0, 28)
        );

        dashboard.setBackground(BACKGROUND);

        dashboard.setBorder(
                new EmptyBorder(35, 40, 35, 40)
        );

        // Welcome
        JPanel welcomePanel = new JPanel();

        welcomePanel.setLayout(new BoxLayout(
                welcomePanel, BoxLayout.Y_AXIS
        ));

        welcomePanel.setBackground(BACKGROUND);

        JLabel welcome = new JLabel("Welcome Back!");

        welcome.setFont(new Font(
                "Segoe UI", Font.BOLD, 27
        ));

        welcome.setForeground(TEXT);

        JLabel description = new JLabel(
                "Here's what's happening with your employees today."
        );

        description.setFont(new Font(
                "Segoe UI", Font.PLAIN, 14
        ));

        description.setForeground(MUTED);

        welcomePanel.add(welcome);
        welcomePanel.add(Box.createVerticalStrut(8));
        welcomePanel.add(description);

        dashboard.add(welcomePanel, BorderLayout.NORTH);

        // Center
        JPanel centerPanel = new JPanel();

        centerPanel.setLayout(new BoxLayout(
                centerPanel, BoxLayout.Y_AXIS
        ));

        centerPanel.setBackground(BACKGROUND);

        // Statistics
        JPanel cardsPanel = new JPanel(
                new GridLayout(1, 4, 16, 0)
        );

        cardsPanel.setBackground(BACKGROUND);

        cardsPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 150)
        );

        cardsPanel.setPreferredSize(
                new Dimension(850, 150)
        );

        cardsPanel.add(createStatCard(
                "Total Employees", "0",
                "All employees", PURPLE
        ));

        cardsPanel.add(createStatCard(
                "Full-Time", "0",
                "Full-time staff", GREEN
        ));

        cardsPanel.add(createStatCard(
                "Part-Time", "0",
                "Part-time staff", new Color(215, 155, 85)
        ));

        cardsPanel.add(createStatCard(
                "Departments", "5",
                "Available departments", new Color(100, 145, 190)
        ));

        centerPanel.add(cardsPanel);

        centerPanel.add(Box.createVerticalStrut(30));

        // Recent Employees
        RoundedPanel recentPanel = new RoundedPanel(
                20, WHITE
        );

        recentPanel.setLayout(
                new BorderLayout(0, 20)
        );

        recentPanel.setBorder(
                new EmptyBorder(25, 27, 25, 27)
        );

        recentPanel.setPreferredSize(
                new Dimension(850, 280)
        );

        recentPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 280)
        );

        JPanel recentHeader = new JPanel(
                new BorderLayout()
        );

        recentHeader.setBackground(WHITE);

        JLabel recentTitle = new JLabel(
                "Recent Employees"
        );

        recentTitle.setFont(new Font(
                "Segoe UI", Font.BOLD, 18
        ));

        recentTitle.setForeground(TEXT);

        JLabel recentSubtitle = new JLabel(
                "Your latest employee records"
        );

        recentSubtitle.setFont(new Font(
                "Segoe UI", Font.PLAIN, 12
        ));

        recentSubtitle.setForeground(MUTED);

        JPanel recentText = new JPanel();

        recentText.setLayout(new BoxLayout(
                recentText, BoxLayout.Y_AXIS
        ));

        recentText.setBackground(WHITE);

        recentText.add(recentTitle);
        recentText.add(Box.createVerticalStrut(5));
        recentText.add(recentSubtitle);

        recentHeader.add(
                recentText, BorderLayout.WEST
        );

        JLabel emptyMessage = new JLabel(
                "No employees added yet."
        );

        emptyMessage.setFont(new Font(
                "Segoe UI", Font.PLAIN, 14
        ));

        emptyMessage.setForeground(MUTED);

        emptyMessage.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        recentPanel.add(
                recentHeader, BorderLayout.NORTH
        );

        recentPanel.add(
                emptyMessage, BorderLayout.CENTER
        );

        centerPanel.add(recentPanel);

        dashboard.add(centerPanel, BorderLayout.CENTER);

        return dashboard;
    }

    // ================= STATISTICS CARDS =================

    private JPanel createStatCard(
            String title,
            String number,
            String description,
            Color accent) {

        RoundedPanel card = new RoundedPanel(
                18, WHITE
        );

        card.setLayout(new BoxLayout(
                card, BoxLayout.Y_AXIS
        ));

        card.setBorder(
                new EmptyBorder(20, 18, 18, 18)
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(new Font(
                "Segoe UI", Font.PLAIN, 12
        ));

        titleLabel.setForeground(MUTED);

        JLabel numberLabel = new JLabel(number);

        numberLabel.setFont(new Font(
                "Segoe UI", Font.BOLD, 30
        ));

        numberLabel.setForeground(accent);

        JLabel descriptionLabel = new JLabel(
                description
        );

        descriptionLabel.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11
        ));

        descriptionLabel.setForeground(MUTED);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(13));
        card.add(numberLabel);
        card.add(Box.createVerticalStrut(7));
        card.add(descriptionLabel);

        return card;
    }

    // ================= ROUNDED PANEL =================

    private class RoundedPanel extends JPanel {

        private final int radius;
        private final Color backgroundColor;

        public RoundedPanel(
                int radius,
                Color backgroundColor) {

            this.radius = radius;
            this.backgroundColor = backgroundColor;

            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // Shadow
            g2.setColor(
                    new Color(40, 30, 60, 12)
            );

            g2.fillRoundRect(
                    2, 4,
                    getWidth() - 5,
                    getHeight() - 6,
                    radius, radius
            );

            // Background
            g2.setColor(backgroundColor);

            g2.fillRoundRect(
                    0, 0,
                    getWidth() - 5,
                    getHeight() - 7,
                    radius, radius
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // ================= MAIN =================

    public static void main(String[] args) {

        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {

            MainFrame frame = new MainFrame();

            frame.setVisible(true);

        });
    }
}
