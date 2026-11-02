/**
 * 790. Domino and Tromino Tiling — Tabulation (bottom-up)
 *
 * Idea: Fill full[i] and part[i] for i = 1..n from the two-state recurrence:
 * full[i] = full[i-1] + full[i-2] + 2*part[i-2], part[i] = part[i-1] + full[i-1].
 *
 * Time complexity:  O(n) — one loop, O(1) per column.
 * Space complexity: O(n) — two arrays.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    private static final int MOD = 1_000_000_007;

    static int numTilings(int n) {
        long[] full = new long[n + 1];   // full[i] = tilings covering exactly the first i columns
        long[] part = new long[n + 1];   // part[i] = tilings of i columns + top cell of column i+1
        full[0] = 1;
        for (int i = 1; i <= n; i++) {
            long twoBack = (i >= 2) ? full[i - 2] : 0;
            long jagged = (i >= 2) ? part[i - 2] : 0;
            full[i] = (full[i - 1] + twoBack + 2 * jagged) % MOD;
            part[i] = (part[i - 1] + full[i - 1]) % MOD;
        }
        return (int) full[n];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", numTilings(1), 1);
        check("n = 2", numTilings(2), 2);
        check("n = 3", numTilings(3), 5);
        check("n = 4", numTilings(4), 11);
        check("n = 5", numTilings(5), 24);
        check("n = 30", numTilings(30), 312342182);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
