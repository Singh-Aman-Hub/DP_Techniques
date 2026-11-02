/**
 * 552. Student Attendance Record II — Memoization (top-down)
 *
 * Idea: ways(remaining, absences, trailingL) = valid ways to append `remaining` characters given
 * how many A's were used (0/1) and how many L's end the prefix (0..2). P always works and resets
 * the L run; A only if no A yet; L only if fewer than 2 trailing L's.
 *
 * Time complexity:  O(n) — 6n states, at most 3 moves each.
 * Space complexity: O(n) — memo table plus recursion depth n.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static final int MOD = 1_000_000_007;
    private static long[][][] memo;   // memo[r][a][l] = completions; -1 = unknown

    static int checkRecord(int n) {
        memo = new long[n + 1][2][3];
        for (long[][] plane : memo) for (long[] row : plane) Arrays.fill(row, -1);
        return (int) ways(n, 0, 0);
    }

    private static long ways(int remaining, int absences, int trailingL) {
        if (remaining == 0) return 1;                     // a complete valid record
        if (memo[remaining][absences][trailingL] != -1) return memo[remaining][absences][trailingL];
        long total = ways(remaining - 1, absences, 0);                                   // append P
        if (absences == 0) total += ways(remaining - 1, 1, 0);                           // append A
        if (trailingL < 2) total += ways(remaining - 1, absences, trailingL + 1);        // append L
        memo[remaining][absences][trailingL] = total % MOD;
        return memo[remaining][absences][trailingL];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", checkRecord(1), 3);
        check("n = 2", checkRecord(2), 8);
        check("n = 3", checkRecord(3), 19);
        check("n = 10101", checkRecord(10101), 183236316);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
