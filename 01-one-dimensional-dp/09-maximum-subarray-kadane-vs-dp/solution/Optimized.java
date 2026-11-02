/**
 * Maximum Subarray (LeetCode 53) — Kadane's algorithm (space-optimized DP)
 *
 * Idea: Kadane's algorithm IS the tabulation with the table replaced by one
 * variable: currentEnding holds endingAt[i-1], and the update
 *     currentEnding = nums[i] + max(0, currentEnding)
 * is exactly the DP recurrence. A second variable keeps the best value seen.
 * maxSubArrayWithBounds also reports where the best subarray starts and ends.
 *
 * Time complexity:  O(n) — one pass.
 * Space complexity: O(1) — a few variables.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int maxSubArray(int[] nums) {
        int currentEnding = nums[0];   // endingAt(i): best sum of a subarray ending at i
        int best = nums[0];            // max of endingAt(0..i)
        for (int i = 1; i < nums.length; i++) {
            currentEnding = nums[i] + Math.max(0, currentEnding); // drop a negative prefix
            best = Math.max(best, currentEnding);
        }
        return best;
    }

    /** Returns {bestSum, startIndex, endIndex} of one maximum subarray. */
    static int[] maxSubArrayWithBounds(int[] nums) {
        int currentEnding = nums[0], currentStart = 0;
        int best = nums[0], bestStart = 0, bestEnd = 0;
        for (int i = 1; i < nums.length; i++) {
            if (currentEnding < 0) {          // the best subarray ending at i starts fresh at i
                currentEnding = nums[i];
                currentStart = i;
            } else {                          // otherwise it extends the one ending at i-1
                currentEnding += nums[i];
            }
            if (currentEnding > best) {
                best = currentEnding;
                bestStart = currentStart;
                bestEnd = i;
            }
        }
        return new int[]{best, bestStart, bestEnd};
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[-2,1,-3,4,-1,2,1,-5,4] (walkthrough example)", maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}), 6);
        check("[5,4,-1,7,8] (whole array)", maxSubArray(new int[]{5, 4, -1, 7, 8}), 23);
        check("[1] (edge: single element)", maxSubArray(new int[]{1}), 1);
        check("[-3,-1,-2] (edge: all negative, must pick one)", maxSubArray(new int[]{-3, -1, -2}), -1);
        check("[-1] (edge: single negative)", maxSubArray(new int[]{-1}), -1);
        check("[2,-5,3] (restart beats extending)", maxSubArray(new int[]{2, -5, 3}), 3);
        int[] bounds = maxSubArrayWithBounds(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4});
        check("bounds sum", bounds[0], 6);
        check("bounds start (value 4 at index 3)", bounds[1], 3);
        check("bounds end (value 1 at index 6)", bounds[2], 6);
        int[] allNegative = maxSubArrayWithBounds(new int[]{-3, -1, -2});
        check("all-negative bounds start", allNegative[1], 1);
        check("all-negative bounds end", allNegative[2], 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
