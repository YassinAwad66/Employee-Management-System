package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import service.*;

public class DashboardPanel extends JPanel {

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

    private final JLabel lblWelcome = new JLabel("Welcome, Admin");
    private final JLabel lblTotalEmployees = new JLabel("0");
    private final JLabel lblTotalDepartments = new JLabel("0");
    private final JLabel lblTotalPayroll = new JLabel("0.00 EGP");

    private final EmployeeManager employeeManager;
    private final DepartmentManager departmentManager;

    public DashboardPanel(EmployeeManager em, DepartmentManager dm) {
        this.employeeManager = em;
        this.departmentManager = dm;

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(30, 35, 30, 35));

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(BG);

        mainContent.add(createHeader());
        mainContent.add(Box.createVerticalStrut(25));
        mainContent.add(createStatsRow());

        add(mainContent, BorderLayout.NORTH);

        refreshStats();
    }

    // ================= HEADER =================

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);

        lblWelcome.setFont(TITLE_FONT);
        lblWelcome.setForeground(TEXT);

        JLabel subtitle = new JLabel("Here is a quick overview of your company");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BG);

        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(lblWelcome);
        textPanel.add(Box.createVerticalStrut(7));
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    // ================= STATS =================

    private JPanel createStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 25, 0));
        row.setBackground(BG);

        row.add(createStatCard("01", "Total Employees", lblTotalEmployees));
        row.add(createStatCard("02", "Total Departments", lblTotalDepartments));
        row.add(createStatCard("03", "Total Monthly Payroll", lblTotalPayroll));
        return row;
    }

    private JPanel createStatCard(String number, String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 22));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 16),
                new EmptyBorder(22, 25, 25, 25)
        ));

        JPanel heading = new JPanel(new BorderLayout(14, 0));
        heading.setBackground(WHITE);

        JLabel numberLabel = new JLabel(number);
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        numberLabel.setForeground(PURPLE);
        numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        numberLabel.setOpaque(true);
        numberLabel.setBackground(LIGHT_PURPLE);
        numberLabel.setPreferredSize(new Dimension(38, 38));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(MUTED);

        heading.add(numberLabel, BorderLayout.WEST);
        heading.add(titleLabel, BorderLayout.CENTER);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(TEXT);

        card.add(heading, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // ================= BEHAVIOR =================

    public void refreshStats() {
        lblWelcome.setText("Welcome, Admin");
        lblTotalEmployees.setText(String.valueOf(employeeManager.getEmployeeCount()));
        lblTotalDepartments.setText(String.valueOf(departmentManager.getAllDepartments().size()));
        lblTotalPayroll.setText(String.format("%,.2f EGP", employeeManager.getTotalPayroll()));
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