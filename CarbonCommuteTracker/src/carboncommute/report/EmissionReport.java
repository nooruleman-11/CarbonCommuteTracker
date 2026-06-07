package carboncommute.report;

import carboncommute.model.CommuteRecord;
import carboncommute.model.User;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*; 

/**
 * EmissionReport - Report Class
 *
 * Demonstrates: Encapsulation, Composition (uses User and CommuteRecord)
 *
 * Generates weekly/monthly emission summaries, eco scores,
 * transport mode breakdowns, and greener travel suggestions.
 */
public class EmissionReport {

    // Eco score thresholds (kg CO2 per week)
    private static final double EXCELLENT_THRESHOLD = 2.0;
    private static final double GOOD_THRESHOLD      = 5.0;
    private static final double FAIR_THRESHOLD      = 10.0;

    // Trees needed to absorb 1 kg CO2 per year (~21 kg/tree/year)
    private static final double KG_CO2_PER_TREE_PER_YEAR = 21.0;

    private User user;

    public EmissionReport(User user) {
        this.user = user;
    }

    // ── Weekly Report 

    /**
     * Generates a weekly emission report for the given week start date (Monday).
     */
    public void generateWeeklyReport(LocalDate weekStart) {
        List<CommuteRecord> records = user.getRecordsForWeek(weekStart);
        LocalDate weekEnd = weekStart.plusDays(6);

        printSectionHeader("WEEKLY EMISSION REPORT");
        System.out.printf("  User    : %s%n", user.getName());
        System.out.printf("  Period  : %s  to  %s%n",weekStart.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                          weekEnd.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        System.out.println();

        if (records.isEmpty()) {
            System.out.println("  No commute records found for this week.");
            printSectionFooter();
            return;
        }
        double totalEmission = sumEmission(records);
        double totalDistance = sumDistance(records);
        printRecordsTable(records);
        System.out.println();
        System.out.printf("  Trips this week   : %d%n", records.size());
        System.out.printf("  Distance covered  : %.1f km%n", totalDistance);
        System.out.printf("  Total CO2 emitted : %.4f kg%n", totalEmission);
        System.out.printf("  Est. monthly total: %.4f kg  (weekly × 4)%n", totalEmission * 4);
        System.out.println();
        printEcoScore(totalEmission, "week");
        printModeBreakdown(records, totalEmission);
        printGreenSuggestions(records);
        printTreeEquivalent(totalEmission);
        printSectionFooter();
    }

    // ── Monthly Report

    /**
     * Generates a monthly emission report for the given year and month.
     */
    public void generateMonthlyReport(int year, int month) {
        List<CommuteRecord> records = user.getRecordsForMonth(year, month);

        String monthName = LocalDate.of(year, month, 1)
                .format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        printSectionHeader("MONTHLY EMISSION REPORT");
        System.out.printf("  User   : %s%n", user.getName());
        System.out.printf("  Period : %s%n", monthName);
        System.out.println();

        if (records.isEmpty()) {
            System.out.println("  No commute records found for this month.");
            printSectionFooter();
            return;
        }

        double totalEmission = sumEmission(records);
        double totalDistance = sumDistance(records);

        printRecordsTable(records);
        System.out.println();
        System.out.printf("   Trips this month  : %d%n", records.size());
        System.out.printf("   Distance covered  : %.1f km%n", totalDistance);
        System.out.printf("   Total CO2 emitted : %.4f kg%n", totalEmission);
        System.out.printf("   Avg per trip      : %.4f kg CO2%n",
                records.isEmpty() ? 0 : totalEmission / records.size());
        System.out.println();
        printEcoScore(totalEmission / 4.0, "week (monthly avg)");
        printModeBreakdown(records, totalEmission);
        printGreenSuggestions(records);
        printTreeEquivalent(totalEmission);
        printSectionFooter();
    }

    // ── Full Summary Report 
    /**
     * Generates a complete all-time summary report for the user.
     */
    public void generateSummaryReport() {
        List<CommuteRecord> records = user.getCommuteHistory();

        printSectionHeader("FULL EMISSION SUMMARY");
        System.out.printf("  User : %s  (%s)%n", user.getName(), user.getEmail());
        System.out.println();

        if (records.isEmpty()) {
            System.out.println("  No commute records found.");
            printSectionFooter();
            return;
        }

        double totalEmission = sumEmission(records);
        double totalDistance = sumDistance(records);

        System.out.printf("  ► Total trips       : %d%n", records.size());
        System.out.printf("  ► Total distance    : %.1f km%n", totalDistance);
        System.out.printf("  ► Total CO2 emitted : %.4f kg%n", totalEmission);
        System.out.printf("  ► Avg per trip      : %.4f kg CO2%n",
                records.isEmpty() ? 0 : totalEmission / records.size());
        System.out.println();
        printModeBreakdown(records, totalEmission);
        printGreenSuggestions(records);
        printTreeEquivalent(totalEmission);
        printSectionFooter();
    }

     // ── Eco Score

 // /**
 //* Prints an eco-score badge based on weekly CO2 output.
 //*     */
     public void showEcoScore() {
         List<CommuteRecord> records = user.getCommuteHistory();
         if (records.isEmpty()) {
             System.out.println("  No data yet. Start logging commutes to see your eco score!");
             return;
         }
         // Use average weekly emission (total / number of weeks in data)
         double totalEmission = sumEmission(records);
         long weeks = Math.max(1, getWeekSpan(records));
         double avgWeekly = totalEmission / weeks;

         printSectionHeader("ECO SCORE");
         System.out.printf("  User          : %s%n", user.getName());
         System.out.printf("  Avg weekly CO2: %.4f kg%n", avgWeekly);
         System.out.println();
         printEcoScore(avgWeekly, "week (average)");
         printSectionFooter();
     }

    // // ── Helpers 

     private double sumEmission(List<CommuteRecord> records) {
         return records.stream().mapToDouble(CommuteRecord::getEmissionKg).sum();
     }

     private double sumDistance(List<CommuteRecord> records) {
         return records.stream().mapToDouble(CommuteRecord::getDistanceKm).sum();
     }

     private void printEcoScore(double weeklyKg, String period) {
         String grade, bar, advice;
         if (weeklyKg == 0) {
             grade = "PLATINUM ✦"; bar = "██████████"; advice = "Zero emissions! Perfect score.";
         } else if (weeklyKg < EXCELLENT_THRESHOLD) {
             grade = "EXCELLENT"; bar = "████████░░"; advice = "Outstanding! Keep up the green habits.";
         } else if (weeklyKg < GOOD_THRESHOLD) {
             grade = "GOOD";      bar = "██████░░░░"; advice = "Good effort. Small changes can push you to Excellent.";
         } else if (weeklyKg < FAIR_THRESHOLD) {
             grade = "FAIR";      bar = "████░░░░░░"; advice = "Room to improve. Try public transport more often.";
         } else {
             grade = "NEEDS WORK"; bar = "██░░░░░░░░"; advice = "High emissions. Consider switching transport modes.";
         }
         System.out.printf("  Eco Score (%s):%n", period);
         System.out.printf("  %s  [%s]  %s%n%n", bar, grade, advice);
     }

     private void printModeBreakdown(List<CommuteRecord> records, double total) {
         // Group emissions by transport mode name
         Map<String, Double> byMode = new LinkedHashMap<>();
         Map<String, Integer> countByMode = new LinkedHashMap<>();
       for (CommuteRecord r : records) {
             String key = r.getTransportMode().getModeName();
             byMode.merge(key, r.getEmissionKg(), Double::sum);
             countByMode.merge(key, 1, Integer::sum);
         }

         System.out.println("  Transport Mode Breakdown:");
         System.out.println("  ─────────────────────────────────────────────────");
         for (Map.Entry<String, Double> entry : byMode.entrySet()) {
             double pct = total > 0 ? (entry.getValue() / total * 100) : 0;
             int bars = (int)(pct / 5);
         String bar = "█".repeat(bars) + "░".repeat(20 - bars);
             System.out.printf("  %-15s | %s | %5.1f%% | %d trips | %.4f kg%n",
                     entry.getKey(), bar, pct,
                     countByMode.get(entry.getKey()), entry.getValue());
        }
         System.out.println("  ─────────────────────────────────────────────────");
        System.out.println();
     }

     private void printGreenSuggestions(List<CommuteRecord> records) {
         // Find the highest-emission mode used and suggest its alternative
         CommuteRecord worst = records.stream()
                 .max(Comparator.comparingDouble(CommuteRecord::getEmissionKg))
                 .orElse(null);
         if (worst != null && worst.getEmissionKg() > 0) {
             System.out.println("  Green Suggestion:");
             System.out.println("  → " + worst.getTransportMode().getGreenAlternative());
             System.out.println();
         }
     }

     private void printTreeEquivalent(double totalKg) {
         double treesPerYear = totalKg / KG_CO2_PER_TREE_PER_YEAR;
         System.out.printf("  Tree Equivalent : You would need %.2f trees to absorb this CO2 in one year.%n%n",
                 treesPerYear);
     }

     private void printRecordsTable(List<CommuteRecord> records) {
         System.out.println("   No. │ Date         │ Transport       │ Distance   │ Emission        ");
         int i = 1;
         for (CommuteRecord r : records) {
             System.out.printf("  %-3d │ %-12s │ %-15s │ %6.1f km  │ %8.4f kg CO2%n",
                     i++,
                     r.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                     r.getTransportMode().getModeName(),
                     r.getDistanceKm(),
                     r.getEmissionKg());     }
     }
    private long getWeekSpan(List<CommuteRecord> records) {
         if (records.isEmpty()) return 1;
         LocalDate min = records.stream().map(CommuteRecord::getDate).min(Comparator.naturalOrder()).get();
         LocalDate max = records.stream().map(CommuteRecord::getDate).max(Comparator.naturalOrder()).get();
         return Math.max(1, (max.toEpochDay() - min.toEpochDay()) / 7 + 1);
     }

     private void printSectionHeader(String title) {
         System.out.println();
         System.out.printf( "  ║  %-48s║%n", title);
     }

     private void printSectionFooter() {
         System.out.println("  ════════════════════════════════════════════════════");
      System.out.println();
     }
}
