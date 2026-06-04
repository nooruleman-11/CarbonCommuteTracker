package carboncommute.transport;

/**
 * Car - Derived Class from TransportMode
 *
 * Demonstrates: Inheritance, Polymorphism (method overriding)
 * Solo car travel is the highest emitter at 0.21 kg CO2/km
 */
public class Car extends TransportMode {

    public Car() {
        super("Car (Solo)", 0.21);
    }

    @Override
    public double calculateEmission(double distanceKm) {
        return distanceKm * getEmissionFactor();
    }

    @Override
    public String getGreenAlternative() {
        return "Consider carpooling, taking the bus, or switching to an electric vehicle.";
    }
}
