/**
 * 279. Perfect Squares — Optimized (number theory instead of DP)
 *
 * Idea: Lagrange: every n is a sum of at most 4 squares. Legendre: n needs exactly 4 iff
 * n = 4^a * (8b + 7). So: answer 1 if n is a square, 2 if n = a^2 + b^2, 4 by Legendre's
 * test, otherwise 3. Shown to contrast with the DP; the DP is the version to derive first.
 *
 * Time complexity:  O(sqrt(n)) — the two-square check tries each a with a*a <= n.
 * Space complexity: O(1) — a handful of variables.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int numSquares(int n) {
        if (isSquare(n)) return 1;
        for (int a = 1; a * a <= n; a++) {
            if (isSquare(n - a * a)) return 2;
        }
        int reduced = n;
        while (reduced % 4 == 0) reduced /= 4;     // strip factors of 4 (they do not change the answer)
        if (reduced % 8 == 7) return 4;
        return 3;
    }

    private static boolean isSquare(int x) {
        int root = (int) Math.sqrt(x);
        return root * root == x;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 12", numSquares(12), 3);
        check("n = 13", numSquares(13), 2);
        check("n = 7", numSquares(7), 4);
        check("n = 1 (edge)", numSquares(1), 1);
        check("n = 43", numSquares(43), 3);
        check("n = 100", numSquares(100), 1);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
