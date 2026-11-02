/**
 * 0/1 Knapsack — Brute force (take/skip recursion)
 *
 * Idea: best(i, capacity) = the most value we can collect from items i..n-1 when the bag
 * still has room for `capacity` weight. Item i is either skipped, or taken (only if it fits),
 * after which we move on to item i+1 with less room. Every subset of items is tried.
 *
 * Time complexity:  O(2^n) — two branches per item, n levels deep.
 * Space complexity: O(n) — recursion depth.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static long knapsack(int[] weight, long[] value, int capacity) {
        return best(weight, value, 0, capacity);
    }

    private static long best(int[] weight, long[] value, int i, int capacity) {
        if (i == weight.length) return 0;                // no items left: nothing more to gain
        long skip = best(weight, value, i + 1, capacity);
        long take = Long.MIN_VALUE;                       // "impossible" unless the item fits
        if (weight[i] <= capacity) {
            take = value[i] + best(weight, value, i + 1, capacity - weight[i]);
        }
        return Math.max(skip, take);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("AtCoder sample 1: W=8, (3,30)(4,50)(5,60)",
                knapsack(new int[]{3, 4, 5}, new long[]{30, 50, 60}, 8), 90);
        check("AtCoder sample 3: W=15, six items",
                knapsack(new int[]{6, 5, 6, 6, 3, 7}, new long[]{5, 6, 4, 6, 5, 2}, 15), 17);
        check("capacity 0 (edge)", knapsack(new int[]{3, 4, 5}, new long[]{30, 50, 60}, 0), 0);
        check("single item too heavy (edge)", knapsack(new int[]{10}, new long[]{99}, 9), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
