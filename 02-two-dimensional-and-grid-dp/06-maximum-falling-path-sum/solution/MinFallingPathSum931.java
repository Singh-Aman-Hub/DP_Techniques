/**
 * Minimum Falling Path Sum (LeetCode 931) — Tabulation and two-row optimization
 *
 * Idea: exactly the same pattern as the maximum version in this folder, with
 * max replaced by min. dp[row][col] = smallest sum of a falling path that ends
 * at (row, col); the answer is the smallest value in the last row.
 * The LeetCode judge problem asks for the MINIMUM — this is the file to submit
 * (rename the method to minFallingPathSum inside LeetCode's Solution class).
 *
 * Time complexity:  O(n^2) — n * n cells, each reading at most 3 parents.
 * Space complexity: O(n^2) for the table version, O(n) for the two-row version.
 *
 * Run: java MinFallingPathSum931.java
 */
public class MinFallingPathSum931 {

    static int minFallingPathSumTable(int[][] matrix) {
        int n = matrix.length;

        // dp[row][col] = smallest falling-path sum over paths that end at (row, col)
        int[][] dp = new int[n][n];
        for (int col = 0; col < n; col++) {
            dp[0][col] = matrix[0][col];
        }
        for (int row = 1; row < n; row++) {
            for (int col = 0; col < n; col++) {
                int bestParent = dp[row - 1][col];
                if (col > 0) bestParent = Math.min(bestParent, dp[row - 1][col - 1]);
                if (col < n - 1) bestParent = Math.min(bestParent, dp[row - 1][col + 1]);
                dp[row][col] = matrix[row][col] + bestParent;
            }
        }
        int best = dp[n - 1][0];
        for (int col = 1; col < n; col++) {
            best = Math.min(best, dp[n - 1][col]);
        }
        return best;
    }

    static int minFallingPathSum(int[][] matrix) {
        int n = matrix.length;

        // prev[col] = smallest falling-path sum ending at (row - 1, col)
        int[] prev = matrix[0].clone();
        for (int row = 1; row < n; row++) {
            int[] cur = new int[n];
            for (int col = 0; col < n; col++) {
                int bestParent = prev[col];
                if (col > 0) bestParent = Math.min(bestParent, prev[col - 1]);
                if (col < n - 1) bestParent = Math.min(bestParent, prev[col + 1]);
                cur[col] = matrix[row][col] + bestParent;
            }
            prev = cur;
        }
        int best = prev[0];
        for (int col = 1; col < n; col++) {
            best = Math.min(best, prev[col]);
        }
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][][] inputs = {
            {{2, 1, 3}, {6, 5, 4}, {7, 8, 9}},
            {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}},
            {{5}},
            {{-19, 57}, {-40, -5}}
        };
        String[] labels = {"3x3 example", "1..9 grid", "1x1 grid (edge)", "negatives"};
        int[] expected = {13, 12, 5, -59};
        for (int t = 0; t < inputs.length; t++) {
            check(labels[t] + " (table)", minFallingPathSumTable(inputs[t]), expected[t]);
            check(labels[t] + " (two rows)", minFallingPathSum(inputs[t]), expected[t]);
        }
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
