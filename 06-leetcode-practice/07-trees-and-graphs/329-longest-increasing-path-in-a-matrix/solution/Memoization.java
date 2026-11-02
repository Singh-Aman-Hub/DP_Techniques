/**
 * 329. Longest Increasing Path in a Matrix — Memoization (DFS on an implicit DAG)
 *
 * Idea: draw an edge from a cell to each strictly larger neighbour. Values strictly increase
 * along edges, so there are no cycles: the grid is a DAG. longestFrom(r,c) = 1 + the best
 * longestFrom of a larger neighbour. Each cell's answer is cached, so it is computed once.
 *
 * Time complexity:  O(m*n) — each cell is solved once and checks 4 neighbours.
 * Space complexity: O(m*n) — the cache, plus a recursion stack up to m*n deep.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    private static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private static int[][] grid;
    // pathLength[r][c] = cells on the longest increasing path starting at (r,c); 0 = not computed
    private static int[][] pathLength;

    static int longestIncreasingPath(int[][] matrix) {
        grid = matrix;
        pathLength = new int[matrix.length][matrix[0].length];
        int best = 0;
        for (int r = 0; r < matrix.length; r++)
            for (int c = 0; c < matrix[0].length; c++)
                best = Math.max(best, longestFrom(r, c));
        return best;
    }

    private static int longestFrom(int row, int col) {
        if (pathLength[row][col] != 0) return pathLength[row][col];
        int best = 1;                                   // the path consisting of this cell alone
        for (int[] d : DIRECTIONS) {
            int nextRow = row + d[0], nextCol = col + d[1];
            boolean inside = nextRow >= 0 && nextRow < grid.length && nextCol >= 0 && nextCol < grid[0].length;
            // strictly greater: guarantees we never loop back, so no visited set is needed
            if (inside && grid[nextRow][nextCol] > grid[row][col]) {
                best = Math.max(best, 1 + longestFrom(nextRow, nextCol));
            }
        }
        pathLength[row][col] = best;
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
