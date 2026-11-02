/**
 * Minimum Path Sum (LeetCode 64) — Tabulation (bottom-up) with route reconstruction
 *
 * Idea: fill dp[r][c] = grid[r][c] + min(dp[r-1][c], dp[r][c-1]) row by row, left to
 *       right. The top row and left column are running sums (only one way in).
 *       Keeping the full table lets us walk back from the end, always stepping to the
 *       predecessor with the smaller value, to recover the cheapest route itself.
 *
 * Time complexity:  O(m * n) — one pass, O(1) per cell; reconstruction is O(m + n).
 * Space complexity: O(m * n) — the full table.
 *
 * Run: java Tabulation.java
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tabulation {

    static int[][] buildTable(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        // dp[r][c] = minimum cost of a right/down route from (0,0) to (r,c), both ends included
        int[][] dp = new int[m][n];
        dp[0][0] = grid[0][0];
        for (int c = 1; c < n; c++) dp[0][c] = dp[0][c - 1] + grid[0][c];   // top row: running sum
        for (int r = 1; r < m; r++) dp[r][0] = dp[r - 1][0] + grid[r][0];   // left column: running sum
        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                dp[r][c] = grid[r][c] + Math.min(dp[r - 1][c], dp[r][c - 1]);
            }
        }
        return dp;
    }

    static int minPathSum(int[][] grid) {
        int[][] dp = buildTable(grid);
        return dp[grid.length - 1][grid[0].length - 1];
    }

    /** Cell values along one cheapest route, from the start to the end, separated by spaces. */
    static String bestRoute(int[][] grid) {
        int[][] dp = buildTable(grid);
        int r = grid.length - 1, c = grid[0].length - 1;
        List<Integer> reversed = new ArrayList<>();
        while (true) {
            reversed.add(grid[r][c]);
            if (r == 0 && c == 0) break;
            if (r == 0) c--;                                  // top row: only from the left
            else if (c == 0) r--;                             // left column: only from above
            else if (dp[r - 1][c] <= dp[r][c - 1]) r--;       // the cheaper predecessor produced dp[r][c]
            else c--;
        }
        Collections.reverse(reversed);
        StringBuilder sb = new StringBuilder();
        for (int value : reversed) sb.append(sb.length() == 0 ? "" : " ").append(value);
        return sb.toString();
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("3x3 running example", minPathSum(new int[][]{{1, 3, 1}, {1, 5, 1}, {4, 2, 1}}), 7);
        check("2x3 [[1,2,3],[4,5,6]]", minPathSum(new int[][]{{1, 2, 3}, {4, 5, 6}}), 12);
        check("2x2 [[1,2],[1,1]]", minPathSum(new int[][]{{1, 2}, {1, 1}}), 3);
        check("single row [[1,2,3]]", minPathSum(new int[][]{{1, 2, 3}}), 6);
        check("single column [[2],[3],[4]]", minPathSum(new int[][]{{2}, {3}, {4}}), 9);
        check("single cell [[5]]", minPathSum(new int[][]{{5}}), 5);
        check("all zeros 2x2", minPathSum(new int[][]{{0, 0}, {0, 0}}), 0);
        checkText("route of running example", bestRoute(new int[][]{{1, 3, 1}, {1, 5, 1}, {4, 2, 1}}), "1 3 1 1 1");
        checkText("route of [[1,2,3],[4,5,6]]", bestRoute(new int[][]{{1, 2, 3}, {4, 5, 6}}), "1 2 3 6");
    }

    private static void checkText(String label, String actual, String expected) {
        String status = actual.equals(expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!actual.equals(expected)) System.exit(1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
