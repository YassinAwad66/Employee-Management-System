package gui;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import exception.ValidationException;
import model.Employee;
import model.EmployeeTableModel;
import service.DepartmentManager;
import service.EmployeeManager;

public class UpdateEmployeePanel extends JPanel {

    // ================= COLORS =================

    private static final Color BG = new Color(246, 245, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color PURPLE = new Color(105, 76, 160);
    private static final Color LIGHT_PURPLE = new Color(239, 234, 248);
    private static final Color TEXT = new Color(45, 42, 55);
    private static final Color MUTED = new Color(135, 130, 145);
    private static final Color BORDER = new Color(230, 226, 237);

    // ================= DEPENDENCIES =================

    private final EmployeeManager employeeManager;
    private final DepartmentManager departmentManager;
    private final Runnable onDataChanged;

    // ================= COMPONENTS =================

    private JTable employeeTable;
    private EmployeeTableModel tableModel;

    private EmployeeFormPanel employeeForm;

    private JButton updateButton;
    private JButton clearButton;
    private JButton refreshButton;

    private JLabel selectedEmployeeLabel;

    // ================= CONSTRUCTOR =================

    public UpdateEmployeePanel(
            EmployeeManager employeeManager,
            DepartmentManager departmentManager,
            Runnable onDataChanged) {

        if (employeeManager == null || departmentManager == null) {
            throw new IllegalArgumentException("Managers cannot be null.");
        }

        this.employeeManager = employeeManager;
        this.departmentManager = departmentManager;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(0, 18));
        setBackground(BG);
        setBorder(new EmptyBorder(5, 5, 5, 5));

        initializeComponents();
        buildLayout();
        setupListeners();

        refresh();
    }

    // ================= INITIALIZATION =================

    private void initializeComponents() {

        tableModel = new EmployeeTableModel(employeeManager);
        employeeTable = new JTable(tableModel);

        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        employeeTable.setRowHeight(32);
        employeeTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        employeeTable.setShowGrid(false);
        employeeTable.setIntercellSpacing(new Dimension(0, 0));
        employeeTable.setFillsViewportHeight(true);

        employeeTable.setBackground(WHITE);
        employeeTable.setForeground(TEXT);

        employeeTable.setSelectionBackground(LIGHT_PURPLE);
        employeeTable.setSelectionForeground(TEXT);

        employeeTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 12));

        employeeTable.getTableHeader().setBackground(LIGHT_PURPLE);
        employeeTable.getTableHeader().setForeground(PURPLE);
        employeeTable.getTableHeader().setPreferredSize(
                new Dimension(0, 35));

        employeeTable.setAutoCreateRowSorter(true);

        employeeForm = new EmployeeFormPanel(departmentManager);
        employeeForm.setIdEditable(false);

        updateButton = createButton(
                "Update Employee", PURPLE, WHITE);

        clearButton = createButton(
                "Clear Form", WHITE, PURPLE);

        refreshButton = createButton(
                "Refresh Table", WHITE, PURPLE);

        selectedEmployeeLabel = new JLabel(
                "No employee selected");

        selectedEmployeeLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 12));

        selectedEmployeeLabel.setForeground(MUTED);
    }

    // ================= LAYOUT =================

    private void buildLayout() {

        JPanel topPanel = buildTableSection();
        JPanel formSection = buildFormSection();

        add(topPanel, BorderLayout.NORTH);
        add(formSection, BorderLayout.CENTER);
    }

    // ================= TABLE SECTION =================

    private JPanel buildTableSection() {

        JPanel section = new JPanel(new BorderLayout(0, 12));
        section.setBackground(WHITE);

        section.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(16, 16, 16, 16)
                )
        );

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);

        JLabel title = new JLabel(
                "Select Employee to Update");

        title.setFont(
                new Font("Segoe UI", Font.BOLD, 17));

        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Select an employee from the table to load their information.");

        subtitle.setFont(
                new Font("Segoe UI", Font.PLAIN, 11));

        subtitle.setForeground(MUTED);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(
                new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        titlePanel.setBackground(WHITE);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(employeeTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(BORDER));

        scrollPane.setPreferredSize(
                new Dimension(0, 190));

        scrollPane.getViewport().setBackground(WHITE);

        section.add(header, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);

        return section;
    }

    // ================= FORM SECTION =================

    private JPanel buildFormSection() {

        JPanel section = new JPanel(
                new BorderLayout(0, 15));

        section.setBackground(WHITE);

        section.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(16, 16, 16, 16)
                )
        );

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);

        JLabel title = new JLabel(
                "Employee Information");

        title.setFont(
                new Font("Segoe UI", Font.BOLD, 17));

        title.setForeground(TEXT);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(WHITE);

        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(selectedEmployeeLabel, BorderLayout.SOUTH);

        header.add(titlePanel, BorderLayout.WEST);

        JScrollPane formScrollPane = new JScrollPane(employeeForm);

        formScrollPane.setBorder(null);

        formScrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        formScrollPane.getViewport().setBackground(BG);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0));

        buttonPanel.setBackground(WHITE);

        buttonPanel.add(clearButton);
        buttonPanel.add(updateButton);

        section.add(header, BorderLayout.NORTH);
        section.add(formScrollPane, BorderLayout.CENTER);
        section.add(buttonPanel, BorderLayout.SOUTH);

        return section;
    }

    // ================= BUTTON CREATION =================

    private JButton createButton(
            String text,
            Color background,
            Color foreground) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("Segoe UI", Font.BOLD, 12));

        button.setForeground(foreground);
        button.setBackground(background);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setBorder(
                new EmptyBorder(11, 18, 11, 18));

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // ================= LISTENERS =================

    private void setupListeners() {

        employeeTable.getSelectionModel()
                .addListSelectionListener(event -> {

                    if (!event.getValueIsAdjusting()) {
                        handleRowSelection();
                    }
                });

        updateButton.addActionListener(
                event -> handleUpdate());

        clearButton.addActionListener(
                event -> clearForm());

        refreshButton.addActionListener(
                event -> refresh());
    }

    // ================= ROW SELECTION =================

    private void handleRowSelection() {

        int selectedRow = employeeTable.getSelectedRow();

        if (selectedRow == -1) {
            resetFormState();
            return;
        }

        int modelRow = employeeTable.convertRowIndexToModel(
                selectedRow);

        Employee selected = tableModel.getEmployeeAt(modelRow);

        if (selected == null) {
            showEmployeeNotFound();
            clearForm();
            return;
        }

        Employee employee = employeeManager.findById(
                selected.getId());

        if (employee == null) {
            showEmployeeNotFound();
            refresh();
            return;
        }

        employeeForm.loadEmployee(employee);
        employeeForm.setIdEditable(false);

        selectedEmployeeLabel.setText(
                "Editing Employee ID: " + employee.getId());

        selectedEmployeeLabel.setForeground(PURPLE);
    }

    // ================= UPDATE EMPLOYEE =================

    private void handleUpdate() {

        int selectedRow = employeeTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an employee from the table first.",
                    "No Employee Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int modelRow = employeeTable.convertRowIndexToModel(
                selectedRow);

        Employee selected = tableModel.getEmployeeAt(modelRow);

        if (selected == null) {

            showEmployeeNotFound();
            return;
        }

        int employeeId = selected.getId();

        Employee existingEmployee = employeeManager.findById(
                employeeId);

        if (existingEmployee == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "The selected employee no longer exists.",
                    "Employee Not Found",
                    JOptionPane.ERROR_MESSAGE
            );

            refresh();
            return;
        }

        try {

            Employee updatedEmployee =
                    employeeForm.buildEmployee();

            // Employee ID must remain unchanged
            if (updatedEmployee.getId() != employeeId) {

                JOptionPane.showMessageDialog(
                        this,
                        "Employee ID cannot be changed.",
                        "Invalid ID",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            // Confirmation before updating
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to update employee "
                            + existingEmployee.getFullName() + "?",
                    "Confirm Update",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }

            // Update employee data
            employeeManager.updateEmployee(
                    employeeId, updatedEmployee);

            JOptionPane.showMessageDialog(
                    this,
                    "Employee updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Notify MainFrame or refresh locally
            if (onDataChanged != null) {
                onDataChanged.run();
            } else {
                refreshTable();
            }

            // Keep the updated employee selected
            selectEmployeeById(employeeId);

        } catch (ValidationException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save employee data.\n"
                            + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "An unexpected error occurred.\n"
                            + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= SELECT EMPLOYEE BY ID =================

    private void selectEmployeeById(int employeeId) {

        for (int row = 0;
             row < tableModel.getRowCount();
             row++) {

            Employee employee =
                    tableModel.getEmployeeAt(row);

            if (employee != null
                    && employee.getId() == employeeId) {

                int viewRow =
                        employeeTable.convertRowIndexToView(row);

                if (viewRow >= 0) {

                    employeeTable.setRowSelectionInterval(
                            viewRow, viewRow);

                    employeeTable.scrollRectToVisible(
                            employeeTable.getCellRect(
                                    viewRow, 0, true));
                }

                return;
            }
        }
    }

    // ================= CLEAR FORM =================

    private void clearForm() {

        employeeTable.clearSelection();
        resetFormState();
    }

    // ================= RESET FORM STATE =================

    private void resetFormState() {

        employeeForm.clearForm();
        employeeForm.setIdEditable(false);

        selectedEmployeeLabel.setText(
                "No employee selected");

        selectedEmployeeLabel.setForeground(MUTED);
    }

    // ================= REFRESH =================

    public void refresh() {

        refreshTable();

        employeeForm.reloadDepartments();

        resetFormState();
    }

    // ================= REFRESH TABLE =================

    private void refreshTable() {

        List<Employee> employees =
                employeeManager.getAllEmployees();

        tableModel.setEmployees(employees);
    }

    // ================= ERROR MESSAGE =================

    private void showEmployeeNotFound() {

        JOptionPane.showMessageDialog(
                this,
                "Employee not found.",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
