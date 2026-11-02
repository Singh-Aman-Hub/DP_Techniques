/**
 * Longest Common Subsequence (LeetCode 1143) — 1D space optimization
 *
 * Idea: Cell (i, j) reads only (i-1, j) "above", (i, j-1) "left" and (i-1, j-1)
 *       "diagonal". Keep ONE array `row` indexed by j. While filling row i from left
 *       to right, row[j] still holds the old value (above) until we overwrite it, and
 *       row[j-1] already holds the new value (left). The only casualty is the diagonal:
 *       the old row[j-1] was overwritten one step earlier, so we save it in a variable
 *       `diagonal` before overwriting. Make text2 the shorter string to use less memory.
 *
 * Time complexity:  O(n * m) — same cells as the 2D table.
 * Space complexity: O(min(n, m)) — one array over the shorter string.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int longestCommonSubsequence(String text1, String text2) {
        if (text2.length() > text1.length()) {      // LCS is symmetric: keep the array small
            String swap = text1; text1 = text2; text2 = swap;
        }
        int m = text2.length();
        int[] row = new int[m + 1];                 // row[j] = dp[current i][j] (or dp[i-1][j] before update)
        for (int i = 1; i <= text1.length(); i++) {
            int diagonal = 0;                       // dp[i-1][j-1]; column 0 is always 0
            for (int j = 1; j <= m; j++) {
                int above = row[j];                 // dp[i-1][j], about to be overwritten
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    row[j] = diagonal + 1;
                } else {
                    row[j] = Math.max(above, row[j - 1]);   // row[j-1] is already dp[i][j-1]
                }
                diagonal = above;                   // becomes dp[i-1][j-1] for the next column
            }
        }
        return row[m];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("abcbdab / bdcaba", longestCommonSubsequence("abcbdab", "bdcaba"), 4);
        check("abcde / ace", longestCommonSubsequence("abcde", "ace"), 3);
        check("abc / abc", longestCommonSubsequence("abc", "abc"), 3);
        check("abc / def (edge: nothing shared)", longestCommonSubsequence("abc", "def"), 0);
        check("a / a (edge: single char)", longestCommonSubsequence("a", "a"), 1);
        check("ace / abcde (shorter first)", longestCommonSubsequence("ace", "abcde"), 3);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
