/**
 * Unique Paths (LeetCode 62) — Brute force recursion
 *
 * Idea: paths(r, c) = number of right/down routes from (0,0) to (r,c). The last move
 *       into (r,c) was either down from (r-1,c) or right from (r,c-1), so we add the
 *       two counts. Row 0 and column 0 have exactly one route each (the base cases).
 *
 * Time complexity:  O(2^(m+n)) — every call makes two calls and the depth is m+n-2;
 *                   the number of leaves equals the answer itself.
 * Space complexity: O(m + n) — the deepest chain of calls on the stack.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int uniquePaths(int m, int n) {
        return countPaths(m - 1, n - 1);
    }

    private static int countPaths(int r, int c) {
        if (r == 0 || c == 0) return 1;                      // base row / base column: one straight route
        return countPaths(r - 1, c) + countPaths(r, c - 1);  // last move was down, or last move was right
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
