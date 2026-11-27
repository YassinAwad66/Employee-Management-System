package gui;

import exception.ValidationException;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import model.Employee;
import model.EmployeeTableModel;
import service.DepartmentManager;
import service.EmployeeManager;

/** Employee update screen. Reuses EmployeeFormPanel and EmployeeManager. */
public class UpdateEmployeePanel extends JPanel {

    private static final Color BG = new Color(246, 245, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color PURPLE = new Color(105, 76, 180);
    private static final Color SOFT_PURPLE = new Color(239, 234, 248);
    private static final Color TEXT = new Color(45, 42, 55);
    private static final Color MUTED = new Color(130, 125, 145);
    private static final Color BORDER = new Color(228, 225, 238);

    private final EmployeeManager employeeManager;
    private final Runnable onDataChanged;
    private final EmployeeFormPanel employeeForm;
    private final EmployeeTableModel tableModel;
    private final JTable employeeTable;
    private final JLabel selectedLabel = new JLabel("No employee selected");
    private final JLabel countLabel = new JLabel("Select a row to edit");
    private final JButton saveButton = button("Save Changes", true);
    private final TableRowSorter<EmployeeTableModel> sorter;
    private final JTextField searchField = new JTextField();

    public UpdateEmployeePanel(EmployeeManager em, DepartmentManager dm, Runnable onChanged) {
        if (em == null || dm == null) {
            throw new IllegalArgumentException("Managers cannot be null.");
        }
        employeeManager = em;
        onDataChanged = onChanged;
        employeeForm = new EmployeeFormPanel(dm, true);
        employeeForm.setIdEditable(false);
        tableModel = new EmployeeTableModel(em);
        employeeTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        employeeTable.setRowSorter(sorter);
        saveButton.setEnabled(false);

        setLayout(new BorderLayout(0, 18));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 22, 20, 22));
        buildLayout();
        refresh();
    }

    private void buildLayout() {
        JPanel heading = new JPanel(new BorderLayout(0, 5));
        heading.setOpaque(false);
        JLabel title = new JLabel("Update Employee");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel("Choose an employee, edit the details, then save your changes.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);
        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);
        add(heading, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildTableCard(), buildEditorCard());
        split.setBorder(null);
        split.setOpaque(false);
        split.setDividerSize(14);
        split.setResizeWeight(0.36);
        split.setContinuousLayout(true);
        add(split, BorderLayout.CENTER);
        SwingUtilities.invokeLater(() -> split.setDividerLocation(0.36));
    }

    private JPanel buildTableCard() {
        JPanel card = card();
        card.setLayout(new BorderLayout(0, 14));
        card.setMinimumSize(new Dimension(250, 200));

        JPanel header = new JPanel(new BorderLayout(0, 12));
        header.setOpaque(false);
        JPanel titleRow = new JPanel(new BorderLayout(8, 0));
        titleRow.setOpaque(false);
        JLabel label = new JLabel("Employee Directory");
        label.setFont(new Font("Segoe UI", Font.BOLD, 17));
        label.setForeground(TEXT);
        JButton refreshButton = button("Refresh", false);
        refreshButton.addActionListener(e -> refresh());
        titleRow.add(label, BorderLayout.CENTER);
        titleRow.add(refreshButton, BorderLayout.EAST);

        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setToolTipText("Search for an employee by name, ID or department");
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(10, 12, 10, 12)));
        searchField.putClientProperty("JTextField.placeholderText", "Search employees...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterRows(); }
            public void removeUpdate(DocumentEvent e) { filterRows(); }
            public void changedUpdate(DocumentEvent e) { filterRows(); }
        });
        header.add(titleRow, BorderLayout.NORTH);
        header.add(searchField, BorderLayout.SOUTH);
        card.add(header, BorderLayout.NORTH);

        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        employeeTable.setRowHeight(40);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setShowGrid(false);
        employeeTable.setIntercellSpacing(new Dimension(0, 0));
        employeeTable.setFillsViewportHeight(true);
        employeeTable.setBackground(WHITE);
        employeeTable.setForeground(TEXT);
        employeeTable.setSelectionBackground(SOFT_PURPLE);
        employeeTable.setSelectionForeground(TEXT);
        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        employeeTable.getTableHeader().setBackground(SOFT_PURPLE);
        employeeTable.getTableHeader().setForeground(PURPLE);
        employeeTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        // Keep the directory compact: ID, Name and Department only.
        // The full employee details remain available in the editor.
        if (employeeTable.getColumnCount() >= 5) {
            employeeTable.removeColumn(employeeTable.getColumnModel().getColumn(4));
            employeeTable.removeColumn(employeeTable.getColumnModel().getColumn(2));
        }
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelection();
        });

        JScrollPane scroll = new JScrollPane(employeeTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(WHITE);
        card.add(scroll, BorderLayout.CENTER);

        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(MUTED);
        card.add(countLabel, BorderLayout.SOUTH);
        return card;
    }

    private void filterRows() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query)));
        }
        updateCount();
    }

    private void updateCount() {
        countLabel.setText(employeeTable.getRowCount() + " employees shown");
    }

    private JPanel buildEditorCard() {
        JPanel card = card();
        card.setLayout(new BorderLayout(0, 12));

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 6));
        header.setOpaque(false);
        JLabel label = new JLabel("Edit Employee Profile");
        label.setFont(new Font("Segoe UI", Font.BOLD, 17));
        label.setForeground(TEXT);
        selectedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        selectedLabel.setForeground(MUTED);
        header.add(label);
        header.add(selectedLabel);
        card.add(header, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(employeeForm);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        card.add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton clearButton = button("Clear", false);
        clearButton.addActionListener(e -> clearSelection());
        saveButton.addActionListener(e -> handleUpdate());
        actions.add(clearButton);
        actions.add(saveButton);
        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 18, 18, 18)));
        return panel;
    }

    private JButton button(String title, boolean primary) {
        JButton button = new JButton(title);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(primary ? WHITE : PURPLE);
        button.setBackground(primary ? PURPLE : SOFT_PURPLE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(11, 17, 11, 17));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadSelection() {
        int viewRow = employeeTable.getSelectedRow();
        if (viewRow < 0) {
            resetForm();
            return;
        }
        int modelRow = employeeTable.convertRowIndexToModel(viewRow);
        Employee rowEmployee = tableModel.getEmployeeAt(modelRow);
        Employee employee = rowEmployee == null ? null : employeeManager.findById(rowEmployee.getId());
        if (employee == null) {
            ThemeDialog.showError(this, "Employee not found.");
            clearSelection();
            return;
        }
        employeeForm.loadEmployee(employee);
        employeeForm.setIdEditable(false);
        selectedLabel.setText("Editing: " + employee.getFullName() + "  |  ID: " + employee.getId());
        selectedLabel.setForeground(PURPLE);
        saveButton.setEnabled(true);
    }

    private void handleUpdate() {
        int viewRow = employeeTable.getSelectedRow();
        if (viewRow < 0) {
            ThemeDialog.showWarning(this, "Select an employee first.");
            return;
        }
        int modelRow = employeeTable.convertRowIndexToModel(viewRow);
        Employee rowEmployee = tableModel.getEmployeeAt(modelRow);
        if (rowEmployee == null) return;
        int id = rowEmployee.getId();
        Employee existing = employeeManager.findById(id);
        if (existing == null) {
            ThemeDialog.showError(this, "The employee no longer exists.");
            refresh();
            return;
        }

        try {
            Employee updated = employeeForm.buildEmployee();
            if (updated.getId() != id) {
                ThemeDialog.showError(this, "Employee ID cannot be changed.");
                return;
            }
            if (!ThemeDialog.showConfirm(this, "Confirm Update",
                    "Save changes to " + existing.getFullName() + "?")) return;

            employeeManager.updateEmployee(id, updated);
            if (onDataChanged != null) onDataChanged.run();
            refreshTable();
            selectEmployeeById(id);
            // Explicitly reload, even if the selection did not change.
            Employee saved = employeeManager.findById(id);
            if (saved != null) {
                employeeForm.loadEmployee(saved);
                selectedLabel.setText("Editing: " + saved.getFullName() + "  |  ID: " + id);
                selectedLabel.setForeground(PURPLE);
            }
            ThemeDialog.showSuccess(this, "Employee updated successfully!");
        } catch (ValidationException ex) {
            ThemeDialog.showWarning(this, ex.getMessage());
        } catch (IOException ex) {
            ThemeDialog.showError(this, "Could not save changes: " + ex.getMessage());
        } catch (RuntimeException ex) {
            ThemeDialog.showError(this, "Unexpected error: " + ex.getMessage());
        }
    }

    private void selectEmployeeById(int id) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            Employee employee = tableModel.getEmployeeAt(row);
            if (employee != null && employee.getId() == id) {
                int viewRow = employeeTable.convertRowIndexToView(row);
                if (viewRow >= 0) {
                    employeeTable.setRowSelectionInterval(viewRow, viewRow);
                    employeeTable.scrollRectToVisible(employeeTable.getCellRect(viewRow, 0, true));
                }
                return;
            }
        }
    }

    private void resetForm() {
        employeeForm.clearForm();
        employeeForm.setIdEditable(false);
        selectedLabel.setText("Select an employee to start editing");
        selectedLabel.setForeground(MUTED);
        saveButton.setEnabled(false);
    }

    private void clearSelection() {
        employeeTable.clearSelection();
        resetForm();
    }

    public void refresh() {
        refreshTable();
        employeeForm.reloadDepartments();
        clearSelection();
    }

    private void refreshTable() {
        List<Employee> employees = employeeManager.getAllEmployees();
        tableModel.setEmployees(employees);
        updateCount();
    }
}
