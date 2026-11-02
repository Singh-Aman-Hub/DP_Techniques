/**
 * Distinct Subsequences (LeetCode 115) — 1D DP, j walks RIGHT TO LEFT
 *
 * Idea: Row i reads only row i-1, at columns j and j-1. With a single array ways[j],
 *       updating j from m DOWN to 1 means ways[j-1] still holds the OLD (row i-1)
 *       value when we read it — exactly like 0/1 knapsack, because each character of
 *       s may be used at most once. Going left to right would let one s character
 *       match two t positions.
 *
 * Time complexity:  O(n * m) — n passes over m columns.
 * Space complexity: O(m) — one array of length m + 1.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static long numDistinct(String s, String t) {
        int m = t.length();
        long[] ways = new long[m + 1];   // ways[j] = ways to form t[0..j) from the prefix of s seen so far
        ways[0] = 1;
        for (int i = 1; i <= s.length(); i++) {
            char current = s.charAt(i - 1);
            for (int j = m; j >= 1; j--) {           // right to left: ways[j-1] is still row i-1
                if (current == t.charAt(j - 1)) ways[j] += ways[j - 1];
            }
        }
        return ways[m];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("rabbbit / rabbit", numDistinct("rabbbit", "rabbit"), 3);
        check("babgbag / bag", numDistinct("babgbag", "bag"), 5);
        check("abc / \"\" (edge: empty t appears once)", numDistinct("abc", ""), 1);
        check("ab / abc (edge: t longer than s)", numDistinct("ab", "abc"), 0);
        check("30 a's / 15 a's = C(30,15)", numDistinct("a".repeat(30), "a".repeat(15)), 155117520);
        // An intermediate cell holds C(40,20) = 137,846,528,820 (> Integer.MAX_VALUE),
        // but it never feeds the final cell, whose true value is 0.
        check("b + 40 a's / 20 a's + b (big intermediate, answer 0)",
                numDistinct("b" + "a".repeat(40), "a".repeat(20) + "b"), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
