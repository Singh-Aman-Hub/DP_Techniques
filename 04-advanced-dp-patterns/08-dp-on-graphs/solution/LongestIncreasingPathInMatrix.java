/**
 * Longest Increasing Path in a Matrix (LeetCode 329) — brute-force DFS vs. memoized DFS
 *
 * Idea: treat each cell as a vertex and draw an edge from a cell to every neighbour
 *       (up/down/left/right) holding a STRICTLY larger value. Values strictly increase
 *       along every edge, so no walk can come back to a cell: the graph is a DAG.
 *       longest[r][c] = 1 + max(longest of larger neighbours), or 1 if there is none.
 *       Memoizing that recursion visits every cell once.
 *
 * Time complexity:  O(R * C) for the memoized DFS — each cell is solved once, 4 neighbours each.
 *                   Exponential for the brute force — it re-explores the same cells from every start.
 * Space complexity: O(R * C) — the memo table, plus a recursion stack up to R * C deep.
 *
 * Run: java LongestIncreasingPathInMatrix.java
 */
public class LongestIncreasingPathInMatrix {

    private static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    // ---------- brute force ----------
    static int bruteForce(int[][] matrix) {
        int best = 0;
        for (int r = 0; r < matrix.length; r++)
            for (int c = 0; c < matrix[0].length; c++)
                best = Math.max(best, explore(matrix, r, c));
        return best;
    }

    private static int explore(int[][] matrix, int r, int c) {
        int best = 1;                                     // the cell alone is a path of length 1
        for (int[] d : DIRECTIONS) {
            int nr = r + d[0], nc = c + d[1];
            if (inside(matrix, nr, nc) && matrix[nr][nc] > matrix[r][c])
                best = Math.max(best, 1 + explore(matrix, nr, nc));
        }
        return best;
    }

    // ---------- memoization ----------
    // memo[r][c] = length (in cells) of the longest increasing path starting at (r, c); 0 = unknown
    private static int[][] memo;

    static int longestIncreasingPath(int[][] matrix) {
        memo = new int[matrix.length][matrix[0].length];
        int best = 0;
        for (int r = 0; r < matrix.length; r++)
            for (int c = 0; c < matrix[0].length; c++)
                best = Math.max(best, longestFrom(matrix, r, c));
        return best;
    }

    private static int longestFrom(int[][] matrix, int r, int c) {
        if (memo[r][c] != 0) return memo[r][c];           // every real answer is >= 1, so 0 means "unknown"
        int best = 1;
        for (int[] d : DIRECTIONS) {
            int nr = r + d[0], nc = c + d[1];
            // strictly larger neighbour only: this is what guarantees there is no cycle
            if (inside(matrix, nr, nc) && matrix[nr][nc] > matrix[r][c])
                best = Math.max(best, 1 + longestFrom(matrix, nr, nc));
        }
        memo[r][c] = best;
        return best;
    }

    private static boolean inside(int[][] matrix, int r, int c) {
        return r >= 0 && r < matrix.length && c >= 0 && c < matrix[0].length;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][][] grids = {
            {{9, 9, 4}, {6, 6, 8}, {2, 1, 1}},
            {{3, 4, 5}, {3, 2, 6}, {2, 2, 1}},
            {{1}},
            {{7, 7}, {7, 7}},
            {{1, 2, 3}, {6, 5, 4}, {7, 8, 9}},
        };
        int[] expected = {4, 4, 1, 1, 9};
        String[] labels = {"example 1", "example 2", "single cell", "all equal", "snake 1..9"};
        for (int t = 0; t < grids.length; t++) {
            check(labels[t] + " memo",  longestIncreasingPath(grids[t]), expected[t]);
            check(labels[t] + " brute", bruteForce(grids[t]), expected[t]);
        }
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
