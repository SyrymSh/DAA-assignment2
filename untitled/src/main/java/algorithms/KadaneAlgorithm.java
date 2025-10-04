package algorithms;

import metrics.PerformanceTracker;
import java.util.Arrays;

/**
 * Implementation of Kadane's Algorithm for finding the maximum subarray sum
 * with position tracking and comprehensive performance metrics.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 *
 * @author Student B
 */
public class KadaneAlgorithm {
    private final PerformanceTracker tracker;

    /**
     * Result class to store maximum subarray information
     */
    public static class MaximumSubarrayResult {
        private final int maxSum;
        private final int startIndex;
        private final int endIndex;

        public MaximumSubarrayResult(int maxSum, int startIndex, int endIndex) {
            this.maxSum = maxSum;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        // Getters
        public int getMaxSum() { return maxSum; }
        public int getStartIndex() { return startIndex; }
        public int getEndIndex() { return endIndex; }

        /**
         * Returns the actual subarray (computed on demand)
         */
        public int[] getSubarray(int[] originalArray) {
            if (originalArray == null) {
                return null;
            }
            return Arrays.copyOfRange(originalArray, startIndex, endIndex + 1);
        }

        @Override
        public String toString() {
            return String.format("Max Sum: %d, Range: [%d, %d]", maxSum, startIndex, endIndex);
        }
    }

    public KadaneAlgorithm() {
        this.tracker = new PerformanceTracker("KadaneAlgorithm");
    }

    /**
     * Finds the maximum sum of a contiguous subarray using Kadane's Algorithm
     */
    public MaximumSubarrayResult findMaximumSubarray(int[] nums) {
        tracker.reset();
        tracker.setInputSize(nums != null ? nums.length : 0);
        tracker.setInputType("standard");
        tracker.startTimer();
        tracker.incrementMemoryAllocation(); // Fixed: no argument

        // Input validation
        if (nums == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }
        if (nums.length == 0) {
            throw new IllegalArgumentException("Input array cannot be empty");
        }

        tracker.incrementArrayAccess(); // Fixed: no argument

        // Handle single element case
        if (nums.length == 1) {
            tracker.incrementArrayAccess(); // Fixed: no argument
            tracker.stopTimer();
            tracker.recordRun();
            return new MaximumSubarrayResult(nums[0], 0, 0);
        }

        int maxEndingHere = nums[0];
        int maxSoFar = nums[0];
        int start = 0;
        int end = 0;
        int tempStart = 0;

        tracker.incrementArrayAccesses(2); // Two element accesses
        tracker.incrementComparison(); // Initial setup comparison

        for (int i = 1; i < nums.length; i++) {
            tracker.incrementComparison(); // Loop condition check
            tracker.incrementArrayAccess(); // Access nums[i]

            // Decide whether to extend previous subarray or start new one
            if (nums[i] > maxEndingHere + nums[i]) {
                maxEndingHere = nums[i];
                tempStart = i;
                tracker.incrementComparison();
            } else {
                maxEndingHere = maxEndingHere + nums[i];
                tracker.incrementComparison();
            }

            // Update maximum sum found so far
            if (maxEndingHere > maxSoFar) {
                maxSoFar = maxEndingHere;
                start = tempStart;
                end = i;
                tracker.incrementComparison();
            }

            tracker.incrementComparison(); // Final if comparison
        }

        tracker.stopTimer();
        tracker.recordRun();
        return new MaximumSubarrayResult(maxSoFar, start, end);
    }

    /**
     * Optimized version that handles all negative numbers case more explicitly
     */
    public MaximumSubarrayResult findMaximumSubarrayOptimized(int[] nums) {
        tracker.reset();
        tracker.setInputSize(nums != null ? nums.length : 0);
        tracker.setInputType("optimized");
        tracker.startTimer();
        tracker.incrementMemoryAllocation(); // Fixed: no argument

        if (nums == null || nums.length == 0) {
            throw new IllegalArgumentException("Input array cannot be null or empty");
        }

        tracker.incrementArrayAccess(); // Fixed: no argument

        // Single element case
        if (nums.length == 1) {
            tracker.incrementArrayAccess(); // Fixed: no argument
            tracker.stopTimer();
            tracker.recordRun();
            return new MaximumSubarrayResult(nums[0], 0, 0);
        }

        int maxSoFar = nums[0];
        int maxEndingHere = nums[0];
        int start = 0, end = 0, tempStart = 0;

        tracker.incrementArrayAccesses(2);
        tracker.incrementComparison();

        boolean allNegative = nums[0] < 0;
        int maxSingleElement = nums[0];
        int maxSingleIndex = 0;

        for (int i = 1; i < nums.length; i++) {
            tracker.incrementComparison();
            tracker.incrementArrayAccess();

            // Check if all elements are negative
            if (nums[i] >= 0) allNegative = false;

            // Track maximum single element for all-negative case
            if (nums[i] > maxSingleElement) {
                maxSingleElement = nums[i];
                maxSingleIndex = i;
                tracker.incrementComparison();
            }

            // Standard Kadane's algorithm logic
            if (maxEndingHere < 0) {
                maxEndingHere = nums[i];
                tempStart = i;
            } else {
                maxEndingHere += nums[i];
            }

            if (maxEndingHere > maxSoFar) {
                maxSoFar = maxEndingHere;
                start = tempStart;
                end = i;
            }

            tracker.incrementComparisons(2);
        }

        // If all numbers are negative, return the maximum single element
        if (allNegative && maxSoFar < 0) {
            tracker.stopTimer();
            tracker.recordRun();
            return new MaximumSubarrayResult(maxSingleElement, maxSingleIndex, maxSingleIndex);
        }

        tracker.stopTimer();
        tracker.recordRun();
        return new MaximumSubarrayResult(maxSoFar, start, end);
    }

    /**
     * Batch testing method for multiple input arrays
     */
    public void runBatchTests(int[][] testArrays, String[] inputTypes) {
        if (testArrays.length != inputTypes.length) {
            throw new IllegalArgumentException("Test arrays and input types must have same length");
        }

        for (int i = 0; i < testArrays.length; i++) {
            tracker.setInputType(inputTypes[i]);
            findMaximumSubarray(testArrays[i]);
        }
    }

    /**
     * Returns the performance metrics for analysis
     */
    public PerformanceTracker getPerformanceTracker() {
        return tracker;
    }
}