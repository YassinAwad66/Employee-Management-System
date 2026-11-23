import javax.swing.*;
import gui.LoginFrame;
import model.*;
import service.*;
import storage.FileHandler;

public class MainApp {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // fall back to the default look and feel
        }

        FileHandler fileHandler = new FileHandler();
        DepartmentManager departmentManager = new DepartmentManager(fileHandler);
        EmployeeManager employeeManager = new EmployeeManager(fileHandler, departmentManager);
        AuthService authService = new AuthService();

        // Departments first: employees are validated against them.
        try {
            departmentManager.loadFromFile();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Could not read the departments file:\n" + ex.getMessage()
                            + "\nStarting with default departments.",
                    "File Error", JOptionPane.WARNING_MESSAGE);
        }

        try {
            employeeManager.loadFromFile();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Could not read the employees file:\n" + ex.getMessage()
                            + "\nStarting with an empty employee list.",
                    "File Error", JOptionPane.WARNING_MESSAGE);
        }

        SwingUtilities.invokeLater(() ->
                new LoginFrame(authService, employeeManager, departmentManager).setVisible(true));
    }
}