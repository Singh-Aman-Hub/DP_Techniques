/**
 * 1039. Minimum Score Triangulation of Polygon — Tabulation (bottom-up)
 *
 * Idea: dp[i][j] = min over k in (i, j) of dp[i][k] + dp[k][j] + v[i]*v[k]*v[j].
 * Both sides are shorter ranges, so fill by increasing gap j - i, starting from
 * gap 2 (a single triangle). Gap 1 (an edge) stays 0.
 *
 * Time complexity:  O(n^3) — gaps x start positions x split points
 * Space complexity: O(n^2) — the n x n table
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int minScoreTriangulation(int[] values) {
        int n = values.length;
        // dp[i][j] = minimum triangulation score of the polygon with vertices i..j
        int[][] dp = new int[n][n];
        for (int gap = 2; gap < n; gap++) {
            for (int i = 0; i + gap < n; i++) {
                int j = i + gap;
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
        check("example [3,7,4,5]", minScoreTriangulation(new int[]{3, 7, 4, 5}), 144);
        check("hexagon [1,3,1,4,1,5]", minScoreTriangulation(new int[]{1, 3, 1, 4, 1, 5}), 13);
        check("square [2,2,2,2]", minScoreTriangulation(new int[]{2, 2, 2, 2}), 16);
        check("triangle (edge)", minScoreTriangulation(new int[]{1, 2, 3}), 6);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
