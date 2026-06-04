package carboncommute.transport;

/**
 * Carpool - Derived Class from TransportMode
 *
 * Demonstrates: Inheritance, Polymorphism
 * Emission shared among 2+ passengers: 0.07 kg CO2/km (per person)
 */
public class Carpool extends TransportMode {

    private int passengers;

    public Carpool(int passengers) {
        // Emission split by number of passengers (minimum 2)
        super("Carpool (" + passengers + " pax)", 0.21 / Math.max(2, passengers));
        this.passengers = Math.max(2, passengers);
    }

    // Default 2-person carpool
    public Carpool() {
        this(2);
    }

    @Override
    public double calculateEmission(double distanceKm) {
        return distanceKm * getEmissionFactor();
    }

    @Override
    public String getGreenAlternative() {
        return "Great effort sharing! Adding one more passenger further halves your footprint.";
    }

    public int getPassengers() {
        return passengers;
    }
}
