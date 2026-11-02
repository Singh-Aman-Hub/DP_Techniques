/**
 * Rod Cutting — Memoization (top-down DP)
 *
 * Idea: Same recursion as BruteForce.java. The pair (maxPiece, remaining) fully
 *       describes a subproblem and there are only n * (n + 1) such pairs, so each
 *       answer is cached the first time it is computed.
 *
 * Time complexity:  O(n^2) — n * (n + 1) states, O(1) work each.
 * Space complexity: O(n^2) for the memo + O(n) recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[maxPiece][remaining] = best revenue for length `remaining` using piece lengths 1..maxPiece;
    // -1 means "not computed yet" (all real answers are >= 0).
    private static int[][] memo;

    static int maxRevenue(int[] prices, int rodLength) {
        if (rodLength == 0) return 0;
        memo = new int[rodLength + 1][rodLength + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return bestPrice(prices, rodLength, rodLength);
    }

    private static int bestPrice(int[] prices, int maxPiece, int remaining) {
        if (remaining == 0) return 0;
        if (maxPiece == 1) return remaining * prices[0];
        if (memo[maxPiece][remaining] != -1) return memo[maxPiece][remaining];

        int skip = bestPrice(prices, maxPiece - 1, remaining);
        int take = Integer.MIN_VALUE;
        if (maxPiece <= remaining) {
            take = prices[maxPiece - 1] + bestPrice(prices, maxPiece, remaining - maxPiece);
        }
        memo[maxPiece][remaining] = Math.max(skip, take);
        return memo[maxPiece][remaining];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[] classic = {1, 5, 8, 9, 10, 17, 17, 20};
        check("classic prices, n = 8", maxRevenue(classic, 8), 22);
        check("prices [1,5,8,9], n = 4", maxRevenue(new int[]{1, 5, 8, 9}, 4), 10);
        check("unit pieces best, n = 8", maxRevenue(new int[]{3, 5, 8, 9, 10, 17, 17, 20}, 8), 24);
        check("n = 1", maxRevenue(new int[]{2}, 1), 2);
        check("n = 0 (edge: nothing to sell)", maxRevenue(new int[]{}, 0), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
