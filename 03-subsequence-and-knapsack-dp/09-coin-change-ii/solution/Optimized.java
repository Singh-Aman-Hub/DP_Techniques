/**
 * Coin Change II (LeetCode 518) — Space-optimized 1D DP
 *
 * Idea: Row i of the table only reads row i-1 (same column) and row i (to the left).
 *       Keep one array: before processing coin i, ways[a] still holds row i-1; walking
 *       a from left to right overwrites it with row i, and ways[a - coin] is already
 *       the new row-i value, which is exactly what "reuse the coin" needs.
 *       COINS OUTER, AMOUNT INNER => each combination is counted once (in coin order).
 *
 * Time complexity:  O(n * amount) — same number of updates as the 2D table.
 * Space complexity: O(amount) — one array of amount + 1 counts.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static long change(int amount, int[] coins) {
        // ways[a] = combinations (of the coin types processed so far) that sum to a
        long[] ways = new long[amount + 1];
        ways[0] = 1;
        for (int coin : coins) {                    // outer loop: coin types, in a fixed order
            for (int a = coin; a <= amount; a++) {  // inner loop: amounts, left to right (unlimited reuse)
                ways[a] += ways[a - coin];
            }
        }
        return ways[amount];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("coins [1,2,5], amount 5", change(5, new int[]{1, 2, 5}), 4);
        check("coins [1,2], amount 3", change(3, new int[]{1, 2}), 2);
        check("coins [2], amount 3 (impossible)", change(3, new int[]{2}), 0);
        check("coins [10], amount 10", change(10, new int[]{10}), 1);
        check("amount 0 (edge: empty combination)", change(0, new int[]{7}), 1);
        check("coins [1,2,5], amount 100", change(100, new int[]{1, 2, 5}), 541);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
