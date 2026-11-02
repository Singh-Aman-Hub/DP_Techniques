/**
 * LeetCode 1039 Minimum Score Triangulation of Polygon — Memoization (top-down interval DP)
 *
 * Idea: same recursion as the brute force, f(i, j) over vertices i..j with a split corner k,
 * but every range (i, j) is solved once and stored.
 *
 * Time complexity:  O(n^3) — O(n^2) ranges, each tries O(n) split points.
 * Space complexity: O(n^2) — the memo table (recursion depth is only O(n)).
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i][j]: min score to triangulate vertices i..j; -1 = not computed
    private static int[][] memo;

    static int minScoreTriangulation(int[] values) {
        int n = values.length;
        memo = new int[n][n];
        for (int[] row : memo) Arrays.fill(row, -1);
        return best(values, 0, n - 1);
    }

    private static int best(int[] values, int i, int j) {
        if (j - i < 2) return 0;
        if (memo[i][j] != -1) return memo[i][j];
        int cheapest = Integer.MAX_VALUE;
        for (int k = i + 1; k < j; k++) {
            int cost = best(values, i, k) + best(values, k, j) + values[i] * values[k] * values[j];
            cheapest = Math.min(cheapest, cost);
        }
        return memo[i][j] = cheapest;
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
