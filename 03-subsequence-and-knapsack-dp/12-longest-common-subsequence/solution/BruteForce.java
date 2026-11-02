/**
 * Longest Common Subsequence (LeetCode 1143) — Brute force recursion
 *
 * Idea: lcs(i, j) looks at the prefixes text1[0..i) and text2[0..j) and compares
 *       their LAST characters. If they match, that character can end the common
 *       subsequence: 1 + lcs(i-1, j-1). If not, at least one of the two last
 *       characters is useless, so try dropping each: max(lcs(i-1, j), lcs(i, j-1)).
 *
 * Time complexity:  O(2^(n+m)) — on mismatches the call branches in two and each
 *                   branch shortens only one string by one character.
 * Space complexity: O(n + m) — recursion depth.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int longestCommonSubsequence(String text1, String text2) {
        return lcs(text1, text2, text1.length(), text2.length());
    }

    // LCS length of the prefixes text1[0..i) and text2[0..j)
    private static int lcs(String text1, String text2, int i, int j) {
        if (i == 0 || j == 0) return 0;   // an empty prefix shares nothing
        if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
            return 1 + lcs(text1, text2, i - 1, j - 1);
        }
        return Math.max(lcs(text1, text2, i - 1, j), lcs(text1, text2, i, j - 1));
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
