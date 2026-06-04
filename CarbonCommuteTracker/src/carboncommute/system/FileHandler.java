package carboncommute.system;
import carboncommute.model.CommuteRecord;
import carboncommute.model.User;
import carboncommute.transport.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * FileHandler - Utility Class for Persistence
 *
 * Demonstrates: File I/O, Encapsulation
 *
 * Saves all user and commute data to a flat text file.
 * Loads and reconstructs User + CommuteRecord objects on startup.
 *
 * File format:
 *   USER|userId|name|email
 *   COMMUTE|date|modeName|emissionFactor|distanceKm|emissionKg|notes
 *   ...
 *   USER|...
 *   COMMUTE|...
 */
public class FileHandler {
    private String filePath;
    public FileHandler(String filePath) {
        this.filePath = filePath;
    }
    // ── Save
    /**
     * Saves all users and their commute records to file.
     */
    public void saveToFile(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (User user : users) {
                writer.write(user.toFileHeader());
                writer.newLine();
                for (CommuteRecord record : user.getCommuteHistory()) {
                    writer.write("COMMUTE|" + record.toFileString());
                    writer.newLine();
                }
            }
            System.out.println(" Data saved to: " + filePath);
        } catch (IOException e) {
            System.out.println(" Error saving data: " + e.getMessage());
        }
    }
    // ── Load
    /**
     * Loads users and commute records from file.
     * Returns an empty list if the file does not exist.
     */
    public List<User> loadFromFile() {
        List<User> users = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("  [i] No saved data found. Starting fresh.");
            return users;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            User currentUser = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts[0].equals("USER") && parts.length >= 4) {
                    currentUser = new User(parts[1], parts[2], parts[3]);
                    users.add(currentUser);
                } else if (parts[0].equals("COMMUTE") && parts.length >= 7 && currentUser != null) {
                    // COMMUTE|date|modeName|emissionFactor|distanceKm|emissionKg|notes
                    LocalDate date = LocalDate.parse(parts[1]);
                    String modeName = parts[2];
                    double emissionFactor = Double.parseDouble(parts[3]);
                    double distanceKm = Double.parseDouble(parts[4]);
                    String notes = parts[6];
                    TransportMode mode = reconstructMode(modeName, emissionFactor);
                    CommuteRecord record = new CommuteRecord(date, distanceKm, mode, notes);
                    currentUser.addCommute(record);
                }
            }
            System.out.printf("  [✔] Loaded %d user(s) from: %s%n", users.size(), filePath);
        } catch (IOException e) {
            System.out.println("  [✘] Error loading data: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  [✘] Corrupt data in file: " + e.getMessage());
        }
        return users;
    }
    // ── Helpers ───────────────────────────────────────────────────────────────
    /**
     * Reconstructs the correct TransportMode subclass from the saved mode name.
     * Demonstrates Polymorphism: the returned reference is always TransportMode.
     */
    private TransportMode reconstructMode(String modeName, double factor) {
        switch (modeName.toLowerCase()) {
            case "car (solo)":   return new Car();
            case "motorbike":    return new Motorbike();
            case "bus":          return new Bus();
            case "bicycle":      return new Bicycle();
            case "walking":      return new Walking();
            default:
                // Carpool or unknown: reconstruct with saved factor
                if (modeName.toLowerCase().startsWith("carpool")) {
                    // Extract passenger count if present
                    int pax = 2;
                    try {
                        String p = modeName.replaceAll("[^0-9]", "");
                        if (!p.isEmpty()) pax = Integer.parseInt(p);
                    } catch (NumberFormatException ignored) {}
                    return new Carpool(pax);
                }
                // Fallback: anonymous transport with saved factor
                return new TransportMode(modeName, factor) {
                    @Override public double calculateEmission(double d) { return d * getEmissionFactor(); }
                    @Override public String getGreenAlternative() { return "Consider a greener option."; }
                };
        }
    }
    public String getFilePath() {
        return filePath;
    }
}