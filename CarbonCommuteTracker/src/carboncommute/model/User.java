package carboncommute.model;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * User - Entity Class
 *
 * Demonstrates: Encapsulation, Composition (HAS-MANY CommuteRecords)
 *
 * Stores user profile information and manages the full commute history.
 * All fields are private; interaction is through public methods only.
 */
public class User {

    // Private fields - Encapsulation
    private String userId;
    private String name;
    private String email;
    private List<CommuteRecord> commuteHistory; // Composition: User HAS-MANY CommuteRecords
    // Constructor
    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.commuteHistory = new ArrayList<>();
    }
    // ── Core Methods ──────────────────────────────────────────────────────────
    /**
     * Adds a commute record to this user's history.
     */
    public void addCommute(CommuteRecord record) {
        if (record != null) {
            commuteHistory.add(record);
        }
    }
    /**
     * Returns the full commute history (defensive copy of list).
     */
    public List<CommuteRecord> getCommuteHistory() {
        return new ArrayList<>(commuteHistory);
    }
    /**
     * Calculates total CO2 emission across all recorded trips.
     */
    public double calculateTotalEmission() {
        double total = 0.0;
        for (CommuteRecord record : commuteHistory) {
            total += record.getEmissionKg();
        }
        return total;
    }

    /**
     * Calculates total distance across all trips.
     */
    public double calculateTotalDistance() {
        double total = 0.0;
        for (CommuteRecord record : commuteHistory) {
            total += record.getDistanceKm();
        }
        return total;
    }

    /**
     * Returns records for a specific week (date must fall in that week's Mon-Sun).
     */
    public List<CommuteRecord> getRecordsForWeek(LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        List<CommuteRecord> weekly = new ArrayList<>();
        for (CommuteRecord record : commuteHistory) {
            LocalDate d = record.getDate();
            if (!d.isBefore(weekStart) && !d.isAfter(weekEnd)) {
                weekly.add(record);
            }
        }
        return weekly;
    }

    /**
     * Returns records for a specific month and year.
     */
    public List<CommuteRecord> getRecordsForMonth(int year, int month) {
        List<CommuteRecord> monthly = new ArrayList<>();
        for (CommuteRecord record : commuteHistory) {
            if (record.getDate().getYear() == year && record.getDate().getMonthValue() == month) {
                monthly.add(record);
            }
        }
        return monthly;
    }

    /**
     * Displays user profile summary.
     */
    public void displayProfile() {
        System.out.printf("  │  User ID  : %-28s│%n", userId);
        System.out.printf("  │  Name     : %-28s│%n", name);
        System.out.printf("  │  Email    : %-28s│%n", email);
        System.out.printf("  │  Trips    : %-28d│%n", commuteHistory.size());
        System.out.printf("  │  Total CO2: %-25.4f kg │%n", calculateTotalEmission());
    }

    /**
     * Displays full commute history in a formatted table.
     */
    public void viewHistory() {
        if (commuteHistory.isEmpty()) {
            System.out.println("  No commute records found for " + name + ".");
            return;
        }
        System.out.println();
        System.out.println("  Commute history for: " + name);
        System.out.println("   No. │ Date         │ Transport       │ Distance   │ Emission        │ Notes");
      
        int i = 1;
        for (CommuteRecord record : commuteHistory) {
            record.displayRecord(i++);
        }
        System.out.printf("  Total: %d trips | %.1f km | %.4f kg CO2%n",
                commuteHistory.size(), calculateTotalDistance(), calculateTotalEmission());
    }

    /**
     * Serializes user to file string format.
     * Format: USER|userId|name|email
     */
    public String toFileHeader() {
        return String.format("USER|%s|%s|%s", userId, name, email);
    }

    // Getters (Encapsulation)
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getTripCount() { return commuteHistory.size(); }

    @Override
    public String toString() {
        return String.format("User[%s | %s | %s | %d trips]", userId, name, email, commuteHistory.size());
    }
}
