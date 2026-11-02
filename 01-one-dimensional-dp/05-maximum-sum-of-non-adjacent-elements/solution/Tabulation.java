/**
 * Maximum Sum of Non-Adjacent Elements — Tabulation (bottom-up DP)
 *
 * Idea: dp[i] = best sum using arr[0..i]. Fill left to right with
 *   dp[i] = max(dp[i-1], arr[i] + dp[i-2]),  where dp[-1] is treated as 0.
 * The full table also lets us walk backwards and recover WHICH indices were picked.
 *
 * Time complexity:  O(n) — one pass to fill, one pass to reconstruct.
 * Space complexity: O(n) — the table.
 *
 * Run: java Tabulation.java
 */
import java.util.ArrayList;
import java.util.List;

public class Tabulation {

    // dp[i] = largest sum of a non-adjacent selection from arr[0..i] (empty selection allowed)
    static long[] buildTable(int[] arr) {
        int n = arr.length;
        long[] dp = new long[n];
        for (int i = 0; i < n; i++) {
            long notPick = (i >= 1) ? dp[i - 1] : 0;          // nothing before index 0
            long pick = arr[i] + ((i >= 2) ? dp[i - 2] : 0);  // dp[-1] and dp[-2] mean "empty" = 0
            dp[i] = Math.max(notPick, pick);
        }
        return dp;
    }

    static long maxNonAdjacentSum(int[] arr) {
        if (arr.length == 0) return 0;
        return buildTable(arr)[arr.length - 1];
    }

    /** Walks the table backwards to list the picked indices in increasing order. */
    static List<Integer> chosenIndices(int[] arr) {
        long[] dp = buildTable(arr);
        List<Integer> picked = new ArrayList<>();
        int i = arr.length - 1;
        while (i >= 0) {
            long notPick = (i >= 1) ? dp[i - 1] : 0;
            if (dp[i] == notPick) {
                i -= 1;                  // skipping arr[i] already achieves the best value
            } else {
                picked.add(0, i);        // arr[i] must have been picked
                i -= 2;                  // so arr[i-1] was not
            }
        }
        return picked;
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
        checkList("chosen indices for [5,-2,4,7,-1,3]",
                  chosenIndices(new int[]{5, -2, 4, 7, -1, 3}), List.of(0, 3, 5));
        checkList("chosen indices for [-3,-1,-2]",
                  chosenIndices(new int[]{-3, -1, -2}), List.of());
    }

    private static void checkList(String label, List<Integer> actual, List<Integer> expected) {
        boolean ok = actual.equals(expected);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
