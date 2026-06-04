package carboncommute.transport;

/**
 * Bus - Derived Class from TransportMode
 *
 * Demonstrates: Inheritance, Polymorphism
 * Shared public transport at 0.08 kg CO2/km
 */
public class Bus extends TransportMode {

    public Bus() {
        super("Bus", 0.08);
    }

    @Override
    public double calculateEmission(double distanceKm) {
        return distanceKm * getEmissionFactor();
    }

    @Override
    public String getGreenAlternative() {
        return "Good choice! For even lower emissions, try cycling or walking for shorter segments.";
    }
}
