package carboncommute.system;
import java.util.Scanner;
/**
 * InputValidator - Utility / Helper Class
 *
 * Demonstrates: Encapsulation (static utility methods)
 *
 * Provides safe, validated input methods to prevent crashes on bad input.
 * Requirement from OOP final guidelines: system must not crash on bad user input.
 */
public class InputValidator {

    private InputValidator() {} // Utility class - not instantiated

    /**
     * Reads a valid integer within [min, max] from the user.
     * Re-prompts on invalid input.
     */
    public static int readInt(Scanner sc, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = sc.nextLine().trim();
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("  [!] Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid input. Please enter a whole number.");
            }
        }
    }

    /**
     * Reads a valid positive double from the user.
     * Re-prompts on invalid or non-positive input.
     */
    public static double readPositiveDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = sc.nextLine().trim();
                double value = Double.parseDouble(line);
                if (value > 0) {
                    return value;
                }
                System.out.println("  [!] Distance must be greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid input. Please enter a number (e.g. 12.5).");
            }
        }
    }

    /**
     * Reads a non-empty string from the user.
     * Re-prompts if blank.
     */
    public static String readNonEmpty(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println(" This field cannot be empty.");
        }
    }
    /**
     * Reads an optional string (can be empty/blank).
     */
    public static String readOptional(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    /**
     * Reads a valid email address (basic check: contains '@' and '.').
     */
    public static String readEmail(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (value.contains("@") && value.contains(".") && value.length() > 5) {
                return value;
            }
            System.out.println("  [!] Please enter a valid email address.");
        }
    }

    /**
     * Reads a valid year (2020 - 2030).
     */
    public static int readYear(Scanner sc) {
        return readInt(sc, "  Enter year (e.g. 2026): ", 2020, 2030);
    }

    /**
     * Reads a valid month (1 - 12).
     */
    public static int readMonth(Scanner sc) {
        return readInt(sc, "  Enter month (1-12): ", 1, 12);
    }
}
