/**
 * Coin Change II (LeetCode 518) — Tabulation (bottom-up DP)
 *
 * Idea: ways[i][a] = number of combinations that make amount a using only the first
 *       i coin types. Either coin type i is not used at all (ways[i-1][a]) or it is
 *       used at least once (ways[i][a - coin], same row because it may be reused).
 *       Rows are filled top to bottom, each row left to right.
 *
 * Time complexity:  O(n * amount) — (n + 1) * (amount + 1) cells, O(1) work each.
 * Space complexity: O(n * amount) — the full table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static long change(int amount, int[] coins) {
        int n = coins.length;
        // ways[i][a] = combinations of the first i coin types that sum to exactly a
        long[][] ways = new long[n + 1][amount + 1];
        ways[0][0] = 1;   // zero coin types make amount 0 in one way (choose nothing); other amounts: 0 ways

        for (int i = 1; i <= n; i++) {
            int coin = coins[i - 1];
            for (int a = 0; a <= amount; a++) {
                ways[i][a] = ways[i - 1][a];                 // do not use this coin type
                if (a >= coin) ways[i][a] += ways[i][a - coin];   // use it at least once
            }
        }
        return ways[n][amount];
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
