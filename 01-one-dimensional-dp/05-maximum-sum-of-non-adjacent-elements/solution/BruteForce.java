/**
 * Maximum Sum of Non-Adjacent Elements — Brute force recursion (pick / not-pick)
 *
 * Idea: best(i) = largest sum of a selection from arr[0..i] with no two chosen
 * indices adjacent (choosing nothing is allowed, sum 0). For the last element
 * arr[i] there are two choices:
 *   not pick: arr[i] is unused, the rest is any valid selection of arr[0..i-1];
 *   pick:     arr[i] is used, so arr[i-1] is forbidden; add best(i-2).
 * Plain recursion explores every valid selection.
 *
 * Time complexity:  O(2^n) — two branches per index, no reuse (about 1.618^n calls).
 * Space complexity: O(n) — recursion depth.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static long maxNonAdjacentSum(int[] arr) {
        return best(arr, arr.length - 1);
    }

    private static long best(int[] arr, int i) {
        if (i < 0) return 0;                       // no elements left: the empty selection
        long notPick = best(arr, i - 1);           // skip arr[i]
        long pick = arr[i] + best(arr, i - 2);     // take arr[i], jump over arr[i-1]
        return Math.max(notPick, pick);
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
