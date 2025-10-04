package metrics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Utility class for exporting performance data to various formats
 */
public class MetricsExporter {

    /**
     * Exports performance data from multiple trackers to a combined CSV
     */
    public static void exportCombinedCSV(List<PerformanceTracker> trackers, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // Write header
            writer.write("algorithm,timestamp,inputSize,inputType,comparisons,arrayAccesses,memoryAllocations,timeNanos,timeMillis\n");

            // Write all data from all trackers
            for (PerformanceTracker tracker : trackers) {
                for (Map<String, Object> run : tracker.getRunHistory()) {
                    writer.write(String.format("%s,%d,%d,%s,%d,%d,%d,%d,%d\n",
                            run.get("algorithm"),
                            run.get("timestamp"),
                            run.get("inputSize"),
                            run.get("inputType"),
                            run.get("comparisons"),
                            run.get("arrayAccesses"),
                            run.get("memoryAllocations"),
                            run.get("timeNanos"),
                            run.get("timeMillis")
                    ));
                }
            }
        }
    }

    /**
     * Exports complexity analysis data for theoretical vs empirical comparison
     */
    public static void exportComplexityAnalysisCSV(
            List<PerformanceTracker> trackers,
            String filename) throws IOException {

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("algorithm,inputSize,inputType,actualTimeMillis,theoreticalTime,actualComparisons,theoreticalComparisons\n");

            for (PerformanceTracker tracker : trackers) {
                for (Map<String, Object> run : tracker.getRunHistory()) {
                    int inputSize = (Integer) run.get("inputSize");
                    long actualTime = (Long) run.get("timeMillis");
                    int actualComparisons = (Integer) run.get("comparisons");

                    // Theoretical values (O(n) complexity)
                    double theoreticalTime = inputSize * 0.001; // Scaling factor
                    double theoreticalComparisons = inputSize * 3.0; // ~3 comparisons per element

                    writer.write(String.format("%s,%d,%s,%d,%.2f,%d,%.2f\n",
                            run.get("algorithm"),
                            inputSize,
                            run.get("inputType"),
                            actualTime,
                            theoreticalTime,
                            actualComparisons,
                            theoreticalComparisons
                    ));
                }
            }
        }
    }
}