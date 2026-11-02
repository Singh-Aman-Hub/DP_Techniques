/**
 * Coin Change II (LeetCode 518) — Brute force recursion
 *
 * Idea: Stand at coin type `index` with `remaining` money still to make. Either
 *       skip this coin type for good (move to index + 1) or use one more copy of it
 *       (stay at index, because coins are unlimited). Because a coin type, once
 *       skipped, is never looked at again, every combination is produced in exactly
 *       one order (coins in array order), so we count combinations, not orderings.
 *
 * Time complexity:  O(2^(amount)) in the worst case — every call branches into two,
 *                   and a path can be as deep as amount / smallestCoin + coins.length.
 * Space complexity: O(amount + n) — the depth of the recursion stack.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int change(int amount, int[] coins) {
        return countWays(coins, 0, amount);
    }

    // Number of combinations of coins[index..] (unlimited copies) that sum to remaining.
    private static int countWays(int[] coins, int index, int remaining) {
        if (remaining == 0) return 1;            // the empty "rest" completes one valid combination
        if (index == coins.length) return 0;     // money left but no coin types left

        int skip = countWays(coins, index + 1, remaining);   // never use coins[index] again
        int take = 0;
        if (coins[index] <= remaining) {
            // stay at the same index: we may take this coin type again
            take = countWays(coins, index, remaining - coins[index]);
        }
        return skip + take;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("coins [1,2,5], amount 5", change(5, new int[]{1, 2, 5}), 4);
        check("coins [1,2], amount 3", change(3, new int[]{1, 2}), 2);
        check("coins [2], amount 3 (impossible)", change(3, new int[]{2}), 0);
        check("coins [10], amount 10", change(10, new int[]{10}), 1);
        check("amount 0 (edge: empty combination)", change(0, new int[]{7}), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
