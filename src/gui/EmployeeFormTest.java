package gui;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class EmployeeFormTest {

    public static void main(String[] args) {
        try {
            // Set the Look and Feel to match the host operating system
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JFrame frame = new JFrame("Employee Management System");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);

        AddEmployeePanel form = new AddEmployeePanel(null, null, null);

        frame.add(form);
        frame.setVisible(true);
    }
}