package carboncommute.transport;

/**
 * Motorbike - Derived Class from TransportMode
 *
 * Demonstrates: Inheritance, Polymorphism
 * Medium emission travel at 0.10 kg CO2/km
 */
public class Motorbike extends TransportMode {

    public Motorbike() {
        super("Motorbike", 0.10);
    }

    @Override
    public double calculateEmission(double distanceKm) {
        return distanceKm * getEmissionFactor();
    }

    @Override
    public String getGreenAlternative() {
        return "Try the bus or train - both emit less CO2 per km than a motorbike.";
    }
}
