/**
 * 647. Palindromic Substrings — Memoization (top-down)
 *
 * Idea: isPal(i, j) is true when s[i] == s[j] and the inside s[i+1..j-1] is a
 * palindrome. Cache each answer so every range is checked in O(1) after its
 * inside is known, then count the true ranges.
 *
 * Time complexity:  O(n^2) — n(n+1)/2 ranges, each resolved once with O(1) work
 * Space complexity: O(n^2) — the Boolean memo table; recursion depth at most n/2
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[i][j] = whether s[i..j] is a palindrome; null = not computed yet
    private static Boolean[][] memo;
    private static String text;

    static int countSubstrings(String s) {
        text = s;
        int n = s.length();
        memo = new Boolean[n][n];
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (isPal(i, j)) count++;
            }
        }
        return count;
    }

    private static boolean isPal(int i, int j) {
        if (i >= j) return true;                       // empty or single character
        if (memo[i][j] != null) return memo[i][j];
        boolean result = text.charAt(i) == text.charAt(j) && isPal(i + 1, j - 1);
        memo[i][j] = result;
        return result;
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
