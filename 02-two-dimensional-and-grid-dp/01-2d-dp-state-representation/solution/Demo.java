/**
 * 2D DP State Representation — Demo
 *
 * Idea: a robot moving right/down on a board has a state of TWO numbers (row, col),
 *       so every approach stores one answer per (row, col) cell. This file shows the
 *       same question ("how many right/down paths from top-left to bottom-right?")
 *       answered five ways — backward recursion, forward recursion, 2D memoization,
 *       2D tabulation and a one-row table — and checks they all agree.
 *       It also contains a checker that decides whether a proposed fill order is
 *       valid for the "up + left" dependency arrows.
 *
 * Time complexity:  recursion O(2^(rows+cols)) — every call branches twice;
 *                   memo / tabulation / one-row O(rows * cols) — each cell computed once.
 * Space complexity: recursion O(rows + cols) stack; memo and tabulation O(rows * cols)
 *                   for the table; one-row O(cols) because only the previous row is read.
 *
 * Run: java Demo.java
 */
import java.util.Arrays;

public class Demo {

    // ---------- backward recursion: paths from (0,0) to (r,c) ----------
    static long waysBackward(int r, int c) {
        if (r < 0 || c < 0) return 0;      // a cell outside the board cannot be a predecessor
        if (r == 0 && c == 0) return 1;    // the start: exactly one (empty) path
        return waysBackward(r - 1, c) + waysBackward(r, c - 1);
    }

    // ---------- forward recursion: paths from (r,c) to (rows-1, cols-1) ----------
    static long pathsForward(int r, int c, int rows, int cols) {
        if (r >= rows || c >= cols) return 0;           // walked off the bottom/right edge
        if (r == rows - 1 && c == cols - 1) return 1;   // standing on the end cell
        return pathsForward(r, c + 1, rows, cols) + pathsForward(r + 1, c, rows, cols);
    }

    // ---------- memoization: the cache is 2D because the state is 2D ----------
    // memo[r][c] = number of paths from (0,0) to (r,c); -1 means "not computed yet"
    private static long[][] memo;

    static long waysMemo(int rows, int cols) {
        memo = new long[rows][cols];
        for (long[] row : memo) Arrays.fill(row, -1);
        return waysMemoHelper(rows - 1, cols - 1);
    }

    private static long waysMemoHelper(int r, int c) {
        if (r < 0 || c < 0) return 0;
        if (r == 0 && c == 0) return 1;
        if (memo[r][c] != -1) return memo[r][c];
        memo[r][c] = waysMemoHelper(r - 1, c) + waysMemoHelper(r, c - 1);
        return memo[r][c];
    }

    // ---------- tabulation in row-major order ----------
    static long waysTable(int rows, int cols) {
        // dp[r][c] = number of paths from (0,0) to (r,c)
        long[][] dp = new long[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0 && c == 0) { dp[r][c] = 1; continue; }
                long fromAbove = (r > 0) ? dp[r - 1][c] : 0;   // guard: row -1 does not exist
                long fromLeft  = (c > 0) ? dp[r][c - 1] : 0;   // guard: column -1 does not exist
                dp[r][c] = fromAbove + fromLeft;
            }
        }
        return dp[rows - 1][cols - 1];
    }

    // ---------- one row: row r only reads row r-1 and itself ----------
    static long waysOneRow(int rows, int cols) {
        // row[c] holds dp[r-1][c] before the update and dp[r][c] after it
        long[] row = new long[cols];
        row[0] = 1;
        for (int r = 0; r < rows; r++) {
            for (int c = 1; c < cols; c++) {
                row[c] = row[c] + row[c - 1];   // old row[c] = from above, row[c-1] = from the left
            }
        }
        return row[cols - 1];
    }

    /**
     * Returns true if filling cells in the given order computes every cell only after
     * the cell above it and the cell to its left (when those exist on the board).
     */
    static boolean isValidUpLeftOrder(int[][] order, int rows, int cols) {
        boolean[][] filled = new boolean[rows][cols];
        for (int[] cell : order) {
            int r = cell[0], c = cell[1];
            if (r > 0 && !filled[r - 1][c]) return false;   // the "from above" source is missing
            if (c > 0 && !filled[r][c - 1]) return false;   // the "from the left" source is missing
            filled[r][c] = true;
        }
        return true;
    }

    static int[][] rowMajor(int rows, int cols) {
        int[][] order = new int[rows * cols][];
        int k = 0;
        for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) order[k++] = new int[]{r, c};
        return order;
    }

    static int[][] columnMajor(int rows, int cols) {
        int[][] order = new int[rows * cols][];
        int k = 0;
        for (int c = 0; c < cols; c++) for (int r = 0; r < rows; r++) order[k++] = new int[]{r, c};
        return order;
    }

    static int[][] bottomRowFirst(int rows, int cols) {
        int[][] order = new int[rows * cols][];
        int k = 0;
        for (int r = rows - 1; r >= 0; r--) for (int c = 0; c < cols; c++) order[k++] = new int[]{r, c};
        return order;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        // Board sizes and hand-computed answers (see the walkthrough and its exercises).
        int[][] boards  = { {2, 3}, {3, 2}, {3, 3}, {1, 1}, {1, 4} };
        long[]  answers = {   3,      3,      6,      1,      1   };

        for (int t = 0; t < boards.length; t++) {
            int rows = boards[t][0], cols = boards[t][1];
            String label = rows + "x" + cols;
            check("backward recursion " + label, waysBackward(rows - 1, cols - 1), answers[t]);
            check("forward recursion  " + label, pathsForward(0, 0, rows, cols), answers[t]);
            check("memoization        " + label, waysMemo(rows, cols), answers[t]);
            check("tabulation         " + label, waysTable(rows, cols), answers[t]);
            check("one row            " + label, waysOneRow(rows, cols), answers[t]);
        }

        // Paths through the centre of a 3x3 board = ways(1,1) * toEnd(1,1) = 2 * 2 = 4
        check("paths through centre of 3x3", waysBackward(1, 1) * pathsForward(1, 1, 3, 3), 4);

        // Fill-order checks on the 2x3 board
        checkBool("row-major order is valid", isValidUpLeftOrder(rowMajor(2, 3), 2, 3), true);
        checkBool("column-major order is valid", isValidUpLeftOrder(columnMajor(2, 3), 2, 3), true);
        checkBool("bottom row first is invalid", isValidUpLeftOrder(bottomRowFirst(2, 3), 2, 3), false);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }

    private static void checkBool(String label, boolean actual, boolean expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
