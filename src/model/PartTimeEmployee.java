package model;

public class PartTimeEmployee extends Employee {
 private double  hourlyRate ;
 private double hoursWorked;
    
    public PartTimeEmployee(int id, String fullName, int age, String departmentName, String position, double hourlyRate, double hoursWorked) {
        super(id, fullName, age, departmentName, position);
        setHourlyRate(hourlyRate);
        setHoursWorked(hoursWorked);
    }

    //getters
    public double getHourlyRate(){return hourlyRate;}
    public double getHoursWorked(){return hoursWorked;}
    
    //setters
    public void setHourlyRate(double hourlyRate){
    if(hourlyRate<0){
    throw new IllegalArgumentException("Hourly rete cannot be negative");
    }
    this.hourlyRate=hourlyRate;
    }
     public void setHoursWorked(double hoursWorked){
    if(hoursWorked<0){
    throw new IllegalArgumentException("Hours worked cannot be negative");
    }
    this.hoursWorked=hoursWorked;
     }
    
    @Override
    public double calculateSalary() {
       return hourlyRate * hoursWorked;  
    }

    @Override
    public String getEmployeeType() {
       return "Part-Time";
    }

    @Override
    public String toFileString() {
         return "PT," + getId() + "," +
           getFullName() + "," +
           getAge() + "," +
           getDepartmentName() + "," +
           getPosition() + "," +
           hourlyRate + "," + hoursWorked;
    }
    
}

