package service;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import model.Employee;
import storage.FileHandler;
import exception.ValidationException;

public class EmployeeManager {

    private List<Employee> employees;
    private FileHandler fileHandler;
    private DepartmentManager departmentManager;

    public EmployeeManager(FileHandler fh, DepartmentManager dm) {

        this.fileHandler = fh;
        this.departmentManager = dm;

        this.employees = new ArrayList<>();
    }
    public void loadFromFile() throws IOException {
        this.employees = fileHandler.loadEmployees();
    } 
    private void saveToFile() throws IOException {
        fileHandler.saveEmployees(employees);
    }
    public boolean idExists(int id) {
        for (Employee emp : employees) {
            if (emp.getId() == id) {
                return true;
            }
        }
        return false;
    }
    public void addEmployee(Employee employee) throws ValidationException, IOException {
        if (employee == null) {
            throw new ValidationException("Employee cannot be null");
        }
        if (!departmentManager.nameExists(employee.getDepartmentName())) {
            throw new ValidationException("Invalid department: " + employee.getDepartmentName());
        }
        if (idExists(employee.getId())) {
            throw new ValidationException("Employee ID already exists: " + employee.getId());
        }
        employees.add(employee);
        saveToFile();
    }
    public Employee findById(int id) {
        for (Employee emp : employees) {
            if (emp.getId() == id) {
                return emp;
            }
        }
        return null;
    }

    public void updateEmployee(int id,Employee updatedEmployee) throws ValidationException, IOException {
        if (updatedEmployee == null) {
            throw new ValidationException("Updated employee cannot be null");
        }
        if (updatedEmployee.getId() != id) {
            throw new ValidationException("Updated employee ID does not match the existing employee ID");
        }
        if (!departmentManager.nameExists(updatedEmployee.getDepartmentName())) {
            throw new ValidationException("Invalid department: " + updatedEmployee.getDepartmentName());
        }
        Employee existingEmployee = findById(id);
        if (existingEmployee == null) {
            throw new ValidationException("Employee with ID " + id + " does not exist");
        }
        int index = employees.indexOf(existingEmployee);
        employees.set(index, updatedEmployee);
        saveToFile();
    }
    public boolean deleteEmployee(int id) throws IOException {
        Employee employeeToDelete = findById(id);
        if (employeeToDelete == null) {
            return false;
        }
        employees.remove(employeeToDelete);
        saveToFile();
        return true;
    }
    public List<Employee> searchByName(String text){
        List<Employee> results = new ArrayList<>();
        if(text==null||text.isEmpty())
            return results;
        text=text.toLowerCase();
        for(Employee e :employees){
            String employeeName=e.getFullName().toLowerCase();
            if(employeeName.contains(text))
                results.add(e);
        }
        return results;
    }
    public List<Employee> searchByDepartment(String departmentName){
        List<Employee> results = new ArrayList<>();
        if(departmentName==null||departmentName.isEmpty())
            return results;
        departmentName=departmentName.toLowerCase();
        for(Employee e:employees){
            if(e.getDepartmentName().toLowerCase().contains(departmentName))
                results.add(e);
        }
        return results;
    }
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees);
    }
    public double calculateSalary(int id) throws ValidationException {
        Employee employee = findById(id);
        if (employee == null) {
            throw new ValidationException("Employee with ID " + id + " does not exist");
        }
        return employee.calculateSalary();
    }
    public double getTotalPayroll() {
        double totalSalary = 0.0;
        for (Employee employee : employees) {
            totalSalary += employee.calculateSalary();
        }
        return totalSalary;
    }
    public int countByDepartment(String dept){
        int count=0;
        if(dept==null||dept.isEmpty())
            return count;
        for(Employee e:employees){
            if(e.getDepartmentName().equalsIgnoreCase(dept))
                count++;
        }
        return count;
    }
        public int getEmployeeCount(){
        return employees.size();
    }
    public int generateNextId(){
        int maxId=0;
        for(Employee e:employees){
            if(e.getId()>maxId)
                maxId=e.getId();
        }
        return maxId+1;
    }
}
