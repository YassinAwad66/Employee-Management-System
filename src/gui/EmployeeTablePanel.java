package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EmployeeTablePanel extends JPanel {

    private JTable employeeTable;
    private DefaultTableModel tableModel;

    public EmployeeTablePanel() {

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));

        // Title
        JLabel title = new JLabel("Employees Management");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        add(title, BorderLayout.NORTH);

        // Table
        String[] columns = {
            "ID", "Name", "Age", "Job Title",
            "Department", "Type", "Salary"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setRowHeight(30);
        employeeTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(employeeTable);

        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonsPanel = new JPanel();

        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);

        add(buttonsPanel, BorderLayout.SOUTH);
    }
}