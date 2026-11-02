/**
 * Rod Cutting — Tabulation (bottom-up DP) with reconstruction of the cuts
 *
 * Idea: best[p][r] = best revenue for a rod of length r using piece lengths 1..p.
 *       best[p][r] = max(best[p-1][r]                       (never cut length p),
 *                        prices[p-1] + best[p][r - p])      (cut one piece of length p).
 *       To recover the cuts we walk back from best[n][n]: if the value equals the cell
 *       above, length p was not used; otherwise one piece of length p was cut.
 *
 * Time complexity:  O(n^2) — n * (n + 1) cells, O(1) each; reconstruction is O(n).
 * Space complexity: O(n^2) — the full table (needed for the walk back).
 *
 * Run: java Tabulation.java
 */
import java.util.ArrayList;
import java.util.List;

public class Tabulation {

    // best[p][r] = max revenue for length r when only piece lengths 1..p are allowed
    static int[][] buildTable(int[] prices, int rodLength) {
        int n = rodLength;
        int[][] best = new int[n + 1][n + 1];
        // Callers guarantee n >= 1. Row 0 is never read: row 1 is the base row,
        // where only unit pieces are allowed, so a rod of length r sells for r * prices[0].
        for (int r = 0; r <= n; r++) best[1][r] = r * prices[0];
        for (int p = 2; p <= n; p++) {
            for (int r = 0; r <= n; r++) {
                best[p][r] = best[p - 1][r];                              // skip length p
                if (p <= r) {
                    best[p][r] = Math.max(best[p][r], prices[p - 1] + best[p][r - p]);   // cut length p
                }
            }
        }
        return best;
    }

    static int maxRevenue(int[] prices, int rodLength) {
        if (rodLength == 0) return 0;
        return buildTable(prices, rodLength)[rodLength][rodLength];
    }

    // Walks back through the table and returns the piece lengths, longest first.
    static List<Integer> cuts(int[] prices, int rodLength) {
        List<Integer> pieces = new ArrayList<>();
        if (rodLength == 0) return pieces;
        int[][] best = buildTable(prices, rodLength);
        int p = rodLength, r = rodLength;
        while (r > 0) {
            if (p > 1 && best[p][r] == best[p - 1][r]) {
                p--;                 // same value without length p: length p not needed here
            } else {
                pieces.add(p);       // the value came from "cut one piece of length p"
                r -= p;              // stay on row p: the same length may be cut again
            }
        }
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

        checkCuts("classic cuts", classic, 8, List.of(6, 2));
        checkCuts("[1,5,8,9] cuts", new int[]{1, 5, 8, 9}, 4, List.of(2, 2));
        checkCuts("unit-piece cuts", new int[]{3, 5, 8, 9, 10, 17, 17, 20}, 8, List.of(1, 1, 1, 1, 1, 1, 1, 1));
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
