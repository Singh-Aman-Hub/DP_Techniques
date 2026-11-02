/**
 * 97. Interleaving String — Memoization (top-down)
 *
 * Idea: f(i, j) = can the first i chars of s1 and first j chars of s2 form the
 * first i + j chars of s3? The last char s3[i+j-1] came either from s1 or from
 * s2; try both. The position in s3 is implied, so (i, j) is the whole state.
 *
 * Time complexity:  O(n * m) — (n+1)*(m+1) states, O(1) work each
 * Space complexity: O(n * m) — Boolean memo table; recursion depth up to n + m
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[i][j] = whether s1[0..i-1] and s2[0..j-1] interleave into s3[0..i+j-1]; null = unknown
    private static Boolean[][] memo;
    private static String first, second, target;

    static boolean isInterleave(String s1, String s2, String s3) {
        if (s1.length() + s2.length() != s3.length()) return false;   // every char must be used
        first = s1;
        second = s2;
        target = s3;
        memo = new Boolean[s1.length() + 1][s2.length() + 1];
        return canForm(s1.length(), s2.length());
    }

    private static boolean canForm(int i, int j) {
        if (i == 0 && j == 0) return true;                // empty + empty = empty
        if (memo[i][j] != null) return memo[i][j];
        char last = target.charAt(i + j - 1);
        boolean result = (i > 0 && first.charAt(i - 1) == last && canForm(i - 1, j))
                      || (j > 0 && second.charAt(j - 1) == last && canForm(i, j - 1));
        memo[i][j] = result;
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example ab / c -> acb", isInterleave("ab", "c", "acb"), true);
        check("aabcc / dbbca -> aadbbcbcac", isInterleave("aabcc", "dbbca", "aadbbcbcac"), true);
        check("aabcc / dbbca -> aadbbbaccc", isInterleave("aabcc", "dbbca", "aadbbbaccc"), false);
        check("greedy trap ab / ac -> acab", isInterleave("ab", "ac", "acab"), true);
        check("length mismatch", isInterleave("a", "b", "a"), false);
        check("all empty (edge)", isInterleave("", "", ""), true);
    }

    private static void check(String label, boolean actual, boolean expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
