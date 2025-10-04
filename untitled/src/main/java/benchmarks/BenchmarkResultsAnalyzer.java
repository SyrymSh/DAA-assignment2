package benchmarks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Analyzes JMH benchmark results and generates reports
 */
public class BenchmarkResultsAnalyzer {
    private final ObjectMapper mapper = new ObjectMapper();
    private final DecimalFormat df = new DecimalFormat("#.###");

    public void analyzeResults(String resultsFile) throws IOException {
        JsonNode root = mapper.readTree(new File(resultsFile));

        System.out.println("JMH Benchmark Results Analysis");
        System.out.println("==============================");

        List<BenchmarkResult> results = new ArrayList<>();

        for (JsonNode benchmark : root) {
            String benchmarkName = benchmark.get("benchmark").asText();
            double score = benchmark.get("primaryMetric").get("score").asDouble();
            String unit = benchmark.get("primaryMetric").get("scoreUnit").asText();
            double error = benchmark.get("primaryMetric").get("scoreError").asDouble();

            results.add(new BenchmarkResult(benchmarkName, score, error, unit));
        }

        // Sort by benchmark name
        results.sort(Comparator.comparing(BenchmarkResult::getBenchmarkName));

        // Print results
        printResultsTable(results);
        printComplexityAnalysis(results);
        printPerformanceSummary(results);
    }

    private void printResultsTable(List<BenchmarkResult> results) {
        System.out.println("\nBenchmark Results:");
        System.out.println("==================");
        System.out.printf("%-40s %-12s %-10s%n", "Benchmark", "Score", "Error");
        System.out.println("-".repeat(70));

        for (BenchmarkResult result : results) {
            System.out.printf("%-40s %-12s %-10s%n",
                    shortenBenchmarkName(result.benchmarkName),
                    df.format(result.score) + " " + result.unit,
                    "±" + df.format(result.error));
        }
    }

    private void printComplexityAnalysis(List<BenchmarkResult> results) {
        System.out.println("\nComplexity Analysis:");
        System.out.println("====================");

        // Extract scalability benchmarks
        Map<String, Double> scalabilityResults = new TreeMap<>();
        for (BenchmarkResult result : results) {
            if (result.benchmarkName.contains("ScalabilityBenchmark")) {
                String size = extractSizeFromBenchmark(result.benchmarkName);
                if (size != null) {
                    scalabilityResults.put(size, result.score);
                }
            }
        }

        if (scalabilityResults.size() >= 2) {
            System.out.printf("%-12s %-15s %-15s %-15s%n",
                    "Size", "Time (µs)", "Time/Element", "O(n) Ratio");
            System.out.println("-".repeat(60));

            Double previousTimePerElement = null;
            List<String> sizes = new ArrayList<>(scalabilityResults.keySet());
            Collections.sort(sizes, Comparator.comparingInt(this::parseSize));

            for (String size : sizes) {
                double time = scalabilityResults.get(size);
                int n = parseSize(size);
                double timePerElement = time / n;

                String ratio = "-";
                if (previousTimePerElement != null) {
                    double actualRatio = timePerElement / previousTimePerElement;
                    double expectedRatio = 1.0; // For O(n), ratio should be ~1
                    ratio = String.format("%.3f (exp: %.3f)", actualRatio, expectedRatio);
                }

                System.out.printf("%-12s %-15.3f %-15.6f %-15s%n",
                        size, time, timePerElement, ratio);

                previousTimePerElement = timePerElement;
            }
        }
    }

    private void printPerformanceSummary(List<BenchmarkResult> results) {
        System.out.println("\nPerformance Summary:");
        System.out.println("====================");

        double minTime = Double.MAX_VALUE;
        double maxTime = Double.MIN_VALUE;
        String fastestBenchmark = "";
        String slowestBenchmark = "";

        for (BenchmarkResult result : results) {
            if (result.score < minTime) {
                minTime = result.score;
                fastestBenchmark = result.benchmarkName;
            }
            if (result.score > maxTime) {
                maxTime = result.score;
                slowestBenchmark = result.benchmarkName;
            }
        }

        System.out.printf("Fastest benchmark: %s (%.3f µs)%n",
                shortenBenchmarkName(fastestBenchmark), minTime);
        System.out.printf("Slowest benchmark: %s (%.3f µs)%n",
                shortenBenchmarkName(slowestBenchmark), maxTime);
        System.out.printf("Performance ratio: %.2fx%n", maxTime / minTime);
    }

    private String shortenBenchmarkName(String fullName) {
        return fullName.substring(fullName.lastIndexOf('.') + 1);
    }

    private String extractSizeFromBenchmark(String benchmarkName) {
        if (benchmarkName.contains("Size100")) return "100";
        if (benchmarkName.contains("Size1000")) return "1,000";
        if (benchmarkName.contains("Size10000")) return "10,000";
        if (benchmarkName.contains("Size50000")) return "50,000";
        if (benchmarkName.contains("Size100000")) return "100,000";
        return null;
    }

    private int parseSize(String size) {
        return Integer.parseInt(size.replace(",", ""));
    }

    private static class BenchmarkResult {
        String benchmarkName;
        double score;
        double error;
        String unit;

        BenchmarkResult(String benchmarkName, double score, double error, String unit) {
            this.benchmarkName = benchmarkName;
            this.score = score;
            this.error = error;
            this.unit = unit;
        }

        String getBenchmarkName() {
            return benchmarkName;
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Usage: java BenchmarkResultsAnalyzer <jmh-results.json>");
            return;
        }

        BenchmarkResultsAnalyzer analyzer = new BenchmarkResultsAnalyzer();
        analyzer.analyzeResults(args[0]);
    }
}