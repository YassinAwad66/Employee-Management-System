package model;

import exception.ValidationException;

public class EmployeeFactory {
    private static final String[]  SUPPORTED_TYPES = {"Full-Time", "Part-Time"};
    
    public static Employee create(String type, int id, String name, int age, String dept, String position, double salaryParam1, double salaryParam2)throws ValidationException{
    if ("Full-Time".equals(type)) {
        return new FullTimeEmployee(id, name, age, dept, position, salaryParam1, salaryParam2);   
    }
    else if("Part-Time".equals(type)) {
        return new PartTimeEmployee(id, name, age, dept, position, salaryParam1, salaryParam2);   
    }
   else {
    throw new ValidationException("Unsupported employee type");
    }
  }
    
    //Convert file string to Employee object
  public static Employee fromFileString(String line)throws ValidationException{
     String[] data = line.split(",");
    if (data.length != 8) {
    throw new ValidationException("Invalid employee data");
   }
    String type = data[0];
    int id = Integer.parseInt(data[1]);
    String name = data[2];
    int age = Integer.parseInt(data[3]);
    String dept = data[4];
    String position = data[5];
    double salary1 = Double.parseDouble(data[6]);
    double salary2 = Double.parseDouble(data[7]);

    if (type.equals("FT")) {
        return new FullTimeEmployee(id, name, age, dept,position, salary1, salary2);
    }

    else if (type.equals("PT")) {
        return new PartTimeEmployee(id, name, age, dept, position, salary1, salary2);
    }

    else {
        throw new ValidationException("Unsupported employee type");
    }
  }
  
  //getter
 public static String[] getSupportedTypes(){ return SUPPORTED_TYPES.clone();} 
 
 // Check if employee type is supported
 public static boolean isSupported(String type) {

    for (String supportedType : SUPPORTED_TYPES) {

        if (supportedType.equals(type)) {
            return true;
        }
    }

    return false;
}
    
}

