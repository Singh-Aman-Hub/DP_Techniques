/**
 * 0/1 Knapsack, variant for huge capacity (AtCoder dp_e "Knapsack 2") — DP indexed by VALUE
 *
 * Idea: when W can be 10^9 but the total value is small (n <= 100, each value <= 1000, so the
 * total is at most 10^5), swap the roles of weight and value:
 *   minWeight[v] = the smallest total weight needed to collect value EXACTLY v.
 * The answer is the largest v whose minWeight[v] <= W. Same 0/1 structure, so the value loop
 * runs from HIGH to LOW for the same reason as the capacity loop in Optimized.java.
 *
 * Time complexity:  O(n * V) where V = sum of all values — every (item, value) pair once.
 * Space complexity: O(V) — one array indexed by value.
 *
 * Run: java ValueIndexed.java
 */
import java.util.Arrays;

public class ValueIndexed {

    static long knapsack(long[] weight, int[] value, long capacity) {
        int totalValue = 0;
        for (int v : value) totalValue += v;

        // A safe "infinity": larger than any real weight sum, but adding a weight to it cannot overflow.
        final long INF = Long.MAX_VALUE / 4;
        // minWeight[v] = smallest weight that achieves value exactly v with the items seen so far
        long[] minWeight = new long[totalValue + 1];
        Arrays.fill(minWeight, INF);
        minWeight[0] = 0;                                   // value 0 costs nothing (take no items)

        for (int i = 0; i < value.length; i++) {
            for (int v = totalValue; v >= value[i]; v--) {  // high to low: each item used at most once
                if (minWeight[v - value[i]] != INF) {
                    minWeight[v] = Math.min(minWeight[v], minWeight[v - value[i]] + weight[i]);
                }
            }
        }
        for (int v = totalValue; v >= 0; v--) {
            if (minWeight[v] <= capacity) return v;
        }
        return 0;   // unreachable: minWeight[0] = 0 always fits
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("dp_e sample 1 (same items as dp_d sample 1)",
                knapsack(new long[]{3, 4, 5}, new int[]{30, 50, 60}, 8), 90);
        check("dp_e sample 2: W = 10^9, one item weighing 10^9 (edge: exactly fits)",
                knapsack(new long[]{1_000_000_000L}, new int[]{10}, 1_000_000_000L), 10);
        check("dp_e sample 3 (six items, W=15)",
                knapsack(new long[]{6, 5, 6, 6, 3, 7}, new int[]{5, 6, 4, 6, 5, 2}, 15), 17);
        check("nothing fits (edge)", knapsack(new long[]{5, 6}, new int[]{7, 8}, 4), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
