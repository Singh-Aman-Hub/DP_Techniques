/**
 * Subarray Squares (CSES 2086) — Slow partition DP, O(K * N^2)
 *
 * Idea: split the array into exactly k contiguous groups and minimize the sum of
 * (group sum)^2. dp[g][i] = the cheapest way to cut the first i elements into g groups.
 * The last group is a[j..i-1] for some cut point j, so
 *     dp[g][i] = min over j of dp[g-1][j] + (prefix[i] - prefix[j])^2.
 * This is the "slow but obviously correct" version that the optimized file is tested against.
 *
 * Time complexity:  O(K * N^2) — K layers, each with N states, each state tries up to N cut points.
 * Space complexity: O(K * N)   — the full dp table (kept so the walkthrough can print it;
 *                                only two rows are really needed).
 *
 * Run: java Slow.java
 */
public class Slow {

    private static final long INF = Long.MAX_VALUE / 4;

    static long minSumOfSquares(long[] values, int groups) {
        int n = values.length;
        // prefix[i] = values[0] + ... + values[i-1], so the sum of a[j..i-1] is prefix[i] - prefix[j]
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + values[i];

        // dp[g][i] = minimum cost to split the first i elements into exactly g non-empty groups
        long[][] dp = new long[groups + 1][n + 1];
        for (long[] row : dp) java.util.Arrays.fill(row, INF);
        dp[0][0] = 0; // zero elements in zero groups costs nothing

        for (int g = 1; g <= groups; g++) {
            // at least g elements are needed to form g non-empty groups
            for (int i = g; i <= n; i++) {
                // j = number of elements in the first g-1 groups; it must be at least g-1
                for (int j = g - 1; j < i; j++) {
                    long groupSum = prefix[i] - prefix[j];
                    long candidate = dp[g - 1][j] + groupSum * groupSum;
                    if (candidate < dp[g][i]) dp[g][i] = candidate;
                }
            }
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
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
