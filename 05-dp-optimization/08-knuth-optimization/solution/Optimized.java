/**
 * Knuth Division (CSES 2088) — Knuth's optimization, O(n^2)
 *
 * Idea: same recurrence as Slow.java,
 *       cost[i][j] = sum(i..j) + min over i <= k < j of cost[i][k] + cost[k+1][j].
 *       The weight sum(i..j) satisfies the quadrangle inequality (with equality)
 *       and grows when the interval grows, so the best split point is monotone:
 *           opt[i][j-1] <= opt[i][j] <= opt[i+1][j].
 *       We only try k inside that window. Over one diagonal (fixed length) the
 *       windows telescope, so all the windows together have O(n) size.
 *
 * Time complexity:  O(n^2) — n lengths, each diagonal costs O(n) total thanks to telescoping.
 * Space complexity: O(n^2) — the cost table and the opt table.
 *
 * Run: java Optimized.java
 */
import java.util.Random;

public class Optimized {

    static long candidatesChecked;

    static long minTotalCost(long[] a) {
        int n = a.length;
        candidatesChecked = 0;
        if (n <= 1) return 0;

        long[] prefix = new long[n + 1];   // prefix[t] = a[0] + ... + a[t-1]
        for (int t = 0; t < n; t++) prefix[t + 1] = prefix[t] + a[t];

        long[][] cost = new long[n][n];    // cost[i][j] = min cost to split a[i..j] into singles
        int[][] opt = new int[n][n];       // opt[i][j]  = smallest k that achieves cost[i][j]
        for (int i = 0; i < n; i++) opt[i][i] = i;   // makes the window formula work for len = 2

        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                int from = opt[i][j - 1];                    // lower bound: opt of the interval without a[j]
                int to = Math.min(opt[i + 1][j], j - 1);     // upper bound: opt of the interval without a[i]
                                                             // (k must stay < j, so clamp; matters only for len = 2)
                long best = Long.MAX_VALUE;
                int bestK = from;
                for (int k = from; k <= to; k++) {
                    candidatesChecked++;
                    long candidate = cost[i][k] + cost[k + 1][j];
                    if (candidate < best) {   // strict "<" keeps the SMALLEST optimal k: a consistent tie rule
                        best = candidate;
                        bestK = k;
                    }
                }
                cost[i][j] = best + (prefix[j + 1] - prefix[i]);
                opt[i][j] = bestK;
            }
        }
        return cost[0][n - 1];
    }

    // Private copy of the O(n^3) DP, the trusted reference for the cross-check.
    private static long slowMinTotalCost(long[] a) {
        int n = a.length;
        if (n <= 1) return 0;
        long[] prefix = new long[n + 1];
        for (int t = 0; t < n; t++) prefix[t + 1] = prefix[t] + a[t];
        long[][] cost = new long[n][n];
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                long best = Long.MAX_VALUE;
                for (int k = i; k < j; k++) best = Math.min(best, cost[i][k] + cost[k + 1][j]);
                cost[i][j] = best + (prefix[j + 1] - prefix[i]);
            }
        }
        return cost[0][n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("walkthrough example [3,1,5,2,4]", minTotalCost(new long[]{3, 1, 5, 2, 4}), 34);
        check("k evaluations for n = 5 (slow needs 20)", candidatesChecked, 15);
        check("[1,1,2,3,5]", minTotalCost(new long[]{1, 1, 2, 3, 5}), 25);
        check("[4,4,4,4] split in halves", minTotalCost(new long[]{4, 4, 4, 4}), 32);
        check("two elements", minTotalCost(new long[]{2, 3}), 5);
        check("edge: single element", minTotalCost(new long[]{7}), 0);

        // Randomized cross-check (fixed seed): many ties come from the small value range,
        // which is exactly where a wrong window would show up.
        Random random = new Random(2088);
        boolean allMatch = true;
        for (int trial = 0; trial < 500; trial++) {
            int n = 1 + random.nextInt(40);
            long[] a = new long[n];
            for (int t = 0; t < n; t++) a[t] = 1 + random.nextInt(trial % 2 == 0 ? 5 : 1_000_000);
            if (minTotalCost(a) != slowMinTotalCost(a)) allMatch = false;
        }
        System.out.println((allMatch ? "PASS" : "FAIL") + "  500 random arrays agree with the O(n^3) DP");
        if (!allMatch) System.exit(1);

        // Show the speed-up on a bigger input: the work grows like n^2, not n^3.
        long[] big = new long[2000];
        for (int t = 0; t < big.length; t++) big[t] = 1 + random.nextInt(1_000_000_000);
        minTotalCost(big);
        boolean quadratic = candidatesChecked < 3L * big.length * big.length;
        System.out.println((quadratic ? "PASS" : "FAIL") + "  n = 2000 checked " + candidatesChecked
                + " split points (cubic DP would check about 1.33e9)");
        if (!quadratic) System.exit(1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
