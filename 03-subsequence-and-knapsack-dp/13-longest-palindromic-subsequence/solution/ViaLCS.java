/**
 * Longest Palindromic Subsequence via LCS(s, reverse(s)) + Minimum Insertions (LeetCode 1312)
 *
 * Idea: A palindrome reads the same forwards and backwards, so a palindromic subsequence
 *       of s is also a subsequence of reverse(s): it is COMMON to both. Conversely the
 *       longest common subsequence of s and reverse(s) has the same length as the LPS
 *       (a standard result; see the walkthrough). So LPS = LCS(s, reverse(s)).
 *       Minimum insertions to make s a palindrome = n - LPS: keep the LPS as the
 *       "skeleton" and insert a mirror partner for every other character.
 *
 * Time complexity:  O(n^2) — an n x n LCS table.
 * Space complexity: O(n) — rolling single-row LCS.
 *
 * Run: java ViaLCS.java
 */
public class ViaLCS {

    static int longestPalindromeSubseq(String s) {
        String reversed = new StringBuilder(s).reverse().toString();
        return lcs(s, reversed);
    }

    static int minInsertions(String s) {
        return s.length() - longestPalindromeSubseq(s);
    }

    // 1D LCS (see topic 12, Optimized.java)
    private static int lcs(String a, String b) {
        int[] row = new int[b.length() + 1];   // row[j] = LCS of a[0..i) and b[0..j)
        for (int i = 1; i <= a.length(); i++) {
            int diagonal = 0;
            for (int j = 1; j <= b.length(); j++) {
                int above = row[j];
                row[j] = (a.charAt(i - 1) == b.charAt(j - 1)) ? diagonal + 1 : Math.max(above, row[j - 1]);
                diagonal = above;
            }
        }
        return row[b.length()];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("bbbab", longestPalindromeSubseq("bbbab"), 4);
        check("cbbd", longestPalindromeSubseq("cbbd"), 2);
        check("agbdba", longestPalindromeSubseq("agbdba"), 5);
        check("abcd (no repeats)", longestPalindromeSubseq("abcd"), 1);
        check("a (edge: single char)", longestPalindromeSubseq("a"), 1);
        check("min insertions zzazz", minInsertions("zzazz"), 0);
        check("min insertions mbadm", minInsertions("mbadm"), 2);
        check("min insertions leetcode", minInsertions("leetcode"), 5);
        check("min insertions a (edge)", minInsertions("a"), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
