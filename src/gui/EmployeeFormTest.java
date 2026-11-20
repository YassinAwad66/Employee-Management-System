package gui;

import javax.swing.JFrame;

public class EmployeeFormTest {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Employee Management System");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);

        EmployeeFormPanel form = new EmployeeFormPanel();

        frame.add(form);
        frame.setVisible(true);
    }
}