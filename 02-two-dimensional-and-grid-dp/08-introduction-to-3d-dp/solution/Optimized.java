/**
 * Cherry Pickup II (LeetCode 1463) — Space-optimized DP (two 2D layers)
 *
 * Idea: Layer `row` of the 3D table reads only layer `row + 1`. So we keep just two
 *       cols x cols grids: `below` (layer row + 1, already finished) and `current`
 *       (layer row, being filled). After each row, `current` becomes the new `below`.
 *
 * Time complexity:  O(rows * cols^2 * 9) — unchanged; we still compute every state once.
 * Space complexity: O(cols^2) — two cols x cols layers instead of rows of them.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int cherryPickup(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        // below[col1][col2] = best cherries from row + 1 to the bottom with robots at col1, col2.
        int[][] below = new int[cols][cols];

        // Base layer: the bottom row has no future, only the cherries on the cells.
        for (int col1 = 0; col1 < cols; col1++) {
            for (int col2 = 0; col2 < cols; col2++) {
                below[col1][col2] = grid[rows - 1][col1] + (col1 != col2 ? grid[rows - 1][col2] : 0);
            }
        }

        for (int row = rows - 2; row >= 0; row--) {
            // current[col1][col2] = same meaning, but for this row.
            int[][] current = new int[cols][cols];
            for (int col1 = 0; col1 < cols; col1++) {
                for (int col2 = 0; col2 < cols; col2++) {
                    int here = grid[row][col1] + (col1 != col2 ? grid[row][col2] : 0);
                    int bestFuture = 0;
                    for (int move1 = -1; move1 <= 1; move1++) {
                        for (int move2 = -1; move2 <= 1; move2++) {
                            int next1 = col1 + move1;
                            int next2 = col2 + move2;
                            if (next1 >= 0 && next1 < cols && next2 >= 0 && next2 < cols) {
                                bestFuture = Math.max(bestFuture, below[next1][next2]);
                            }
                        }
                    }
                    current[col1][col2] = here + bestFuture;
                }
            }
            below = current; // this row is finished; it is "below" for the row above
        }
        return below[0][cols - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] example1 = {{3, 1, 1}, {2, 5, 1}, {1, 5, 5}, {2, 1, 1}};
        int[][] example2 = {
            {1, 0, 0, 0, 0, 0, 1},
            {2, 0, 0, 0, 0, 3, 0},
            {2, 0, 9, 0, 0, 0, 0},
            {0, 3, 0, 5, 4, 0, 0},
            {1, 0, 2, 3, 0, 0, 6}
        };
        int[][] tiny = {{1, 1}, {1, 1}};
        int[][] empty = {{0, 0}, {0, 0}};

        check("example 1 (4x3)", cherryPickup(example1), 24);
        check("example 2 (5x7)", cherryPickup(example2), 28);
        check("edge: 2x2 all ones", cherryPickup(tiny), 4);
        check("edge: 2x2 no cherries", cherryPickup(empty), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
