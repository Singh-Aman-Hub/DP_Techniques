/**
 * Rod Cutting — Brute force recursion (unbounded-knapsack view)
 *
 * Idea: prices[k - 1] is the selling price of a piece of length k. Treat each piece
 *       length as an "item" with weight k and value prices[k - 1], and the rod length
 *       as the knapsack capacity. bestPrice(maxPiece, remaining) = the most money for
 *       a rod of length `remaining` if we may only cut pieces of length 1..maxPiece.
 *       Choices: never use length maxPiece (skip) or cut one more piece of it (take,
 *       and stay at maxPiece because the same length may be cut again).
 *
 * Time complexity:  exponential, about O(2^n) — every call branches into two.
 * Space complexity: O(n) — recursion depth is at most n + n.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int maxRevenue(int[] prices, int rodLength) {
        if (rodLength == 0) return 0;
        return bestPrice(prices, rodLength, rodLength);
    }

    private static int bestPrice(int[] prices, int maxPiece, int remaining) {
        if (remaining == 0) return 0;
        // Only length-1 pieces allowed: the whole rest must be cut into `remaining` unit pieces.
        if (maxPiece == 1) return remaining * prices[0];

        int skip = bestPrice(prices, maxPiece - 1, remaining);
        int take = Integer.MIN_VALUE;
        if (maxPiece <= remaining) {
            take = prices[maxPiece - 1] + bestPrice(prices, maxPiece, remaining - maxPiece);
        }
        return Math.max(skip, take);
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
