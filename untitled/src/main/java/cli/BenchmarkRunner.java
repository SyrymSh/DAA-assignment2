package cli;

import algorithms.KadaneAlgorithm;
import algorithms.KadaneAlgorithm.MaximumSubarrayResult;
import metrics.MetricsExporter;
import metrics.PerformanceTracker;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Comprehensive CLI benchmark runner for Kadane's Algorithm
 * Supports configurable input sizes, distributions, and output formats
 */
public class BenchmarkRunner {
    private static final Random random = new Random(42); // Fixed seed for reproducibility
    private final KadaneAlgorithm kadane;
    private final PerformanceTracker tracker;

    // Benchmark configuration
    private int[] sizes = {100, 1000, 10000, 100000};
    private String[] distributions = {"random", "sorted", "reverse_sorted", "all_positive", "all_negative", "alternating"};
    private int warmupIterations = 3;
    private int benchmarkIterations = 5;
    private boolean exportCSV = true;
    private boolean verbose = false;

    public BenchmarkRunner() {
        this.kadane = new KadaneAlgorithm();
        this.tracker = kadane.getPerformanceTracker();
    }

    public static void main(String[] args) {
        BenchmarkRunner runner = new BenchmarkRunner();

        // Parse command line arguments
        runner.parseArguments(args);

        System.out.println("Kadane's Algorithm Benchmark Runner");
        System.out.println("===================================");
        runner.printConfiguration();

        try {
            runner.runCompleteBenchmark();
            System.out.println("\nBenchmark completed successfully!");
        } catch (Exception e) {
            System.err.println("Benchmark failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Parse command line arguments
     */
    private void parseArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--sizes":
                    if (i + 1 < args.length) {
                        this.sizes = parseSizes(args[++i]);
                    }
                    break;
                case "--distributions":
                    if (i + 1 < args.length) {
                        this.distributions = parseDistributions(args[++i]);
                    }
                    break;
                case "--warmup":
                    if (i + 1 < args.length) {
                        this.warmupIterations = Integer.parseInt(args[++i]);
                    }
                    break;
                case "--iterations":
                    if (i + 1 < args.length) {
                        this.benchmarkIterations = Integer.parseInt(args[++i]);
                    }
                    break;
                case "--export-csv":
                    this.exportCSV = true;
                    break;
                case "--no-export":
                    this.exportCSV = false;
                    break;
                case "--verbose":
                case "-v":
                    this.verbose = true;
                    break;
                case "--help":
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
            }
        }
    }

    /**
     * Run complete benchmark suite
     */
    public void runCompleteBenchmark() throws IOException {
        System.out.println("\nStarting benchmark...");

        // Warmup phase
        if (warmupIterations > 0) {
            System.out.println("\nPhase 1: Warmup (" + warmupIterations + " iterations)");
            runWarmup();
        }

        // Main benchmark phase
        System.out.println("\nPhase 2: Main Benchmark");
        List<BenchmarkResult> results = runBenchmarkSuite();

        // Export results
        if (exportCSV) {
            exportResults(results);
        }

        // Print summary
        printSummary(results);
    }

    /**
     * Warmup JVM to ensure consistent performance measurements
     */
    private void runWarmup() {
        int[] warmupSizes = {1000, 5000, 10000};

        for (int size : warmupSizes) {
            for (int i = 0; i < warmupIterations; i++) {
                int[] array = generateArray(size, "random");
                kadane.findMaximumSubarray(array);

                if (verbose) {
                    System.out.printf("  Warmup: size=%,d, iteration=%d%n", size, i + 1);
                }
            }
        }

        // Clear metrics after warmup
        tracker.getRunHistory().clear();
    }

    /**
     * Run the main benchmark suite
     */
    private List<BenchmarkResult> runBenchmarkSuite() {
        List<BenchmarkResult> results = new ArrayList<>();
        int totalTests = sizes.length * distributions.length * benchmarkIterations;
        int currentTest = 0;

        System.out.printf("Running %,d test combinations...%n", totalTests);

        for (int size : sizes) {
            for (String distribution : distributions) {
                List<Long> times = new ArrayList<>();
                List<Integer> comparisons = new ArrayList<>();
                List<Integer> arrayAccesses = new ArrayList<>();

                for (int iteration = 0; iteration < benchmarkIterations; iteration++) {
                    currentTest++;

                    if (verbose) {
                        System.out.printf("  Progress: %d/%d (Size: %,d, Dist: %s, Iter: %d)%n",
                                currentTest, totalTests, size, distribution, iteration + 1);
                    }

                    // Generate test array
                    int[] array = generateArray(size, distribution);

                    // Run benchmark
                    long startTime = System.nanoTime();
                    MaximumSubarrayResult result = kadane.findMaximumSubarray(array);
                    long endTime = System.nanoTime();

                    long duration = endTime - startTime;
                    times.add(duration);
                    comparisons.add(tracker.getComparisons());
                    arrayAccesses.add(tracker.getArrayAccesses());

                    // Validate result
                    validateResult(array, result, distribution);
                }

                // Calculate statistics for this configuration
                BenchmarkResult result = calculateStatistics(size, distribution, times, comparisons, arrayAccesses);
                results.add(result);

                System.out.printf("  Completed: size=%,8d, dist=%-15s → avg: %8.3f ms%n",
                        size, distribution, result.getAvgTimeMs());
            }
        }

        return results;
    }

    /**
     * Calculate statistics for a benchmark configuration
     */
    private BenchmarkResult calculateStatistics(int size, String distribution,
                                                List<Long> times, List<Integer> comparisons,
                                                List<Integer> arrayAccesses) {
        BenchmarkResult result = new BenchmarkResult(size, distribution);

        // Time statistics
        result.setAvgTimeMs(calculateAverage(times) / 1_000_000.0);
        result.setMinTimeMs(Collections.min(times) / 1_000_000.0);
        result.setMaxTimeMs(Collections.max(times) / 1_000_000.0);
        result.setStdDevTimeMs(calculateStdDev(times) / 1_000_000.0);

        // Operation statistics
        result.setAvgComparisons(calculateAverageInt(comparisons));
        result.setAvgArrayAccesses(calculateAverageInt(arrayAccesses));

        return result;
    }

    /**
     * Validate that the algorithm produced a correct result
     */
    private void validateResult(int[] array, MaximumSubarrayResult result, String distribution) {
        // Basic validation
        if (result.getStartIndex() < 0 || result.getStartIndex() >= array.length) {
            throw new RuntimeException("Invalid start index: " + result.getStartIndex());
        }
        if (result.getEndIndex() < result.getStartIndex() || result.getEndIndex() >= array.length) {
            throw new RuntimeException("Invalid end index: " + result.getEndIndex());
        }

        // Verify subarray sum matches reported sum
        int actualSum = 0;
        for (int i = result.getStartIndex(); i <= result.getEndIndex(); i++) {
            actualSum += array[i];
        }

        if (actualSum != result.getMaxSum()) {
            throw new RuntimeException(String.format(
                    "Sum validation failed: expected %d, got %d", result.getMaxSum(), actualSum));
        }
    }

    /**
     * Export results to CSV files
     */
    private void exportResults(List<BenchmarkResult> results) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());

        // Export detailed results
        String detailsFile = "kadane_benchmark_details_" + timestamp + ".csv";
        exportDetailedResults(results, detailsFile);

        // Export summary results
        String summaryFile = "kadane_benchmark_summary_" + timestamp + ".csv";
        exportSummaryResults(results, summaryFile);

        // Export performance metrics
        if (!tracker.getRunHistory().isEmpty()) {
            String metricsFile = "kadane_performance_metrics_" + timestamp + ".csv";
            tracker.exportToCSV(metricsFile);
        }

        System.out.printf("\nExported results to:%n");
        System.out.printf("  - %s (detailed results)%n", detailsFile);
        System.out.printf("  - %s (summary results)%n", summaryFile);
    }

    private void exportDetailedResults(List<BenchmarkResult> results, String filename) throws IOException {
        try (Formatter writer = new Formatter(filename)) {
            // Header
            writer.format("timestamp,size,distribution,avg_time_ms,min_time_ms,max_time_ms,std_dev_ms,avg_comparisons,avg_array_accesses%n");

            // Data
            for (BenchmarkResult result : results) {
                writer.format("%d,%d,%s,%.6f,%.6f,%.6f,%.6f,%.2f,%.2f%n",
                        System.currentTimeMillis(),
                        result.getSize(),
                        result.getDistribution(),
                        result.getAvgTimeMs(),
                        result.getMinTimeMs(),
                        result.getMaxTimeMs(),
                        result.getStdDevTimeMs(),
                        result.getAvgComparisons(),
                        result.getAvgArrayAccesses()
                );
            }
        }
    }

    private void exportSummaryResults(List<BenchmarkResult> results, String filename) throws IOException {
        // Group by size and calculate averages
        Map<Integer, List<BenchmarkResult>> bySize = new TreeMap<>();
        for (BenchmarkResult result : results) {
            bySize.computeIfAbsent(result.getSize(), k -> new ArrayList<>()).add(result);
        }

        try (Formatter writer = new Formatter(filename)) {
            writer.format("size,avg_time_all_dists_ms,min_time_ms,max_time_ms,time_per_element_ns%n");

            for (Map.Entry<Integer, List<BenchmarkResult>> entry : bySize.entrySet()) {
                int size = entry.getKey();
                List<BenchmarkResult> sizeResults = entry.getValue();

                double avgTime = sizeResults.stream().mapToDouble(BenchmarkResult::getAvgTimeMs).average().orElse(0);
                double minTime = sizeResults.stream().mapToDouble(BenchmarkResult::getMinTimeMs).min().orElse(0);
                double maxTime = sizeResults.stream().mapToDouble(BenchmarkResult::getMaxTimeMs).max().orElse(0);
                double timePerElement = (avgTime * 1_000_000) / size; // nanoseconds per element

                writer.format("%d,%.6f,%.6f,%.6f,%.3f%n",
                        size, avgTime, minTime, maxTime, timePerElement);
            }
        }
    }

    /**
     * Print benchmark summary
     */
    private void printSummary(List<BenchmarkResult> results) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("BENCHMARK SUMMARY");
        System.out.println("=".repeat(80));

        // Group by size
        Map<Integer, List<BenchmarkResult>> bySize = new TreeMap<>();
        for (BenchmarkResult result : results) {
            bySize.computeIfAbsent(result.getSize(), k -> new ArrayList<>()).add(result);
        }

        // Print table header
        System.out.printf("%-12s %-15s %-12s %-12s %-12s %-12s%n",
                "Size", "Distribution", "Avg Time", "Min Time", "Max Time", "Std Dev");
        System.out.printf("%-12s %-15s %-12s %-12s %-12s %-12s%n",
                "(elements)", "", "(ms)", "(ms)", "(ms)", "(ms)");
        System.out.println("-".repeat(80));

        // Print results
        for (Map.Entry<Integer, List<BenchmarkResult>> entry : bySize.entrySet()) {
            int size = entry.getKey();
            List<BenchmarkResult> sizeResults = entry.getValue();

            for (BenchmarkResult result : sizeResults) {
                System.out.printf("%,-12d %-15s %-12.3f %-12.3f %-12.3f %-12.3f%n",
                        result.getSize(),
                        result.getDistribution(),
                        result.getAvgTimeMs(),
                        result.getMinTimeMs(),
                        result.getMaxTimeMs(),
                        result.getStdDevTimeMs()
                );
            }
            System.out.println("-".repeat(80));
        }

        // Print complexity analysis
        printComplexityAnalysis(bySize);
    }

    /**
     * Print complexity analysis
     */
    private void printComplexityAnalysis(Map<Integer, List<BenchmarkResult>> bySize) {
        System.out.println("\nCOMPLEXITY ANALYSIS");
        System.out.println("-".repeat(50));

        List<Integer> sizesList = new ArrayList<>(bySize.keySet());
        Collections.sort(sizesList);

        if (sizesList.size() < 2) {
            System.out.println("Need at least 2 different sizes for complexity analysis");
            return;
        }

        System.out.printf("%-12s %-12s %-12s %-12s%n",
                "Size", "Avg Time", "Time/Element", "O(n) Ratio");
        System.out.println("-".repeat(50));

        double previousTimePerElement = 0;
        for (int i = 0; i < sizesList.size(); i++) {
            int size = sizesList.get(i);
            List<BenchmarkResult> results = bySize.get(size);

            double avgTime = results.stream().mapToDouble(BenchmarkResult::getAvgTimeMs).average().orElse(0);
            double timePerElement = (avgTime * 1_000_000) / size; // nanoseconds per element

            String ratio = "-";
            if (i > 0) {
                double expectedRatio = (double) size / sizesList.get(i-1);
                double actualRatio = timePerElement / previousTimePerElement;
                ratio = String.format("%.2f (exp: %.2f)", actualRatio, expectedRatio);
            }

            System.out.printf("%,-12d %-12.3f %-12.3f %-12s%n",
                    size, avgTime, timePerElement, ratio);

            previousTimePerElement = timePerElement;
        }
    }

    /**
     * Print configuration
     */
    private void printConfiguration() {
        System.out.println("Configuration:");
        System.out.printf("  Sizes: %s%n", Arrays.toString(sizes));
        System.out.printf("  Distributions: %s%n", Arrays.toString(distributions));
        System.out.printf("  Warmup iterations: %d%n", warmupIterations);
        System.out.printf("  Benchmark iterations: %d%n", benchmarkIterations);
        System.out.printf("  CSV export: %s%n", exportCSV);
        System.out.printf("  Verbose: %s%n", verbose);
    }

    /**
     * Print help message
     */
    private static void printHelp() {
        System.out.println("Kadane's Algorithm Benchmark Runner");
        System.out.println();
        System.out.println("Usage: java -cp target/classes cli.BenchmarkRunner [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --sizes SIZE1,SIZE2,...     Input sizes to test (default: 100,1000,10000,100000)");
        System.out.println("  --distributions DIST1,...   Input distributions (default: random,sorted,reverse_sorted,all_positive,all_negative,alternating)");
        System.out.println("  --warmup ITERATIONS         Warmup iterations (default: 3)");
        System.out.println("  --iterations ITERATIONS     Benchmark iterations per configuration (default: 5)");
        System.out.println("  --export-csv                Export results to CSV (default: true)");
        System.out.println("  --no-export                 Disable CSV export");
        System.out.println("  --verbose, -v               Verbose output");
        System.out.println("  --help, -h                  Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -cp target/classes cli.BenchmarkRunner --sizes 1000,5000 --distributions random,sorted");
        System.out.println("  java -cp target/classes cli.BenchmarkRunner --iterations 10 --verbose --no-export");
    }

    // Utility methods
    private int[] parseSizes(String sizesStr) {
        return Arrays.stream(sizesStr.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private String[] parseDistributions(String distStr) {
        return distStr.split(",");
    }

    private double calculateAverage(List<Long> values) {
        return values.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    private double calculateAverageInt(List<Integer> values) {
        return values.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    private double calculateStdDev(List<Long> values) {
        double mean = calculateAverage(values);
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average().orElse(0);
        return Math.sqrt(variance);
    }

    /**
     * Generate test array based on distribution type
     */
    public static int[] generateArray(int size, String distribution) {
        return switch (distribution.toLowerCase()) {
            case "random" -> generateRandomArray(size, -100, 100);
            case "sorted" -> generateSortedArray(size);
            case "reverse_sorted" -> generateReverseSortedArray(size);
            case "all_positive" -> generateRandomArray(size, 1, 100);
            case "all_negative" -> generateRandomArray(size, -100, -1);
            case "alternating" -> generateAlternatingArray(size);
            case "sparse_positive" -> generateSparsePositiveArray(size);
            default -> generateRandomArray(size, -100, 100);
        };
    }

    private static int[] generateRandomArray(int size, int min, int max) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(max - min + 1) + min;
        }
        return array;
    }

    private static int[] generateSortedArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = i + 1; // 1, 2, 3, ...
        }
        return array;
    }

    private static int[] generateReverseSortedArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = size - i; // n, n-1, n-2, ...
        }
        return array;
    }

    private static int[] generateAlternatingArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = (i % 2 == 0) ? 1 : -1;
        }
        return array;
    }

    private static int[] generateSparsePositiveArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            if (random.nextDouble() < 0.1) { // 10% positive elements
                array[i] = random.nextInt(100) + 1;
            } else {
                array[i] = random.nextInt(10) - 15; // Mostly negative
            }
        }
        return array;
    }

    /**
     * Benchmark result container class
     */
    public static class BenchmarkResult {
        private final int size;
        private final String distribution;
        private double avgTimeMs;
        private double minTimeMs;
        private double maxTimeMs;
        private double stdDevTimeMs;
        private double avgComparisons;
        private double avgArrayAccesses;

        public BenchmarkResult(int size, String distribution) {
            this.size = size;
            this.distribution = distribution;
        }

        // Getters and setters
        public int getSize() { return size; }
        public String getDistribution() { return distribution; }
        public double getAvgTimeMs() { return avgTimeMs; }
        public void setAvgTimeMs(double avgTimeMs) { this.avgTimeMs = avgTimeMs; }
        public double getMinTimeMs() { return minTimeMs; }
        public void setMinTimeMs(double minTimeMs) { this.minTimeMs = minTimeMs; }
        public double getMaxTimeMs() { return maxTimeMs; }
        public void setMaxTimeMs(double maxTimeMs) { this.maxTimeMs = maxTimeMs; }
        public double getStdDevTimeMs() { return stdDevTimeMs; }
        public void setStdDevTimeMs(double stdDevTimeMs) { this.stdDevTimeMs = stdDevTimeMs; }
        public double getAvgComparisons() { return avgComparisons; }
        public void setAvgComparisons(double avgComparisons) { this.avgComparisons = avgComparisons; }
        public double getAvgArrayAccesses() { return avgArrayAccesses; }
        public void setAvgArrayAccesses(double avgArrayAccesses) { this.avgArrayAccesses = avgArrayAccesses; }
    }
}