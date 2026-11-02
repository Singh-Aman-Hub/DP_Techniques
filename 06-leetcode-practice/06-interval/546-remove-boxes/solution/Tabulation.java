/**
 * 546. Remove Boxes — Tabulation (bottom-up)
 *
 * Idea: the same 3D recurrence bottom-up. dp[l][r][k] reads only states with a
 * larger left index (l+1 or m), so l runs from n-1 down to 0. k only needs to
 * go up to l, since at most l boxes can sit to the left of l. Extra size
 * keeps empty ranges (value 0) inside the array.
 *
 * Time complexity:  O(n^4) — n^3 states, each scans up to n partners m
 * Space complexity: O(n^3) — the (n+1)^3 table
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int removeBoxes(int[] boxes) {
        int n = boxes.length;
        // dp[l][r][k] = best score for boxes[l..r] with k glued boxes of colour boxes[l]; l > r means 0
        int[][][] dp = new int[n + 1][n + 1][n + 1];
        for (int l = n - 1; l >= 0; l--) {
            for (int r = l; r < n; r++) {
                for (int k = 0; k <= l; k++) {
                    int result = (k + 1) * (k + 1) + dp[l + 1][r][0];       // remove now
                    for (int m = l + 1; m <= r; m++) {
                        if (boxes[m] == boxes[l]) {                        // merge with box m later
                            result = Math.max(result, dp[l + 1][m - 1][0] + dp[m][r][k + 1]);
                        }
                    }
                    dp[l][r][k] = result;
                }
            }
        }
        return dp[0][n - 1][0];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example [1,2,1]", removeBoxes(new int[]{1, 2, 1}), 5);
        check("[1,3,2,2,2,3,4,3,1]", removeBoxes(new int[]{1, 3, 2, 2, 2, 3, 4, 3, 1}), 23);
        check("[1,2,1,2,1]", removeBoxes(new int[]{1, 2, 1, 2, 1}), 11);
        check("all same [1,1,1]", removeBoxes(new int[]{1, 1, 1}), 9);
        check("single box (edge)", removeBoxes(new int[]{1}), 1);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
