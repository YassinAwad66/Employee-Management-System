package model;

public abstract class Employee {
  private final int id;
  private String fullName;
  private int age;
  private String departmentName;
  private String position;
  
  public Employee(int id, String fullName, int age,
                String departmentName, String position) {

    this.id = id;
    setFullName(fullName);
    setAge(age);
    setDepartmentName(departmentName);
    setPosition(position);
}
  
  //getters
  
 public int getId(){return id;} 
 public String getFullName(){return fullName;}
 public int getAge(){return age;}
 public String getDepartmentName(){return departmentName;} 
 public String getPosition(){return position;}
 
 //setters
 
public void  setFullName(String name){
       if (name == null || name.trim().isEmpty()) {
        throw new IllegalArgumentException("Name cannot be empty");
    }
    this.fullName=name;
} 
public void setAge(int age){
if(age<=0) {
    throw new IllegalArgumentException("Age must be positive");
}
this.age=age;
}   
public void setDepartmentName(String dept){
if(dept==null || dept.trim().isEmpty()) {
throw new IllegalArgumentException("dept cannot be empty");}
this.departmentName=dept;
}
 public void setPosition(String position){
 if(position==null || position.trim().isEmpty()) {
     throw new IllegalArgumentException("position cannot be empty");
 }
    this.position=position; 
 }
 public abstract double calculateSalary();
 public abstract String getEmployeeType();
 public abstract String toFileString();
 @Override
 public String toString() {
     return "ID: " + id +
     ", Name: " + fullName +
     ", Age: " + age +
     ", Department: " + departmentName +
     ", Position: " + position;           
 }
 }