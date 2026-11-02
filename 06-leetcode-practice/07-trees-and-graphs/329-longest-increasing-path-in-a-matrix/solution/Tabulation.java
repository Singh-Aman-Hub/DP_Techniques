/**
 * 329. Longest Increasing Path in a Matrix — Tabulation (topological order by value)
 *
 * Idea: a cell depends only on strictly larger neighbours. Sorting cells by value, largest
 * first, is therefore a valid topological order: when we reach a cell, all cells it depends
 * on are finished. Equal values never depend on each other, so ties can go in any order.
 *
 * Time complexity:  O(m*n log(m*n)) — sorting the cells; the DP pass itself is O(m*n).
 * Space complexity: O(m*n) — the table and the list of cells.
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;

public class Tabulation {

    private static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    static int longestIncreasingPath(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        // cells[k] = {value, row, col}, sorted by value descending
        int[][] cells = new int[rows * cols][];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) cells[r * cols + c] = new int[]{grid[r][c], r, c};
        Arrays.sort(cells, (a, b) -> Integer.compare(b[0], a[0]));

        // pathLength[r][c] = cells on the longest increasing path starting at (r,c)
        int[][] pathLength = new int[rows][cols];
        int best = 0;
        for (int[] cell : cells) {
            int row = cell[1], col = cell[2];
            int length = 1;
            for (int[] d : DIRECTIONS) {
                int nr = row + d[0], nc = col + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] > grid[row][col]) {
                    length = Math.max(length, 1 + pathLength[nr][nc]);   // already final: larger value
                }
            }
            pathLength[row][col] = length;
            best = Math.max(best, length);
        }
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[[9,9,4],[6,6,8],[2,1,1]]", longestIncreasingPath(new int[][]{{9, 9, 4}, {6, 6, 8}, {2, 1, 1}}), 4);
        check("[[3,4,5],[3,2,6],[2,2,1]]", longestIncreasingPath(new int[][]{{3, 4, 5}, {3, 2, 6}, {2, 2, 1}}), 4);
        check("[[1]] (edge: single cell)", longestIncreasingPath(new int[][]{{1}}), 1);
        check("all equal values", longestIncreasingPath(new int[][]{{7, 7}, {7, 7}}), 1);
        // snake 1..9 through a 3x3 grid: every cell is on one increasing path
        check("snake 1..9", longestIncreasingPath(new int[][]{{1, 2, 3}, {6, 5, 4}, {7, 8, 9}}), 9);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
