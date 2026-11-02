/**
 * Distinct Subsequences (LeetCode 115) — Brute force recursion
 *
 * Idea: ways(i, j) = number of ways to pick t[0..j) as a subsequence of s[0..i).
 *       Look at the last character of s's prefix, s[i-1]:
 *         - we can always SKIP it: ways(i-1, j);
 *         - if s[i-1] == t[j-1] we can also USE it to match t's last character:
 *           ways(i-1, j-1).
 *       The two options produce different index choices, so their counts ADD.
 *
 * Time complexity:  O(2^n) — on a match each call branches into two.
 * Space complexity: O(n) — recursion depth (i drops by 1 per call).
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static long numDistinct(String s, String t) {
        return ways(s, t, s.length(), t.length());
    }

    private static long ways(String s, String t, int i, int j) {
        if (j == 0) return 1;      // empty target: choose nothing, exactly one way
        if (i == 0) return 0;      // target left but source used up
        long skip = ways(s, t, i - 1, j);
        long use = 0;
        if (s.charAt(i - 1) == t.charAt(j - 1)) use = ways(s, t, i - 1, j - 1);
        return skip + use;
    }

    // ---------- self-check ----------  (small inputs only: this is exponential)
    public static void main(String[] args) {
        check("rabbbit / rabbit", numDistinct("rabbbit", "rabbit"), 3);
        check("babgbag / bag", numDistinct("babgbag", "bag"), 5);
        check("abc / \"\" (edge: empty t appears once)", numDistinct("abc", ""), 1);
        check("ab / abc (edge: t longer than s)", numDistinct("ab", "abc"), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
