/**
 * Rod Cutting — 1D DP ("choose the first cut") with reconstruction
 *
 * Idea: best[r] = most money for a rod of length r. Whatever the optimal plan is, it
 *       has a first piece of some length c (1..r); after selling it we face a rod of
 *       length r - c, which we cut optimally. So best[r] = max over c of
 *       prices[c-1] + best[r - c]. Storing firstCut[r] (the c that won) lets us list
 *       the cuts by jumping r -> r - firstCut[r]. This is the 1D unbounded-knapsack
 *       table, so only O(n) memory is needed.
 *
 * Time complexity:  O(n^2) — for each of n lengths we try up to n first cuts.
 * Space complexity: O(n) — two arrays of length n + 1.
 *
 * Run: java Optimized.java
 */
import java.util.ArrayList;
import java.util.List;

public class Optimized {

    private static int[] best;      // best[r] = max revenue for a rod of length r
    private static int[] firstCut;  // firstCut[r] = length of the first piece in one optimal plan for r

    private static void solve(int[] prices, int rodLength) {
        best = new int[rodLength + 1];
        firstCut = new int[rodLength + 1];
        for (int r = 1; r <= rodLength; r++) {
            best[r] = Integer.MIN_VALUE;
            for (int c = 1; c <= r; c++) {
                int candidate = prices[c - 1] + best[r - c];
                if (candidate > best[r]) {   // strict '>' keeps the shortest winning first cut
                    best[r] = candidate;
                    firstCut[r] = c;
                }
            }
        }
    }

    static int maxRevenue(int[] prices, int rodLength) {
        solve(prices, rodLength);
        return best[rodLength];
    }

    static List<Integer> cuts(int[] prices, int rodLength) {
        solve(prices, rodLength);
        List<Integer> pieces = new ArrayList<>();
        for (int r = rodLength; r > 0; r -= firstCut[r]) pieces.add(firstCut[r]);
        return pieces;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[] classic = {1, 5, 8, 9, 10, 17, 17, 20};
        check("classic prices, n = 8", maxRevenue(classic, 8), 22);
        check("prices [1,5,8,9], n = 4", maxRevenue(new int[]{1, 5, 8, 9}, 4), 10);
        check("unit pieces best, n = 8", maxRevenue(new int[]{3, 5, 8, 9, 10, 17, 17, 20}, 8), 24);
        check("n = 1", maxRevenue(new int[]{2}, 1), 2);
        check("n = 0 (edge: nothing to sell)", maxRevenue(new int[]{}, 0), 0);

        checkCuts("classic cuts", classic, 8, List.of(2, 6));
        checkCuts("[1,5,8,9] cuts", new int[]{1, 5, 8, 9}, 4, List.of(2, 2));
        checkCuts("n = 0 cuts", new int[]{}, 0, List.of());
    }

    private static void checkCuts(String label, int[] prices, int n, List<Integer> expected) {
        List<Integer> actual = cuts(prices, n);
        int length = 0, revenue = 0;
        for (int piece : actual) { length += piece; revenue += prices[piece - 1]; }
        boolean ok = actual.equals(expected) && length == n && revenue == maxRevenue(prices, n);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
