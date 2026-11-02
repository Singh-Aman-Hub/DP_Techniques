/**
 * Subset Sum — Space-optimized (one boolean array, reverse loop)
 *
 * Idea: exactly like 0/1 knapsack, row i only reads row i-1 at the same or a smaller sum.
 * One array updated from s = target DOWN to s = value keeps dp[s - value] at its
 * "before this element" state, so every element is used at most once.
 *
 * Time complexity:  O(n * target) — each element sweeps the sums once.
 * Space complexity: O(target) — one boolean array.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static boolean canMakeSum(int[] arr, int target) {
        // reachable[s] = can some subset of the elements processed so far sum to s?
        boolean[] reachable = new boolean[target + 1];
        reachable[0] = true;
        for (int value : arr) {
            for (int s = target; s >= value; s--) {      // high to low: value is used at most once
                if (reachable[s - value]) reachable[s] = true;
            }
        }
        return reachable[target];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[3,4,5,2], target 9 (4+5)", canMakeSum(new int[]{3, 4, 5, 2}, 9), true);
        check("[3,4,5,2], target 13 (only 1 short of the total, but there is no 1)", canMakeSum(new int[]{3, 4, 5, 2}, 13), false);
        check("[3,4,5,2], target 1 (smaller than every element)", canMakeSum(new int[]{3, 4, 5, 2}, 1), false);
        check("[3,4,5,2], target 0 (edge: the empty subset)", canMakeSum(new int[]{3, 4, 5, 2}, 0), true);
        check("[3,34,4,12,5,2], target 9", canMakeSum(new int[]{3, 34, 4, 12, 5, 2}, 9), true);
        check("[3,34,4,12,5,2], target 30", canMakeSum(new int[]{3, 34, 4, 12, 5, 2}, 30), false);
        check("[] , target 0 (edge: empty array)", canMakeSum(new int[]{}, 0), true);
    }

    private static void check(String label, boolean actual, boolean expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
