package carboncommute.transport;

/**
 * Bicycle - Derived Class from TransportMode
 *
 * Demonstrates: Inheritance, Polymorphism
 * Zero-emission sustainable transport: 0.00 kg CO2/km
 */
public class Bicycle extends TransportMode {

    public Bicycle() {
        super("Bicycle", 0.00);
    }

    @Override
    public double calculateEmission(double distanceKm) {
        return 0.0; // Zero direct emissions
    }

    @Override
    public String getGreenAlternative() {
        return "Excellent! Cycling is zero-emission and the healthiest commute option.";
    }
}
