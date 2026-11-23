package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import model.*;
import service.*;

public class MainFrame extends JFrame {

    // ================= COLORS =================

    private static final Color BG = new Color(246, 245, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color PURPLE = new Color(105, 76, 160);
    private static final Color DARK_PURPLE = new Color(83, 57, 133);
    private static final Color LIGHT_PURPLE = new Color(239, 234, 248);
    private static final Color TEXT = new Color(45, 42, 55);
    private static final Color MUTED = new Color(135, 130, 145);
    private static final Color BORDER = new Color(230, 226, 237);
    private static final Color DANGER = new Color(190, 60, 70);
    private static final Color HOVER = new Color(247, 244, 251);

    // ================= PANEL NAMES =================

    private static final String DASHBOARD = "Dashboard";
    private static final String ADD = "Add Employee";
    private static final String VIEW = "View Employees";
    private static final String SEARCH = "Search";
    private static final String UPDATE = "Update Employee";
    private static final String DEPARTMENTS = "Departments";

    // ================= COMPONENTS =================

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    private final JLabel pageTitle = new JLabel(DASHBOARD);
    private final JLabel welcomeLabel = new JLabel("Manage your organization efficiently");

    private DashboardPanel dashboardPanel;
    private AddEmployeePanel addPanel;
    private ViewEmployeesPanel viewPanel;
    private SearchPanel searchPanel;
    private UpdateEmployeePanel updatePanel;
    private DepartmentPanel departmentPanel;

    // ================= SERVICES =================

    private final EmployeeManager employeeManager;
    private final DepartmentManager departmentManager;
    private final AuthService authService;

    // ================= NAVIGATION =================

    private final java.util.Map<String, JButton> navButtons =
            new java.util.LinkedHashMap<>();

    private String currentPanel = DASHBOARD;

    // ================= CONSTRUCTOR =================

    public MainFrame(
            EmployeeManager em,
            DepartmentManager dm,
            AuthService authService
    ) {

        if (em == null || dm == null || authService == null) {
            throw new IllegalArgumentException(
                    "Application services cannot be null."
            );
        }

        this.employeeManager = em;
        this.departmentManager = dm;
        this.authService = authService;

        initializeFrame();
        initializePanels();
        initializeLayout();

        showPanel(DASHBOARD);
    }

    // ================= FRAME INITIALIZATION =================

    private void initializeFrame() {

        setTitle("Employee Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1250, 780);
        setMinimumSize(new Dimension(1050, 680));

        setLocationRelativeTo(null);
        setBackground(BG);
    }

    // ================= PANELS INITIALIZATION =================

    private void initializePanels() {

        Runnable refresh = this::refreshAllPanels;

        dashboardPanel = new DashboardPanel(
                employeeManager,
                departmentManager
        );

        addPanel = new AddEmployeePanel(
                employeeManager,
                departmentManager,
                refresh
        );

        viewPanel = new ViewEmployeesPanel(
                employeeManager,
                departmentManager,
                refresh
        );

        searchPanel = new SearchPanel(
                employeeManager,
                departmentManager
        );

        updatePanel = new UpdateEmployeePanel(
                employeeManager,
                departmentManager,
                refresh
        );

        departmentPanel = new DepartmentPanel(
                departmentManager,
                employeeManager,
                refresh
        );

        contentPanel.setBackground(BG);

        contentPanel.add(dashboardPanel, DASHBOARD);
        contentPanel.add(addPanel, ADD);
        contentPanel.add(viewPanel, VIEW);
        contentPanel.add(searchPanel, SEARCH);
        contentPanel.add(updatePanel, UPDATE);
        contentPanel.add(departmentPanel, DEPARTMENTS);
    }

    // ================= MAIN LAYOUT =================

    private void initializeLayout() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainContent(), BorderLayout.CENTER);

        setContentPane(root);
    }

    // ================= SIDEBAR =================

    private JPanel buildSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setLayout(
                new BoxLayout(sidebar, BoxLayout.Y_AXIS)
        );

        sidebar.setBackground(WHITE);
        sidebar.setPreferredSize(new Dimension(245, 0));

        sidebar.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 0, 1, BORDER
                        ),
                        new EmptyBorder(28, 18, 25, 18)
                )
        );

        sidebar.add(buildBrand());

        sidebar.add(Box.createVerticalStrut(40));

        JLabel menuLabel = new JLabel("MAIN MENU");

        menuLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 11)
        );

        menuLabel.setForeground(MUTED);
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(menuLabel);
        sidebar.add(Box.createVerticalStrut(15));

        String[] navigationItems = {
                DASHBOARD,
                ADD,
                VIEW,
                SEARCH,
                UPDATE,
                DEPARTMENTS
        };

        for (String item : navigationItems) {

            JButton button = createNavButton(item);

            navButtons.put(item, button);

            sidebar.add(button);
            sidebar.add(Box.createVerticalStrut(8));
        }

        sidebar.add(Box.createVerticalGlue());

        JSeparator separator = new JSeparator();

        separator.setForeground(BORDER);
        separator.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 1)
        );

        sidebar.add(separator);
        sidebar.add(Box.createVerticalStrut(18));

        JButton logoutButton = createLogoutButton();

        sidebar.add(logoutButton);

        return sidebar;
    }

    // ================= BRAND =================

    private JPanel buildBrand() {

        JLabel badge = new JLabel(
                "EMS",
                SwingConstants.CENTER
        );

        badge.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        badge.setForeground(WHITE);
        badge.setBackground(PURPLE);
        badge.setOpaque(true);

        badge.setPreferredSize(new Dimension(48, 48));
        badge.setMinimumSize(new Dimension(48, 48));
        badge.setMaximumSize(new Dimension(48, 48));

        JLabel title = new JLabel("Employee System");

        title.setFont(
                new Font("Segoe UI", Font.BOLD, 16)
        );

        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Management Portal");

        subtitle.setFont(
                new Font("Segoe UI", Font.PLAIN, 11)
        );

        subtitle.setForeground(MUTED);

        JPanel textPanel = new JPanel();

        textPanel.setLayout(
                new BoxLayout(textPanel, BoxLayout.Y_AXIS)
        );

        textPanel.setBackground(WHITE);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);

        JPanel brand = new JPanel(
                new BorderLayout(12, 0)
        );

        brand.setBackground(WHITE);

        brand.add(badge, BorderLayout.WEST);
        brand.add(textPanel, BorderLayout.CENTER);

        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 52)
        );

        return brand;
    }

    // ================= NAVIGATION BUTTON =================

    private JButton createNavButton(String name) {

        JButton button = new JButton(name);

        button.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        button.setForeground(TEXT);
        button.setBackground(WHITE);

        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setBorder(
                new EmptyBorder(13, 16, 13, 12)
        );

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 46)
        );

        button.addActionListener(
                e -> showPanel(name)
        );

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {

                        if (!name.equals(currentPanel)) {
                            button.setBackground(HOVER);
                        }
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        updateNavigationHighlight();
                    }
                }
        );

        return button;
    }

    // ================= LOGOUT BUTTON =================

    private JButton createLogoutButton() {

        JButton button = new JButton("Logout");

        button.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        button.setForeground(DANGER);
        button.setBackground(WHITE);

        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setBorder(
                new EmptyBorder(13, 16, 13, 12)
        );

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 46)
        );

        button.addActionListener(
                e -> handleLogout()
        );

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {
                        button.setBackground(
                                new Color(255, 242, 243)
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {
                        button.setBackground(WHITE);
                    }
                }
        );

        return button;
    }

    // ================= MAIN CONTENT =================

    private JPanel buildMainContent() {

        JPanel main = new JPanel(
                new BorderLayout()
        );

        main.setBackground(BG);

        main.add(buildHeader(), BorderLayout.NORTH);

        JPanel wrapper = new JPanel(
                new BorderLayout()
        );

        wrapper.setBackground(BG);

        wrapper.setBorder(
                new EmptyBorder(22, 25, 22, 25)
        );

        wrapper.add(contentPanel, BorderLayout.CENTER);

        main.add(wrapper, BorderLayout.CENTER);

        return main;
    }

    // ================= HEADER =================

    private JPanel buildHeader() {

        JPanel header = new JPanel(
                new BorderLayout()
        );

        header.setBackground(WHITE);

        header.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0, BORDER
                        ),
                        new EmptyBorder(20, 28, 20, 28)
                )
        );

        pageTitle.setFont(
                new Font("Segoe UI", Font.BOLD, 23)
        );

        pageTitle.setForeground(TEXT);

        welcomeLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        welcomeLabel.setForeground(MUTED);

        JPanel titlePanel = new JPanel();

        titlePanel.setLayout(
                new BoxLayout(titlePanel, BoxLayout.Y_AXIS)
        );

        titlePanel.setBackground(WHITE);

        titlePanel.add(pageTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(welcomeLabel);

        JLabel adminLabel = new JLabel("Administrator");

        adminLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        adminLabel.setForeground(PURPLE);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(adminLabel, BorderLayout.EAST);

        return header;
    }

    // ================= NAVIGATION =================

    public void showPanel(String name) {

        if (!navButtons.containsKey(name)) {
            return;
        }

        try {

            refreshPanel(name);

            cardLayout.show(contentPanel, name);

            currentPanel = name;

            pageTitle.setText(name);

            updateNavigationHighlight();

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load the selected page.\n"
                            + ex.getMessage(),
                    "Navigation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= REFRESH SELECTED PANEL =================

    private void refreshPanel(String name) {

        switch (name) {

            case DASHBOARD:
                dashboardPanel.refreshStats();
                break;

            case ADD:
                addPanel.refresh();
                break;

            case VIEW:
                viewPanel.refreshTable();
                break;

            case SEARCH:
                searchPanel.refresh();
                break;

            case UPDATE:
                updatePanel.refresh();
                break;

            case DEPARTMENTS:
                departmentPanel.refreshTable();
                break;

            default:
                break;
        }
    }

    // ================= NAVIGATION HIGHLIGHT =================

    private void updateNavigationHighlight() {

        for (
                java.util.Map.Entry<String, JButton> entry
                : navButtons.entrySet()
        ) {

            boolean active = entry.getKey().equals(currentPanel);

            JButton button = entry.getValue();

            button.setBackground(
                    active ? LIGHT_PURPLE : WHITE
            );

            button.setForeground(
                    active ? PURPLE : TEXT
            );
        }
    }

    // ================= REFRESH ALL PANELS =================

    public void refreshAllPanels() {

        dashboardPanel.refreshStats();

        addPanel.refresh();

        viewPanel.refreshTable();

        searchPanel.refresh();

        updatePanel.refresh();

        departmentPanel.refreshTable();
    }

    // ================= LOGOUT =================

    private void handleLogout() {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {

            dispose();

            SwingUtilities.invokeLater(() -> {

                LoginFrame loginFrame = new LoginFrame(
                        authService,
                        employeeManager,
                        departmentManager
                );

                loginFrame.setVisible(true);
            });
        }
    }
}
