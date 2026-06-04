package carboncommute.transport;

/**
 * Walking - Derived Class from TransportMode
 *
 * Demonstrates: Inheritance, Polymorphism
 * Zero-emission and the healthiest option: 0.00 kg CO2/km
 */
public class Walking extends TransportMode {

    public Walking() {
        super("Walking", 0.00);
    }

    @Override
    public double calculateEmission(double distanceKm) {
        return 0.0; // Zero emissions
    }

    @Override
    public String getGreenAlternative() {
        return "Perfect! Walking produces zero emissions. Keep it up!";
    }
}
