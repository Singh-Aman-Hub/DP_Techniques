/**
 * Longest Common Subsequence (LeetCode 1143) — Tabulation + rebuilding the LCS string
 *
 * Idea: dp[i][j] = LCS length of text1[0..i) and text2[0..j). Row 0 and column 0 are 0
 *       (an empty prefix). Each cell needs the cells above, to the left and diagonally
 *       up-left, so filling rows top to bottom and each row left to right works.
 *       To print one LCS, start at dp[n][m] and walk back: on a character match step
 *       diagonally and record the character; otherwise step toward the neighbour that
 *       holds the larger value (up on ties).
 *
 * Time complexity:  O(n * m) — one O(1) cell per pair (i, j); the walk back is O(n + m).
 * Space complexity: O(n * m) — the full table is needed to walk back.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    // dp[i][j] = LCS length of text1[0..i) and text2[0..j)
    static int[][] buildTable(String text1, String text2) {
        int n = text1.length(), m = text2.length();
        int[][] dp = new int[n + 1][m + 1];          // row 0 and column 0 stay 0
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;                   // use the matching pair
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);   // drop one last character
                }
            }
        }
        return dp;
    }

    static int longestCommonSubsequence(String text1, String text2) {
        return buildTable(text1, text2)[text1.length()][text2.length()];
    }

    static String lcsString(String text1, String text2) {
        int[][] dp = buildTable(text1, text2);
        StringBuilder reversed = new StringBuilder();
        int i = text1.length(), j = text2.length();
        while (i > 0 && j > 0) {
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                reversed.append(text1.charAt(i - 1));   // this pair is part of the LCS
                i--;
                j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) {
                i--;                                    // the value came from above
            } else {
                j--;                                    // the value came from the left
            }
        }
        return reversed.reverse().toString();
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("abcbdab / bdcaba", longestCommonSubsequence("abcbdab", "bdcaba"), 4);
        check("abcde / ace", longestCommonSubsequence("abcde", "ace"), 3);
        check("abc / abc", longestCommonSubsequence("abc", "abc"), 3);
        check("abc / def (edge: nothing shared)", longestCommonSubsequence("abc", "def"), 0);
        check("a / a (edge: single char)", longestCommonSubsequence("a", "a"), 1);

        checkString("LCS string abcbdab / bdcaba", lcsString("abcbdab", "bdcaba"), "bcba");
        checkString("LCS string abcde / ace", lcsString("abcde", "ace"), "ace");
        checkString("LCS string abc / def", lcsString("abc", "def"), "");
    }

    private static void checkString(String label, String actual, String expected) {
        boolean ok = actual.equals(expected);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> \"" + actual + "\" (expected \"" + expected + "\")");
        if (!ok) System.exit(1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
