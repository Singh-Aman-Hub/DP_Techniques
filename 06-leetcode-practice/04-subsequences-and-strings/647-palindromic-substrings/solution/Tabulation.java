/**
 * 647. Palindromic Substrings — Tabulation (bottom-up)
 *
 * Idea: isPal[i][j] = (s[i] == s[j]) && (length <= 2 || isPal[i+1][j-1]).
 * Since (i, j) needs (i+1, j-1), fill i from right to left and j from i upward,
 * counting every true cell.
 *
 * Time complexity:  O(n^2) — the upper triangle of an n x n table, O(1) per cell
 * Space complexity: O(n^2) — the boolean table
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int countSubstrings(String s) {
        int n = s.length();
        // isPal[i][j] = true when s[i..j] reads the same both ways
        boolean[][] isPal = new boolean[n][n];
        int count = 0;
        for (int i = n - 1; i >= 0; i--) {             // larger i first: row i reads row i+1
            for (int j = i; j < n; j++) {
                if (s.charAt(i) == s.charAt(j) && (j - i < 2 || isPal[i + 1][j - 1])) {
                    isPal[i][j] = true;
                    count++;
                }
            }
        }
        return count;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example abba", countSubstrings("abba"), 6);
        check("abc (only singles)", countSubstrings("abc"), 3);
        check("aaa (overlapping)", countSubstrings("aaa"), 6);
        check("single char (edge)", countSubstrings("a"), 1);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
