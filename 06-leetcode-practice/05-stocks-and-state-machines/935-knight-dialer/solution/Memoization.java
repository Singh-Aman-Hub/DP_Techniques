/**
 * 935. Knight Dialer — Memoization (top-down)
 *
 * Idea: count(remaining, digit) = numbers with `remaining` digits that start on `digit`. The next
 * digit must be a knight jump away, so sum over the neighbour list. Only (remaining, digit)
 * matters, so cache it.
 *
 * Time complexity:  O(10 * n * 3) — 10n states, at most 3 neighbours each.
 * Space complexity: O(10 * n) — memo table plus recursion depth n.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static final int MOD = 1_000_000_007;
    // NEIGHBOURS[d] = digits a knight can jump to from d on the phone keypad
    private static final int[][] NEIGHBOURS = {
        {4, 6}, {6, 8}, {7, 9}, {4, 8}, {0, 3, 9}, {}, {0, 1, 7}, {2, 6}, {1, 3}, {2, 4}
    };
    private static long[][] memo;   // memo[r][d] = numbers of length r starting on d; -1 = unknown

    static int knightDialer(int n) {
        memo = new long[n + 1][10];
        for (long[] row : memo) Arrays.fill(row, -1);
        long total = 0;
        for (int d = 0; d <= 9; d++) total += count(n, d);
        return (int) (total % MOD);
    }

    private static long count(int remaining, int digit) {
        if (remaining == 1) return 1;                    // this digit is the last one
        if (memo[remaining][digit] != -1) return memo[remaining][digit];
        long ways = 0;
        for (int next : NEIGHBOURS[digit]) ways += count(remaining - 1, next);
        memo[remaining][digit] = ways % MOD;
        return memo[remaining][digit];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", knightDialer(1), 10);
        check("n = 2", knightDialer(2), 20);
        check("n = 3", knightDialer(3), 46);
        check("n = 3131", knightDialer(3131), 136006598);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
