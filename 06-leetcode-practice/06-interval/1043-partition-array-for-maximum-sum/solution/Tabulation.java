/**
 * 1043. Partition Array for Maximum Sum — Tabulation (bottom-up)
 *
 * Idea: dp[i] = best total for the first i elements. For each i, grow the last
 * block leftwards one element at a time (len = 1..k), tracking its maximum,
 * and take dp[i - len] + len * blockMax.
 *
 * Time complexity:  O(n * k) — n prefixes x k block lengths, O(1) each
 * Space complexity: O(n) — the dp array
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int maxSumAfterPartitioning(int[] arr, int k) {
        int n = arr.length;
        // dp[i] = maximum sum after partitioning arr[0..i-1]
        int[] dp = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            int blockMax = 0;
            for (int len = 1; len <= Math.min(k, i); len++) {
                blockMax = Math.max(blockMax, arr[i - len]);
                dp[i] = Math.max(dp[i], dp[i - len] + len * blockMax);
            }
        }
        return dp[n];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example k=3", maxSumAfterPartitioning(new int[]{1, 15, 7, 9, 2, 5, 10}, 3), 84);
        check("eleven numbers k=4", maxSumAfterPartitioning(new int[]{1, 4, 1, 5, 7, 3, 6, 1, 9, 9, 3}, 4), 83);
        check("one block [5,1] k=2", maxSumAfterPartitioning(new int[]{5, 1}, 2), 10);
        check("single element (edge)", maxSumAfterPartitioning(new int[]{1}, 1), 1);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
