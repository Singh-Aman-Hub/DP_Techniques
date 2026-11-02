/**
 * Subarray Squares (CSES 2086) — Divide and Conquer optimization, O(K * N log N)
 *
 * Idea: for a fixed layer g, let opt(i) be the (leftmost) best cut point j for dp[g][i].
 * Because the cost (sum of the group)^2 satisfies the quadrangle inequality, opt(i) never
 * moves left as i grows: opt(i) <= opt(i+1). So we solve the middle state first by scanning
 * its whole allowed j-range, then the left half only needs j <= opt(mid) and the right half
 * only needs j >= opt(mid). That is compute(lo, hi, optLo, optHi).
 *
 * Time complexity:  O(K * N log N) — per layer the recursion has log N levels and on each level
 *                   the j-ranges of all calls overlap only at their endpoints, so each level scans
 *                   O(N) cut points in total.
 * Space complexity: O(N) — two rows (previous and current layer) plus O(log N) recursion stack.
 *
 * Run: java Optimized.java
 */
import java.util.Random;

public class Optimized {

    private static final long INF = Long.MAX_VALUE / 4;

    private static long[] prefix;
    private static long[] previousLayer; // previousLayer[j] = dp[g-1][j]
    private static long[] currentLayer;  // currentLayer[i]  = dp[g][i], filled by compute(...)

    // cost of one group made of a[j..i-1]
    private static long cost(int j, int i) {
        long groupSum = prefix[i] - prefix[j];
        return groupSum * groupSum;
    }

    static long minSumOfSquares(long[] values, int groups) {
        int n = values.length;
        prefix = new long[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + values[i];

        previousLayer = new long[n + 1];
        java.util.Arrays.fill(previousLayer, INF);
        previousLayer[0] = 0;

        for (int g = 1; g <= groups; g++) {
            currentLayer = new long[n + 1];
            java.util.Arrays.fill(currentLayer, INF);
            // states i in [g, n]; their cut points j lie in [g-1, n-1]
            compute(g, n, g - 1, n - 1);
            previousLayer = currentLayer;
        }
        return previousLayer[n];
    }

    /**
     * Fills currentLayer[lo..hi], knowing that every optimal cut point for those states
     * lies inside [optLo, optHi].
     */
    private static void compute(int lo, int hi, int optLo, int optHi) {
        if (lo > hi) return;
        int mid = (lo + hi) / 2;
        long best = INF;
        int bestJ = optLo;
        // a cut point must also satisfy j < mid (the last group is non-empty)
        int lastJ = Math.min(mid - 1, optHi);
        for (int j = optLo; j <= lastJ; j++) {
            long candidate = previousLayer[j] + cost(j, mid);
            if (candidate < best) { // strict '<' keeps the LEFTMOST optimum, which is the monotone one
                best = candidate;
                bestJ = j;
            }
        }
        currentLayer[mid] = best;
        compute(lo, mid - 1, optLo, bestJ);  // states left of mid: opt <= opt(mid)
        compute(mid + 1, hi, bestJ, optHi);  // states right of mid: opt >= opt(mid)
    }

    // ---------- private copy of the slow O(K*N^2) DP, used only for cross-checking ----------
    private static long slowMinSumOfSquares(long[] values, int groups) {
        int n = values.length;
        long[] pre = new long[n + 1];
        for (int i = 0; i < n; i++) pre[i + 1] = pre[i] + values[i];
        long[][] dp = new long[groups + 1][n + 1];
        for (long[] row : dp) java.util.Arrays.fill(row, INF);
        dp[0][0] = 0;
        for (int g = 1; g <= groups; g++)
            for (int i = g; i <= n; i++)
                for (int j = g - 1; j < i; j++) {
                    long s = pre[i] - pre[j];
                    dp[g][i] = Math.min(dp[g][i], dp[g - 1][j] + s * s);
                }
        return dp[groups][n];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("CSES sample [2,3,1,2,2,3,4,1], k=3", minSumOfSquares(new long[]{2, 3, 1, 2, 2, 3, 4, 1}, 3), 110);
        check("walkthrough example, k=2", minSumOfSquares(new long[]{2, 3, 1, 2, 2, 3, 4, 1}, 2), 164);
        check("k=1 keeps everything together: 6^2", minSumOfSquares(new long[]{1, 2, 3}, 1), 36);
        check("k=n puts each element alone: 1+4+9", minSumOfSquares(new long[]{1, 2, 3}, 3), 14);
        check("edge: single element", minSumOfSquares(new long[]{5}, 1), 25);

        // randomized cross-check against the slow DP (fixed seed => reproducible)
        Random random = new Random(2086);
        int trials = 500;
        for (int t = 0; t < trials; t++) {
            int n = 1 + random.nextInt(25);
            int groups = 1 + random.nextInt(n);
            long[] values = new long[n];
            for (int i = 0; i < n; i++) values[i] = 1 + random.nextInt(30);
            long fast = minSumOfSquares(values, groups);
            long slow = slowMinSumOfSquares(values, groups);
            if (fast != slow) {
                System.out.println("FAIL  random trial " + t + ": fast=" + fast + " slow=" + slow
                        + " values=" + java.util.Arrays.toString(values) + " k=" + groups);
                System.exit(1);
            }
        }
        System.out.println("PASS  " + trials + " random arrays: divide & conquer == slow DP");
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
