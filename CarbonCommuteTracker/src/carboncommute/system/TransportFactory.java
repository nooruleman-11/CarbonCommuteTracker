package carboncommute.system;
import carboncommute.transport.*;
/**
 * TransportFactory - Factory Utility Class
 *
 * Demonstrates: Encapsulation, Polymorphism
 *
 * Creates TransportMode objects based on menu selection.
 * Centralizes object creation so CarbonCommuteSystem stays clean.
 * All returned references are typed as TransportMode (polymorphism).
 */
public class TransportFactory {

    // Private constructor - this is a utility class, not instantiated
    private TransportFactory() {}

    /**
     * Returns a TransportMode object for the given menu choice.
     * Runtime polymorphism: caller receives a TransportMode reference
     * but the actual object is a specific subclass (Car, Bus, etc.)
     *
     * @param choice 1-6 from the transport menu
     * @param carpoolPassengers number of carpool passengers (used only for choice 4)
     * @return  a TransportMode subclass instance, or null if choice is invalid
     */
    public static TransportMode create(int choice, int carpoolPassengers) {
        switch (choice) {
            case 1: return new Car();
            case 2: return new Motorbike();
            case 3: return new Bus();
            case 4: return new Carpool(carpoolPassengers);
            case 5: return new Bicycle();
            case 6: return new Walking();
            default: return null;
        }
    }

    /**
     * Overload without carpool parameter for non-carpool modes.
     */
    public static TransportMode create(int choice) {
        return create(choice, 2);
    }

    /**
     * Prints the transport selection menu.
     */
    public static void printMenu() {
        System.out.println();
        System.out.println("                  SELECT TRANSPORT MODE                  ");
        System.out.println("         Mode               Emission Factor               ");
        System.out.println("  1  │  Car (Solo)      │  0.210 kg CO2/km (highest)     ");
        System.out.println("  2  │  Motorbike       │  0.100 kg CO2/km               ");
        System.out.println("  3  │  Bus             │  0.080 kg CO2/km               ");
        System.out.println("  4  │  Carpool         │  0.21 ÷ passengers kg CO2/km   ");
        System.out.println("  5  │  Bicycle         │  0.000 kg CO2/km (zero)        ");
        System.out.println("  6  │  Walking         │  0.000 kg CO2/km (zero)        ");
       
    }
}
