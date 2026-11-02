/**
 * 375. Guess Number Higher or Lower II — Tabulation (bottom-up)
 *
 * Idea: dp[lo][hi] = min over x of x + max(dp[lo][x-1], dp[x+1][hi]). Every
 * dependency is a strictly smaller range inside [lo..hi], so lo runs from n
 * down to 1 and hi from lo+1 up to n. Size n+2 keeps dp[x+1][hi] in bounds.
 *
 * Time complexity:  O(n^3) — ranges x guesses
 * Space complexity: O(n^2) — the (n+2) x (n+2) table
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int getMoneyAmount(int n) {
        // dp[lo][hi] = minimum money that guarantees a win when the secret is in [lo..hi]
        int[][] dp = new int[n + 2][n + 2];
        for (int lo = n; lo >= 1; lo--) {
            for (int hi = lo + 1; hi <= n; hi++) {
                dp[lo][hi] = Integer.MAX_VALUE;
                for (int x = lo; x <= hi; x++) {
                    int worstSide = Math.max(dp[lo][x - 1], dp[x + 1][hi]);   // empty ranges hold 0
                    dp[lo][hi] = Math.min(dp[lo][hi], x + worstSide);
                }
            }
        }
        return dp[1][n];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example n = 4", getMoneyAmount(4), 4);
        check("n = 10", getMoneyAmount(10), 16);
        check("n = 3", getMoneyAmount(3), 2);
        check("n = 2", getMoneyAmount(2), 1);
        check("n = 1 (edge)", getMoneyAmount(1), 0);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
