/**
 * CSES 1633 Dice Combinations — tabulation (bottom-up)
 *
 * Idea: ways[s] reads ways[s-1] .. ways[s-6], all SMALLER sums, so fill the
 * table from s = 1 up to n (left to right). No recursion, so n = 10^6 is fine.
 *
 * Time complexity:  O(6n) = O(n) — n cells, at most 6 additions each.
 * Space complexity: O(n) — one cell per sum 0..n.
 *
 * Run: java DiceCombinationsTabulation.java
 */
public class DiceCombinationsTabulation {

    private static final long MOD = 1_000_000_007L;

    static long solve(int n) {
        // ways[s] = number of ordered throw sequences (faces 1..6) summing to exactly s, mod MOD
        long[] ways = new long[n + 1];
        ways[0] = 1; // the empty sequence
        for (int s = 1; s <= n; s++) {
            for (int face = 1; face <= 6 && face <= s; face++) { // face <= s: never index below 0
                ways[s] = (ways[s] + ways[s - face]) % MOD;
            }
        }
        return ways[n];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", solve(1), 1);
        check("n = 3", solve(3), 4);
        check("n = 4", solve(4), 8);
        check("n = 6", solve(6), 32);
        check("n = 7", solve(7), 63);
        check("n = 10", solve(10), 492);
        check("n = 50 (mod 1e9+7)", solve(50), 660_641_036);
        check("n = 1000000 (largest CSES input)", solve(1_000_000), 874_273_980);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
