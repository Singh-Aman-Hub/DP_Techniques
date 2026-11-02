/**
 * Subset Sum — Brute force (take/skip recursion)
 *
 * Idea: canMake(i, remaining) asks "can some subset of arr[i..n-1] add up to exactly
 * remaining?". Either skip arr[i], or take it (only if it is not larger than remaining).
 * If either branch says yes, the answer is yes. This is 0/1 knapsack where the target plays
 * the role of capacity and "value" is replaced by a yes/no answer.
 *
 * Time complexity:  O(2^n) — two branches per element.
 * Space complexity: O(n) — recursion depth.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static boolean canMakeSum(int[] arr, int target) {
        return canMake(arr, 0, target);
    }

    private static boolean canMake(int[] arr, int i, int remaining) {
        if (remaining == 0) return true;            // the chosen elements already hit the target
        if (i == arr.length) return false;          // no elements left and still something to cover
        boolean skip = canMake(arr, i + 1, remaining);
        boolean take = arr[i] <= remaining && canMake(arr, i + 1, remaining - arr[i]);
        return skip || take;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[3,4,5,2], target 9 (4+5)", canMakeSum(new int[]{3, 4, 5, 2}, 9), true);
        check("[3,4,5,2], target 13 (only 1 short of the total, but there is no 1)", canMakeSum(new int[]{3, 4, 5, 2}, 13), false);
        check("[3,4,5,2], target 1 (smaller than every element)", canMakeSum(new int[]{3, 4, 5, 2}, 1), false);
        check("[3,4,5,2], target 0 (edge: the empty subset)", canMakeSum(new int[]{3, 4, 5, 2}, 0), true);
        check("[3,34,4,12,5,2], target 9", canMakeSum(new int[]{3, 34, 4, 12, 5, 2}, 9), true);
        check("[3,34,4,12,5,2], target 30", canMakeSum(new int[]{3, 34, 4, 12, 5, 2}, 30), false);
    }

    private static void check(String label, boolean actual, boolean expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
