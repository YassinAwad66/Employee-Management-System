import javax.swing.table.AbstractTableModel;
import manager.EmployeeManager;
import java.util.List;
public class EmployeeTableModel extends AbstractTableModel{
    private EmployeeManager employeeManager ;
    private List<Employee> employees;
    public  EmployeeTableModel(EmployeeManager employeeManager ) {
        this.employeeManager=employeeManager;
        employees=this.employeeManager.getAllEmployees();
    }
    @Override 
    public int getRowCount(){
        return employees.size();
    }
    @Override 
    public int  getColumnCount(){
        return 5;
    }
    @Override
public String getColumnName(int column) {
    switch (column) {
        case 0:
            return "ID";
        case 1:
            return "Full Name";
        case 2:
            return "Salary";
        case 3:
            return "Department Name";
        case 4:
            return "Position";
        default:
            return "";
        }
    }
    @Override
public Object getValueAt(int rowIndex, int columnIndex) {
    Employee emp = employees.get(rowIndex);
    switch (columnIndex) {
        case 0:
            return emp.getId();
        case 1:
            return emp.getFullName();
        case 2:
            return emp.calculateSalary();
        case 3:
            return emp.getDepartmentName();
        case 4:
            return emp.getPosition();
        default:
            return "";
        }
    }
public void refresh() {
    employees = employeeManager.getAllEmployees();
    fireTableDataChanged();
    }
public void setEmployees(List<Employee> employees) {
    if (employees == null) {
        throw new IllegalArgumentException("Employees cannot be null");
        }

    this.employees = employees;
    fireTableDataChanged();
    }
    public Employee getEmployeeAt(int row){
        return employees.get(row);
    }
}
