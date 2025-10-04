package benchmarks;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for running JMH benchmarks with different configurations
 */
public class JMHRunner {

    public static void runQuickBenchmark() throws RunnerException {
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());

        Options opt = new OptionsBuilder()
                .include(KadaneAlgorithmBenchmark.class.getSimpleName())
                .warmupIterations(2)
                .warmupTime(TimeValue.seconds(1))
                .measurementIterations(3)
                .measurementTime(TimeValue.seconds(2))
                .forks(1)
                .verbosity(VerboseMode.NORMAL)
                .result("results/quick_benchmark_" + timestamp + ".json")
                .resultFormat(org.openjdk.jmh.results.format.ResultFormatType.JSON)
                .build();

        new Runner(opt).run();
    }

    public static void runComprehensiveBenchmark() throws RunnerException {
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());

        Options opt = new OptionsBuilder()
                .include(".*Benchmark.*")
                .warmupIterations(3)
                .warmupTime(TimeValue.seconds(2))
                .measurementIterations(5)
                .measurementTime(TimeValue.seconds(3))
                .forks(2)
                .threads(1)
                .verbosity(VerboseMode.NORMAL)
                .result("results/comprehensive_benchmark_" + timestamp + ".json")
                .resultFormat(org.openjdk.jmh.results.format.ResultFormatType.JSON)
                .build();

        new Runner(opt).run();
    }

    public static void runScalabilityBenchmark() throws RunnerException {
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());

        Options opt = new OptionsBuilder()
                .include(ScalabilityBenchmark.class.getSimpleName())
                .warmupIterations(2)
                .warmupTime(TimeValue.seconds(1))
                .measurementIterations(3)
                .measurementTime(TimeValue.seconds(2))
                .forks(1)
                .result("results/scalability_benchmark_" + timestamp + ".json")
                .resultFormat(org.openjdk.jmh.results.format.ResultFormatType.JSON)
                .build();

        new Runner(opt).run();
    }

    public static void main(String[] args) throws RunnerException {
        System.out.println("JMH Benchmark Runner for Kadane's Algorithm");
        System.out.println("===========================================");

        if (args.length > 0 && "quick".equals(args[0])) {
            System.out.println("Running quick benchmark...");
            runQuickBenchmark();
        } else if (args.length > 0 && "scalability".equals(args[0])) {
            System.out.println("Running scalability benchmark...");
            runScalabilityBenchmark();
        } else {
            System.out.println("Running comprehensive benchmark...");
            runComprehensiveBenchmark();
        }

        System.out.println("Benchmark completed. Results saved to results/ directory.");
    }
}