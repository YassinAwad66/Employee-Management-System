package gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import service.AuthService;
import service.DepartmentManager;
import service.EmployeeManager;
import storage.FileHandler;

public class LoginFrameTest {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to the default look and feel.
        }

        SwingUtilities.invokeLater(() -> {
            FileHandler fileHandler = new FileHandler();
            DepartmentManager departmentManager = new DepartmentManager(fileHandler);
            EmployeeManager employeeManager = new EmployeeManager(fileHandler, departmentManager);
            AuthService authService = new AuthService();

            new LoginFrame(authService, employeeManager, departmentManager).setVisible(true);
        });
    }
}
