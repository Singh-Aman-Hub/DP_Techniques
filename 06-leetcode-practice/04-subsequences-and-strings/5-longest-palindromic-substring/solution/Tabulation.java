/**
 * 5. Longest Palindromic Substring — Tabulation (bottom-up)
 *
 * Idea: fill isPal[i][j] exactly as in 647 (i descending, j ascending) and
 * remember the start and length of the longest true cell.
 *
 * Time complexity:  O(n^2) — upper triangle of the table, O(1) per cell
 * Space complexity: O(n^2) — the boolean table
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static String longestPalindrome(String s) {
        int n = s.length();
        // isPal[i][j] = true when s[i..j] is a palindrome
        boolean[][] isPal = new boolean[n][n];
        int bestStart = 0, bestLength = 0;
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                if (s.charAt(i) == s.charAt(j) && (j - i < 2 || isPal[i + 1][j - 1])) {
                    isPal[i][j] = true;
                    if (j - i + 1 > bestLength) {
                        bestStart = i;
                        bestLength = j - i + 1;
                    }
                }
            }
        }
        return s.substring(bestStart, bestStart + bestLength);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example cbbd", longestPalindrome("cbbd"), "bb");
        check("racecarxyz (odd length)", longestPalindrome("racecarxyz"), "racecar");
        check("forgeeksskeegfor (even length)", longestPalindrome("forgeeksskeegfor"), "geeksskeeg");
        check("single char (edge)", longestPalindrome("a"), "a");
    }

    private static void check(String label, String actual, String expected) {
        boolean ok = actual.equals(expected);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
