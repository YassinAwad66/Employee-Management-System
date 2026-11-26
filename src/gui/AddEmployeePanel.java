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

        setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create employee form
        employeeForm = new EmployeeFormPanel(departmentManager);

        // Create buttons
        addButton = new JButton("Add Employee");
        clearButton = new JButton("Clear");

        // Style buttons
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

        buttonPanel.setLayout(
                new FlowLayout(FlowLayout.RIGHT));

        buttonPanel.setOpaque(false);

        buttonPanel.add(clearButton);
        buttonPanel.add(addButton);

        // Add form and buttons
        JScrollPane scrollPane = new JScrollPane(employeeForm);

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder());

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
                    JOptionPane.ERROR_MESSAGE);

            return;

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to save employee data.",
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        clearFields();
        clearFields();

        showSuccessDialog();

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

    private void showSuccessDialog() {
        Window owner = SwingUtilities.getWindowAncestor(this);

        JDialog dialog = new JDialog(
                owner, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);

        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(28, 30, 25, 30));

        // Custom rounded background
        JPanel container = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
            }
        };
        container.setOpaque(false);

        JLabel icon = new JLabel("\u2714", SwingConstants.CENTER);
        icon.setFont(new Font("Dialog", Font.BOLD, 30));
        icon.setForeground(new Color(105, 76, 180));

        JLabel title = new JLabel("Employee Added!", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(new Color(55, 55, 65));

        JLabel message = new JLabel(
                "The employee record has been saved successfully.",
                SwingConstants.CENTER);
        message.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        message.setForeground(new Color(130, 130, 145));

        JButton continueButton = new JButton("Continue");
        continueButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        continueButton.setForeground(Color.WHITE);
        continueButton.setBackground(new Color(105, 76, 180));
        continueButton.setFocusPainted(false);
        continueButton.setBorderPainted(false);
        continueButton.setOpaque(true);
        continueButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        continueButton.setPreferredSize(new Dimension(0, 42));
        continueButton.addActionListener(e -> dialog.dispose());

        JPanel content = new JPanel(new GridLayout(3, 1, 0, 8));
        content.setBackground(Color.WHITE);
        content.add(icon);
        content.add(title);
        content.add(message);

        card.add(content, BorderLayout.CENTER);
        card.add(continueButton, BorderLayout.SOUTH);
        container.add(card, BorderLayout.CENTER);

        dialog.setContentPane(container);
        dialog.setSize(420, 245);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}