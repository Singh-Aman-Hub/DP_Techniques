/**
 * LeetCode 1039 Minimum Score Triangulation of Polygon — Tabulation (fill by length)
 *
 * Idea: dp[i][j] depends on dp[i][k] and dp[k][j], which are SHORTER ranges. So we fill
 * the table in order of increasing range length (j - i): first all length-3 ranges,
 * then length 4, and so on up to the whole polygon dp[0][n-1].
 *
 * Time complexity:  O(n^3) — about n^2/2 cells, each scanning up to n split points.
 * Space complexity: O(n^2) — the table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int minScoreTriangulation(int[] values) {
        int n = values.length;
        // dp[i][j]: min score to triangulate the polygon formed by vertices i..j (0 if fewer than 3)
        int[][] dp = new int[n][n];
        for (int length = 3; length <= n; length++) {         // number of vertices in the range
            for (int i = 0; i + length - 1 < n; i++) {
                int j = i + length - 1;
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = i + 1; k < j; k++) {
                    int cost = dp[i][k] + dp[k][j] + values[i] * values[k] * values[j];
                    dp[i][j] = Math.min(dp[i][j], cost);
                }
            }
        }
        return dp[0][n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("values=[1,2,3]", minScoreTriangulation(new int[]{1, 2, 3}), 6);
        check("values=[3,7,4,5]", minScoreTriangulation(new int[]{3, 7, 4, 5}), 144);
        check("values=[1,3,1,4,1,5]", minScoreTriangulation(new int[]{1, 3, 1, 4, 1, 5}), 13);
        check("values=[1,2,3,4]", minScoreTriangulation(new int[]{1, 2, 3, 4}), 18);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
