package algorithms;

import metrics.PerformanceTracker;

/**
 * Enhanced Kadane's Algorithm with comprehensive performance tracking
 */
public class KadaneAlgorithm {
    private final PerformanceTracker tracker;

    public KadaneAlgorithm() {
        this.tracker = new PerformanceTracker("KadaneAlgorithm");
    }

    /**
     * Finds maximum subarray sum with full performance tracking
     */
    public MaximumSubarrayResult findMaximumSubarray(int[] nums) {
        tracker.reset();
        tracker.setInputSize(nums.length);
        tracker.setInputType("standard");
        tracker.startTimer();
        tracker.incrementMemoryAllocation(1); // For result object

        // Input validation
        if (nums == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }
        if (nums.length == 0) {
            throw new IllegalArgumentException("Input array cannot be empty");
        }

        tracker.incrementArrayAccess(1); // Access nums.length

        // Handle single element case
        if (nums.length == 1) {
            tracker.incrementArrayAccess(1);
            tracker.stopTimer();
            tracker.recordRun();
            return new MaximumSubarrayResult(nums[0], 0, 0);
        }

        int maxEndingHere = nums[0];
        int maxSoFar = nums[0];
        int start = 0;
        int end = 0;
        int tempStart = 0;

        tracker.incrementArrayAccess(2); // First two element accesses
        tracker.incrementComparison(1); // Initial setup comparison

        for (int i = 1; i < nums.length; i++) {
            tracker.incrementComparison(1); // Loop condition check
            tracker.incrementArrayAccess(1); // Access nums[i]

            // Decide whether to extend previous subarray or start new one
            if (nums[i] > maxEndingHere + nums[i]) {
                maxEndingHere = nums[i];
                tempStart = i;
                tracker.incrementComparison(1);
            } else {
                maxEndingHere = maxEndingHere + nums[i];
                tracker.incrementComparison(1);
            }

            // Update maximum sum found so far
            if (maxEndingHere > maxSoFar) {
                maxSoFar = maxEndingHere;
                start = tempStart;
                end = i;
                tracker.incrementComparison(1);
            }

            tracker.incrementComparison(1); // Final if comparison
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

    // ... (rest of KadaneAlgorithm class remains the same)

    public PerformanceTracker getPerformanceTracker() {
        return tracker;
    }
}