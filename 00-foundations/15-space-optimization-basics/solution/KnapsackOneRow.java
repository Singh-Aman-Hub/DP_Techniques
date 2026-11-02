/**
 * Space optimization basics — 0/1 knapsack in one row, and why the loop must run backwards
 *
 * Idea: best[i][w] = max(best[i-1][w], values[i-1] + best[i-1][w - weights[i-1]])
 * reads only row i-1, at the same column w and at a SMALLER column. If we keep
 * one array best[w] and overwrite it, the smaller column must still hold the
 * OLD (row i-1) value when we read it. Looping w from high to low guarantees
 * that: smaller columns are overwritten later. Looping low to high overwrites
 * them first, so an item can be "taken again" — that silently turns the 0/1
 * knapsack into the unbounded knapsack and gives a wrong answer.
 *
 * Time complexity:  O(n * W) — n items, W+1 capacities each, O(1) work per cell.
 * Space complexity: 2D table O(n * W); one row O(W).
 *
 * Run: java KnapsackOneRow.java
 */
public class KnapsackOneRow {

    /** Full 2D table, for comparison. */
    static int fullTable(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        // best[i][w] = best value using only the first i items with capacity w
        int[][] best = new int[n + 1][capacity + 1];
        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= capacity; w++) {
                best[i][w] = best[i - 1][w]; // skip item i-1
                if (weights[i - 1] <= w) {
                    best[i][w] = Math.max(best[i][w], values[i - 1] + best[i - 1][w - weights[i - 1]]);
                }
            }
        }
        return best[n][capacity];
    }

    /** One row, capacity loop from HIGH to LOW (correct). */
    static int oneRowReverse(int[] weights, int[] values, int capacity) {
        // best[w] = best value with capacity w using the items processed so far
        int[] best = new int[capacity + 1];
        for (int i = 0; i < weights.length; i++) {
            for (int w = capacity; w >= weights[i]; w--) {
                // best[w - weights[i]] has not been touched in this round yet: it is the "row above"
                best[w] = Math.max(best[w], values[i] + best[w - weights[i]]);
            }
        }
        return best[capacity];
    }

    /** One row, capacity loop from LOW to HIGH (WRONG for 0/1 knapsack). */
    static int oneRowForwardWrong(int[] weights, int[] values, int capacity) {
        int[] best = new int[capacity + 1];
        for (int i = 0; i < weights.length; i++) {
            for (int w = weights[i]; w <= capacity; w++) {
                // best[w - weights[i]] was ALREADY updated with item i, so item i can be used twice
                best[w] = Math.max(best[w], values[i] + best[w - weights[i]]);
            }
        }
        return best[capacity];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[] weights = {1, 3, 4};
        int[] values = {15, 20, 30};
        check("2D table, canonical", fullTable(weights, values, 4), 35);
        check("one row reverse, canonical", oneRowReverse(weights, values, 4), 35);
        check("one row FORWARD (wrong) gives 60: item 0 taken 4 times",
                oneRowForwardWrong(weights, values, 4), 60);

        int[] w2 = {2, 3, 4, 5};
        int[] v2 = {3, 4, 5, 6};
        check("2D table, capacity 5", fullTable(w2, v2, 5), 7);          // items of weight 2 and 3
        check("one row reverse, capacity 5", oneRowReverse(w2, v2, 5), 7);

        check("2D table, capacity 0 (edge)", fullTable(weights, values, 0), 0);
        check("one row reverse, capacity 0 (edge)", oneRowReverse(weights, values, 0), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
