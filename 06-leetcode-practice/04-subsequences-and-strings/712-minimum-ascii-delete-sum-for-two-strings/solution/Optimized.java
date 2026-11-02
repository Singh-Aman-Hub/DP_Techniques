/**
 * 712. Minimum ASCII Delete Sum for Two Strings — Space-optimized DP
 *
 * Idea: same recurrence with one rolling row. row[0] becomes the running ASCII
 * sum of s1's prefix at the start of each row; the diagonal is saved before
 * each overwrite.
 *
 * Time complexity:  O(n * m) — same loops as tabulation
 * Space complexity: O(m) — one row plus a saved diagonal
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int minimumDeleteSum(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        // row[j] = dp[i][j] once written in this pass, dp[i-1][j] before that
        int[] row = new int[m + 1];
        for (int j = 1; j <= m; j++) row[j] = row[j - 1] + s2.charAt(j - 1);
        for (int i = 1; i <= n; i++) {
            char last1 = s1.charAt(i - 1);
            int diagonal = row[0];                        // dp[i-1][0]
            row[0] += last1;                              // dp[i][0] = ASCII sum of s1[0..i-1]
            for (int j = 1; j <= m; j++) {
                char last2 = s2.charAt(j - 1);
                int above = row[j];                       // dp[i-1][j]
                if (last1 == last2) {
                    row[j] = diagonal;
                } else {
                    row[j] = Math.min(last1 + above, last2 + row[j - 1]);
                }
                diagonal = above;
            }
        }
        return row[m];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example sea / eat", minimumDeleteSum("sea", "eat"), 231);
        check("delete / leet", minimumDeleteSum("delete", "leet"), 403);
        check("identical strings", minimumDeleteSum("abc", "abc"), 0);
        check("nothing in common (edge)", minimumDeleteSum("a", "b"), 195);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
