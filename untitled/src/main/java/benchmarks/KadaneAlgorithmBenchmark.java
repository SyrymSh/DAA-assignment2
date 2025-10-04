package benchmarks;

import algorithms.KadaneAlgorithm;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * JMH Microbenchmarks for Kadane's Algorithm
 * Provides accurate performance measurements for empirical analysis
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@State(Scope.Thread)
public class KadaneAlgorithmBenchmark {

    @State(Scope.Thread)
    public static class AlgorithmState {
        KadaneAlgorithm kadane;
        int[] smallRandomArray;
        int[] mediumRandomArray;
        int[] largeRandomArray;
        int[] allPositiveArray;
        int[] allNegativeArray;
        int[] sortedArray;
        int[] worstCaseArray;

        Random random = new Random(42); // Fixed seed for reproducibility

        @Setup(Level.Trial)
        public void setUp() {
            kadane = new KadaneAlgorithm();

            // Generate test arrays
            smallRandomArray = generateRandomArray(100, -100, 100);
            mediumRandomArray = generateRandomArray(10_000, -1000, 1000);
            largeRandomArray = generateRandomArray(100_000, -10000, 10000);
            allPositiveArray = generateRandomArray(10_000, 1, 100);
            allNegativeArray = generateRandomArray(10_000, -100, -1);
            sortedArray = generateSortedArray(10_000);
            worstCaseArray = generateWorstCaseArray(10_000);
        }

        private int[] generateRandomArray(int size, int min, int max) {
            int[] array = new int[size];
            for (int i = 0; i < size; i++) {
                array[i] = random.nextInt(max - min + 1) + min;
            }
            return array;
        }

        private int[] generateSortedArray(int size) {
            int[] array = new int[size];
            for (int i = 0; i < size; i++) {
                array[i] = i + 1;
            }
            return array;
        }

        private int[] generateWorstCaseArray(int size) {
            int[] array = new int[size];
            for (int i = 0; i < size; i++) {
                array[i] = (i % 2 == 0) ? 1 : -1; // Alternating pattern
            }
            return array;
        }
    }

    // Benchmark methods for different input sizes

    @Benchmark
    public void benchmarkSmallRandomArray(AlgorithmState state, Blackhole blackhole) {
        var result = state.kadane.findMaximumSubarray(state.smallRandomArray);
        blackhole.consume(result);
    }

    @Benchmark
    public void benchmarkMediumRandomArray(AlgorithmState state, Blackhole blackhole) {
        var result = state.kadane.findMaximumSubarray(state.mediumRandomArray);
        blackhole.consume(result);
    }

    @Benchmark
    public void benchmarkLargeRandomArray(AlgorithmState state, Blackhole blackhole) {
        var result = state.kadane.findMaximumSubarray(state.largeRandomArray);
        blackhole.consume(result);
    }

    @Benchmark
    public void benchmarkAllPositiveArray(AlgorithmState state, Blackhole blackhole) {
        var result = state.kadane.findMaximumSubarray(state.allPositiveArray);
        blackhole.consume(result);
    }

    @Benchmark
    public void benchmarkAllNegativeArray(AlgorithmState state, Blackhole blackhole) {
        var result = state.kadane.findMaximumSubarray(state.allNegativeArray);
        blackhole.consume(result);
    }

    @Benchmark
    public void benchmarkSortedArray(AlgorithmState state, Blackhole blackhole) {
        var result = state.kadane.findMaximumSubarray(state.sortedArray);
        blackhole.consume(result);
    }

    @Benchmark
    public void benchmarkWorstCaseArray(AlgorithmState state, Blackhole blackhole) {
        var result = state.kadane.findMaximumSubarray(state.worstCaseArray);
        blackhole.consume(result);
    }

    @Benchmark
    public void benchmarkOptimizedVersion(AlgorithmState state, Blackhole blackhole) {
        var result = state.kadane.findMaximumSubarrayOptimized(state.mediumRandomArray);
        blackhole.consume(result);
    }

    // Main method to run benchmarks
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(KadaneAlgorithmBenchmark.class.getSimpleName())
                .result("jmh_results.json")
                .resultFormat(ResultFormatType.JSON)
                .build();

        new Runner(opt).run();
    }
}