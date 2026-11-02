/**
 * Space optimization basics — when you can NOT throw rows away: reconstructing the choice
 *
 * Idea: the one-row knapsack tells us the best VALUE (35), but not WHICH items
 * give it. To answer "which items?", we walk back through the full table:
 * at row i and capacity w, if best[i][w] == best[i-1][w], item i-1 was not
 * needed; otherwise it was taken and we move to capacity w - weights[i-1].
 * That walk reads every row, so all rows must still exist.
 *
 * Time complexity:  O(n * W) to fill the table + O(n) to walk back.
 * Space complexity: O(n * W) — the full table must be kept for the walk back.
 *
 * Run: java ReconstructionNeedsTable.java
 */
import java.util.ArrayList;
import java.util.List;

public class ReconstructionNeedsTable {

    /** Returns the indices of the chosen items, in increasing order. */
    static List<Integer> chosenItems(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        // best[i][w] = best value using only the first i items with capacity w
        int[][] best = new int[n + 1][capacity + 1];
        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= capacity; w++) {
                best[i][w] = best[i - 1][w];
                if (weights[i - 1] <= w) {
                    best[i][w] = Math.max(best[i][w], values[i - 1] + best[i - 1][w - weights[i - 1]]);
                }
            }
        }
        // Walk back from the answer cell to row 0.
        List<Integer> chosen = new ArrayList<>();
        int w = capacity;
        for (int i = n; i >= 1; i--) {
            if (best[i][w] != best[i - 1][w]) { // value changed, so item i-1 was taken
                chosen.add(0, i - 1);
                w -= weights[i - 1];
            }
        }
        return chosen;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("canonical knapsack chooses items", chosenItems(new int[]{1, 3, 4}, new int[]{15, 20, 30}, 4), List.of(0, 1));
        check("capacity 5 example chooses items", chosenItems(new int[]{2, 3, 4, 5}, new int[]{3, 4, 5, 6}, 5), List.of(0, 1));
        check("single item that fits", chosenItems(new int[]{4}, new int[]{30}, 4), List.of(0));
        check("capacity 0 (edge) chooses nothing", chosenItems(new int[]{1, 3, 4}, new int[]{15, 20, 30}, 0), List.of());
    }

    private static void check(String label, List<Integer> actual, List<Integer> expected) {
        boolean ok = actual.equals(expected);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
