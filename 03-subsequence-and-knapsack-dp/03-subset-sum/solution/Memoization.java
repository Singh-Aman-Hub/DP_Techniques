/**
 * Subset Sum — Memoization (top-down DP)
 *
 * Idea: canMake(i, remaining) depends only on (i, remaining), so we cache it. A boolean
 * cannot express "not computed yet", so the memo uses a Boolean object (null = unknown).
 *
 * Time complexity:  O(n * target) — at most n * (target + 1) states, O(1) work each.
 * Space complexity: O(n * target) for the memo, plus O(n) recursion stack.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[i][r] = can some subset of arr[i..n-1] sum to exactly r?  null = not computed yet
    private static Boolean[][] memo;

    static boolean canMakeSum(int[] arr, int target) {
        memo = new Boolean[arr.length + 1][target + 1];
        return canMake(arr, 0, target);
    }

    private static boolean canMake(int[] arr, int i, int remaining) {
        if (remaining == 0) return true;
        if (i == arr.length) return false;
        if (memo[i][remaining] != null) return memo[i][remaining];

        boolean skip = canMake(arr, i + 1, remaining);
        boolean take = arr[i] <= remaining && canMake(arr, i + 1, remaining - arr[i]);
        memo[i][remaining] = skip || take;
        return memo[i][remaining];
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
