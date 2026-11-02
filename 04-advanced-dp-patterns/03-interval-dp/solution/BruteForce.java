/**
 * LeetCode 1039 Minimum Score Triangulation of Polygon — Brute force recursion
 *
 * Idea: f(i, j) = cheapest triangulation of the sub-polygon made of vertices i..j.
 * The edge (i, j) must belong to exactly one triangle (i, k, j) with i < k < j. Trying
 * every k splits the polygon into two independent smaller polygons i..k and k..j.
 * Without caching, the same ranges are solved again and again.
 *
 * Time complexity:  exponential (roughly Catalan-number growth) — each call branches on every k
 *                   and the same (i, j) ranges are recomputed many times.
 * Space complexity: O(n) — recursion depth.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int minScoreTriangulation(int[] values) {
        return best(values, 0, values.length - 1);
    }

    private static int best(int[] values, int i, int j) {
        if (j - i < 2) return 0; // fewer than 3 vertices: no triangle to pay for
        int cheapest = Integer.MAX_VALUE;
        for (int k = i + 1; k < j; k++) { // k = third corner of the triangle on edge (i, j)
            int cost = best(values, i, k) + best(values, k, j) + values[i] * values[k] * values[j];
            cheapest = Math.min(cheapest, cost);
        }
        return cheapest;
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
