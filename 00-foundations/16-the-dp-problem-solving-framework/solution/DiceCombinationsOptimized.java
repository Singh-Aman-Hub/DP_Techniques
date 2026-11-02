/**
 * CSES 1633 Dice Combinations — space-optimized with a window of 6
 *
 * Idea: ways[s] reads only the six previous sums, so we keep just those six
 * values in a circular array: ways[s] lives in slot s % 6. When we compute
 * ways[s], slot s % 6 holds ways[s-6], which is the oldest value and is not
 * needed after this step, so we can overwrite it.
 *
 * Time complexity:  O(6n) = O(n) — same additions as the table.
 * Space complexity: O(1) — six numbers, whatever n is.
 *
 * Run: java DiceCombinationsOptimized.java
 */
public class DiceCombinationsOptimized {

    private static final long MOD = 1_000_000_007L;

    static long solve(int n) {
        // last[s % 6] = ways[s] for the six most recent sums; sums below 0 count as 0
        long[] last = new long[6];
        last[0] = 1; // ways[0] = 1; the other slots stand for negative sums, which have 0 ways
        for (int s = 1; s <= n; s++) {
            long total = 0;
            for (int face = 1; face <= 6 && face <= s; face++) {
                total += last[(s - face) % 6];
            }
            last[s % 6] = total % MOD; // overwrites ways[s-6], which is no longer needed
        }
        return last[n % 6];
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
