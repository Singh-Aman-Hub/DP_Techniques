/**
 * 1039. Minimum Score Triangulation of Polygon — Memoization (top-down)
 *
 * Idea: the edge (i, j) of the sub-polygon i..j belongs to exactly one
 * triangle (i, k, j). Choosing k splits the polygon into i..k and k..j, solved
 * independently: f(i, j) = min over k of f(i, k) + f(k, j) + v[i]*v[k]*v[j].
 *
 * Time complexity:  O(n^3) — about n^2/2 states, each tries up to n split points
 * Space complexity: O(n^2) — memo table; recursion depth up to n
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[i][j] = min score for the polygon of vertices i..j; 0 = not computed
    // (safe: real polygons with 3+ vertices score at least 1, edges return before lookup)
    private static int[][] memo;
    private static int[] labels;

    static int minScoreTriangulation(int[] values) {
        labels = values;
        memo = new int[values.length][values.length];
        return minScore(0, values.length - 1);
    }

    private static int minScore(int i, int j) {
        if (j - i < 2) return 0;                               // only an edge, no triangle
        if (memo[i][j] != 0) return memo[i][j];
        int best = Integer.MAX_VALUE;
        for (int k = i + 1; k < j; k++) {                      // third vertex of the triangle on edge i-j
            int cost = minScore(i, k) + minScore(k, j) + labels[i] * labels[k] * labels[j];
            best = Math.min(best, cost);
        }
        memo[i][j] = best;
        return best;
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
