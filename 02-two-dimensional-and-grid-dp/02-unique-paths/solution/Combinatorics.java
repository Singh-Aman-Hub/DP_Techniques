/**
 * Unique Paths (LeetCode 62) — Combinatorics shortcut (aside, not DP)
 *
 * Idea: every route makes exactly m-1 down moves and n-1 right moves, m+n-2 moves in all.
 *       A route is decided by which positions hold the down moves, so the answer is
 *       C(m+n-2, m-1). We build the binomial one factor at a time: after step i the
 *       running value equals C(total-k+i, i), an integer, so every division is exact and
 *       no factorial (which would overflow) is ever formed.
 *       Works only because the board has no obstacles or costs.
 *
 * Time complexity:  O(min(m, n)) — one multiply and one divide per factor.
 * Space complexity: O(1) — a few variables.
 *
 * Run: java Combinatorics.java
 */
public class Combinatorics {

    static long uniquePaths(int m, int n) {
        int totalMoves = m + n - 2;
        int k = Math.min(m - 1, n - 1);      // C(t, k) == C(t, t-k): use the smaller k for fewer steps
        long result = 1;
        for (int i = 1; i <= k; i++) {
            result = result * (totalMoves - k + i) / i;
        }
        return result;
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
