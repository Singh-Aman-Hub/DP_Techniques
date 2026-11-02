/**
 * LeetCode 1696 — Jump Game VI — the SLOW versions (brute force + O(n·k) tabulation)
 *
 * Idea: dp[i] = best score of a path that starts at index 0 and ends exactly at index i.
 *       The last jump into i came from some j in [i-k, i-1], so
 *           dp[i] = nums[i] + max(dp[i-k .. i-1]).
 *       The inner max over a window of the last k dp values is what the monotonic deque
 *       will later answer in amortized O(1).
 *
 * Time complexity:  bruteForce: O(k^n) in the worst case — every index branches into k jumps.
 *                   slowTable:  O(n·k) — n cells, each scanning up to k earlier cells.
 * Space complexity: bruteForce: O(n) recursion depth. slowTable: O(n) for the dp array.
 *
 * Run: java Slow.java
 */
public class Slow {

    /** Best score from index `from` to the last index (plain recursion, small inputs only). */
    static long bruteForce(int[] nums, int k, int from) {
        if (from == nums.length - 1) return nums[from];
        long best = Long.MIN_VALUE;
        for (int step = 1; step <= k && from + step < nums.length; step++) {
            best = Math.max(best, bruteForce(nums, k, from + step));
        }
        return nums[from] + best;
    }

    static long slowTable(int[] nums, int k) {
        int n = nums.length;
        // dp[i] = maximum score of a path that starts at index 0 and ends at index i
        long[] dp = new long[n];
        dp[0] = nums[0];                        // we always start on index 0
        for (int i = 1; i < n; i++) {
            long bestPrevious = Long.MIN_VALUE;
            for (int j = Math.max(0, i - k); j < i; j++) {   // the window of the last k cells
                bestPrevious = Math.max(bestPrevious, dp[j]);
            }
            dp[i] = nums[i] + bestPrevious;
        }
        return dp[n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[] example = {1, -1, -2, 4, -7, 3};   // 1 -> -1 -> 4 -> 3 = 7
        check("brute example k=2", bruteForce(example, 2, 0), 7);
        check("table example k=2", slowTable(example, 2), 7);
        check("table [10,-5,-2,4,0,3] k=3", slowTable(new int[]{10, -5, -2, 4, 0, 3}, 3), 17);
        check("table [1,-5,-20,4,-1,3,-6,-3] k=2", slowTable(new int[]{1, -5, -20, 4, -1, 3, -6, -3}, 2), 0);
        check("table single element (edge)", slowTable(new int[]{5}, 1), 5);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
