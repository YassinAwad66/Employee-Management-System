package storage;

import model.Employee;
import model.Department;
import model.EmployeeFactory;
import exception.ValidationException;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    
    private static final String DATA_DIR = "data";
    private static final String EMPLOYEE_FILE = "data/employees.csv";
    private static final String DEPARTMENT_FILE = "data/departments.csv";


    // Save Employees
    public void saveEmployees(List<Employee> list) throws IOException {

        ensureDataDirectory();

        Path original = Paths.get(EMPLOYEE_FILE);
        Path temp = Files.createTempFile(Paths.get(DATA_DIR), "employees_", ".tmp");

        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temp)) {

                for (Employee emp : list) {
                    writer.write(emp.toFileString());
                    writer.newLine();
                }
            }

            Files.move(
                temp,
                original,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            );

        } finally {
            Files.deleteIfExists(temp);
        }
    }


    // Load Employees
    public List<Employee> loadEmployees() throws IOException {

        List<Employee> employees = new ArrayList<>();

        Path file = Paths.get(EMPLOYEE_FILE);

        if (!Files.exists(file)) {
            return employees;
        }

        try (BufferedReader reader = Files.newBufferedReader(file)) {

            String line;

            while ((line = reader.readLine()) != null) {

                try {
                    Employee emp = EmployeeFactory.fromFileString(line);
                    employees.add(emp);

                } catch (ValidationException | RuntimeException e) {
                    System.err.println("Invalid employee line: " + line);
                    System.err.println("Reason: " + e.getMessage());
                }
            }
        }

        return employees;
    }


    // Save Departments
    public void saveDepartments(List<Department> list) throws IOException {

        ensureDataDirectory();

        Path original = Paths.get(DEPARTMENT_FILE);
        Path temp = Files.createTempFile(Paths.get(DATA_DIR), "departments_", ".tmp");

        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temp)) {

                for (Department dept : list) {
                    writer.write(dept.toFileString());
                    writer.newLine();
                }
            }

            Files.move(
                temp,
                original,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            );

        } finally {
            Files.deleteIfExists(temp);
        }
    }


    // Load Departments
    public List<Department> loadDepartments() throws IOException {

        List<Department> departments = new ArrayList<>();

        Path file = Paths.get(DEPARTMENT_FILE);

        if (!Files.exists(file)) {
            return departments;
        }

        try (BufferedReader reader = Files.newBufferedReader(file)) {

            String line;

            while ((line = reader.readLine()) != null) {

                try {
                    Department dept = Department.fromFileString(line);
                    departments.add(dept);

                } catch (RuntimeException e) {
                    System.err.println("Invalid department line: " + line);
                    System.err.println("Reason: " + e.getMessage());
                }
            }
        }

        return departments;
    }


    // Create data folder if it does not exist
    public void ensureDataDirectory() throws IOException {

        Path dataFolder = Paths.get(DATA_DIR);

        Files.createDirectories(dataFolder);
    }
}

