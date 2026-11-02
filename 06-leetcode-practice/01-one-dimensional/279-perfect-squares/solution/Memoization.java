/**
 * 279. Perfect Squares — Memoization (top-down)
 *
 * Idea: best(r) = fewest squares summing to r. Try every square k*k <= r as the last one and
 * recurse on r - k*k. Each remainder is solved once thanks to the cache.
 *
 * Time complexity:  O(n * sqrt(n)) — n states, each tries at most sqrt(n) squares.
 * Space complexity: O(n) — memo array plus recursion stack (depth up to n via repeated 1s).
 *
 * Run: java Memoization.java
 */
public class Memoization {

    private static int[] memo;   // memo[r] = fewest squares summing to r; 0 = unknown (except r = 0)

    static int numSquares(int n) {
        memo = new int[n + 1];
        return best(n);
    }

    private static int best(int remaining) {
        if (remaining == 0) return 0;
        if (memo[remaining] != 0) return memo[remaining];   // every real answer for r > 0 is >= 1
        int answer = Integer.MAX_VALUE;
        for (int k = 1; k * k <= remaining; k++) {
            answer = Math.min(answer, 1 + best(remaining - k * k));
        }
        memo[remaining] = answer;
        return answer;
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
