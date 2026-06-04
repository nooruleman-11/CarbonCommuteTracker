package carboncommute;
import carboncommute.system.CarbonCommuteSystem;
/**
 * Main - Application Entry Point
 *
 * CarbonCommute Tracker & Emission Reporter
 * OOP Lab Project | BSAI Section: Orange
 *
 * Authors : Noor Ul Eman (B25F0322AI193)
 *           Hafsa Tahir  (B25F1962AI181)
 * Submitted To: Sir Obaidullah Miakhil
 *
 * Run: javac -d out src/carboncommute/**‌/*.java
 *      java  -cp out carboncommute.Main
 */
public class Main {
    public static void main(String[] args) {
        // Instantiate and run the controller
        CarbonCommuteSystem system = new CarbonCommuteSystem();
        system.run();
    }
}
