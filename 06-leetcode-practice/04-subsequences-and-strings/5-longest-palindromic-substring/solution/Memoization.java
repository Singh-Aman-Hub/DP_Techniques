/**
 * 5. Longest Palindromic Substring — Memoization (top-down)
 *
 * Idea: isPal(i, j) = s[i] == s[j] && isPal(i+1, j-1), cached per range.
 * Try every range and keep the longest one that is a palindrome.
 *
 * Time complexity:  O(n^2) — every range resolved once with O(1) work
 * Space complexity: O(n^2) — the Boolean memo table; recursion depth at most n/2
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[i][j] = whether s[i..j] is a palindrome; null = not computed yet
    private static Boolean[][] memo;
    private static String text;

    static String longestPalindrome(String s) {
        text = s;
        int n = s.length();
        memo = new Boolean[n][n];
        int bestStart = 0, bestLength = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (j - i + 1 > bestLength && isPal(i, j)) {
                    bestStart = i;
                    bestLength = j - i + 1;
                }
            }
        }
        return s.substring(bestStart, bestStart + bestLength);
    }

    private static boolean isPal(int i, int j) {
        if (i >= j) return true;
        if (memo[i][j] != null) return memo[i][j];
        boolean result = text.charAt(i) == text.charAt(j) && isPal(i + 1, j - 1);
        memo[i][j] = result;
        return result;
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
