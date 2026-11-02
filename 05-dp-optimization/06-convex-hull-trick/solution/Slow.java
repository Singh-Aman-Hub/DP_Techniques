/**
 * Frog 3 (AtCoder DP Contest, Task Z) — Slow DP, O(N^2)
 *
 * Idea: dp[i] = minimum total cost for the frog to reach stone i (0-indexed) from stone 0.
 * The last jump came from some earlier stone j and cost (h[i] - h[j])^2 + C, so
 *     dp[i] = min over j < i of dp[j] + (h[i] - h[j])^2 + C.
 * Correct and simple, but every state scans all earlier stones.
 *
 * Time complexity:  O(N^2) — N states, each tries up to N previous stones.
 * Space complexity: O(N)   — one dp value per stone.
 *
 * Run: java Slow.java
 */
public class Slow {

    static long minFrogCost(long[] heights, long jumpCost) {
        int n = heights.length;
        // dp[i] = cheapest total cost to stand on stone i
        long[] dp = new long[n];
        dp[0] = 0; // the frog starts here for free
        for (int i = 1; i < n; i++) {
            dp[i] = Long.MAX_VALUE;
            for (int j = 0; j < i; j++) {
                long diff = heights[i] - heights[j];
                long candidate = dp[j] + diff * diff + jumpCost;
                if (candidate < dp[i]) dp[i] = candidate;
            }
        }
        return dp[n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("sample 1: h=1..5, C=6", minFrogCost(new long[]{1, 2, 3, 4, 5}, 6), 20);
        check("sample 2: two far stones, C=1e12",
                minFrogCost(new long[]{500000, 1000000}, 1000000000000L), 1250000000000L);
        check("walkthrough example, C=5",
                minFrogCost(new long[]{1, 3, 4, 5, 10, 11, 12, 13}, 5), 62);
        check("edge: one stone, nothing to do", minFrogCost(new long[]{7}, 100), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
