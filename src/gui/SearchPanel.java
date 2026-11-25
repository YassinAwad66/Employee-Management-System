import java.awt.*;
import javax.swing.*;
import java.util.List;
import manager.EmployeeManager;
import manager.DepartmentManager;

public class SearchPanel extends JPanel {
    private EmployeeManager employeeManager;
    private EmployeeTableModel tableModel;
    private JComboBox<String> cmbSearchBy;
    private JTable resultsTable;
    private JButton btnSearch;
    private JButton btnShowAll;
    private JTextField txtSearch;

    public SearchPanel(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
        cmbSearchBy = new JComboBox<>(new String[] { "ID", "Name", "Department" });
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Search");
        btnShowAll = new JButton("Show All");
        tableModel = new EmployeeTableModel(employeeManager);
        resultsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setFillsViewportHeight(true);
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        JLabel title = new JLabel("Search Employees");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(245, 247, 250));
        topPanel.add(cmbSearchBy);
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnShowAll);

        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> handleSearch());
        btnShowAll.addActionListener(e -> handleShowAll());
    }

    public void handleSearch() {
        String searchBy = (String) cmbSearchBy.getSelectedItem();
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<Employee> results;
        switch (searchBy) {
            case "ID":
                try {
                    int id = Integer.parseInt(searchText);
                    Employee emp = employeeManager.findById(id);
                    results = emp != null ? List.of(emp) : List.of();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid ID.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                break;
            case "Name":
                results = employeeManager.searchByName(searchText);
                break;
            case "Department":
                results = employeeManager.searchByDepartment(searchText);
                break;
            default:
                results = List.of();
        }
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No matching employee found");
        }
        tableModel.setEmployees(results);
    }

    public void handleShowAll() {
        txtSearch.setText("");
        tableModel.refresh();
    }

    public void refresh() {
        cmbSearchBy.setSelectedIndex(0);
        txtSearch.setText("");
        tableModel.refresh();
    }
}
