/**
 * 97. Interleaving String — Tabulation (bottom-up)
 *
 * Idea: grid reachability. dp[i][j] is true when the cell above is true and
 * s1's next char matches s3, or the cell to the left is true and s2's next char
 * matches. Start from dp[0][0] = true; answer dp[n][m].
 *
 * Time complexity:  O(n * m) — every cell filled once in O(1)
 * Space complexity: O(n * m) — the boolean grid
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static boolean isInterleave(String s1, String s2, String s3) {
        int n = s1.length(), m = s2.length();
        if (n + m != s3.length()) return false;
        // dp[i][j] = s1[0..i-1] and s2[0..j-1] can interleave into s3[0..i+j-1]
        boolean[][] dp = new boolean[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= m; j++) {
                if (i == 0 && j == 0) {
                    dp[0][0] = true;
                    continue;
                }
                char last = s3.charAt(i + j - 1);
                boolean fromFirst = i > 0 && dp[i - 1][j] && s1.charAt(i - 1) == last;
                boolean fromSecond = j > 0 && dp[i][j - 1] && s2.charAt(j - 1) == last;
                dp[i][j] = fromFirst || fromSecond;
            }
        }
        return dp[n][m];
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
