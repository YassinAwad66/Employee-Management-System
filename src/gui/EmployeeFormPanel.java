package gui;

import java.awt.*;
import java.awt.event.ItemEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import exception.ValidationException;
import model.Employee;
import model.EmployeeFactory;
import model.FullTimeEmployee;
import model.PartTimeEmployee;
import service.DepartmentManager;
import validation.Validator;

public class EmployeeFormPanel extends JPanel {

    // ================= COLORS =================

    private static final Color BG = new Color(246, 245, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color PURPLE = new Color(105, 76, 180);
    private static final Color TEXT = new Color(55, 55, 65);
    private static final Color MUTED = new Color(130, 130, 145);

    // ================= DEPENDENCIES =================

    private final DepartmentManager departmentManager;
    private final boolean profileStyle;

    // ================= PERSONAL INFORMATION =================

    private JTextField idField;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField positionField;

    private JComboBox<String> departmentCombo;
    private JComboBox<String> typeCombo;

    // ================= FULL-TIME FIELDS =================

    private JTextField basicSalaryField;
    private JTextField bonusField;

    // ================= PART-TIME FIELDS =================

    private JTextField hourlyRateField;
    private JTextField hoursWorkedField;

    // ================= SALARY CARDS =================

    private CardLayout salaryCardLayout;
    private JPanel salaryCards;

    // ================= CONSTRUCTORS =================

    public EmployeeFormPanel(DepartmentManager departmentManager) {
        this(departmentManager, false);
    }

    // Profile style is used only by the Update screen; Add keeps its layout.
    public EmployeeFormPanel(DepartmentManager departmentManager, boolean profileStyle) {
        this.departmentManager = departmentManager;
        this.profileStyle = profileStyle;

        setLayout(new BorderLayout(0, 15));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initializeComponents();
        if (profileStyle) buildProfileForm();
        else buildForm();

        reloadDepartments();
        onTypeChanged();
    }

    // Compatibility constructor for GUI preview/testing.
    public EmployeeFormPanel() {
        this(null);
    }

    // ================= INITIALIZATION =================

    private void initializeComponents() {

        idField = createTextField();
        nameField = createTextField();
        ageField = createTextField();
        positionField = createTextField();

        departmentCombo = new JComboBox<>();
        styleComboBox(departmentCombo);

        typeCombo = new JComboBox<>(EmployeeFactory.getSupportedTypes());
        styleComboBox(typeCombo);

        basicSalaryField = createTextField();
        bonusField = createTextField();

        hourlyRateField = createTextField();
        hoursWorkedField = createTextField();

        salaryCardLayout = new CardLayout();
        salaryCards = new JPanel(salaryCardLayout);
        salaryCards.setBackground(WHITE);
    }

    // ================= FORM DESIGN =================

    private void buildForm() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG);

        // Personal Information
        JPanel personalPanel = createSection("Personal Information");

        addField(personalPanel, "Employee ID", idField);
        addField(personalPanel, "Full Name", nameField);
        addField(personalPanel, "Age", ageField);
        addField(personalPanel, "Job Position", positionField);

        // Employment Information
        JPanel employmentPanel = createSection("Employment Information");

        addField(employmentPanel, "Department", departmentCombo);
        addField(employmentPanel, "Employee Type", typeCombo);

        // Salary Information
        JPanel salaryPanel = createSection("Salary Information");
        salaryPanel.setLayout(new BorderLayout(15, 12));
        salaryPanel.setBackground(WHITE);

        buildSalaryCards();

        JPanel salaryContent = new JPanel(new BorderLayout());
        salaryContent.setBackground(WHITE);
        salaryContent.add(salaryCards, BorderLayout.CENTER);
        JLabel salaryTitle = new JLabel("Salary Information");
        salaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        salaryTitle.setForeground(PURPLE);

        salaryPanel.add(salaryTitle, BorderLayout.NORTH);
        salaryPanel.add(salaryContent, BorderLayout.CENTER);

        mainPanel.add(personalPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(employmentPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(salaryPanel);

        add(mainPanel, BorderLayout.CENTER);

        typeCombo.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                onTypeChanged();
            }
        });
    }

    // Compact card layout for the Update screen only.
    private void buildProfileForm() {
        setBorder(new EmptyBorder(4, 2, 4, 2));
        setBackground(BG);
        buildSalaryCards();
        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JPanel personal = profileCard("01   Personal Information",
                "Employee ID", idField, "Full Name", nameField,
                "Age", ageField);
        JPanel job = profileCard("02   Job Details",
                "Department", departmentCombo, "Position", positionField,
                "Employee Type", typeCombo);
        JPanel salary = profileCard("03   Salary Details");
        salary.add(salaryCards, BorderLayout.CENTER);

        for (JPanel section : new JPanel[]{personal, job, salary}) {
            section.setAlignmentX(Component.LEFT_ALIGNMENT);
            details.add(section);
            details.add(Box.createVerticalStrut(12));
        }
        add(details, BorderLayout.NORTH);
        typeCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) onTypeChanged();
        });
    }

    private JPanel profileCard(String heading, Object... fields) {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 226, 239)),
                new EmptyBorder(16, 17, 17, 17)));

        JLabel title = new JLabel(heading);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(PURPLE);
        card.add(title, BorderLayout.NORTH);

        if (fields.length > 0) {
            JPanel grid = new JPanel(new GridLayout(0, 2, 12, 12));
            grid.setOpaque(false);
            for (int i = 0; i < fields.length; i += 2) {
                grid.add(createFieldPanel((String) fields[i], (JComponent) fields[i + 1]));
            }
            card.add(grid, BorderLayout.CENTER);
        }
        return card;
    }

    // ================= SALARY CARDS =================

    private void buildSalaryCards() {

        // Full-Time Card
        JPanel fullTimePanel = new JPanel(new GridLayout(profileStyle ? 1 : 2, profileStyle ? 2 : 1, 12, 12));
        fullTimePanel.setBackground(WHITE);

        JPanel basicSalaryPanel = createFieldPanel("Basic Salary", basicSalaryField);
        JPanel bonusPanel = createFieldPanel("Bonus", bonusField);

        fullTimePanel.add(basicSalaryPanel);
        fullTimePanel.add(bonusPanel);

        // Part-Time Card
        JPanel partTimePanel = new JPanel(new GridLayout(profileStyle ? 1 : 2, profileStyle ? 2 : 1, 12, 12));
        partTimePanel.setBackground(WHITE);

        JPanel hourlyRatePanel = createFieldPanel("Hourly Rate", hourlyRateField);
        JPanel hoursPanel = createFieldPanel("Hours Worked", hoursWorkedField);

        partTimePanel.add(hourlyRatePanel);
        partTimePanel.add(hoursPanel);

        salaryCards.add(fullTimePanel, "Full-Time");
        salaryCards.add(partTimePanel, "Part-Time");
    }

    private void onTypeChanged() {

        String selectedType = getEmployeeType();

        if ("Full-Time".equalsIgnoreCase(selectedType)) {
            salaryCardLayout.show(salaryCards, "Full-Time");

        } else if ("Part-Time".equalsIgnoreCase(selectedType)) {
            salaryCardLayout.show(salaryCards, "Part-Time");
        }
    }

    // ================= DEPARTMENTS =================

    public void reloadDepartments() {

        if (departmentManager == null) {
            return;
        }

        reloadDepartments(departmentManager.getDepartmentNames());
    }

    // Kept for compatibility with existing GUI code.
    public void reloadDepartments(String[] departments) {

        Object selected = departmentCombo.getSelectedItem();

        departmentCombo.removeAllItems();

        if (departments == null) {
            return;
        }

        for (String department : departments) {
            if (department != null && !department.trim().isEmpty()) {
                departmentCombo.addItem(department.trim());
            }
        }

        if (selected != null) {
            for (int i = 0; i < departmentCombo.getItemCount(); i++) {
                if (departmentCombo.getItemAt(i).equals(selected)) {
                    departmentCombo.setSelectedItem(selected);
                    break;
                }
            }
        }
    }
    // ================= BUILD EMPLOYEE =================

    public Employee buildEmployee() throws ValidationException {

        if (departmentManager == null) {
            throw new ValidationException(
                    "Department manager is not initialized.");
        }

        int id = Validator.parseId(getEmployeeId());

        String name = Validator.requireNotEmpty(
                getEmployeeName(), "Full Name");

        int age = Validator.parseAge(getAge());

        String position = Validator.requireNotEmpty(
                getJobTitle(), "Job Position");

        String department = Validator.requireValidDepartment(
                getDepartment(), departmentManager);

        String type = Validator.requireSupportedType(
                getEmployeeType());

        double firstSalaryParameter;
        double secondSalaryParameter;

        if ("Full-Time".equalsIgnoreCase(type)) {

            firstSalaryParameter = Validator.parseNonNegativeDouble(
                    getBasicSalary(), "Basic Salary");

            secondSalaryParameter = Validator.parseNonNegativeDouble(
                    getBonus(), "Bonus");

        } else if ("Part-Time".equalsIgnoreCase(type)) {

            firstSalaryParameter = Validator.parseNonNegativeDouble(
                    getHourlyRate(), "Hourly Rate");

            secondSalaryParameter = Validator.parseNonNegativeDouble(
                    getHoursWorked(), "Hours Worked");

        } else {

            throw new ValidationException(
                    "Unsupported employee type: " + type);
        }

        return EmployeeFactory.create(
                type,
                id,
                name,
                age,
                department,
                position,
                firstSalaryParameter,
                secondSalaryParameter);
    }

    // ================= LOAD EMPLOYEE =================

    public void loadEmployee(Employee employee) {

        if (employee == null) {
            return;
        }

        idField.setText(String.valueOf(employee.getId()));
        nameField.setText(employee.getFullName());
        ageField.setText(String.valueOf(employee.getAge()));
        positionField.setText(employee.getPosition());

        departmentCombo.setSelectedItem(
                employee.getDepartmentName());

        String type = employee.getEmployeeType();

        typeCombo.setSelectedItem(type);

        if (employee instanceof FullTimeEmployee) {

            FullTimeEmployee fullTime = (FullTimeEmployee) employee;

            basicSalaryField.setText(
                    String.valueOf(fullTime.getBasicSalary()));

            bonusField.setText(
                    String.valueOf(fullTime.getBonus()));

        } else if (employee instanceof PartTimeEmployee) {

            PartTimeEmployee partTime = (PartTimeEmployee) employee;

            hourlyRateField.setText(
                    String.valueOf(partTime.getHourlyRate()));

            hoursWorkedField.setText(
                    String.valueOf(partTime.getHoursWorked()));
        }

        onTypeChanged();
    }

    // ================= LOAD EMPLOYEE DATA =================

    // Kept for compatibility with the previous implementation.
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

        idField.setText(String.valueOf(id));
        nameField.setText(fullName);
        ageField.setText(String.valueOf(age));
        positionField.setText(position);

        departmentCombo.setSelectedItem(department);
        typeCombo.setSelectedItem(type);

        if (basicSalary != null) {
            basicSalaryField.setText(String.valueOf(basicSalary));
        }

        if (bonus != null) {
            bonusField.setText(String.valueOf(bonus));
        }

        if (hourlyRate != null) {
            hourlyRateField.setText(String.valueOf(hourlyRate));
        }

        if (hoursWorked != null) {
            hoursWorkedField.setText(String.valueOf(hoursWorked));
        }

        onTypeChanged();
    }

    // ================= ID EDITING =================

    public void setIdEditable(boolean editable) {
        idField.setEditable(editable);
        idField.setBackground(editable ? WHITE : new Color(235, 235, 240));
    }

    // ================= CLEAR FORM =================

    public void clearForm() {

        idField.setText("");
        nameField.setText("");
        ageField.setText("");
        positionField.setText("");

        basicSalaryField.setText("");
        bonusField.setText("");

        hourlyRateField.setText("");
        hoursWorkedField.setText("");

        if (departmentCombo.getItemCount() > 0) {
            departmentCombo.setSelectedIndex(0);
        }

        if (typeCombo.getItemCount() > 0) {
            typeCombo.setSelectedIndex(0);
        }

        onTypeChanged();
    }

    // ================= GETTERS =================

    public String getEmployeeId() {
        return idField.getText().trim();
    }

    public String getEmployeeName() {
        return nameField.getText().trim();
    }

    public String getAge() {
        return ageField.getText().trim();
    }

    public String getJobTitle() {
        return positionField.getText().trim();
    }

    public String getDepartment() {
        Object selected = departmentCombo.getSelectedItem();
        return selected == null ? null : selected.toString();
    }

    public String getEmployeeType() {
        Object selected = typeCombo.getSelectedItem();
        return selected == null ? null : selected.toString();
    }

    public String getBasicSalary() {
        return basicSalaryField.getText().trim();
    }

    public String getBonus() {
        return bonusField.getText().trim();
    }

    public String getHourlyRate() {
        return hourlyRateField.getText().trim();
    }

    public String getHoursWorked() {
        return hoursWorkedField.getText().trim();
    }

    // ================= UI HELPERS =================

    private JPanel createSection(String title) {

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 15, 12));
        panel.setBackground(WHITE);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(230, 228, 238)),
                        new EmptyBorder(18, 18, 18, 18)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PURPLE);

        panel.add(titleLabel);
        panel.add(new JLabel(""));

        return panel;
    }

    private void addField(
            JPanel parent,
            String label,
            JComponent component) {

        parent.add(createFieldPanel(label, component));
    }

    private JPanel createFieldPanel(
            String label,
            JComponent component) {

        JPanel panel = new JPanel(new BorderLayout(0, 7));
        panel.setBackground(WHITE);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fieldLabel.setForeground(TEXT);

        panel.add(fieldLabel, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);

        return panel;
    }

    private JTextField createTextField() {

        JTextField field = new JTextField(profileStyle ? 10 : 15);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(TEXT);
        field.setBackground(WHITE);

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 218, 230)),
                        new EmptyBorder(9, 10, 9, 10)));

        return field;
    }

    private void styleComboBox(JComboBox<String> combo) {

        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setForeground(TEXT);
        combo.setBackground(WHITE);

        combo.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 218, 230)));
    }
}
