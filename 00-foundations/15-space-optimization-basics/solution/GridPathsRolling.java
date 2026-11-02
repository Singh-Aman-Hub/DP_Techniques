/**
 * Space optimization basics — grid paths with two rows, then one row
 *
 * Idea: paths[r][c] = paths[r-1][c] + paths[r][c-1] reads only the row above
 * and the cell to the left in the current row. So we need at most two rows
 * (previous and current). Going further: if we overwrite a single row from
 * left to right, then at the moment we update row[c]
 *   - row[c]   still holds the value from the row above (not yet overwritten),
 *   - row[c-1] already holds the new value for the current row,
 * which is exactly what the recurrence needs.
 *
 * Time complexity:  O(rows * cols) — every cell is computed once, O(1) work each.
 * Space complexity: full table O(rows * cols); two rows O(cols); one row O(cols).
 *
 * Run: java GridPathsRolling.java
 */
public class GridPathsRolling {

    /** Full table: paths[r][c] = number of right/down routes from (0,0) to (r,c). */
    static long fullTable(int rows, int cols) {
        long[][] paths = new long[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                paths[r][c] = (r == 0 || c == 0) ? 1 : paths[r - 1][c] + paths[r][c - 1];
            }
        }
        return paths[rows - 1][cols - 1];
    }

    /** Two rows: previous[] is row r-1, current[] is row r. */
    static long twoRows(int rows, int cols) {
        long[] previous = new long[cols];
        java.util.Arrays.fill(previous, 1); // row 0: one straight path to every cell
        for (int r = 1; r < rows; r++) {
            long[] current = new long[cols];
            current[0] = 1; // column 0: one straight path down
            for (int c = 1; c < cols; c++) {
                current[c] = previous[c] + current[c - 1]; // above + left
            }
            previous = current; // the current row becomes "the row above" for the next r
        }
        return previous[cols - 1];
    }

    /** One row, overwritten in place from left to right. */
    static long oneRow(int rows, int cols) {
        // row[c] = paths to (r, c) for the row being built; before overwrite it holds row r-1
        long[] row = new long[cols];
        java.util.Arrays.fill(row, 1);
        for (int r = 1; r < rows; r++) {
            for (int c = 1; c < cols; c++) {
                row[c] = row[c] + row[c - 1]; // old row[c] = above, new row[c-1] = left
            }
        }
        return row[cols - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] grids = {{3, 3}, {3, 7}, {2, 3}, {1, 1}, {10, 10}};
        long[] expected = {6, 28, 3, 1, 48620};
        for (int k = 0; k < grids.length; k++) {
            int rows = grids[k][0], cols = grids[k][1];
            String g = rows + "x" + cols;
            check("full table " + g, fullTable(rows, cols), expected[k]);
            check("two rows " + g, twoRows(rows, cols), expected[k]);
            check("one row " + g, oneRow(rows, cols), expected[k]);
        }
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
