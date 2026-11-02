/**
 * Unique Paths (LeetCode 62) — Memoization (top-down)
 *
 * Idea: the same recursion as BruteForce, but each cell's count is stored the first
 *       time it is computed. The state is (row, col), so the cache is a 2D array.
 *
 * Time complexity:  O(m * n) — there are m*n distinct cells and each is computed once
 *                   with O(1) work (two lookups and an addition).
 * Space complexity: O(m * n) — the memo table, plus O(m + n) recursion stack.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[r][c] = number of routes from (0,0) to (r,c); 0 means "not computed yet"
    // (safe here: every real count is at least 1)
    private static int[][] memo;

    static int uniquePaths(int m, int n) {
        memo = new int[m][n];
        return countPaths(m - 1, n - 1);
    }

    private static int countPaths(int r, int c) {
        if (r == 0 || c == 0) return 1;           // base row / base column
        if (memo[r][c] != 0) return memo[r][c];   // already solved: reuse it
        memo[r][c] = countPaths(r - 1, c) + countPaths(r, c - 1);
        return memo[r][c];
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
