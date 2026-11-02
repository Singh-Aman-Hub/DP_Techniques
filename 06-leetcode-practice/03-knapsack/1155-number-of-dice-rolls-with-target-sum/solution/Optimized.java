/**
 * 1155. Number of Dice Rolls With Target Sum — Optimized (two rows + sliding window sum)
 *
 * Idea: current[t] = previous[t-k] + ... + previous[t-1], a sum over a window of width k.
 * Sliding t to t+1 adds previous[t] and drops previous[t-k], so each cell is O(1).
 * Only the previous row is ever read, so two 1D arrays replace the table.
 *
 * Time complexity:  O(n * target) — each of the n*target cells in O(1).
 * Space complexity: O(target) — two rows.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    private static final int MOD = 1_000_000_007;

    static int numRollsToTarget(int n, int k, int target) {
        long[] previous = new long[target + 1];   // previous[t] = ways for (i-1) dice to sum to t
        previous[0] = 1;
        for (int i = 1; i <= n; i++) {
            long[] current = new long[target + 1];
            long window = 0;                      // sum of previous[t-k .. t-1]
            for (int t = 1; t <= target; t++) {
                window = (window + previous[t - 1]) % MOD;                  // slide in the new face-1 cell
                if (t - k - 1 >= 0) window = (window - previous[t - k - 1] + MOD) % MOD;  // slide out
                current[t] = window;
            }
            previous = current;
        }
        return (int) previous[target];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n=1, k=6, target=3", numRollsToTarget(1, 6, 3), 1);
        check("n=2, k=6, target=7", numRollsToTarget(2, 6, 7), 6);
        check("n=1, k=2, target=3 (edge: impossible)", numRollsToTarget(1, 2, 3), 0);
        check("n=30, k=30, target=500", numRollsToTarget(30, 30, 500), 222616187);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
