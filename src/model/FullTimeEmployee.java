package model;

public class FullTimeEmployee extends Employee {
  private double  basicSalary;
  private double bonus;
    
    
    public FullTimeEmployee(int id, String fullName, int age, String departmentName, String position, double  basicSalary, double bonus) {
        super(id, fullName, age, departmentName, position);
       setBasicSalary(basicSalary);
       setBonus(bonus);
    }
    //getters
  public double getBasicSalary(){return basicSalary;}
  public double getBonus(){return bonus;}
   
    //setters
  public void  setBasicSalary(double basicSalary){
  if(basicSalary < 0){
   throw new IllegalArgumentException (" Basic salary cannot be negative");   
  }
  this.basicSalary=basicSalary;
  }
  public void setBonus(double bonus){
  if(bonus < 0){
   throw new IllegalArgumentException (" bonus cannot be negative");   
  }
  this.bonus=bonus;
  
  }
  
  
    @Override
    public double calculateSalary() {
        return basicSalary+bonus;
    }

    @Override
    public String getEmployeeType() {
      return "Full-Time"; 
    }

    @Override
    public String toFileString() {
       return "FT," + getId() + "," +
           getFullName() + "," +
           getAge() + "," +
           getDepartmentName() + "," +
           getPosition() + "," +
           basicSalary + "," + bonus;
    }
    
}

