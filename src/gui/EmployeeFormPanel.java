package gui;

import exception.ValidationException;
import java.awt.*;
import java.awt.event.ItemEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.*;
import service.DepartmentManager;
import validation.Validator;

public class EmployeeFormPanel extends JPanel {

    private static final Color BG = new Color(246, 245, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color PURPLE = new Color(105, 76, 180);
    private static final Color TEXT = new Color(55, 55, 65);
    private static final Color BORDER = new Color(220, 218, 230);

    private final DepartmentManager dm;
    private final boolean profile;

    private final JTextField id, name, age, position;
    private final JTextField salary, bonus, rate, hours;

    private final JComboBox<String> department = new JComboBox<>();
    private final JComboBox<String> type =
            new JComboBox<>(EmployeeFactory.getSupportedTypes());

    private final CardLayout cards = new CardLayout();
    private final JPanel salaryPanel = new JPanel(cards);

    // Constructors

    public EmployeeFormPanel() { this(null); }

    public EmployeeFormPanel(DepartmentManager dm) { this(dm, false); }

    public EmployeeFormPanel(DepartmentManager dm, boolean profile) {
        this.dm = dm;
        this.profile = profile;

        setLayout(new BorderLayout(0, 15));
        setBackground(BG);
        setBorder(profile ? new EmptyBorder(4, 2, 4, 2)
                          : new EmptyBorder(20, 20, 20, 20));

        id = textField();
        name = textField();
        age = textField();
        position = textField();
        salary = textField();
        bonus = textField();
        rate = textField();
        hours = textField();

        style(department);
        style(type);

        buildForm();

        type.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                showSalary();
        });

        reloadDepartments();
        showSalary();
    }

    // Form Design

    private void buildForm() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(BG);
        if (profile) main.setOpaque(false);

        JPanel personal = section(profile
                ? "01   Personal Information" : "Personal Information");

        addField(personal, "Employee ID", id);
        addField(personal, "Full Name", name);
        addField(personal, "Age", age);

        if (!profile)
            addField(personal, "Job Position", position);

        JPanel job = section(profile
                ? "02   Job Details" : "Employment Information");

        addField(job, "Department", department);

        if (profile)
            addField(job, "Position", position);

        addField(job, "Employee Type", type);

        JPanel full = salaryCard("Basic Salary", salary, "Bonus", bonus);
        JPanel part = salaryCard("Hourly Rate", rate, "Hours Worked", hours);

        salaryPanel.setBackground(WHITE);
        salaryPanel.add(full, "Full-Time");
        salaryPanel.add(part, "Part-Time");

        JPanel payment = card();
        payment.setLayout(new BorderLayout(profile ? 0 : 15,
                                          profile ? 14 : 12));

        payment.add(label(profile ? "03   Salary Details"
                : "Salary Information", profile ? 14 : 16, PURPLE),
                BorderLayout.NORTH);

        payment.add(salaryPanel, BorderLayout.CENTER);

        for (JPanel p : new JPanel[]{personal, job, payment}) {
            if (profile) p.setAlignmentX(Component.LEFT_ALIGNMENT);
            main.add(p);

            if (profile || p != payment)
                main.add(Box.createVerticalStrut(profile ? 12 : 15));
        }

        add(main, profile ? BorderLayout.NORTH : BorderLayout.CENTER);
    }

    private JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(profile
                        ? new Color(230, 226, 239)
                        : new Color(230, 228, 238)),
                profile ? new EmptyBorder(16, 17, 17, 17)
                        : new EmptyBorder(18, 18, 18, 18)));

        return panel;
    }

    private JPanel section(String title) {
        JPanel panel = card();

        if (profile) {
            panel.setLayout(new BorderLayout(0, 14));
            panel.add(label(title, 14, PURPLE), BorderLayout.NORTH);

            JPanel grid = new JPanel(new GridLayout(0, 2, 12, 12));
            grid.setOpaque(false);
            panel.add(grid, BorderLayout.CENTER);
        } else {
            panel.setLayout(new GridLayout(0, 2, 15, 12));
            panel.add(label(title, 16, PURPLE));
            panel.add(new JLabel(""));
        }

        return panel;
    }

    private void addField(JPanel panel, String title, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(0, 7));
        row.setBackground(WHITE);

        row.add(label(title, 12, TEXT), BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);

        if (panel.getLayout() instanceof BorderLayout) {
            BorderLayout layout = (BorderLayout) panel.getLayout();
            JPanel grid = (JPanel) layout.getLayoutComponent(BorderLayout.CENTER);
            grid.add(row);
        } else {
            panel.add(row);
        }
    }

    private JPanel salaryCard(String a, JTextField first,
                              String b, JTextField second) {

        JPanel panel = new JPanel(new GridLayout(0, profile ? 2 : 1, 12, 12));
        panel.setBackground(WHITE);

        addField(panel, a, first);
        addField(panel, b, second);

        return panel;
    }

    private void showSalary() {
        String selected = getEmployeeType();

        if ("Full-Time".equalsIgnoreCase(selected))
            cards.show(salaryPanel, "Full-Time");
        else if ("Part-Time".equalsIgnoreCase(selected))
            cards.show(salaryPanel, "Part-Time");
    }

    // Departments

    public void reloadDepartments() {
        if (dm != null)
            reloadDepartments(dm.getDepartmentNames());
    }

    public void reloadDepartments(String[] names) {
        String selected = getDepartment();
        department.removeAllItems();

        if (names == null) return;

        for (String n : names)
            if (n != null && !n.trim().isEmpty())
                department.addItem(n.trim());

        if (selected != null)
            department.setSelectedItem(selected);
    }

    // Create Employee

    public Employee buildEmployee() throws ValidationException {
        if (dm == null)
            throw new ValidationException("Department manager is not initialized.");

        int i = Validator.parseId(getEmployeeId());
        String n = Validator.requireNotEmpty(getEmployeeName(), "Full Name");
        int a = Validator.parseAge(getAge());
        String p = Validator.requireNotEmpty(getJobTitle(), "Job Position");
        String d = Validator.requireValidDepartment(getDepartment(), dm);
        String t = Validator.requireSupportedType(getEmployeeType());

        double first, second;

        if ("Full-Time".equalsIgnoreCase(t)) {
            first = Validator.parseNonNegativeDouble(getBasicSalary(), "Basic Salary");
            second = Validator.parseNonNegativeDouble(getBonus(), "Bonus");
        } else if ("Part-Time".equalsIgnoreCase(t)) {
            first = Validator.parseNonNegativeDouble(getHourlyRate(), "Hourly Rate");
            second = Validator.parseNonNegativeDouble(getHoursWorked(), "Hours Worked");
        } else {
            throw new ValidationException("Unsupported employee type: " + t);
        }

        return EmployeeFactory.create(t, i, n, a, d, p, first, second);
    }

    // Load Employee

    public void loadEmployee(Employee e) {
        if (e == null) return;

        loadEmployeeData(e.getId(), e.getFullName(), e.getAge(),
                e.getPosition(), e.getDepartmentName(),
                e.getEmployeeType(), null, null, null, null);

        if (e instanceof FullTimeEmployee) {
            FullTimeEmployee f = (FullTimeEmployee) e;
            salary.setText(String.valueOf(f.getBasicSalary()));
            bonus.setText(String.valueOf(f.getBonus()));
        } else if (e instanceof PartTimeEmployee) {
            PartTimeEmployee p = (PartTimeEmployee) e;
            rate.setText(String.valueOf(p.getHourlyRate()));
            hours.setText(String.valueOf(p.getHoursWorked()));
        }
    }

    public void loadEmployeeData(
            int i, String n, int a, String p, String d, String t,
            Double s, Double b, Double r, Double h) {

        id.setText(String.valueOf(i));
        name.setText(n);
        age.setText(String.valueOf(a));
        position.setText(p);
        department.setSelectedItem(d);
        type.setSelectedItem(t);

        if (s != null) salary.setText(s.toString());
        if (b != null) bonus.setText(b.toString());
        if (r != null) rate.setText(r.toString());
        if (h != null) hours.setText(h.toString());

        showSalary();
    }

    // Clear and Edit

    public void clearForm() {
        for (JTextField f : new JTextField[]{
                id, name, age, position, salary, bonus, rate, hours})
            f.setText("");

        if (department.getItemCount() > 0)
            department.setSelectedIndex(0);

        if (type.getItemCount() > 0)
            type.setSelectedIndex(0);

        showSalary();
    }

    public void setIdEditable(boolean editable) {
        id.setEditable(editable);
        id.setBackground(editable ? WHITE : new Color(235, 235, 240));
    }

    // Getters

    public String getEmployeeId() { return id.getText().trim(); }
    public String getEmployeeName() { return name.getText().trim(); }
    public String getAge() { return age.getText().trim(); }
    public String getJobTitle() { return position.getText().trim(); }
    public String getBasicSalary() { return salary.getText().trim(); }
    public String getBonus() { return bonus.getText().trim(); }
    public String getHourlyRate() { return rate.getText().trim(); }
    public String getHoursWorked() { return hours.getText().trim(); }
    public String getDepartment() { return (String) department.getSelectedItem(); }
    public String getEmployeeType() { return (String) type.getSelectedItem(); }

    // UI Helpers

    private JLabel label(String text, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, size));
        l.setForeground(color);
        return l;
    }

    private void style(JComponent c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setForeground(TEXT);
        c.setBackground(WHITE);
        c.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    private JTextField textField() {
        JTextField f = new JTextField(profile ? 10 : 15);
        style(f);

        f.setBorder(BorderFactory.createCompoundBorder(
                f.getBorder(), new EmptyBorder(9, 10, 9, 10)));

        return f;
    }
}