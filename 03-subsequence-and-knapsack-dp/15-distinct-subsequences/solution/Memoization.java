/**
 * Distinct Subsequences (LeetCode 115) — Memoization (top-down DP)
 *
 * Idea: Cache ways(i, j) for the (n+1)(m+1) prefix pairs. Counts are stored as long:
 *       LeetCode promises only that the FINAL answer fits in 32 bits.
 *
 * Time complexity:  O(n * m) — each state once, O(1) work.
 * Space complexity: O(n * m) for the memo + O(n) recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i][j] = ways to form t[0..j) from s[0..i); -1 = not computed yet
    private static long[][] memo;

    static long numDistinct(String s, String t) {
        memo = new long[s.length() + 1][t.length() + 1];
        for (long[] row : memo) Arrays.fill(row, -1);
        return ways(s, t, s.length(), t.length());
    }

    private static long ways(String s, String t, int i, int j) {
        if (j == 0) return 1;
        if (i == 0) return 0;
        if (memo[i][j] != -1) return memo[i][j];
        long result = ways(s, t, i - 1, j);                                    // skip s[i-1]
        if (s.charAt(i - 1) == t.charAt(j - 1)) result += ways(s, t, i - 1, j - 1);   // use s[i-1]
        memo[i][j] = result;
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("rabbbit / rabbit", numDistinct("rabbbit", "rabbit"), 3);
        check("babgbag / bag", numDistinct("babgbag", "bag"), 5);
        check("abc / \"\" (edge: empty t appears once)", numDistinct("abc", ""), 1);
        check("ab / abc (edge: t longer than s)", numDistinct("ab", "abc"), 0);
        check("30 a's / 15 a's = C(30,15)", numDistinct("a".repeat(30), "a".repeat(15)), 155117520);
        // An intermediate cell holds C(40,20) = 137,846,528,820 (> Integer.MAX_VALUE),
        // but it never feeds the final cell, whose true value is 0.
        check("b + 40 a's / 20 a's + b (big intermediate, answer 0)",
                numDistinct("b" + "a".repeat(40), "a".repeat(20) + "b"), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
