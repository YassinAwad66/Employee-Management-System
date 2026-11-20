package gui;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class EmployeeFormPanel extends JPanel {

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
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // Personal Information
    private final JTextField txtId = createTextField();
    private final JTextField txtName = createTextField();
    private final JTextField txtAge = createTextField();
    private final JTextField txtPosition = createTextField();

    // Job Information
    private final JComboBox<String> cmbDepartment = new JComboBox<>();
    private final JComboBox<String> cmbType = new JComboBox<>(new String[]{
        "Full-Time", "Part-Time"
    });

    // Salary Information
    private final JTextField txtBasicSalary = createTextField();
    private final JTextField txtBonus = createTextField();
    private final JTextField txtHourlyRate = createTextField();
    private final JTextField txtHoursWorked = createTextField();

    private final CardLayout salaryCardLayout = new CardLayout();
    private final JPanel salaryCards = new JPanel(salaryCardLayout);

    public EmployeeFormPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(30, 35, 30, 35));

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(BG);

        mainContent.add(createHeader());
        mainContent.add(Box.createVerticalStrut(25));

        mainContent.add(createPersonalSection());
        mainContent.add(Box.createVerticalStrut(18));

        mainContent.add(createJobSection());
        mainContent.add(Box.createVerticalStrut(18));

        mainContent.add(createSalarySection());

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG);
        scrollPane.getViewport().setBackground(BG);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        reloadDepartments(List.of(
                "IT",
                "Human Resources",
                "Finance",
                "Marketing",
                "Sales"
        ));

        cmbType.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                onTypeChanged();
            }
        });

        onTypeChanged();
    }

    // ================= HEADER =================

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);

        JLabel title = new JLabel("Employee Information");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Enter employee details and compensation information");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BG);

        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(7));
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    // ================= PERSONAL SECTION =================

    private JPanel createPersonalSection() {
        JPanel card = createCard("01", "Personal Information", "Basic employee details");
        JPanel grid = createGrid();

        grid.add(createFieldGroup("Employee ID", txtId));
        grid.add(createFieldGroup("Full Name", txtName));
        grid.add(createFieldGroup("Age", txtAge));
        grid.add(createFieldGroup("Position", txtPosition));

        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    // ================= JOB SECTION =================

    private JPanel createJobSection() {
        JPanel card = createCard("02", "Job Information", "Department and employment type");
        JPanel grid = createGrid();

        styleComboBox(cmbDepartment);
        styleComboBox(cmbType);

        grid.add(createFieldGroup("Department", cmbDepartment));
        grid.add(createFieldGroup("Employee Type", cmbType));

        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    // ================= SALARY SECTION =================

    private JPanel createSalarySection() {
        JPanel card = createCard("03", "Compensation Details", "Salary and payment information");

        JPanel fullTimePanel = createGrid();
        fullTimePanel.add(createFieldGroup("Basic Monthly Salary (EGP)", txtBasicSalary));
        fullTimePanel.add(createFieldGroup("Monthly Bonus (EGP)", txtBonus));

        JPanel partTimePanel = createGrid();
        partTimePanel.add(createFieldGroup("Hourly Rate (EGP)", txtHourlyRate));
        partTimePanel.add(createFieldGroup("Hours Worked / Month", txtHoursWorked));

        salaryCards.setOpaque(false);
        salaryCards.add(fullTimePanel, "Full-Time");
        salaryCards.add(partTimePanel, "Part-Time");

        card.add(salaryCards, BorderLayout.CENTER);
        return card;
    }

    // ================= CARD DESIGN =================

    private JPanel createCard(String number, String title, String description) {
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

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SECTION_FONT);
        titleLabel.setForeground(TEXT);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionLabel.setForeground(MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(descriptionLabel);

        heading.add(numberLabel, BorderLayout.WEST);
        heading.add(titlePanel, BorderLayout.CENTER);

        card.add(heading, BorderLayout.NORTH);
        return card;
    }

    private JPanel createGrid() {
        JPanel grid = new JPanel(new GridLayout(0, 2, 25, 20));
        grid.setBackground(WHITE);
        return grid;
    }

    private JPanel createFieldGroup(String labelText, JComponent input) {
        JPanel group = new JPanel(new BorderLayout(0, 9));
        group.setBackground(WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT);

        group.add(label, BorderLayout.NORTH);
        group.add(input, BorderLayout.CENTER);
        return group;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(INPUT_FONT);
        field.setForeground(TEXT);
        field.setCaretColor(PURPLE);
        field.setBackground(new Color(250, 249, 252));
        field.setPreferredSize(new Dimension(200, 44));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 10),
                new EmptyBorder(0, 13, 0, 13)
        ));
        return field;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(INPUT_FONT);
        combo.setForeground(TEXT);
        combo.setBackground(new Color(250, 249, 252));
        combo.setPreferredSize(new Dimension(200, 44));
        combo.setBorder(new RoundedBorder(BORDER, 10));
    }

    // ================= BEHAVIOR & STATE =================

    public void onTypeChanged() {
        String selectedType = getEmployeeType();
        if ("Part-Time".equalsIgnoreCase(selectedType)) {
            salaryCardLayout.show(salaryCards, "Part-Time");
        } else {
            salaryCardLayout.show(salaryCards, "Full-Time");
        }
        salaryCards.revalidate();
        salaryCards.repaint();
    }

    public void reloadDepartments(List<String> departments) {
        String currentSelection = (String) cmbDepartment.getSelectedItem();
        cmbDepartment.removeAllItems();

        if (departments != null) {
            for (String dept : departments) {
                cmbDepartment.addItem(dept);
            }
        }

        if (currentSelection != null) {
            cmbDepartment.setSelectedItem(currentSelection);
        }
    }

    public void setIdEditable(boolean editable) {
        txtId.setEditable(editable);
        txtId.setEnabled(editable);
    }

    public void loadEmployeeData(
            int id,
            String fullName,
            int age,
            String position,
            String department,
            String type,
            Double basicSalary,
            Double bonus,
            Double hourlyRate,
            Double hoursWorked) {

        txtId.setText(String.valueOf(id));
        txtName.setText(fullName != null ? fullName : "");
        txtAge.setText(String.valueOf(age));
        txtPosition.setText(position != null ? position : "");

        if (department != null) {
            cmbDepartment.setSelectedItem(department);
        }

        if ("Part-Time".equalsIgnoreCase(type)) {
            cmbType.setSelectedItem("Part-Time");
            txtHourlyRate.setText(hourlyRate != null ? String.valueOf(hourlyRate) : "");
            txtHoursWorked.setText(hoursWorked != null ? String.valueOf(hoursWorked) : "");
            txtBasicSalary.setText("");
            txtBonus.setText("");
        } else {
            cmbType.setSelectedItem("Full-Time");
            txtBasicSalary.setText(basicSalary != null ? String.valueOf(basicSalary) : "");
            txtBonus.setText(bonus != null ? String.valueOf(bonus) : "");
            txtHourlyRate.setText("");
            txtHoursWorked.setText("");
        }

        onTypeChanged();
    }

    public void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtAge.setText("");
        txtPosition.setText("");

        txtBasicSalary.setText("");
        txtBonus.setText("");
        txtHourlyRate.setText("");
        txtHoursWorked.setText("");

        if (cmbDepartment.getItemCount() > 0) {
            cmbDepartment.setSelectedIndex(0);
        }

        cmbType.setSelectedItem("Full-Time");
        onTypeChanged();
    }

    // ================= VALIDATION =================

    public void validateFormInput() throws IllegalArgumentException {
        if (getEmployeeId().isEmpty()
                || getEmployeeName().isEmpty()
                || getAge().isEmpty()
                || getJobTitle().isEmpty()) {
            throw new IllegalArgumentException("Please fill in all required employee fields.");
        }

        if (getEmployeeName().contains(",") || getJobTitle().contains(",")) {
            throw new IllegalArgumentException("Full Name and Position must not contain commas (,).");
        }

        if (getDepartment() == null || getDepartment().trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a valid department.");
        }

        int id;
        int age;
        try {
            id = Integer.parseInt(getEmployeeId());
            age = Integer.parseInt(getAge());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Employee ID and Age must be valid integers.");
        }

        if (id <= 0) {
            throw new IllegalArgumentException("Employee ID must be a positive integer.");
        }

        if (age <= 0) {
            throw new IllegalArgumentException("Age must be a positive integer.");
        }

        if ("Part-Time".equalsIgnoreCase(getEmployeeType())) {
            parseNonNegative(getHourlyRate(), "Hourly Rate");
            parseNonNegative(getHoursWorked(), "Hours Worked");
        } else {
            parseNonNegative(getBasicSalary(), "Basic Salary");
            parseNonNegative(getBonus(), "Bonus");
        }
    }

    private double parseNonNegative(String raw, String fieldName) {
        if (raw.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        try {
            double value = Double.parseDouble(raw);
            if (!Double.isFinite(value) || value < 0) {
                throw new IllegalArgumentException(fieldName + " must be a non-negative number.");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    // ================= GETTERS =================

    public String getEmployeeId() { return txtId.getText().trim(); }
    public String getEmployeeName() { return txtName.getText().trim(); }
    public String getAge() { return txtAge.getText().trim(); }
    public String getJobTitle() { return txtPosition.getText().trim(); }
    public String getDepartment() { return (String) cmbDepartment.getSelectedItem(); }
    public String getEmployeeType() { return (String) cmbType.getSelectedItem(); }
    public String getBasicSalary() { return txtBasicSalary.getText().trim(); }
    public String getBonus() { return txtBonus.getText().trim(); }
    public String getHourlyRate() { return txtHourlyRate.getText().trim(); }
    public String getHoursWorked() { return txtHoursWorked.getText().trim(); }

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