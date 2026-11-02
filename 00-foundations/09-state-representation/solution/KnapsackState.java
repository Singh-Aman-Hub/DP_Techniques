/**
 * State representation — 2D state for 0/1 knapsack (and what goes wrong with too few parameters)
 *
 * Idea: Walking through the items one by one, two things change: which item we
 *       are deciding about (i) and how much capacity is still free. So the state
 *       is best(i, remaining) = "the maximum value we can still collect from
 *       items i..n-1 when `remaining` units of capacity are left".
 *       The method bestTooFewParameters caches by i ONLY. Two calls with the same
 *       i but different remaining capacity then share one cache slot, and the
 *       second call silently gets the first call's answer -> a wrong result.
 *
 * Time complexity:  O(n * W) — n items times W + 1 capacities, O(1) work each.
 * Space complexity: O(n * W) — the 2D cache, plus a recursion stack of depth n.
 *
 * Run: java KnapsackState.java
 */
import java.util.Arrays;

public class KnapsackState {

    private static int[] weights, values;

    // memo[i][remaining] = best value from items i..n-1 with `remaining` capacity; -1 = not computed
    private static long[][] memo;

    static long maxValue(int[] w, int[] v, int capacity) {
        weights = w;
        values = v;
        memo = new long[w.length][capacity + 1];
        for (long[] row : memo) Arrays.fill(row, -1);
        return best(0, capacity);
    }

    private static long best(int i, int remaining) {
        if (i == weights.length) return 0;          // no items left to decide about
        if (memo[i][remaining] != -1) return memo[i][remaining];
        long skip = best(i + 1, remaining);         // choice 1: leave item i
        long take = Long.MIN_VALUE;                 // choice 2: take item i, only if it fits
        if (weights[i] <= remaining) take = values[i] + best(i + 1, remaining - weights[i]);
        memo[i][remaining] = Math.max(skip, take);
        return memo[i][remaining];
    }

    // ---------- the WRONG version: the cache forgets the capacity ----------

    // badMemo[i] = "the answer for item i" -- but which capacity? That information is lost.
    private static long[] badMemo;

    static long maxValueTooFewParameters(int[] w, int[] v, int capacity) {
        weights = w;
        values = v;
        badMemo = new long[w.length];
        Arrays.fill(badMemo, -1);
        return bestTooFewParameters(0, capacity);
    }

    private static long bestTooFewParameters(int i, int remaining) {
        if (i == weights.length) return 0;
        if (badMemo[i] != -1) return badMemo[i];    // BUG: may belong to a different capacity
        long skip = bestTooFewParameters(i + 1, remaining);
        long take = Long.MIN_VALUE;
        if (weights[i] <= remaining) take = values[i] + bestTooFewParameters(i + 1, remaining - weights[i]);
        badMemo[i] = Math.max(skip, take);
        return badMemo[i];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[] w = {1, 3, 4};
        int[] v = {15, 20, 30};
        check("weights [1,3,4], values [15,20,30], W = 4", maxValue(w, v, 4), 35);
        check("same items, W = 3", maxValue(w, v, 3), 20);
        check("same items, W = 8 (everything fits)", maxValue(w, v, 8), 65);
        check("same items, W = 0 (edge: nothing fits)", maxValue(w, v, 0), 0);
        // Trace of the too-few-parameters version (W = 4):
        //   bad(2) is first computed with capacity 4 -> 30 and cached as "bad(2) = 30".
        //   bad(1) with capacity 4: take item 1 (w=3) needs bad(2) with capacity 1,
        //     but the cache hands back 30 -> 20 + 30 = 50 (items 1+2 weigh 7 > 4!).
        //   bad(0): take item 0 needs bad(1) with capacity 3, the cache hands back 50
        //     -> 15 + 50 = 65. That is "all three items", weight 8, in a bag of size 4.
        check("too few parameters gives a WRONG answer", maxValueTooFewParameters(w, v, 4), 65);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
