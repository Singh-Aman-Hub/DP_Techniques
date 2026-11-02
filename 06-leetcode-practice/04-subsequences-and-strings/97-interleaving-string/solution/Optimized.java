/**
 * 97. Interleaving String — Space-optimized DP
 *
 * Idea: each cell reads only "above" and "left". With one boolean row,
 * row[j] still holds the above value before it is overwritten and row[j-1]
 * already holds the left value of the current row.
 *
 * Time complexity:  O(n * m) — same loops as tabulation
 * Space complexity: O(m) — one boolean row
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static boolean isInterleave(String s1, String s2, String s3) {
        int n = s1.length(), m = s2.length();
        if (n + m != s3.length()) return false;
        // row[j] = dp[i][j] once written in this pass, dp[i-1][j] before that
        boolean[] row = new boolean[m + 1];
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= m; j++) {
                if (i == 0 && j == 0) {
                    row[0] = true;
                    continue;
                }
                char last = s3.charAt(i + j - 1);
                boolean fromFirst = i > 0 && row[j] && s1.charAt(i - 1) == last;        // above
                boolean fromSecond = j > 0 && row[j - 1] && s2.charAt(j - 1) == last;   // left
                row[j] = fromFirst || fromSecond;
            }
        }
        return row[m];
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
