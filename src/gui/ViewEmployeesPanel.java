package gui;

import java.awt.*;
import javax.swing.*;
import service.*;
import model.*;

public class ViewEmployeesPanel extends JPanel {
    private JTable employeeTable;
    private EmployeeTableModel tableModel;
    private JComboBox<String> departmentFilter;
    private JButton refreshButton;
    private JButton deleteButton;
    private EmployeeManager employeeManager;
    private DepartmentManager departmentManager;
    private Runnable onChanged;
    private static final Color BG = new Color(246, 245, 250);
    private static final Color prpl = new Color(139, 92, 246);
    private static final Color darkprpl = new Color(112, 65, 216);
    private static final Color lightprpl = new Color(237, 229, 255);
    private static final Color txt = new Color(41, 36, 58);

    public ViewEmployeesPanel(EmployeeManager em, DepartmentManager dm, Runnable onChanged) {

        this.departmentManager = dm;
        this.employeeManager = em;
        this.onChanged = onChanged;
        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(lightprpl);
        refreshButton.setForeground(txt);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 15));
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        deleteButton = new JButton("Delete");
        deleteButton.setBackground(darkprpl);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 15));
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        setLayout(new BorderLayout(15, 15));
        setBackground(BG);

        JLabel title = new JLabel("Employee Management");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(txt);
        
        
        tableModel = new EmployeeTableModel(employeeManager);
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setFillsViewportHeight(true);
        employeeTable.setFont(new Font("Arial", Font.PLAIN, 14));
        employeeTable.setRowHeight(32);
        employeeTable.setBackground(Color.WHITE);
        employeeTable.setForeground(txt);
        employeeTable.setSelectionBackground(prpl);
        employeeTable.setSelectionForeground(txt);
        employeeTable.setGridColor(darkprpl);
        employeeTable.setShowGrid(true);
        
        employeeTable.getTableHeader().setBackground(prpl);
        employeeTable.getTableHeader().setForeground(Color.WHITE);
        employeeTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        employeeTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        employeeTable.setShowGrid(true);
        employeeTable.setGridColor(darkprpl);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(lightprpl));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        String[] departments = departmentManager.getDepartmentNames();
        String[] options = new String[departments.length + 1];
        options[0] = "All";
        System.arraycopy(departments, 0, options, 1, departments.length);
        departmentFilter = new JComboBox<>(options);
        departmentFilter.setFont(new Font("Arial", Font.PLAIN, 15));
        departmentFilter.setBackground(Color.WHITE);
        departmentFilter.setForeground(darkprpl);
        departmentFilter.setBorder(BorderFactory.createLineBorder(prpl));
        departmentFilter.setPreferredSize(new Dimension(160, 35));
        JPanel topPanel = new JPanel();
        topPanel.setBackground(BG);

        topPanel.add(departmentFilter);
        topPanel.add(refreshButton);
        topPanel.add(deleteButton);

        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(BG);

        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        refreshButton.addActionListener(e -> refreshTable());
        deleteButton.addActionListener(e -> handleDelete());
        departmentFilter.addActionListener(e -> handleFilter());
    }

    public void refreshTable() {
        handleFilter();
    }

    public void handleDelete() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.");
            return;
        }
        Employee emp = tableModel.getEmployeeAt(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + emp.getFullName() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = false;
            try {
                deleted = employeeManager.deleteEmployee(emp.getId());
            } catch (java.io.IOException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting employee: " + ex.getMessage());
                return;
            }
            if (deleted) {
                refreshTable();
                if (onChanged != null) {
                    onChanged.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Employee deletion failed.");
            }
        }
    }

    public void handleFilter() {

    String selectedDepartment =
            (String) departmentFilter.getSelectedItem();

    if (selectedDepartment == null) {
        return;
    }

    if (selectedDepartment.equals("All")) {

        tableModel.setEmployees(employeeManager.getAllEmployees());

    } else {

        tableModel.setEmployees(
                employeeManager.searchByDepartment(selectedDepartment)
        );
    }
}
}
