package gui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import model.Employee;
import service.EmployeeManager;
import service.DepartmentManager;
import exception.ValidationException;

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

        setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // Create employee form
        employeeForm = new EmployeeFormPanel(departmentManager);

        // Create buttons
        addButton = new JButton("Add Employee");
        clearButton = new JButton("Clear");

        // Style buttons
        addButton.setBackground(new Color(0,0,0));
        addButton.setForeground(Color.WHITE);

        clearButton.setBackground(Color.WHITE);

        // Create button panel
        JPanel buttonPanel = new JPanel();

        buttonPanel.setLayout(
            new FlowLayout(FlowLayout.RIGHT)
        );

        buttonPanel.setOpaque(false);

        buttonPanel.add(clearButton);
        buttonPanel.add(addButton);

        // Add form and buttons
        JScrollPane scrollPane = new JScrollPane(employeeForm);

        scrollPane.setBorder(
            BorderFactory.createEmptyBorder()
        );

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

            JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );

            return;

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to save employee data.",
                "File Error",
                JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        clearFields();

        JOptionPane.showMessageDialog(
            this,
            "Employee added successfully!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );

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