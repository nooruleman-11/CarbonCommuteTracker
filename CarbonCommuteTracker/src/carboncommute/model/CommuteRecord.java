package carboncommute.model;
import carboncommute.transport.TransportMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
/**
 * CommuteRecord - Entity / Composition Class
 * Demonstrates: Encapsulation, Composition (HAS-A TransportMode)
 * Stores one trip record: date, distance, transport mode, calculated emission.
 * Serializable to/from file format for persistence.
 */
public class CommuteRecord {
    // Encapsulated fields
    private LocalDate date;
    private double distanceKm;
    private TransportMode transportMode; // Composition: CommuteRecord HAS-A TransportMode
    private double emissionKg;
    private String notes;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // Constructor
    public CommuteRecord(LocalDate date, double distanceKm, TransportMode transportMode, String notes) {
        this.date = date;
        this.distanceKm = distanceKm;
        this.transportMode = transportMode;
        this.emissionKg = transportMode.calculateEmission(distanceKm); // Polymorphism in action
        this.notes = (notes == null || notes.trim().isEmpty()) ? "N/A" : notes.trim();
    }
    // Constructor without notes
    public CommuteRecord(LocalDate date, double distanceKm, TransportMode transportMode) {
        this(date, distanceKm, transportMode, "N/A");
    }
    // Getters (Encapsulation)
    public LocalDate getDate() { return date; }
    public double getDistanceKm() { return distanceKm; }
    public TransportMode getTransportMode() { return transportMode; }
    public double getEmissionKg() { return emissionKg; }
    public String getNotes() { return notes; }
    /**
     * Serializes this record to a pipe-delimited string for file storage.
     * Format: DATE|MODE_NAME|EMISSION_FACTOR|DISTANCE|EMISSION|NOTES
     */
    public String toFileString() {
        return String.format("%s|%s|%.3f|%.2f|%.4f|%s",
                date.format(DATE_FORMAT),
                transportMode.getModeName(),
                transportMode.getEmissionFactor(),
                distanceKm,
                emissionKg,
                notes);
    }
    /**
     * Displays this record in a formatted table row
     */
    public void displayRecord(int index) {
        System.out.printf("  %-3d | %-12s | %-15s | %6.1f km | %8.4f kg CO2 | %s%n",
                index,
                date.format(DATE_FORMAT),
                transportMode.getModeName(),
                distanceKm,
                emissionKg,
                notes);
    }
    @Override
    public String toString() {
        return String.format("[%s] %s | %.1f km | %.4f kg CO2",
                date.format(DATE_FORMAT),
                transportMode.getModeName(),
                distanceKm,
                emissionKg);
    }
}
