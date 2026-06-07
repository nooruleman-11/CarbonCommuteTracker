package carboncommute.system;
import carboncommute.model.CommuteRecord;
import carboncommute.model.User;
import carboncommute.report.EmissionReport;
import carboncommute.transport.TransportMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * CarbonCommuteSystem - Controller Class (Main System)
 *
 * Demonstrates: Encapsulation, Composition (HAS-MANY Users)
 *
 * Controls all menus, user sessions, commute entry, reports,
 * file I/O, and the overall application flow.
 *
 * This is the central controller of the application.
 */
public class CarbonCommuteSystem {

    // Composition: system HAS-MANY users
    private List<User> users;
    private User currentUser;
    private FileHandler fileHandler;
    private Scanner scanner;

    private static final String DATA_FILE = "data/commute_data.txt";
    private static final String APP_VERSION = "1.0.0";

    // Constructor
    public CarbonCommuteSystem() {
        this.users = new ArrayList<>();
        this.currentUser = null;
        this.fileHandler = new FileHandler(DATA_FILE);
        this.scanner = new Scanner(System.in);
    }

    // ── Application Entry ────────────────────────────────────────────────────

    /**
     * Starts the application. Called from main().
     */
    public void run() {
        printBanner();
        loadData();
        mainMenu();
        saveData();
        printGoodbye();
        scanner.close();
    }

    // ── Main Menu ─────────────────────────────────────────────────────────────

    private void mainMenu() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = InputValidator.readInt(scanner, "  → Enter choice: ", 1, 7);

            switch (choice) {
                case 1: registerUser();     break;
                case 2: loginUser();        break;
                case 3: addCommuteRecord(); break;
                case 4: viewHistory();      break;
                case 5: reportsMenu();      break;
                case 6: viewAllUsers();     break;
                case 7: running = false;    break;
            }
        }
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("       CARBON COMMUTE TRACKER           ");
       
        if (currentUser != null) {
            System.out.printf( "   Logged in as: %-33s║%n", currentUser.getName());
        }
        System.out.println("    1. Register new user                          ");
        System.out.println("    2. Switch / login user                       ");
        System.out.println("    3. Add commute record           [login req.]  ");
        System.out.println("    4. View commute history         [login req.]  ");
        System.out.println("    5. Generate emission reports    [login req.]  ");
        System.out.println("    6. View all registered users                  ");
        System.out.println("    7. Save and exit                              ");
    }
    // ── User Registration 
    private void registerUser() {
        System.out.println();
        System.out.println("  ── REGISTER NEW USER ");
        String name  = InputValidator.readNonEmpty(scanner, "  Full name  : ");
        String email = InputValidator.readEmail(scanner,    "  Email      : ");
        // Check for duplicate email
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                System.out.println("  [!] A user with this email already exists.");
                return;
            }
        }
        String userId = "U" + String.format("%03d", users.size() + 1);
        User newUser = new User(userId, name, email);
        users.add(newUser);
        currentUser = newUser;

        System.out.println();
        System.out.println("  User registered successfully!");
        System.out.println("   You are now logged in as: " + name);
        System.out.printf( "      User ID: %s%n", userId);
    }
    // ── Login / Switch User ───────────────────────────────────────────────────

    private void loginUser() {
        if (users.isEmpty()) {
            System.out.println("  [!] No users registered yet. Please register first.");
            return;
        }
        System.out.println();
        System.out.println("  ── SELECT USER ");
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            System.out.printf("  %d. %s  (%s)  [%d trips]%n",
                    i + 1, u.getName(), u.getEmail(), u.getTripCount());
        }
        int choice = InputValidator.readInt(scanner, "  → Select user: ", 1, users.size());
        currentUser = users.get(choice - 1);
        System.out.println("  [✔] Logged in as: " + currentUser.getName());
    }

    // ── Add Commute Record ────────────────────────────────────────────────────

    private void addCommuteRecord() {
        if (!requireLogin()) return;

        System.out.println();
        System.out.println("  ── ADD COMMUTE RECORD");
        // Date entry
        System.out.println("  Date of commute:");
        System.out.println("  (Press Enter to use today: " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ")");
        System.out.print("  Enter date (YYYY-MM-DD) or press Enter: ");
        String dateInput = scanner.nextLine().trim();
        LocalDate date;
        if (dateInput.isEmpty()) {
            date = LocalDate.now();
        } else {
            try {
                date = LocalDate.parse(dateInput);
            } catch (Exception e) {
                System.out.println("  [!] Invalid date format. Using today's date.");
                date = LocalDate.now();
            }
        }

        // Transport selection (Polymorphism via TransportFactory)
        TransportFactory.printMenu();
        int modeChoice = InputValidator.readInt(scanner, "  → Select transport mode: ", 1, 6);

        TransportMode mode;
        if (modeChoice == 4) {
            // Carpool - ask for passenger count
            int pax = InputValidator.readInt(scanner, "  Number of passengers (including driver): ", 2, 8);
            mode = TransportFactory.create(modeChoice, pax);
        } else {
            mode = TransportFactory.create(modeChoice);
        }

        if (mode == null) {
            System.out.println("  [!] Invalid transport selection.");
            return;
        }

        // Distance
        double distance = InputValidator.readPositiveDouble(scanner, "  Distance (km): ");

        // Optional notes
        String notes = InputValidator.readOptional(scanner, "  Notes (optional, press Enter to skip): ");

        // Create the record - CommuteRecord HAS-A TransportMode (Composition)
        // calculateEmission() called here via Polymorphism
        CommuteRecord record = new CommuteRecord(date, distance, mode,
                notes.isEmpty() ? "N/A" : notes);

        currentUser.addCommute(record);

        // Display result
        System.out.println();
        System.out.println("   Commute recorded successfully!");
        System.out.printf("  Date       : %s%n", date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        System.out.printf("  Transport  : %s%n", mode.getModeName());
        System.out.printf("  Distance   : %.1f km%n", distance);
        System.out.printf("  CO2 Emitted: %.4f kg%n", record.getEmissionKg());
        System.out.println();
        System.out.printf("  Weekly  emission (10 trips): %.4f kg CO2%n", record.getEmissionKg() * 10);
        System.out.printf("  Monthly emission (× 4)     : %.4f kg CO2%n", record.getEmissionKg() * 10 * 4);
        System.out.println();
        System.out.println("  Suggestion: " + mode.getGreenAlternative());
        System.out.println("  ─────────────────────────────────────────────────");
    }

    // ── View History

    private void viewHistory() {
        if (!requireLogin()) return;
        currentUser.viewHistory();
    }

    // ── Reports Menu ─

    private void reportsMenu() {
        if (!requireLogin()) return;

        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("  ── EMISSION REPORTS ");
            System.out.println("  1. Weekly report");
            System.out.println("  2. Monthly report");
            System.out.println("  3. Full summary report");
            System.out.println("  4. My Eco Score");
            System.out.println("  5. Back to main menu");

            int choice = InputValidator.readInt(scanner, "  → Enter choice: ", 1, 5);
            EmissionReport report = new EmissionReport(currentUser);

            switch (choice) {
                case 1:
                    System.out.println("  Enter the start of the week (Monday):");
                    int wy = InputValidator.readYear(scanner);
                    int wm = InputValidator.readMonth(scanner);
                    int wd = InputValidator.readInt(scanner, "  Enter day (1-31): ", 1, 31);
                    try {
                        LocalDate weekStart = LocalDate.of(wy, wm, wd);
                        report.generateWeeklyReport(weekStart);
                    } catch (Exception e) {
                        System.out.println("  [!] Invalid date.");
                    }
                    break;
                case 2:
                    int year = InputValidator.readYear(scanner);
                    int month = InputValidator.readMonth(scanner);
                    report.generateMonthlyReport(year, month);
                    break;
                case 3:
                    report.generateSummaryReport();
                    break;
                case 4:
                    report.showEcoScore();
                    break;
                case 5:
                    back = true;
                    break;
            }
        }
    }

    // ── View All Users ────────────────────────────────────────────────────────

    private void viewAllUsers() {
        System.out.println();
        System.out.println("  ── REGISTERED USERS ");
        if (users.isEmpty()) {
            System.out.println("  No users registered yet.");
            return;
        }
       
        System.out.println("   No.   ID       Name          Email          Trips   ");

        int i = 1;
        for (User u : users) {
            System.out.printf("  %-3d │ %-8s │ %-20s │ %-20s │ %d%n",
                    i++, u.getUserId(), u.getName(), u.getEmail(), u.getTripCount());
        }
        System.out.printf("  Total users: %d%n", users.size());
    }

    // ── File I/O 

    private void loadData() {
        System.out.println();
        users = fileHandler.loadFromFile();
    }

    private void saveData() {
        System.out.println();
        fileHandler.saveToFile(users);
    }

    // ── Helpers
    /**
     * Checks if a user is logged in. Prints a message if not.
     */
    private boolean requireLogin() {
        if (currentUser == null) {
            System.out.println("  [!] Please register or select a user first (options 1 or 2).");
            return false;
        }
        return true;
    }

    // ── Banners 

    private void printBanner() {
        System.out.println("     CARBON COMMUTE TRACKER & EMISSION REPORTER       ║");
    }

    private void printGoodbye() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║  Thank you for using CarbonCommute Tracker!      ║");
        System.out.println("  ║  Every green trip makes a difference.            ║");
        System.out.println("  ╚══════════════════════════════════════════════════╝");
        System.out.println();
    }
}
