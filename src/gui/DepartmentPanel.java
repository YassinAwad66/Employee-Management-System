package gui;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import model.*;
import service.*;
import exception.*;

public class DepartmentPanel extends JPanel {

    // Colors
    private static final Color BG = new Color(246, 245, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color PURPLE = new Color(105, 76, 160);
    private static final Color LIGHT_PURPLE = new Color(239, 234, 248);
    private static final Color TEXT = new Color(45, 42, 55);
    private static final Color MUTED = new Color(135, 130, 145);
    private static final Color BORDER = new Color(230, 226, 237);

    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 25);
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private final JTextField txtDeptId = createTextField();
    private final JTextField txtDeptName = createTextField();
    private final JTable departmentTable = new JTable();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Department Name", "Employees"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JButton btnAdd = new JButton("Add Department");
    private final JButton btnDelete = new JButton("Delete Selected");

    private final DepartmentManager departmentManager;
    private final EmployeeManager employeeManager;
    private final Runnable onDepartmentsChanged;

    public DepartmentPanel(DepartmentManager dm, EmployeeManager em, Runnable onChanged) {
        this.departmentManager = dm;
        this.employeeManager = em;
        this.onDepartmentsChanged = onChanged;

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(30, 35, 30, 35));

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(BG);

        mainContent.add(createHeader());
        mainContent.add(Box.createVerticalStrut(25));
        mainContent.add(createAddSection());
        mainContent.add(Box.createVerticalStrut(18));

        add(mainContent, BorderLayout.NORTH);
        add(createTableSection(), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> handleAdd());
        btnDelete.addActionListener(e -> handleDelete());

        refreshTable();
    }

    // ================= HEADER =================

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);

        JLabel title = new JLabel("Departments");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Add, view and delete company departments");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BG);

        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(7));
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    // ================= ADD SECTION =================

    private JPanel createAddSection() {
        JPanel card = createCard("01", "New Department", "Department ID and unique name");

        JPanel grid = new JPanel(new GridLayout(0, 2, 25, 20));
        grid.setBackground(WHITE);
        grid.add(createFieldGroup("Department ID", txtDeptId));
        grid.add(createFieldGroup("Department Name", txtDeptName));

        styleButton(btnAdd, true);
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonRow.setBackground(WHITE);
        buttonRow.add(btnAdd);

        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(WHITE);
        body.add(grid, BorderLayout.CENTER);
        body.add(buttonRow, BorderLayout.SOUTH);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // ================= TABLE SECTION =================

    private JPanel createTableSection() {
        JPanel card = createCard("02", "All Departments", "Select a row to delete it");

        departmentTable.setModel(tableModel);
        departmentTable.setFont(INPUT_FONT);
        departmentTable.setForeground(TEXT);
        departmentTable.setRowHeight(38);
        departmentTable.setShowVerticalLines(false);
        departmentTable.setGridColor(BORDER);
        departmentTable.setSelectionBackground(LIGHT_PURPLE);
        departmentTable.setSelectionForeground(PURPLE);
        departmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader tableHeader = departmentTable.getTableHeader();
        tableHeader.setFont(LABEL_FONT);
        tableHeader.setForeground(PURPLE);
        tableHeader.setBackground(LIGHT_PURPLE);
        tableHeader.setPreferredSize(new Dimension(0, 38));

        JScrollPane scroll = new JScrollPane(departmentTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(WHITE);

        styleButton(btnDelete, false);
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonRow.setBackground(WHITE);
        buttonRow.add(btnDelete);

        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(WHITE);
        body.add(scroll, BorderLayout.CENTER);
        body.add(buttonRow, BorderLayout.SOUTH);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // ================= BEHAVIOR =================

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Department> list = departmentManager.getAllDepartments();
        for (Department d : list) {
            tableModel.addRow(new Object[]{
                d.getId(),
                d.getName(),
                employeeManager.countByDepartment(d.getName())
            });
        }
        if (txtDeptId.getText().trim().isEmpty()) {
            txtDeptId.setText(String.valueOf(departmentManager.generateNextId()));
        }
    }

    private void handleAdd() {
        String idText = txtDeptId.getText().trim();
        String name = txtDeptName.getText().trim();

        if (idText.isEmpty() || name.isEmpty()) {
            showError("Please enter both Department ID and Department Name.");
            return;
        }
        if (name.contains(",")) {
            showError("Department Name must not contain commas (,).");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException ex) {
            showError("Department ID must be a valid integer.");
            return;
        }
        if (id <= 0) {
            showError("Department ID must be a positive integer.");
            return;
        }

        try {
            departmentManager.addDepartment(id, name);
        } catch (ValidationException ex) {
            showError(ex.getMessage());
            return;
        } catch(DuplicateEntryException ex){
            showError(ex.getMessage());
            return;
        }
        catch (IOException ex) {
            showError("Could not save the department: " + ex.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Department added successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);

        txtDeptId.setText("");
        txtDeptName.setText("");
        if (onDepartmentsChanged != null) {
            onDepartmentsChanged.run();
        } else {
            refreshTable();
        }
    }

    private void handleDelete() {
        int row = departmentTable.getSelectedRow();
        if (row < 0) {
            showError("No department selected.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);

        if (employeeManager.countByDepartment(name) > 0) {
            showError("Cannot delete \"" + name + "\" because employees are still assigned to it.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete department \"" + name + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            departmentManager.deleteDepartment(id);
        } catch (IOException ex) {
            showError("Could not delete the department: " + ex.getMessage());
            return;
        }

        if (onDepartmentsChanged != null) {
            onDepartmentsChanged.run();
        } else {
            refreshTable();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ================= CARD DESIGN =================

    private JPanel createCard(String number, String title, String description) {
        JPanel card = new JPanel(new BorderLayout(0, 22));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 16),
                new EmptyBorder(22, 25, 25, 25)
        ));

        JPanel heading = new JPanel(new BorderLayout(14, 0));
        heading.setBackground(WHITE);

        JLabel numberLabel = new JLabel(number);
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        numberLabel.setForeground(PURPLE);
        numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        numberLabel.setOpaque(true);
        numberLabel.setBackground(LIGHT_PURPLE);
        numberLabel.setPreferredSize(new Dimension(38, 38));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SECTION_FONT);
        titleLabel.setForeground(TEXT);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionLabel.setForeground(MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(descriptionLabel);

        heading.add(numberLabel, BorderLayout.WEST);
        heading.add(titlePanel, BorderLayout.CENTER);

        card.add(heading, BorderLayout.NORTH);
        return card;
    }

    private JPanel createFieldGroup(String labelText, JComponent input) {
        JPanel group = new JPanel(new BorderLayout(0, 9));
        group.setBackground(WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT);

        group.add(label, BorderLayout.NORTH);
        group.add(input, BorderLayout.CENTER);
        return group;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(INPUT_FONT);
        field.setForeground(TEXT);
        field.setCaretColor(PURPLE);
        field.setBackground(new Color(250, 249, 252));
        field.setPreferredSize(new Dimension(200, 44));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 10),
                new EmptyBorder(0, 13, 0, 13)
        ));
        return field;
    }

    private void styleButton(JButton button, boolean primary) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(primary ? WHITE : PURPLE);
        button.setBackground(primary ? PURPLE : LIGHT_PURPLE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 26, 12, 26));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                g2.dispose();
                super.paint(g, c);
            }
        });
    }

    // ================= ROUNDED BORDER =================

    private static class RoundedBorder extends AbstractBorder {

        private final Color color;
        private final int radius;

        public RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, 2, 2);
        }
    }
}