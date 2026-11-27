package gui;

import exception.ValidationException;
import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import model.Employee;
import service.DepartmentManager;
import service.EmployeeManager;

public class AddEmployeePanel extends JPanel {

    private EmployeeManager employeeManager;
    private EmployeeFormPanel employeeForm;
    private Runnable onDataChanged;

    private JButton addButton;
    private JButton clearButton;

    // Constructor
    public AddEmployeePanel(
            EmployeeManager employeeManager,
            DepartmentManager departmentManager,
            Runnable onDataChanged) {

        this.employeeManager = employeeManager;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(246, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create employee form
        employeeForm = new EmployeeFormPanel(departmentManager);

        // Create buttons
        addButton = new JButton("Add Employee");
        clearButton = new JButton("Clear");

        // Style buttons (unchanged)
        addButton.setBackground(new Color(105, 76, 160));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        addButton.setOpaque(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(11, 18, 11, 18));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                g2.dispose();
                super.paint(g, c);
            }
        });

        clearButton.setBackground(Color.WHITE);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(clearButton);
        buttonPanel.add(addButton);

        // Add form and buttons
        JScrollPane scrollPane = new JScrollPane(employeeForm);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button events
        addButton.addActionListener(e -> addEmployee());
        clearButton.addActionListener(e -> clearFields());
    }

    // Add employee
    private void addEmployee() {
        try {
            Employee employee = employeeForm.buildEmployee();
            employeeManager.addEmployee(employee);
        } catch (ValidationException e) {
            ThemeDialog.showError(this, e.getMessage());
            return;
        } catch (IOException e) {
            ThemeDialog.showError(this, "Unable to save employee data.");
            return;
        }

        clearFields();
        ThemeDialog.showSuccess(
                this,
                "The employee record has been saved successfully.");

        if (onDataChanged != null) {
            onDataChanged.run();
        }
    }

    // Clear employee form
    private void clearFields() {
        employeeForm.clearForm();
    }

    // Refresh department list
    public void refresh() {
        employeeForm.reloadDepartments();
    }
}
