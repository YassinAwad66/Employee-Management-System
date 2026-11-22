package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.*;
import service.*;

public class MainFrame extends JFrame {

    // Colors
    private static final Color BG = new Color(246, 245, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color PURPLE = new Color(105, 76, 160);
    private static final Color LIGHT_PURPLE = new Color(239, 234, 248);
    private static final Color TEXT = new Color(45, 42, 55);
    private static final Color MUTED = new Color(135, 130, 145);
    private static final Color BORDER = new Color(230, 226, 237);

    // Panel names
    private static final String DASHBOARD = "Dashboard";
    private static final String ADD = "Add Employee";
    private static final String VIEW = "View Employees";
    private static final String SEARCH = "Search";
    private static final String UPDATE = "Update Employee";
    private static final String DEPARTMENTS = "Departments";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    private DashboardPanel dashboardPanel;
    private AddEmployeePanel addPanel;
    private ViewEmployeesPanel viewPanel;
    private SearchPanel searchPanel;
    private UpdateEmployeePanel updatePanel;
    private DepartmentPanel departmentPanel;

    private final EmployeeManager employeeManager;
    private final DepartmentManager departmentManager;
    private final AuthService authService;

    private final java.util.Map<String, JButton> navButtons = new java.util.LinkedHashMap<>();

    public MainFrame(EmployeeManager em, DepartmentManager dm, AuthService a) {
        this.employeeManager = em;
        this.departmentManager = dm;
        this.authService = a;

        setTitle("Employee Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        Runnable refresh = this::refreshAllPanels;

        dashboardPanel = new DashboardPanel(em, dm);
        addPanel = new AddEmployeePanel(em, dm, refresh);
        viewPanel = new ViewEmployeesPanel(em, dm, refresh);
        searchPanel = new SearchPanel(em, dm);
        updatePanel = new UpdateEmployeePanel(em, dm, refresh);
        departmentPanel = new DepartmentPanel(dm, em, refresh);

        contentPanel.setBackground(BG);
        contentPanel.add(dashboardPanel, DASHBOARD);
        contentPanel.add(addPanel, ADD);
        contentPanel.add(viewPanel, VIEW);
        contentPanel.add(searchPanel, SEARCH);
        contentPanel.add(updatePanel, UPDATE);
        contentPanel.add(departmentPanel, DEPARTMENTS);

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);
        setContentPane(root);

        showPanel(DASHBOARD);
    }

    // ================= SIDEBAR =================

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(WHITE);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER),
                new EmptyBorder(25, 18, 25, 18)
        ));

        // Brand
        JLabel badge = new JLabel("EMS", SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badge.setForeground(WHITE);
        badge.setOpaque(true);
        badge.setBackground(PURPLE);
        badge.setPreferredSize(new Dimension(44, 44));

        JLabel title = new JLabel("Employee System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Admin Panel");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(MUTED);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setBackground(WHITE);
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(3));
        titleBox.add(subtitle);

        JPanel brand = new JPanel(new BorderLayout(12, 0));
        brand.setBackground(WHITE);
        brand.add(badge, BorderLayout.WEST);
        brand.add(titleBox, BorderLayout.CENTER);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(35));

        String[] names = {DASHBOARD, ADD, VIEW, SEARCH, UPDATE, DEPARTMENTS};
        for (String name : names) {
            JButton btn = createNavButton(name);
            navButtons.put(name, btn);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(8));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton logout = createNavButton("Logout");
        logout.setForeground(new Color(190, 60, 70));
        logout.addActionListener(e -> handleLogout());
        sidebar.add(logout);

        return sidebar;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(TEXT);
        btn.setBackground(WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(12, 16, 12, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.addActionListener(e -> showPanel(text));
        return btn;
    }

    private void highlight(String active) {
        for (java.util.Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            boolean isActive = entry.getKey().equals(active);
            entry.getValue().setBackground(isActive ? LIGHT_PURPLE : WHITE);
            entry.getValue().setForeground(isActive ? PURPLE : TEXT);
        }
    }

    // ================= NAVIGATION =================

    public void showPanel(String name) {
        switch (name) {
            case DASHBOARD -> dashboardPanel.refreshStats();
            case ADD -> addPanel.refresh();
            case VIEW -> viewPanel.refreshTable();
            case SEARCH -> searchPanel.refresh();
            case UPDATE -> updatePanel.refresh();
            case DEPARTMENTS -> departmentPanel.refreshTable();
            default -> { }
        }
        cardLayout.show(contentPanel, name);
        highlight(name);
    }

    public void refreshAllPanels() {
        dashboardPanel.refreshStats();
        addPanel.refresh();
        viewPanel.refreshTable();
        searchPanel.refresh();
        updatePanel.refresh();
        departmentPanel.refreshTable();
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?",
                "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame(authService, employeeManager, departmentManager).setVisible(true);
        }
    }
}