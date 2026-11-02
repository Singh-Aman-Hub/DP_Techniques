/**
 * Longest Palindromic Subsequence (LeetCode 516) — O(n) space (two rows)
 *
 * Idea: Row i of the interval table reads only row i+1 (cells j and j-1) and row i
 *       itself (cell j-1). So keep `below` = row i+1 and `current` = row i, then swap.
 *
 * Time complexity:  O(n^2) — same cells as the 2D table.
 * Space complexity: O(n) — two arrays of length n.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int longestPalindromeSubseq(String s) {
        int n = s.length();
        int[] below = new int[n];     // below[j]   = dp[i+1][j]
        int[] current = new int[n];   // current[j] = dp[i][j]
        for (int i = n - 1; i >= 0; i--) {
            current[i] = 1;
            for (int j = i + 1; j < n; j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    // dp[i+1][j-1]; when j == i+1 the window is empty. below[i] still holds
                    // a stale value, so handle that case explicitly.
                    int inner = (j - 1 >= i + 1) ? below[j - 1] : 0;
                    current[j] = 2 + inner;
                } else {
                    current[j] = Math.max(below[j], current[j - 1]);
                }
            }
            int[] swap = below; below = current; current = swap;   // row i becomes "below" for i-1
        }
        return below[n - 1];
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
