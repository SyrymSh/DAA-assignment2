package metrics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced performance tracker with CSV export capabilities
 * Tracks algorithm metrics across multiple runs for empirical analysis
 */
public class PerformanceTracker {
    private final String algorithmName;
    private int comparisons;
    private int arrayAccesses;
    private int memoryAllocations;
    private long startTime;
    private long endTime;
    private int inputSize;
    private String inputType;

    private final List<Map<String, Object>> runHistory;
    private final Map<String, Long> operationTimings;

    public PerformanceTracker(String algorithmName) {
        this.algorithmName = algorithmName;
        this.runHistory = new ArrayList<>();
        this.operationTimings = new HashMap<>();
        reset();
    }

    public void reset() {
        comparisons = 0;
        arrayAccesses = 0;
        memoryAllocations = 0;
        startTime = 0;
        endTime = 0;
        inputSize = 0;
        inputType = "unknown";
    }

    // Metric increment methods
    public void incrementComparison() { comparisons++; }
    public void incrementComparisons(int count) { comparisons += count; }
    public void incrementArrayAccess() { arrayAccesses++; }
    public void incrementArrayAccesses(int count) { arrayAccesses += count; }
    public void incrementMemoryAllocation() { memoryAllocations++; }
    public void incrementMemoryAllocations(int count) { memoryAllocations += count; }

    // Timing methods
    public void startTimer() {
        startTime = System.nanoTime();
    }

    public void stopTimer() {
        endTime = System.nanoTime();
    }

    public long getElapsedTimeNanos() {
        return endTime - startTime;
    }

    public long getElapsedTimeMillis() {
        return (endTime - startTime) / 1_000_000;
    }

    // Configuration methods
    public void setInputSize(int size) {
        this.inputSize = size;
    }

    public void setInputType(String type) {
        this.inputType = type;
    }

    /**
     * Records a complete run with all metrics
     */
    public void recordRun() {
        Map<String, Object> runData = new HashMap<>();
        runData.put("algorithm", algorithmName);
        runData.put("timestamp", System.currentTimeMillis());
        runData.put("inputSize", inputSize);
        runData.put("inputType", inputType);
        runData.put("comparisons", comparisons);
        runData.put("arrayAccesses", arrayAccesses);
        runData.put("memoryAllocations", memoryAllocations);
        runData.put("timeNanos", getElapsedTimeNanos());
        runData.put("timeMillis", getElapsedTimeMillis());

        runHistory.add(runData);

        // Reset for next run
        reset();
    }

    /**
     * Records a specific operation timing
     */
    public void recordOperation(String operationName, int inputSize) {
        operationTimings.put(operationName + "_n" + inputSize, System.nanoTime());
    }

    /**
     * Exports all run history to CSV file
     */
    public void exportToCSV(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // Write CSV header
            writer.write("algorithm,timestamp,inputSize,inputType,comparisons,arrayAccesses,memoryAllocations,timeNanos,timeMillis\n");

            // Write data rows
            for (Map<String, Object> run : runHistory) {
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

    /**
     * Exports summary statistics to CSV
     */
    public void exportSummaryToCSV(String filename) throws IOException {
        if (runHistory.isEmpty()) return;

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("algorithm,inputSize,inputType,avgComparisons,avgArrayAccesses,avgMemoryAllocations,avgTimeMillis,minTimeMillis,maxTimeMillis\n");

            // Group by input size and type
            Map<String, List<Map<String, Object>>> groupedData = new HashMap<>();
            for (Map<String, Object> run : runHistory) {
                String key = run.get("inputSize") + "_" + run.get("inputType");
                groupedData.computeIfAbsent(key, k -> new ArrayList<>()).add(run);
            }

            // Calculate statistics for each group
            for (List<Map<String, Object>> group : groupedData.values()) {
                if (group.isEmpty()) continue;

                long totalComparisons = 0;
                long totalArrayAccesses = 0;
                long totalMemoryAllocations = 0;
                long totalTimeMillis = 0;
                long minTimeMillis = Long.MAX_VALUE;
                long maxTimeMillis = Long.MIN_VALUE;

                Map<String, Object> firstRun = group.get(0);

                for (Map<String, Object> run : group) {
                    totalComparisons += (Integer) run.get("comparisons");
                    totalArrayAccesses += (Integer) run.get("arrayAccesses");
                    totalMemoryAllocations += (Integer) run.get("memoryAllocations");

                    long timeMillis = (Long) run.get("timeMillis");
                    totalTimeMillis += timeMillis;
                    minTimeMillis = Math.min(minTimeMillis, timeMillis);
                    maxTimeMillis = Math.max(maxTimeMillis, timeMillis);
                }

                int count = group.size();
                writer.write(String.format("%s,%d,%s,%.2f,%.2f,%.2f,%.2f,%d,%d\n",
                        firstRun.get("algorithm"),
                        firstRun.get("inputSize"),
                        firstRun.get("inputType"),
                        (double) totalComparisons / count,
                        (double) totalArrayAccesses / count,
                        (double) totalMemoryAllocations / count,
                        (double) totalTimeMillis / count,
                        minTimeMillis,
                        maxTimeMillis
                ));
            }
        }
    }

    // Getters
    public int getComparisons() { return comparisons; }
    public int getArrayAccesses() { return arrayAccesses; }
    public int getMemoryAllocations() { return memoryAllocations; }
    public List<Map<String, Object>> getRunHistory() { return new ArrayList<>(runHistory); }
    public Map<String, Long> getOperationTimings() { return new HashMap<>(operationTimings); }
    public int getRunCount() { return runHistory.size(); }

    /**
     * Gets summary statistics for the current run history
     */
    public Map<String, Object> getSummaryStatistics() {
        Map<String, Object> stats = new HashMap<>();

        if (runHistory.isEmpty()) {
            return stats;
        }

        long totalComparisons = 0;
        long totalArrayAccesses = 0;
        long totalMemoryAllocations = 0;
        long totalTimeMillis = 0;
        long minTimeMillis = Long.MAX_VALUE;
        long maxTimeMillis = Long.MIN_VALUE;

        for (Map<String, Object> run : runHistory) {
            totalComparisons += (Integer) run.get("comparisons");
            totalArrayAccesses += (Integer) run.get("arrayAccesses");
            totalMemoryAllocations += (Integer) run.get("memoryAllocations");

            long timeMillis = (Long) run.get("timeMillis");
            totalTimeMillis += timeMillis;
            minTimeMillis = Math.min(minTimeMillis, timeMillis);
            maxTimeMillis = Math.max(maxTimeMillis, timeMillis);
        }

        int count = runHistory.size();
        stats.put("runCount", count);
        stats.put("avgComparisons", (double) totalComparisons / count);
        stats.put("avgArrayAccesses", (double) totalArrayAccesses / count);
        stats.put("avgMemoryAllocations", (double) totalMemoryAllocations / count);
        stats.put("avgTimeMillis", (double) totalTimeMillis / count);
        stats.put("minTimeMillis", minTimeMillis);
        stats.put("maxTimeMillis", maxTimeMillis);

        return stats;
    }

    @Override
    public String toString() {
        return String.format(
                "%s Metrics - Comparisons: %d, Array Accesses: %d, Memory Allocations: %d, Time: %d ms",
                algorithmName, comparisons, arrayAccesses, memoryAllocations, getElapsedTimeMillis()
        );
    }
}