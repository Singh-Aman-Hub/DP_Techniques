/**
 * 1499. Max Value of Equation — Tabulation (O(n^2) "slow DP" over the best partner)
 *
 * Idea: Points are sorted by x, so for i < j: yi + yj + |xi - xj| = (yj + xj) + (yi - xi).
 *       bestEndingAt[j] = (yj + xj) + max over i < j with xj - xi <= k of (yi - xi).
 *       The answer is the max of bestEndingAt[j]. Scanning i from j-1 backwards and stopping once
 *       xj - xi > k (x only grows) gives each cell in O(window).
 *
 * Time complexity:  O(n^2) in the worst case — each j may scan every earlier point.
 * Space complexity: O(1) besides the input.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int findMaxValueOfEquation(int[][] points, int k) {
        long best = Long.MIN_VALUE;
        for (int j = 1; j < points.length; j++) {
            long bestPartner = Long.MIN_VALUE;        // max of (yi - xi) over the window before j
            for (int i = j - 1; i >= 0 && points[j][0] - points[i][0] <= k; i--) {
                bestPartner = Math.max(bestPartner, (long) points[i][1] - points[i][0]);
            }
            if (bestPartner != Long.MIN_VALUE) best = Math.max(best, points[j][1] + points[j][0] + bestPartner);
        }
        return (int) best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[[1,3],[2,0],[5,10],[6,-10]], k=1", findMaxValueOfEquation(new int[][]{{1, 3}, {2, 0}, {5, 10}, {6, -10}}, 1), 4);
        check("[[0,0],[3,0],[9,2]], k=3", findMaxValueOfEquation(new int[][]{{0, 0}, {3, 0}, {9, 2}}, 3), 3);
        check("[[1,1],[2,2]], k=1 (edge: two points)", findMaxValueOfEquation(new int[][]{{1, 1}, {2, 2}}, 1), 4);
        check("[[-3,5],[-1,-2],[0,4],[2,1],[6,7]], k=4", findMaxValueOfEquation(new int[][]{{-3, 5}, {-1, -2}, {0, 4}, {2, 1}, {6, 7}}, 4), 12); // (-3,5)+(0,4): 9+3, or (2,1)+(6,7): 8+4
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
