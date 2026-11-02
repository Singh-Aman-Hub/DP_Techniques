/**
 * 790. Domino and Tromino Tiling — Memoization (top-down)
 *
 * Idea: Two boundary shapes. full(i) = tilings of exactly i columns; part(i) = tilings of i
 * columns plus the top cell of column i+1. The last tile placed decides the previous shape:
 * full(i) = full(i-1) + full(i-2) + 2*part(i-2); part(i) = part(i-1) + full(i-1).
 *
 * Time complexity:  O(n) — 2n states, O(1) each.
 * Space complexity: O(n) — two memo arrays plus recursion depth about n.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    private static final int MOD = 1_000_000_007;
    private static Long[] fullMemo;   // fullMemo[i] = tilings of a flush 2 x i board
    private static Long[] partMemo;   // partMemo[i] = tilings of 2 x i plus one overhanging cell

    static int numTilings(int n) {
        fullMemo = new Long[n + 1];
        partMemo = new Long[n + 1];
        return (int) full(n);
    }

    private static long full(int i) {
        if (i < 0) return 0;
        if (i == 0) return 1;                        // empty board: one tiling (place nothing)
        if (fullMemo[i] != null) return fullMemo[i];
        long ways = full(i - 1)                      // vertical domino
                  + full(i - 2)                      // two horizontal dominoes
                  + 2 * part(i - 2);                 // tromino closing a jagged edge (top or bottom)
        fullMemo[i] = ways % MOD;
        return fullMemo[i];
    }

    private static long part(int i) {
        if (i <= 0) return 0;                        // a lone overhanging cell cannot be tiled
        if (partMemo[i] != null) return partMemo[i];
        long ways = part(i - 1)                      // horizontal domino extends the overhang
                  + full(i - 1);                     // tromino creates the overhang
        partMemo[i] = ways % MOD;
        return partMemo[i];
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
