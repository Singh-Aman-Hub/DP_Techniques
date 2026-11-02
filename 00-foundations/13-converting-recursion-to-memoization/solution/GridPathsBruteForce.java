/**
 * Grid paths (Unique Paths style) — Brute force (the "before" picture)
 *
 * Idea: a robot moves only right or down. paths(r, c) = number of routes from
 * the top-left cell (0, 0) to cell (r, c). The last move into (r, c) came from
 * above (r-1, c) or from the left (r, c-1), so we add those two counts. Cells in
 * row 0 or column 0 can be reached in exactly one way (a straight line).
 *
 * Time complexity:  O(2^(rows + cols)) — every call branches in two, and the
 *                   tree has 2 * paths - 1 nodes, which is exponential.
 * Space complexity: O(rows + cols) — recursion depth; each call moves one step closer.
 *
 * Run: java GridPathsBruteForce.java
 */
public class GridPathsBruteForce {

    static long uniquePaths(int rows, int cols) {
        return paths(rows - 1, cols - 1); // the target cell is the bottom-right corner
    }

    /** Number of right/down routes from (0, 0) to (r, c). */
    static long paths(int r, int c) {
        if (r == 0 || c == 0) return 1;            // straight line along the top row or left column
        return paths(r - 1, c) + paths(r, c - 1);  // last move was "down" or "right"
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("3 x 3", uniquePaths(3, 3), 6);
        check("3 x 7", uniquePaths(3, 7), 28);
        check("2 x 3", uniquePaths(2, 3), 3);
        check("1 x 1 (edge: start is the target)", uniquePaths(1, 1), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
