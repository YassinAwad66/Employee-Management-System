package gui;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
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

    private static final int ROW_HEIGHT = 38;

    private final JTextField txtDeptId = createTextField();
    private final JTextField txtDeptName = createTextField();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "ID", "Department Name", "Employees" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable departmentTable = new JTable(tableModel);
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

        JPanel top = new JPanel(new BorderLayout(0, 18));
        top.setBackground(BG);
        top.add(createHeader(), BorderLayout.NORTH);
        top.add(createAddSection(), BorderLayout.CENTER);

        JPanel page = new ScrollablePage();
        page.setLayout(new BorderLayout(0, 18));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(20, 35, 20, 35));
        page.add(top, BorderLayout.NORTH);
        page.add(createTableSection(), BorderLayout.CENTER);

        // The page scrolls when the window is too short, so the table is never squeezed
        JScrollPane pageScroll = new JScrollPane(page);
        pageScroll.setBorder(null);
        pageScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(pageScroll, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> handleAdd());
        btnDelete.addActionListener(e -> handleDelete());

        refreshTable();
    }

    // ================= HEADER =================

    private JPanel createHeader() {
        JPanel header = new JPanel(new GridLayout(0, 1, 0, 5));
        header.setBackground(BG);

        JLabel title = new JLabel("Departments");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Add, view and delete company departments");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        header.add(title);
        header.add(subtitle);
        return header;
    }

    // ================= ADD SECTION =================

    private JPanel createAddSection() {
        JPanel card = createCard("01", "New Department", "Department ID and unique name");

        JPanel grid = new JPanel(new GridLayout(1, 2, 25, 0));
        grid.setBackground(WHITE);
        grid.add(createFieldGroup("Department ID", txtDeptId));
        grid.add(createFieldGroup("Department Name", txtDeptName));

        styleButton(btnAdd, true);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(WHITE);
        body.add(grid, BorderLayout.CENTER);
        body.add(createButtonRow(btnAdd), BorderLayout.SOUTH);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // ================= TABLE SECTION =================

    private JPanel createTableSection() {
        JPanel card = createCard("02", "All Departments", "Select a row to delete it");

        departmentTable.setFont(INPUT_FONT);
        departmentTable.setForeground(TEXT);
        departmentTable.setRowHeight(ROW_HEIGHT);
        departmentTable.setFillsViewportHeight(true);
        departmentTable.setShowVerticalLines(false);
        departmentTable.setGridColor(BORDER);
        departmentTable.setSelectionBackground(LIGHT_PURPLE);
        departmentTable.setSelectionForeground(PURPLE);
        departmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        departmentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        JTableHeader tableHeader = departmentTable.getTableHeader();
        tableHeader.setPreferredSize(new Dimension(0, ROW_HEIGHT));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setForeground(PURPLE);
        headerRenderer.setBackground(LIGHT_PURPLE);
        tableHeader.setDefaultRenderer(headerRenderer);
        DefaultTableCellRenderer idHeaderRenderer = new DefaultTableCellRenderer();
        idHeaderRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        idHeaderRenderer.setForeground(PURPLE);
        idHeaderRenderer.setBackground(LIGHT_PURPLE);
        departmentTable.getColumnModel().getColumn(0).setHeaderRenderer(idHeaderRenderer);

        JScrollPane scroll = new JScrollPane(departmentTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(WHITE);
        // Header + 5 rows (+2 for the border)
        scroll.setPreferredSize(new Dimension(700, 6 * ROW_HEIGHT + 2));

        styleButton(btnDelete, false);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(WHITE);
        body.add(scroll, BorderLayout.CENTER);
        body.add(createButtonRow(btnDelete), BorderLayout.SOUTH);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // ================= BEHAVIOR =================

    public void refreshTable() {
        List<Department> list = departmentManager.getAllDepartments();
        if (list.isEmpty()) {
            try {
                departmentManager.loadFromFile();
                list = departmentManager.getAllDepartments();
            } catch (IOException ex) {
                showError("Could not load departments: " + ex.getMessage());
                return;
            }
        }

        tableModel.setRowCount(0);
        for (Department d : list) {
            tableModel.addRow(new Object[] {
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
        } catch (DuplicateEntryException ex) {
            showError(ex.getMessage());
            return;
        } catch (ValidationException ex) {
            showError(ex.getMessage());
            return;
        } catch (IOException ex) {
            showError("Could not save the department: " + ex.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Department added successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);

        txtDeptId.setText("");
        txtDeptName.setText("");
        notifyChanged();
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

        notifyChanged();
    }

    private void notifyChanged() {
        refreshTable();
        if (onDepartmentsChanged != null) {
            onDepartmentsChanged.run();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ================= CARD DESIGN =================

    private JPanel createCard(String number, String title, String description) {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 16),
                new EmptyBorder(16, 25, 18, 25)));

        JPanel heading = new JPanel(new BorderLayout(14, 0));
        heading.setBackground(WHITE);

        JLabel numberLabel = new JLabel(number, SwingConstants.CENTER);
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        numberLabel.setForeground(PURPLE);
        numberLabel.setOpaque(true);
        numberLabel.setBackground(LIGHT_PURPLE);
        numberLabel.setPreferredSize(new Dimension(38, 38));

        JPanel titlePanel = new JPanel(new GridLayout(0, 1, 0, 2));
        titlePanel.setBackground(WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SECTION_FONT);
        titleLabel.setForeground(TEXT);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionLabel.setForeground(MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(descriptionLabel);

        heading.add(numberLabel, BorderLayout.WEST);
        heading.add(titlePanel, BorderLayout.CENTER);

        card.add(heading, BorderLayout.NORTH);
        return card;
    }

    private JPanel createFieldGroup(String labelText, JComponent input) {
        JPanel group = new JPanel(new BorderLayout(0, 7));
        group.setBackground(WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT);

        group.add(label, BorderLayout.NORTH);
        group.add(input, BorderLayout.CENTER);
        return group;
    }

    private JPanel createButtonRow(JButton button) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(WHITE);
        row.add(button);
        return row;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(INPUT_FONT);
        field.setForeground(TEXT);
        field.setCaretColor(PURPLE);
        field.setBackground(new Color(250, 249, 252));
        field.setPreferredSize(new Dimension(200, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 10),
                new EmptyBorder(0, 13, 0, 13)));
        return field;
    }

    private void styleButton(JButton button, boolean primary) {
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
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(primary ? WHITE : PURPLE);
        button.setBackground(primary ? PURPLE : LIGHT_PURPLE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 24, 10, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
    }

    // ================= SCROLLABLE PAGE =================

    /**
     * Follows the window width, fills a tall window, and scrolls in a short one.
     */
    private static class ScrollablePage extends JPanel implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
            return visible.height;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return getParent().getHeight() > getPreferredSize().height;
        }
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