package carboncommute.transport;

/**
 * TransportMode - Abstract Base Class
 *
 * Demonstrates: Abstraction, Encapsulation, Polymorphism
 *
 * Each concrete transport type extends this class and overrides
 * calculateEmission() with its own emission factor logic.
 */
public abstract class TransportMode {

    // Encapsulated fields - private, accessed through getters
    private String modeName;
    private double emissionFactor; // kg CO2 per km

    // Constructor
    public TransportMode(String modeName, double emissionFactor) {
        this.modeName = modeName;
        this.emissionFactor = emissionFactor;
    }

    // Abstract method - forces all subclasses to implement their own calculation
    // Demonstrates: Abstraction + Polymorphism
    public abstract double calculateEmission(double distanceKm);

    // Returns a formatted suggestion for greener alternatives
    public abstract String getGreenAlternative();

    // Getters (Encapsulation)
    public String getModeName() {
        return modeName;
    }

    public double getEmissionFactor() {
        return emissionFactor;
    }

    // toString for display
    @Override
    public String toString() {
        return String.format("%-15s (%.3f kg CO2/km)", modeName, emissionFactor);
    }
}
