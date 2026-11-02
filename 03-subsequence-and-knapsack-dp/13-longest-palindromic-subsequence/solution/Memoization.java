/**
 * Longest Palindromic Subsequence (LeetCode 516) — Memoization (interval form)
 *
 * Idea: The window (i, j) has only n * n possible values, but the brute force reaches
 *       the same window through many paths (drop left then right == drop right then left).
 *       Cache each window's answer.
 *
 * Time complexity:  O(n^2) — about n^2 / 2 windows, O(1) work each.
 * Space complexity: O(n^2) for the memo + O(n) recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i][j] = LPS length of s[i..j]; -1 = not computed yet
    private static int[][] memo;

    static int longestPalindromeSubseq(String s) {
        int n = s.length();
        memo = new int[n][n];
        for (int[] row : memo) Arrays.fill(row, -1);
        return lps(s, 0, n - 1);
    }

    private static int lps(String s, int i, int j) {
        if (i > j) return 0;
        if (i == j) return 1;
        if (memo[i][j] != -1) return memo[i][j];
        if (s.charAt(i) == s.charAt(j)) {
            memo[i][j] = 2 + lps(s, i + 1, j - 1);
        } else {
            memo[i][j] = Math.max(lps(s, i + 1, j), lps(s, i, j - 1));
        }
        return memo[i][j];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("bbbab", longestPalindromeSubseq("bbbab"), 4);
        check("cbbd", longestPalindromeSubseq("cbbd"), 2);
        check("agbdba", longestPalindromeSubseq("agbdba"), 5);
        check("abcd (no repeats)", longestPalindromeSubseq("abcd"), 1);
        check("a (edge: single char)", longestPalindromeSubseq("a"), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
