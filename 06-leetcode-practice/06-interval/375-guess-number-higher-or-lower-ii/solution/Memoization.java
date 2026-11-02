/**
 * 375. Guess Number Higher or Lower II — Memoization (top-down)
 *
 * Idea: cost(lo, hi) = money that guarantees a win when the secret is in
 * [lo..hi]. You pick the guess x (min); a wrong guess costs x and the adversary
 * sends you to the more expensive side (max).
 *
 * Time complexity:  O(n^3) — about n^2/2 ranges, each tries up to n guesses
 * Space complexity: O(n^2) — memo table; recursion depth up to n
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[lo][hi] = guaranteed cost for range [lo..hi]; 0 = not computed
    // (safe: a real range with 2+ numbers costs at least lo >= 1)
    private static int[][] memo;

    static int getMoneyAmount(int n) {
        memo = new int[n + 2][n + 2];
        return cost(1, n);
    }

    private static int cost(int lo, int hi) {
        if (lo >= hi) return 0;                                 // 0 or 1 candidate: no wrong guess needed
        if (memo[lo][hi] != 0) return memo[lo][hi];
        int best = Integer.MAX_VALUE;
        for (int x = lo; x <= hi; x++) {
            int worstSide = Math.max(cost(lo, x - 1), cost(x + 1, hi));   // adversary's choice
            best = Math.min(best, x + worstSide);                         // our choice
        }
        memo[lo][hi] = best;
        return best;
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
