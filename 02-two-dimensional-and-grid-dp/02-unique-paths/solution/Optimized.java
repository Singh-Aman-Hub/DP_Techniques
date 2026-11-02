/**
 * Unique Paths (LeetCode 62) — Space-optimized (one rolling row)
 *
 * Idea: row r of the table reads only row r-1 and itself, so one array of length n is
 *       enough. Before row[c] is overwritten it still holds the value from the row above;
 *       row[c-1] was already updated this pass, so it holds the value from the left.
 *       The loop therefore MUST go left to right.
 *
 * Time complexity:  O(m * n) — the same cells are computed as in Tabulation.
 * Space complexity: O(n) — a single row.
 *
 * Run: java Optimized.java
 */
import java.util.Arrays;

public class Optimized {

    static int uniquePaths(int m, int n) {
        // row[c] = number of routes to (r, c) for the row r currently being processed
        int[] row = new int[n];
        Arrays.fill(row, 1);                 // row 0 is all ones
        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                row[c] += row[c - 1];        // old row[c] (from above) + new row[c-1] (from the left)
            }
        }
        return row[n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("m=3, n=3 (running example)", uniquePaths(3, 3), 6);
        check("m=3, n=7", uniquePaths(3, 7), 28);
        check("m=7, n=3 (rows and columns swapped)", uniquePaths(7, 3), 28);
        check("m=3, n=2", uniquePaths(3, 2), 3);
        check("m=10, n=10", uniquePaths(10, 10), 48620);
        check("m=1, n=5 (single row)", uniquePaths(1, 5), 1);
        check("m=1, n=1 (start is the end)", uniquePaths(1, 1), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
