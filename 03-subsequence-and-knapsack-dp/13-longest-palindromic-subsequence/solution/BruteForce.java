/**
 * Longest Palindromic Subsequence (LeetCode 516) — Brute force recursion (interval form)
 *
 * Idea: lps(i, j) = longest palindromic subsequence inside s[i..j]. Look at the two
 *       ends. If s[i] == s[j] they can be the outer pair of a palindrome: 2 + lps(i+1, j-1).
 *       Otherwise they cannot BOTH be used, so drop one end: max(lps(i+1, j), lps(i, j-1)).
 *
 * Time complexity:  O(2^n) — on mismatches each call branches into two.
 * Space complexity: O(n) — recursion depth (the window shrinks by at least 1 per call).
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int longestPalindromeSubseq(String s) {
        return lps(s, 0, s.length() - 1);
    }

    // Longest palindromic subsequence of the window s[i..j] (inclusive)
    private static int lps(String s, int i, int j) {
        if (i > j) return 0;     // empty window
        if (i == j) return 1;    // one character is a palindrome
        if (s.charAt(i) == s.charAt(j)) return 2 + lps(s, i + 1, j - 1);
        return Math.max(lps(s, i + 1, j), lps(s, i, j - 1));
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
