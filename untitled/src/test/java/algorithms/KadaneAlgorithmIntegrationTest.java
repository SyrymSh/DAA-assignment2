package algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

@DisplayName("Kadane Algorithm Integration Tests")
class KadaneAlgorithmIntegrationTest {

    private KadaneAlgorithm kadane;

    @BeforeEach
    void setUp() {
        kadane = new KadaneAlgorithm();
    }

    @Test
    @DisplayName("Integration test with multiple consecutive operations")
    void testMultipleConsecutiveOperations() {
        int[][] testArrays = {
                {1, 2, 3},
                {-1, -2, -3},
                {2, -1, 3},
                {0, 0, 0, 0},
                {10, -5, 8, -3, 6}
        };

        int[] expectedSums = {6, -1, 4, 0, 16};

        for (int i = 0; i < testArrays.length; i++) {
            KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(testArrays[i]);
            assertEquals(expectedSums[i], result.getMaxSum(),
                    "Failed for array: " + Arrays.toString(testArrays[i]));
        }
    }

    @Test
    @DisplayName("Integration test with performance tracking")
    void testPerformanceTrackingIntegration() {
        int[] nums = {1, -2, 3, 4, -1, 2, 1, -5, 4};

        // First run
        KadaneAlgorithm.MaximumSubarrayResult result1 = kadane.findMaximumSubarray(nums);

        // Second run with same data
        KadaneAlgorithm.MaximumSubarrayResult result2 = kadane.findMaximumSubarray(nums);

        // Results should be identical
        assertEquals(result1.getMaxSum(), result2.getMaxSum());
        assertEquals(result1.getStartIndex(), result2.getStartIndex());
        assertEquals(result1.getEndIndex(), result2.getEndIndex());

        // Performance tracker should have recorded both runs
        assertTrue(kadane.getPerformanceTracker().getRunCount() >= 2);
    }

    @Test
    @DisplayName("Integration test with subarray extraction")
    void testSubarrayExtractionIntegration() {
        int[] nums = {4, -1, 2, 1, -5, 3, 2};
        KadaneAlgorithm.MaximumSubarrayResult result = kadane.findMaximumSubarray(nums);

        // Extract the subarray
        int[] subarray = result.getSubarray(nums);

        // Verify the subarray sum matches the result
        int subarraySum = Arrays.stream(subarray).sum();
        assertEquals(result.getMaxSum(), subarraySum);

        // Verify the subarray indices are correct
        assertArrayEquals(
                Arrays.copyOfRange(nums, result.getStartIndex(), result.getEndIndex() + 1),
                subarray
        );
    }
}