/**
 * Knuth Division (CSES 2088) — plain interval DP, O(n^3)
 *
 * Idea: think of the process backwards: instead of cutting the array into single
 *       elements, MERGE neighbouring pieces, paying the sum of the merged piece.
 *       cost[i][j] = cheapest way to turn a[i..j] into single elements
 *                  = sum(i..j) + min over split k (i <= k < j) of cost[i][k] + cost[k+1][j].
 *       Every split point k is tried, which is what makes it cubic.
 *
 * Time complexity:  O(n^3) — about n^2/2 intervals, each tries up to n split points.
 * Space complexity: O(n^2) — the cost table, plus O(n) for prefix sums.
 *
 * Run: java Slow.java
 */
public class Slow {

    // How many (interval, k) pairs were examined — used to compare against Knuth's version.
    static long candidatesChecked;

    static long minTotalCost(long[] a) {
        int n = a.length;
        candidatesChecked = 0;
        if (n <= 1) return 0;   // nothing to cut

        // prefix[t] = a[0] + ... + a[t-1], so sum(i..j) = prefix[j+1] - prefix[i] in O(1)
        long[] prefix = new long[n + 1];
        for (int t = 0; t < n; t++) prefix[t + 1] = prefix[t] + a[t];

        // cost[i][j] = minimum total cost to split a[i..j] (inclusive) into single elements
        long[][] cost = new long[n][n];   // cost[i][i] = 0 already: a single element needs no cut

        // Shorter intervals first, because cost[i][j] reads only strictly shorter intervals.
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                long best = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {          // first cut: left part a[i..k], right part a[k+1..j]
                    candidatesChecked++;
                    best = Math.min(best, cost[i][k] + cost[k + 1][j]);
                }
                cost[i][j] = best + (prefix[j + 1] - prefix[i]);   // the cut itself costs sum(i..j)
            }
        }
        return cost[0][n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("walkthrough example [3,1,5,2,4]", minTotalCost(new long[]{3, 1, 5, 2, 4}), 34);
        check("k evaluations for n = 5", candidatesChecked, 20);   // 4*1 + 3*2 + 2*3 + 1*4
        check("[1,1,2,3,5]", minTotalCost(new long[]{1, 1, 2, 3, 5}), 25);
        check("[4,4,4,4] split in halves", minTotalCost(new long[]{4, 4, 4, 4}), 32);
        check("two elements", minTotalCost(new long[]{2, 3}), 5);
        check("edge: single element", minTotalCost(new long[]{7}), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
