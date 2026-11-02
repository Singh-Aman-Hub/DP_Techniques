/**
 * 1411. Number of Ways to Paint N x 3 Grid — Optimized (two pattern types)
 *
 * Idea: The 12 row patterns split into 6 "ABA" (ends match) and 6 "ABC" (all different).
 * Below an ABA row fit 3 ABA + 2 ABC patterns; below an ABC row fit 2 ABA + 2 ABC. So only the
 * number of colourings ending in each type matters:
 *   aba' = 3*aba + 2*abc,   abc' = 2*aba + 2*abc.
 *
 * Time complexity:  O(n) — constant work per row.
 * Space complexity: O(1) — two counters.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    private static final int MOD = 1_000_000_007;

    static int numOfWays(int n) {
        long aba = 6, abc = 6;    // colourings whose last row is of type ABA / ABC (row 0)
        for (int r = 1; r < n; r++) {
            long newAba = (3 * aba + 2 * abc) % MOD;
            long newAbc = (2 * aba + 2 * abc) % MOD;
            aba = newAba;
            abc = newAbc;
        }
        return (int) ((aba + abc) % MOD);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", numOfWays(1), 12);
        check("n = 2", numOfWays(2), 54);
        check("n = 3", numOfWays(3), 246);
        check("n = 5000", numOfWays(5000), 30228214);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
