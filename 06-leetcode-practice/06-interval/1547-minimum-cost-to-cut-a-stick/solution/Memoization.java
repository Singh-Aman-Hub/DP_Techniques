/**
 * 1547. Minimum Cost to Cut a Stick — Memoization (top-down)
 *
 * Idea: after sorting the cuts and adding walls 0 and n, f(i, j) = min cost to
 * make every cut strictly between points[i] and points[j]. Pick the first cut
 * k inside: it costs the whole piece (points[j] - points[i]) and leaves two
 * independent pieces (i, k) and (k, j).
 *
 * Time complexity:  O(m^3) — (m+2)^2/2 pieces, each tries up to m first cuts (m = number of cuts)
 * Space complexity: O(m^2) — memo table over cut indices; recursion depth up to m
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;
public class Memoization {

    // memo[i][j] = min cost to make all cuts between points[i] and points[j]; -1 = not computed
    private static int[][] memo;
    private static int[] points;

    static int minCost(int n, int[] cuts) {
        points = buildPoints(n, cuts);
        int size = points.length;
        memo = new int[size][size];
        for (int[] row : memo) Arrays.fill(row, -1);
        return cost(0, size - 1);
    }

    private static int cost(int i, int j) {
        if (j - i < 2) return 0;                              // no cut strictly inside this piece
        if (memo[i][j] != -1) return memo[i][j];
        int best = Integer.MAX_VALUE;
        for (int k = i + 1; k < j; k++) {                     // k = the first cut made on this piece
            best = Math.min(best, cost(i, k) + cost(k, j));
        }
        memo[i][j] = best + points[j] - points[i];            // the first cut always costs the whole piece
        return memo[i][j];
    }

    // points = [0, sorted cuts..., n]; a piece is "between points[i] and points[j]"
    private static int[] buildPoints(int n, int[] cuts) {
        int[] points = new int[cuts.length + 2];
        int[] sortedCuts = cuts.clone();
        Arrays.sort(sortedCuts);
        for (int c = 0; c < sortedCuts.length; c++) points[c + 1] = sortedCuts[c];
        points[points.length - 1] = n;                        // points[0] = 0 already
        return points;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example n=7 [1,3,4,5]", minCost(7, new int[]{1, 3, 4, 5}), 16);
        check("n=9 [5,6,1,4,2]", minCost(9, new int[]{5, 6, 1, 4, 2}), 22);
        check("n=10 [2,5]", minCost(10, new int[]{2, 5}), 15);
        check("one cut (edge)", minCost(2, new int[]{1}), 2);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
