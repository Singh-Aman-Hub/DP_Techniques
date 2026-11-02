/**
 * 1289. Minimum Falling Path Sum II — Optimized (best and second-best of the previous row)
 *
 * Idea: "min over every previous column except c" is the row minimum, unless c IS the column of
 * that minimum, in which case it is the second-smallest value. Keep just those three numbers per
 * row (smallest, its column, second smallest) so each cell costs O(1).
 *
 * Time complexity:  O(n^2) — each cell once, O(1) work.
 * Space complexity: O(1) — six integers, no table.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int minFallingPathSum(int[][] grid) {
        int n = grid.length;
        // summary of the previous row's dp values
        int smallest = 0, smallestColumn = -1, secondSmallest = 0;   // "row -1" contributes 0
        for (int r = 0; r < n; r++) {
            int newSmallest = Integer.MAX_VALUE, newSmallestColumn = -1, newSecond = Integer.MAX_VALUE;
            for (int c = 0; c < n; c++) {
                int bestPrevious = (c == smallestColumn) ? secondSmallest : smallest;
                int value = grid[r][c] + bestPrevious;               // dp[r][c]
                if (value < newSmallest) {
                    newSecond = newSmallest;
                    newSmallest = value;
                    newSmallestColumn = c;
                } else if (value < newSecond) {
                    newSecond = value;
                }
            }
            smallest = newSmallest;
            smallestColumn = newSmallestColumn;
            secondSmallest = newSecond;
        }
        return smallest;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("3x3 example", minFallingPathSum(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}), 13);
        check("1x1 (edge)", minFallingPathSum(new int[][]{{7}}), 7);
        check("2x2", minFallingPathSum(new int[][]{{1, 2}, {3, 4}}), 5);
        check("repeated minimum", minFallingPathSum(new int[][]{{2, 2, 1, 2, 2}, {2, 2, 1, 2, 2}, {2, 2, 1, 2, 2}, {2, 1, 2, 2, 2}, {2, 2, 2, 2, 2}}), 7);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
