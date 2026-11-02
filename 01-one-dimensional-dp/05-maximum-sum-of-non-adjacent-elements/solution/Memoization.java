/**
 * Maximum Sum of Non-Adjacent Elements — Memoization (top-down DP)
 *
 * Idea: best(i) = max(best(i-1), arr[i] + best(i-2)), best(negative) = 0,
 * with each best(i) cached. The memo is a Long[] so that null means "unknown".
 * Here every best(i) is >= 0 (the empty selection is allowed), so -1 would also
 * work, but null stays correct in problems whose answers can be negative.
 *
 * Time complexity:  O(n) — n states, O(1) work each.
 * Space complexity: O(n) — memo plus recursion stack.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[i] = best sum using arr[0..i] with no two adjacent picks; null = not computed
    private static Long[] memo;

    static long maxNonAdjacentSum(int[] arr) {
        memo = new Long[arr.length];
        return best(arr, arr.length - 1);
    }

    private static long best(int[] arr, int i) {
        if (i < 0) return 0;
        if (memo[i] != null) return memo[i];
        long notPick = best(arr, i - 1);
        long pick = arr[i] + best(arr, i - 2);
        memo[i] = Math.max(notPick, pick);
        return memo[i];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[5,-2,4,7,-1,3] (walkthrough example)", maxNonAdjacentSum(new int[]{5, -2, 4, 7, -1, 3}), 15);
        check("[2,1,4,9]", maxNonAdjacentSum(new int[]{2, 1, 4, 9}), 11);
        check("[3,2,5,10,7]", maxNonAdjacentSum(new int[]{3, 2, 5, 10, 7}), 15);
        check("[-3,-1,-2] (edge: all negative, pick nothing)", maxNonAdjacentSum(new int[]{-3, -1, -2}), 0);
        check("[] (edge: empty array)", maxNonAdjacentSum(new int[]{}), 0);
        check("[7] (edge: single element)", maxNonAdjacentSum(new int[]{7}), 7);
        check("[-5] (edge: single negative)", maxNonAdjacentSum(new int[]{-5}), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
