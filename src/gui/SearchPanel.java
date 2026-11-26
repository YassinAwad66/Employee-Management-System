package gui;

import java.awt.*;
import javax.swing.*;
import java.util.List;
import service.*;
import model.*;

public class SearchPanel extends JPanel {

    private EmployeeManager employeeManager;
    private EmployeeTableModel tableModel;
    private JComboBox<String> cmbSearchBy;
    private JTable resultsTable;
    private JButton btnSearch;
    private JButton btnShowAll;
    private JTextField txtSearch;
    private static final Color BG = new Color(246, 245, 250);
    private static final Color prpl = new Color(139, 92, 246);
    private static final Color darkprpl = new Color(112, 65, 216);
    private static final Color lightprpl = new Color(237, 229, 255);
    private static final Color txt = new Color(41, 36, 58);

    public SearchPanel(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
        cmbSearchBy = new JComboBox<>(new String[] { "ID", "Name", "Department" });
        cmbSearchBy.setFont(new Font("Arial", Font.PLAIN, 15));
        cmbSearchBy.setBackground(Color.WHITE);
        cmbSearchBy.setForeground(txt);
        cmbSearchBy.setBorder(BorderFactory.createLineBorder(prpl));
        cmbSearchBy.setPreferredSize(new Dimension(120, 30));

        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 15));
        txtSearch.setBackground(Color.WHITE);
        txtSearch.setForeground(txt);
        txtSearch.setBorder(BorderFactory.createLineBorder(prpl));
        txtSearch.setCaretColor(prpl);
        txtSearch.setPreferredSize(new Dimension(200, 30));
        txtSearch.setText("Enter search text ...");
        

        btnSearch = new JButton("Search");
        btnSearch.setBackground(lightprpl);
        btnSearch.setForeground(txt);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.setBorderPainted(false);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(100, 35));
        btnSearch.setFont(new Font("Arial", Font.BOLD, 15));

        btnShowAll = new JButton("Show All");
        btnShowAll.setBackground(BG);
        btnShowAll.setForeground(darkprpl);
        btnShowAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnShowAll.setBorder(BorderFactory.createLineBorder(prpl));
        btnShowAll.setFocusPainted(false);
        btnShowAll.setPreferredSize(new Dimension(100, 35));
        btnShowAll.setFont(new Font("Arial", Font.BOLD, 15));

        tableModel = new EmployeeTableModel(employeeManager);
        resultsTable = new JTable(tableModel);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        resultsTable.setRowHeight(32);
        resultsTable.setBackground(Color.WHITE);
        resultsTable.setForeground(txt);
        resultsTable.setSelectionBackground(lightprpl);
        resultsTable.setSelectionForeground(txt);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.getTableHeader().setBackground(prpl);
        resultsTable.getTableHeader().setForeground(darkprpl);
        resultsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        resultsTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        resultsTable.setShowGrid(true);
        resultsTable.setGridColor(darkprpl);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(lightprpl));
        setLayout(new BorderLayout(15, 15));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setBackground(BG);
        JLabel title = new JLabel("Search Employees");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel topPanel = new JPanel();
        topPanel.setBackground(BG);
        topPanel.add(cmbSearchBy);
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnShowAll);

        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(BG);
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
