package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    private static final Color DANGER = new Color(190, 60, 70);
    private static final Color HOVER = new Color(247, 244, 251);
    private static final Color DANGER_HOVER = new Color(255, 242, 243);

    // Page names (also used as the button text and page title)
    private static final String DASHBOARD = "Dashboard";
    private static final String ADD = "Add Employee";
    private static final String VIEW = "View Employees";
    private static final String SEARCH = "Search";
    private static final String UPDATE = "Update Employee";
    private static final String DEPARTMENTS = "Departments";

    // CardLayout shows one page at a time
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final JLabel pageTitle = createLabel(DASHBOARD, Font.BOLD, 23, TEXT);

    // Pages
    private DashboardPanel dashboardPanel;
    private AddEmployeePanel addPanel;
    private ViewEmployeesPanel viewPanel;
    private SearchPanel searchPanel;
    private UpdateEmployeePanel updatePanel;
    private DepartmentPanel departmentPanel;

    // Services
    private final EmployeeManager employeeManager;
    private final DepartmentManager departmentManager;
    private final AuthService authService;

    // Sidebar buttons, stored by page name
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private String currentPanel = DASHBOARD;

    public MainFrame(EmployeeManager em, DepartmentManager dm, AuthService authService) {
        if (em == null || dm == null || authService == null) {
            throw new IllegalArgumentException("Application services cannot be null.");
        }

        this.employeeManager = em;
        this.departmentManager = dm;
        this.authService = authService;

        initializeFrame();
        initializePanels();
        initializeLayout();
        showPanel(DASHBOARD);
    }

    // ================= SETUP =================

    private void initializeFrame() {
        setTitle("Employee Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 780);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null); // center on screen
        setBackground(BG);
    }

    private void initializePanels() {
        // Pages call this after they change data
        Runnable refresh = this::refreshAllPanels;

        dashboardPanel = new DashboardPanel(employeeManager, departmentManager);
        addPanel = new AddEmployeePanel(employeeManager, departmentManager, refresh);
        viewPanel = new ViewEmployeesPanel(employeeManager, departmentManager, refresh);
        searchPanel = new SearchPanel(employeeManager);
        updatePanel = new UpdateEmployeePanel(employeeManager, departmentManager, refresh);
        departmentPanel = new DepartmentPanel(departmentManager, employeeManager, refresh);

        contentPanel.setBackground(BG);
        contentPanel.add(dashboardPanel, DASHBOARD);
        contentPanel.add(addPanel, ADD);
        contentPanel.add(viewPanel, VIEW);
        contentPanel.add(searchPanel, SEARCH);
        contentPanel.add(updatePanel, UPDATE);
        contentPanel.add(departmentPanel, DEPARTMENTS);
    }

    // Sidebar on the left, header and page in the rest
    private void initializeLayout() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainContent(), BorderLayout.CENTER);
        setContentPane(root);
    }

    // ================= SIDEBAR =================

    private JPanel buildSidebar() {
        JPanel sidebar = createVerticalPanel();
        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER), // right line
                new EmptyBorder(28, 18, 25, 18))); // padding

        sidebar.add(buildBrand());
        sidebar.add(Box.createVerticalStrut(40));
        sidebar.add(createLabel("MAIN MENU", Font.BOLD, 11, MUTED));
        sidebar.add(Box.createVerticalStrut(15));

        String[] pages = { DASHBOARD, ADD, VIEW, SEARCH, UPDATE, DEPARTMENTS };

        for (String page : pages) {
            JButton button = createNavButton(page);
            navButtons.put(page, button);
            sidebar.add(button);
            sidebar.add(Box.createVerticalStrut(8));
        }

        sidebar.add(Box.createVerticalGlue()); // pushes logout to the bottom

        JSeparator separator = new JSeparator();
        separator.setForeground(BORDER);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        sidebar.add(separator);
        sidebar.add(Box.createVerticalStrut(18));
        sidebar.add(createLogoutButton());
        return sidebar;
    }

    // "EMS" square with two lines of text beside it
    private JPanel buildBrand() {
        JLabel badge = createLabel("EMS", Font.BOLD, 14, WHITE);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setBackground(PURPLE);
        badge.setOpaque(true);
        badge.setPreferredSize(new Dimension(48, 48));

        JPanel textPanel = createVerticalPanel();
        textPanel.add(createLabel("Employee System", Font.BOLD, 16, TEXT));
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(createLabel("Management Portal", Font.PLAIN, 11, MUTED));

        JPanel brand = new JPanel(new BorderLayout(12, 0));
        brand.setBackground(WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        brand.add(badge, BorderLayout.WEST);
        brand.add(textPanel, BorderLayout.CENTER);
        return brand;
    }

    private JButton createNavButton(String page) {
        JButton button = createSidebarButton(page, TEXT);
        button.addActionListener(e -> showPanel(page));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!page.equals(currentPanel)) {
                    button.setBackground(HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                updateNavigationHighlight();
            }
        });
        return button;
    }

    private JButton createLogoutButton() {
        JButton button = createSidebarButton("Logout", DANGER);
        button.addActionListener(e -> handleLogout());

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(DANGER_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(WHITE);
            }
        });
        return button;
    }

    // The style shared by all sidebar buttons
    private JButton createSidebarButton(String text, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(textColor);
        button.setBackground(WHITE);
        button.setHorizontalAlignment(SwingConstants.LEFT); // text position
        button.setAlignmentX(Component.LEFT_ALIGNMENT); // button position
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(13, 16, 13, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        return button;
    }

    // ================= HEADER AND CONTENT =================

    private JPanel buildMainContent() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.setBorder(new EmptyBorder(22, 25, 22, 25));
        wrapper.add(contentPanel, BorderLayout.CENTER);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);
        main.add(buildHeader(), BorderLayout.NORTH);
        main.add(wrapper, BorderLayout.CENTER);
        return main;
    }

    private JPanel buildHeader() {
        JPanel titlePanel = createVerticalPanel();
        titlePanel.add(pageTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(createLabel("Manage your organization efficiently", Font.PLAIN, 12, MUTED));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER), // bottom line
                new EmptyBorder(20, 28, 20, 28))); // padding
        header.add(titlePanel, BorderLayout.WEST);
        header.add(createLabel("Administrator", Font.BOLD, 13, PURPLE), BorderLayout.EAST);
        return header;
    }

    // ================= HELPERS =================

    private JLabel createLabel(String text, int style, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", style, size));
        label.setForeground(color);
        return label;
    }

    // A white panel that stacks its components from top to bottom
    private JPanel createVerticalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(WHITE);
        return panel;
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
            ThemeDialog.showError(
                    this,
                    "Unable to load the selected page.\n" + ex.getMessage());
        }
    }

    // Color the current page's button purple, the others normal
    private void updateNavigationHighlight() {
        for (String page : navButtons.keySet()) {
            boolean active = page.equals(currentPanel);
            JButton button = navButtons.get(page);
            button.setBackground(active ? LIGHT_PURPLE : WHITE);
            button.setForeground(active ? PURPLE : TEXT);
        }
    }

    // ================= REFRESH =================

    // Reload the data of one page
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
        }
    }

    // Reload the data of every page
    public void refreshAllPanels() {
        for (String page : navButtons.keySet()) {
            refreshPanel(page);
        }
    }

    // ================= LOGOUT =================

    private void handleLogout() {

        boolean confirmed = ThemeDialog.showConfirm(
                this,
                "Confirm Logout",
                "Are you sure you want to log out?");

        if (!confirmed) {
            return;
        }

        dispose();

        SwingUtilities.invokeLater(() -> new LoginFrame(
                authService,
                employeeManager,
                departmentManager).setVisible(true));
    }
}