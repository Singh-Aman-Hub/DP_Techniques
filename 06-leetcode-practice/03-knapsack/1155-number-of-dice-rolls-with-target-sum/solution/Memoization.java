/**
 * 1155. Number of Dice Rolls With Target Sum — Memoization (top-down)
 *
 * Idea: ways(dice, remaining) = number of ways the next `dice` dice can sum to `remaining`.
 * Try every face 1..k for the current die and recurse. Only (dice, remaining) matters for the
 * future, so cache it.
 *
 * Time complexity:  O(n * target * k) — n*target states, k faces each.
 * Space complexity: O(n * target) — memo table plus recursion depth n.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static final int MOD = 1_000_000_007;
    private static int faces;
    private static long[][] memo;   // memo[d][r] = ways for d dice to sum to r; -1 = unknown

    static int numRollsToTarget(int n, int k, int target) {
        faces = k;
        memo = new long[n + 1][target + 1];
        for (long[] row : memo) Arrays.fill(row, -1);
        return (int) ways(n, target);
    }

    private static long ways(int dice, int remaining) {
        if (dice == 0) return remaining == 0 ? 1 : 0;      // no dice left: success only at exact sum
        if (memo[dice][remaining] != -1) return memo[dice][remaining];
        long total = 0;
        for (int face = 1; face <= faces && face <= remaining; face++) {
            total = (total + ways(dice - 1, remaining - face)) % MOD;
        }
        memo[dice][remaining] = total;
        return total;
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
