/**
 * Longest Common Subsequence (LeetCode 1143) — Memoization (top-down DP)
 *
 * Idea: The state (i, j) = "lengths of the two prefixes" has only (n + 1) * (m + 1)
 *       values, but the brute force visits the same (i, j) many times through
 *       different mismatch paths. Cache each answer.
 *
 * Time complexity:  O(n * m) — each state computed once with O(1) work.
 * Space complexity: O(n * m) for the memo + O(n + m) recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i][j] = LCS length of text1[0..i) and text2[0..j); -1 = not computed yet
    private static int[][] memo;

    static int longestCommonSubsequence(String text1, String text2) {
        memo = new int[text1.length() + 1][text2.length() + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return lcs(text1, text2, text1.length(), text2.length());
    }

    private static int lcs(String text1, String text2, int i, int j) {
        if (i == 0 || j == 0) return 0;
        if (memo[i][j] != -1) return memo[i][j];
        if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
            memo[i][j] = 1 + lcs(text1, text2, i - 1, j - 1);
        } else {
            memo[i][j] = Math.max(lcs(text1, text2, i - 1, j), lcs(text1, text2, i, j - 1));
        }
        return memo[i][j];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("abcbdab / bdcaba", longestCommonSubsequence("abcbdab", "bdcaba"), 4);
        check("abcde / ace", longestCommonSubsequence("abcde", "ace"), 3);
        check("abc / abc", longestCommonSubsequence("abc", "abc"), 3);
        check("abc / def (edge: nothing shared)", longestCommonSubsequence("abc", "def"), 0);
        check("a / a (edge: single char)", longestCommonSubsequence("a", "a"), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
