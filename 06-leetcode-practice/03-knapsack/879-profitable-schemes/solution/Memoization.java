/**
 * 879. Profitable Schemes — Memoization (top-down)
 *
 * Idea: count(i, membersLeft, need) = schemes from crimes i.. that fit in membersLeft and earn
 * at least `need`. Skip crime i, or commit it if enough members remain. Profit above the target
 * is irrelevant, so `need` is clamped at 0, keeping the profit axis to 0..minProfit.
 *
 * Time complexity:  O(L * n * P) — L*(n+1)*(P+1) states, O(1) each.
 * Space complexity: O(L * n * P) — the memo cube plus recursion depth L.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static final int MOD = 1_000_000_007;
    private static int[] group, profit;
    private static long[][][] memo;   // memo[i][members][need] = scheme count; -1 = unknown

    static int profitableSchemes(int n, int minProfit, int[] crimeGroup, int[] crimeProfit) {
        group = crimeGroup;
        profit = crimeProfit;
        memo = new long[group.length][n + 1][minProfit + 1];
        for (long[][] plane : memo) for (long[] row : plane) Arrays.fill(row, -1);
        return (int) count(0, n, minProfit);
    }

    private static long count(int i, int membersLeft, int need) {
        if (i == group.length) return need == 0 ? 1 : 0;      // decided every crime
        if (memo[i][membersLeft][need] != -1) return memo[i][membersLeft][need];
        long ways = count(i + 1, membersLeft, need);           // skip crime i
        if (group[i] <= membersLeft) {                         // commit crime i
            ways += count(i + 1, membersLeft - group[i], Math.max(0, need - profit[i]));
        }
        memo[i][membersLeft][need] = ways % MOD;
        return memo[i][membersLeft][need];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example 1", profitableSchemes(5, 3, new int[]{2, 2}, new int[]{2, 3}), 2);
        check("example 2", profitableSchemes(10, 5, new int[]{2, 3, 5}, new int[]{6, 7, 8}), 7);
        check("only empty scheme (edge)", profitableSchemes(1, 0, new int[]{2}, new int[]{1}), 1);
        check("three single crimes", profitableSchemes(1, 1, new int[]{1, 1, 1}, new int[]{1, 1, 1}), 3);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
