import java.awt.*;
import javax.swing.*;
import manager.EmployeeManager;
import manager.DepartmentManager;

public class ViewEmployeesPanel extends JPanel {
    private JTable employeeTable;
    private EmployeeTableModel tableModel;
    private JComboBox<String> departmentFilter;
    private JButton refreshButton;
    private JButton deleteButton;
    private EmployeeManager employeeManager;
    private DepartmentManager departmentManager;
    private Runnable onChanged;

    public ViewEmployeesPanel(EmployeeManager em, DepartmentManager dm, Runnable onChanged) {

        this.departmentManager = dm;
        this.employeeManager = em;
        this.onChanged = onChanged;
        refreshButton = new JButton("Refresh");
        deleteButton = new JButton("Delete");
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setFillsViewportHeight(true);
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("Employee Management");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        tableModel = new EmployeeTableModel(employeeManager);
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);

        String[] departments = departmentManager.getDepartmentNames();
        String[] options = new String[departments.length + 1];
        options[0] = "All";
        System.arraycopy(departments, 0, options, 1, departments.length);
        departmentFilter = new JComboBox<>(options);

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(245, 247, 250));

        topPanel.add(departmentFilter);
        topPanel.add(refreshButton);
        topPanel.add(deleteButton);

        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(new Color(245, 247, 250));

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
            boolean deleted = employeeManager.deleteEmployee(emp.getId());
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
