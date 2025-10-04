package benchmarks;

import algorithms.KadaneAlgorithm;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Scalability benchmarks to verify O(n) time complexity
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)
public class ScalabilityBenchmark {

    @State(Scope.Thread)
    public static class ScalabilityState {
        KadaneAlgorithm kadane;
        Random random = new Random(42);

        int[] size100;
        int[] size1000;
        int[] size10000;
        int[] size50000;
        int[] size100000;

        @Setup(Level.Trial)
        public void setUp() {
            kadane = new KadaneAlgorithm();

            size100 = generateRandomArray(100, -100, 100);
            size1000 = generateRandomArray(1000, -100, 100);
            size10000 = generateRandomArray(10000, -100, 100);
            size50000 = generateRandomArray(50000, -100, 100);
            size100000 = generateRandomArray(100000, -100, 100);
        }

        private int[] generateRandomArray(int size, int min, int max) {
            int[] array = new int[size];
            for (int i = 0; i < size; i++) {
                array[i] = random.nextInt(max - min + 1) + min;
            }
            return array;
        }
    }

    @Benchmark
    public void benchmarkSize100(ScalabilityState state, Blackhole blackhole) {
        var result = state.kadane.findMaximumSubarray(state.size100);
        blackhole.consume(result);
    }

    @Benchmark
    public void benchmarkSize1000(ScalabilityState state, Blackhole blackhole) {
        var result = state.kadane.findMaximumSubarray(state.size1000);
        blackhole.consume(result);
    }

    @Benchmark
    public void benchmarkSize10000(ScalabilityState state, Blackhole blackhole) {
        var result = state.kadane.findMaximumSubarray(state.size10000);
        blackhole.consume(result);
    }

    @Benchmark
    public void benchmarkSize50000(ScalabilityState state, Blackhole blackhole) {
        var result = state.kadane.findMaximumSubarray(state.size50000);
        blackhole.consume(result);
    }

    @Benchmark
    public void benchmarkSize100000(ScalabilityState state, Blackhole blackhole) {
        var result = state.kadane.findMaximumSubarray(state.size100000);
        blackhole.consume(result);
    }
}