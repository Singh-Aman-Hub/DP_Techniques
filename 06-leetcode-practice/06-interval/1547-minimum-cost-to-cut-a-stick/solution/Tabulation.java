/**
 * 1547. Minimum Cost to Cut a Stick — Tabulation (bottom-up)
 *
 * Idea: dp[i][j] = (points[j] - points[i]) + min over i < k < j of
 * dp[i][k] + dp[k][j]. Neighbouring points need no cut (dp = 0). Fill by
 * increasing gap j - i so both halves are ready.
 *
 * Time complexity:  O(m^3) — gap x start x first cut, after an O(m log m) sort
 * Space complexity: O(m^2) — the table over cut indices (independent of n)
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;
public class Tabulation {

    static int minCost(int n, int[] cuts) {
        int[] points = buildPoints(n, cuts);
        int size = points.length;
        // dp[i][j] = min cost to make every cut strictly between points[i] and points[j]
        int[][] dp = new int[size][size];                     // gap 1 (neighbours) stays 0
        for (int gap = 2; gap < size; gap++) {
            for (int i = 0; i + gap < size; i++) {
                int j = i + gap;
                int best = Integer.MAX_VALUE;
                for (int k = i + 1; k < j; k++) {
                    best = Math.min(best, dp[i][k] + dp[k][j]);
                }
                dp[i][j] = best + points[j] - points[i];
            }
        }
        return dp[0][size - 1];
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
