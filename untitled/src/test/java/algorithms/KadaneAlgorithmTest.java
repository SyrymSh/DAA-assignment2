package algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("Kadane's Algorithm Comprehensive Test Suite")
class KadaneAlgorithmTest {
    private KadaneAlgorithm kadane;
    private Random random;

    @BeforeEach
    void setUp() {
        kadane = new KadaneAlgorithm();
        random = new Random(42); // Fixed seed for reproducible tests
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Conditions")
    class EdgeCases {
        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should throw IllegalArgumentException for null or empty arrays")
        void testInvalidInputs(int[] invalidArray) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> kadane.findMaximumSubarray(invalidArray)
            );

            assertThat(exception.getMessage())
                    .containsIgnoringCase("cannot be null")
                    .or(() -> assertThat(exception.getMessage()).containsIgnoringCase("cannot be empty"));
        }

        @Test
        @DisplayName("Should handle single positive element")
        void testSinglePositiveElement() {
            int[] nums = {42};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

            assertAll("Single positive element",
                    () -> assertEquals(42, result.getMaxSum()),
                    () -> assertEquals(0, result.getStartIndex()),
                    () -> assertEquals(0, result.getEndIndex())
            );
        }

        @Test
        @DisplayName("Should handle single negative element")
        void testSingleNegativeElement() {
            int[] nums = {-17};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

            assertAll("Single negative element",
                    () -> assertEquals(-17, result.getMaxSum()),
                    () -> assertEquals(0, result.getStartIndex()),
                    () -> assertEquals(0, result.getEndIndex())
            );
        }

        @Test
        @DisplayName("Should handle single zero element")
        void testSingleZeroElement() {
            int[] nums = {0};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

            assertAll("Single zero element",
                    () -> assertEquals(0, result.getMaxSum()),
                    () -> assertEquals(0, result.getStartIndex()),
                    () -> assertEquals(0, result.getEndIndex())
            );
        }

        @Test
        @DisplayName("Should handle two elements - positive then negative")
        void testTwoElementsPositiveNegative() {
            int[] nums = {5, -3};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

            assertAll("Two elements positive then negative",
                    () -> assertEquals(5, result.getMaxSum()),
                    () -> assertEquals(0, result.getStartIndex()),
                    () -> assertEquals(0, result.getEndIndex())
            );
        }

        @Test
        @DisplayName("Should handle two elements - negative then positive")
        void testTwoElementsNegativePositive() {
            int[] nums = {-3, 5};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

            assertAll("Two elements negative then positive",
                    () -> assertEquals(5, result.getMaxSum()),
                    () -> assertEquals(1, result.getStartIndex()),
                    () -> assertEquals(1, result.getEndIndex())
            );
        }

        @Test
        @DisplayName("Should handle all zeros")
        void testAllZeros() {
            int[] nums = {0, 0, 0, 0, 0};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

            assertAll("All zeros",
                    () -> assertEquals(0, result.getMaxSum()),
                    () -> assertThat(result.getStartIndex()).isBetween(0, 4),
                    () -> assertThat(result.getEndIndex()).isBetween(result.getStartIndex(), 4)
            );
        }

        @Test
        @DisplayName("Should handle all negative numbers")
        void testAllNegativeNumbers() {
            int[] nums = {-5, -3, -8, -1, -4};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarrayOptimized(nums);

            assertAll("All negative numbers",
                    () -> assertEquals(-1, result.getMaxSum()), // Largest single element
                    () -> assertEquals(3, result.getStartIndex()),
                    () -> assertEquals(3, result.getEndIndex())
            );
        }

        @Test
        @DisplayName("Should handle Integer.MAX_VALUE elements")
        void testMaxIntegerValues() {
            int[] nums = {Integer.MAX_VALUE, Integer.MAX_VALUE};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

            assertAll("Max integer values",
                    () -> assertEquals(Integer.MAX_VALUE * 2L, (long) result.getMaxSum()),
                    () -> assertEquals(0, result.getStartIndex()),
                    () -> assertEquals(1, result.getEndIndex())
            );
        }

        @Test
        @DisplayName("Should handle Integer.MIN_VALUE elements")
        void testMinIntegerValues() {
            int[] nums = {Integer.MIN_VALUE, Integer.MIN_VALUE};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

            assertAll("Min integer values",
                    () -> assertEquals(Integer.MIN_VALUE, result.getMaxSum()),
                    () -> assertThat(result.getStartIndex()).isBetween(0, 1),
                    () -> assertThat(result.getEndIndex()).isBetween(result.getStartIndex(), 1)
            );
        }
    }

    @Nested
    @DisplayName("Standard Test Cases from Literature")
    class StandardTestCases {
        @Test
        @DisplayName("Classic example from algorithm description")
        void testClassicExample() {
            int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

            assertAll("Classic example",
                    () -> assertEquals(6, result.getMaxSum()),
                    () -> assertEquals(3, result.getStartIndex()),
                    () -> assertEquals(6, result.getEndIndex())
            );
        }

        @Test
        @DisplayName("All positive numbers - entire array is solution")
        void testAllPositiveEntireArray() {
            int[] nums = {1, 2, 3, 4, 5};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

            assertAll("All positive entire array",
                    () -> assertEquals(15, result.getMaxSum()),
                    () -> assertEquals(0, result.getStartIndex()),
                    () -> assertEquals(4, result.getEndIndex())
            );
        }

        @Test
        @DisplayName("Solution at beginning of array")
        void testSolutionAtBeginning() {
            int[] nums = {10, 5, -20, 3, 4};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

            assertAll("Solution at beginning",
                    () -> assertEquals(15, result.getMaxSum()), // 10 + 5
                    () -> assertEquals(0, result.getStartIndex()),
                    () -> assertEquals(1, result.getEndIndex())
            );
        }

        @Test
        @DisplayName("Solution at end of array")
        void testSolutionAtEnd() {
            int[] nums = {1, -5, 3, 10, 8};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

            assertAll("Solution at end",
                    () -> assertEquals(21, result.getMaxSum()), // 3 + 10 + 8
                    () -> assertEquals(2, result.getStartIndex()),
                    () -> assertEquals(4, result.getEndIndex())
            );
        }

        @Test
        @DisplayName("Solution is single middle element")
        void testSolutionSingleMiddleElement() {
            int[] nums = {-1, -2, 10, -3, -4};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

            assertAll("Single middle element",
                    () -> assertEquals(10, result.getMaxSum()),
                    () -> assertEquals(2, result.getStartIndex()),
                    () -> assertEquals(2, result.getEndIndex())
            );
        }
    }

    @Nested
    @DisplayName("Property-Based Testing")
    class PropertyBasedTesting {
        @Test
        @DisplayName("Sum of maximum subarray should be at least the maximum element")
        void testMaximumSubarrayAtLeastMaxElement() {
            for (int i = 0; i < 100; i++) {
                int[] nums = generateRandomArray(50, -100, 100);
                KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

                int maxElement = Arrays.stream(nums).max().getAsInt();
                assertThat(result.getMaxSum())
                        .as("Maximum subarray sum should be at least the maximum element")
                        .isGreaterThanOrEqualTo(maxElement);
            }
        }

        @Test
        @DisplayName("Maximum subarray sum should be consistent with brute force for small arrays")
        void testAgainstBruteForceSmallArrays() {
            for (int i = 0; i < 50; i++) {
                int[] nums = generateRandomArray(20, -50, 50);
                KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);
                int bruteForceResult = bruteForceMaximumSubarray(nums);

                assertThat(result.getMaxSum())
                        .as("Should match brute force result for array: " + Arrays.toString(nums))
                        .isEqualTo(bruteForceResult);
            }
        }

        @Test
        @DisplayName("Indices should point to valid subarray with correct sum")
        void testIndicesPointToValidSubarray() {
            for (int i = 0; i < 50; i++) {
                int[] nums = generateRandomArray(30, -50, 50);
                KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

                // Verify that the indicated subarray actually has the claimed sum
                int actualSum = 0;
                for (int j = result.getStartIndex(); j <= result.getEndIndex(); j++) {
                    actualSum += nums[j];
                }

                assertThat(actualSum)
                        .as("Indicated subarray should have the claimed sum")
                        .isEqualTo(result.getMaxSum());
            }
        }

        @Test
        @DisplayName("Result should be invariant to array duplication")
        void testInvariantToDuplication() {
            int[] nums = generateRandomArray(10, -20, 20);
            KadaneAlgorithm.MaximumSubarrayResult result1 = kadane.findMaximumSubarray(nums);

            // Create a new array with the same elements
            int[] numsCopy = Arrays.copyOf(nums, nums.length);
            KadaneAlgorithm.MaximumSubarrayResult result2 = kadane.findMaximumSubarray(numsCopy);

            assertThat(result1.getMaxSum()).isEqualTo(result2.getMaxSum());
        }

        private int bruteForceMaximumSubarray(int[] nums) {
            int maxSum = Integer.MIN_VALUE;
            for (int i = 0; i < nums.length; i++) {
                int currentSum = 0;
                for (int j = i; j < nums.length; j++) {
                    currentSum += nums[j];
                    if (currentSum > maxSum) {
                        maxSum = currentSum;
                    }
                }
            }
            return maxSum;
        }
    }

    @Nested
    @DisplayName("Performance and Stress Testing")
    class PerformanceStressTesting {
        @Test
        @Timeout(value = 1, unit = TimeUnit.SECONDS)
        @DisplayName("Should handle large arrays efficiently")
        void testLargeArrayPerformance() {
            int[] largeArray = generateRandomArray(100000, -1000, 1000);

            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(largeArray);

            assertThat(result.getMaxSum()).isNotNull();
            assertThat(result.getStartIndex()).isBetween(0, largeArray.length - 1);
            assertThat(result.getEndIndex()).isBetween(result.getStartIndex(), largeArray.length - 1);
        }

        @Test
        @DisplayName("Should handle worst-case input (alternating positive/negative)")
        void testWorstCaseInput() {
            int[] worstCase = new int[10000];
            for (int i = 0; i < worstCase.length; i++) {
                worstCase[i] = (i % 2 == 0) ? 1 : -1;
            }

            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(worstCase);

            // In alternating pattern, maximum subarray is single element 1
            assertThat(result.getMaxSum()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should handle best-case input (all positive)")
        void testBestCaseInput() {
            int[] bestCase = generateRandomArray(10000, 1, 100);

            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(bestCase);

            // For all positive, entire array is maximum subarray
            int totalSum = Arrays.stream(bestCase).sum();
            assertThat(result.getMaxSum()).isEqualTo(totalSum);
            assertThat(result.getStartIndex()).isEqualTo(0);
            assertThat(result.getEndIndex()).isEqualTo(bestCase.length - 1);
        }

        @Test
        @DisplayName("Linear time complexity verification across sizes")
        void testLinearTimeComplexity() {
            int[] sizes = {100, 1000, 5000, 10000};
            long[] times = new long[sizes.length];

            for (int i = 0; i < sizes.length; i++) {
                int[] array = generateRandomArray(sizes[i], -100, 100);

                long startTime = System.nanoTime();
                kadane.findMaximumSubarray(array);
                long endTime = System.nanoTime();

                times[i] = endTime - startTime;
            }

            // Verify that time increases approximately linearly
            for (int i = 1; i < times.length; i++) {
                double timeRatio = (double) times[i] / times[i-1];
                double sizeRatio = (double) sizes[i] / sizes[i-1];

                // Allow some tolerance (should be roughly linear for O(n))
                assertThat(timeRatio)
                        .as("Time complexity should be roughly linear O(n)")
                        .isCloseTo(sizeRatio, within(3.0));
            }
        }
    }

    @Nested
    @DisplayName("Parameterized Test Cases")
    class ParameterizedTests {
        static Stream<Arguments> provideComprehensiveTestCases() {
            return Stream.of(
                    // Format: {input array}, expectedSum, expectedStart, expectedEnd
                    Arguments.of(new int[]{1, 2, 3}, 6, 0, 2),
                    Arguments.of(new int[]{-1, -2, -3}, -1, 0, 0),
                    Arguments.of(new int[]{2, -1, 2, 3, 4, -5}, 10, 0, 4),
                    Arguments.of(new int[]{-2, -3, 4, -1, -2, 1, 5, -3}, 7, 2, 6),
                    Arguments.of(new int[]{1, -3, 2, 1, -1}, 3, 2, 3),
                    Arguments.of(new int[]{8, -19, 5, -4, 20}, 21, 2, 4),
                    Arguments.of(new int[]{-1, 2, 3, -4, 5, 6}, 11, 4, 5),
                    Arguments.of(new int[]{-1, 2, 3, -4, 5, 6, -1}, 11, 4, 5),
                    Arguments.of(new int[]{3, -2, 5, -1}, 6, 0, 2),
                    Arguments.of(new int[]{-1, -2, 5, -4, 3, 2, -1, 2}, 6, 2, 7)
            );
        }

        @ParameterizedTest
        @MethodSource("provideComprehensiveTestCases")
        @DisplayName("Comprehensive parameterized test cases")
        void testParameterizedCases(int[] input, int expectedSum, int expectedStart, int expectedEnd) {
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(input);

            assertAll("Parameterized test case for: " + Arrays.toString(input),
                    () -> assertEquals(expectedSum, result.getMaxSum(),
                            "Maximum sum should match"),
                    () -> assertEquals(expectedStart, result.getStartIndex(),
                            "Start index should match"),
                    () -> assertEquals(expectedEnd, result.getEndIndex(),
                            "End index should match")
            );
        }

        @ParameterizedTest
        @CsvSource({
                "1,2,3,4,5, 15, 0, 4",
                "-1,-2,-3,-4,-5, -1, 0, 0",
                "0,0,0,0,0, 0, 0, 0",
                "1,-1,1,-1,1, 1, 0, 0"
        })
        @DisplayName("CSV-based test cases")
        void testCsvCases(String inputStr, int expectedSum, int expectedStart, int expectedEnd) {
            int[] input = Arrays.stream(inputStr.split(","))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(input);

            assertAll("CSV test case",
                    () -> assertEquals(expectedSum, result.getMaxSum()),
                    () -> assertEquals(expectedStart, result.getStartIndex()),
                    () -> assertEquals(expectedEnd, result.getEndIndex())
            );
        }
    }

    @Nested
    @DisplayName("Subarray Extraction and Validation")
    class SubarrayExtractionTests {
        @Test
        @DisplayName("Should extract correct subarray from original array")
        void testSubarrayExtraction() {
            int[] nums = {1, -2, 3, 4, -1, 2};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);
            int[] subarray = result.getSubarray(nums);

            int[] expected = {3, 4, -1, 2};
            assertArrayEquals(expected, subarray,
                    "Extracted subarray should match the actual maximum subarray");
        }

        @Test
        @DisplayName("Should handle single element subarray extraction")
        void testSingleElementSubarrayExtraction() {
            int[] nums = {-5, -2, 7, -3};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);
            int[] subarray = result.getSubarray(nums);

            int[] expected = {7};
            assertArrayEquals(expected, subarray,
                    "Single element subarray should be extracted correctly");
        }

        @Test
        @DisplayName("Should handle entire array subarray extraction")
        void testEntireArraySubarrayExtraction() {
            int[] nums = {5, 4, 3, 2, 1};
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);
            int[] subarray = result.getSubarray(nums);

            assertArrayEquals(nums, subarray,
                    "For all positive arrays, entire array should be extracted");
        }

        @Test
        @DisplayName("Subarray extraction should work with null original array")
        void testSubarrayExtractionWithNullOriginal() {
            KadaneAlgorithm.MaximumSubarrayResult result =
                    new KadaneAlgorithm.MaximumSubarrayResult(10, 0, 2);

            int[] subarray = result.getSubarray(null);

            assertNull(subarray, "Should return null when original array is null");
        }
    }

    @Nested
    @DisplayName("Cross-Validation with Built-in Methods")
    class CrossValidationTests {
        @Test
        @DisplayName("Should match results with simple cases")
        void testCrossValidationSimpleCases() {
            int[][] testCases = {
                    {1, 2, 3},
                    {-1, -2, -3},
                    {1, -2, 3},
                    {0, 0, 0}
            };

            for (int[] testCase : testCases) {
                KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(testCase);
                int expected = calculateExpectedSum(testCase);

                assertEquals(expected, result.getMaxSum(),
                        "Should match expected sum for: " + Arrays.toString(testCase));
            }
        }

        private int calculateExpectedSum(int[] nums) {
            // Simple O(n^2) approach for validation
            int maxSum = Integer.MIN_VALUE;
            for (int i = 0; i < nums.length; i++) {
                int currentSum = 0;
                for (int j = i; j < nums.length; j++) {
                    currentSum += nums[j];
                    if (currentSum > maxSum) {
                        maxSum = currentSum;
                    }
                }
            }
            return maxSum;
        }
    }

    // Helper method to generate random arrays for testing
    private int[] generateRandomArray(int size, int min, int max) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(max - min + 1) + min;
        }
        return array;
    }
}