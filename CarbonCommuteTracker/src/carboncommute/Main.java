package carboncommute;
import carboncommute.system.CarbonCommuteSystem;
/**
 * Main - Application Entry Point
 */
public class Main {
    public static void main(String[] args) {
        // Instantiate and run the controller
        CarbonCommuteSystem system = new CarbonCommuteSystem();
        system.run();
    }
}
