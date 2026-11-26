
package validation;

import exception.ValidationException;

public class ValidatorTest {

    private static int passed = 0;
    private static int failed = 0;

    @FunctionalInterface
    interface TestAction {
        void run() throws ValidationException;
    }

    private static void expectValid(String name, TestAction action) {
        try {
            action.run();
            System.out.println("PASS: " + name);
            passed++;
        } catch (Exception e) {
            System.out.println("FAIL: " + name + " - " + e.getMessage());
            failed++;
        }
    }

    private static void expectError(String name, TestAction action) {
        try {
            action.run();
            System.out.println("FAIL: " + name + " - Expected an error");
            failed++;
        } catch (ValidationException e) {
            System.out.println("PASS: " + name);
            passed++;
        } catch (Exception e) {
            System.out.println("FAIL: " + name + " - " + e.getMessage());
            failed++;
        }
    }

    public static void main(String[] args) {

        System.out.println("===== VALIDATOR TESTS =====");

        // Test Full Name
        expectValid("Valid name",
                () -> Validator.requireNotEmpty("Sara Ahmed", "Name"));

        expectError("Empty name",
                () -> Validator.requireNotEmpty("", "Name"));

        expectError("Spaces only",
                () -> Validator.requireNotEmpty("   ", "Name"));

        expectError("Name contains comma",
                () -> Validator.requireNotEmpty("Sara,Ahmed", "Name"));

        // Test Employee ID
        expectValid("Valid ID",
                () -> Validator.parseId("101"));

        expectError("Negative ID",
                () -> Validator.parseId("-5"));

        expectError("Zero ID",
                () -> Validator.parseId("0"));

        expectError("Letters in ID",
                () -> Validator.parseId("abc"));

        // Test Age
        expectValid("Valid age",
                () -> Validator.parseAge("25"));

        expectError("Negative age",
                () -> Validator.parseAge("-10"));

        expectError("Zero age",
                () -> Validator.parseAge("0"));

        expectError("Invalid age",
                () -> Validator.parseAge("xyz"));

        // Test Salary
        expectValid("Valid salary",
                () -> Validator.parseNonNegativeDouble("15000", "Salary"));

        expectValid("Zero bonus",
                () -> Validator.parseNonNegativeDouble("0", "Bonus"));

        expectError("Negative salary",
                () -> Validator.parseNonNegativeDouble("-500", "Salary"));

        // Test Working Hours
        expectValid("Valid working hours",
                () -> Validator.parseNonNegativeDouble("80", "Hours"));

        expectError("Negative working hours",
                () -> Validator.parseNonNegativeDouble("-8", "Hours"));

        // Test Employee Type
        expectValid("Valid employee type",
                () -> Validator.requireSupportedType("Full-Time"));

        expectError("Invalid employee type",
                () -> Validator.requireSupportedType("Freelancer"));

        System.out.println("\n===== TEST RESULTS =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
    }
}
