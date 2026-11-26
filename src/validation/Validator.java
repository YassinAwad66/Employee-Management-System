package validation;

import exception.ValidationException;
import model.EmployeeFactory;
import service.DepartmentManager;

public final class Validator {

    private Validator() {
    }

    public static String requireNotEmpty(String value, String fieldName) throws ValidationException {

        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required.");
        }

        if (value.contains(",")) {
            throw new ValidationException(fieldName + " must not contain a comma.");
        }

        return value.trim();
    }

    public static int parseId(String text) throws ValidationException {

        String clean = requireNotEmpty(text, "ID");

        try {
            int id = Integer.parseInt(clean);

            if (id <= 0) {
                throw new ValidationException("ID must be greater than zero.");
            }

            return id;

        } catch (NumberFormatException e) {
            throw new ValidationException("ID must be a valid whole number.");
        }
    }

    public static int parseAge(String text) throws ValidationException {

        String clean = requireNotEmpty(text, "Age");

        try {
            int age = Integer.parseInt(clean);

            if (age <= 0) {
                throw new ValidationException("Age must be greater than zero.");
            }

            return age;

        } catch (NumberFormatException e) {
            throw new ValidationException("Age must be a valid whole number.");
        }
    }

    public static double parseNonNegativeDouble(String text, String fieldName) throws ValidationException {

        String clean = requireNotEmpty(text, fieldName);

        try {
            double number = Double.parseDouble(clean);

            if (!Double.isFinite(number) || number < 0) {
                throw new ValidationException(fieldName + " must be a non-negative number.");
            }

            return number;

        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid number.");
        }
    }

    public static String requireSupportedType(String type) throws ValidationException {

        if (type == null || !EmployeeFactory.isSupported(type)) {
            throw new ValidationException("Unsupported employee type: " + type);
        }

        return type;
    }

   public static String requireValidDepartment(
        String name,
        DepartmentManager dm) throws ValidationException {

    if (dm == null) {
        throw new ValidationException("Department manager is not available.");
    }

    String cleanName = requireNotEmpty(name, "Department");

    if (!dm.nameExists(cleanName)) {
        throw new ValidationException("Please select a valid department.");
    }

    return cleanName;
}
}