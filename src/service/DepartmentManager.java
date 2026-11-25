package service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import storage.FileHandler;
import exception.*;
import model.Department;

public class DepartmentManager {
    private List<Department> departments;
    private FileHandler fileHandler;

    public DepartmentManager(FileHandler fileHandler) {

        this.fileHandler = fileHandler;
        departments = new ArrayList<>();
    }

    private void seedDefaultDepartments() throws IOException {

        departments.clear();

        departments.add(new Department(1, "IT"));
        departments.add(new Department(2, "Human Resources"));
        departments.add(new Department(3, "Finance"));
        departments.add(new Department(4, "Marketing"));
        departments.add(new Department(5, "Sales"));

        fileHandler.saveDepartments(departments);
    }

    public void loadFromFile() throws IOException {

        departments = fileHandler.loadDepartments();

        if (departments.isEmpty()) {
            seedDefaultDepartments();
        }
    }

    public boolean idExists(int id) {
        for (Department department : departments) {
            if (department.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public boolean nameExists(String name) {
        for (Department department : departments) {
            if (department.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void addDepartment(int id, String name)
            throws ValidationException, DuplicateEntryException, IOException {

        if (id <= 0) {
            throw new ValidationException("Department ID must be positive");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Department name cannot be empty");
        }

        name = name.trim();

        if (idExists(id)) {
            throw new DuplicateEntryException("Department ID already exists");
        }

        if (nameExists(name)) {
            throw new DuplicateEntryException("Department name already exists");
        }

        Department department = new Department(id, name);

        departments.add(department);

        fileHandler.saveDepartments(departments);
    }

    public Department findById(int id) {

        for (Department department : departments) {

            if (department.getId() == id) {
                return department;
            }
        }

        return null;
    }

    public boolean deleteDepartment(int id) throws IOException {

        Department target = findById(id);

        if (target == null) {
            return false;
        }

        departments.remove(target);

        fileHandler.saveDepartments(departments);

        return true;
    }

    public List<Department> getAllDepartments() {

        return Collections.unmodifiableList(
                new ArrayList<>(departments));
    }

    public String[] getDepartmentNames() {

        String[] names = new String[departments.size()];

        for (int i = 0; i < departments.size(); i++) {

            names[i] = departments.get(i).getName();
        }

        return names;
    }

    public int generateNextId() {

        int maxId = 0;

        for (Department department : departments) {

            if (department.getId() > maxId) {
                maxId = department.getId();
            }
        }

        return maxId + 1;
    }

}
