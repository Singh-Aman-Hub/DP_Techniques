/**
 * Maximum Sum of Non-Adjacent Elements — Space-optimized DP
 *
 * Idea: dp[i] reads only dp[i-1] and dp[i-2], so two variables suffice.
 * Starting both at 0 encodes "the empty selection" for indices before 0,
 * so no special cases are needed.
 *
 * Time complexity:  O(n) — one pass.
 * Space complexity: O(1) — two variables.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static long maxNonAdjacentSum(int[] arr) {
        long bestTwoBack = 0;   // dp[i-2]: best using arr[0..i-2]
        long bestOneBack = 0;   // dp[i-1]: best using arr[0..i-1]
        for (int value : arr) {
            long current = Math.max(bestOneBack, value + bestTwoBack); // skip vs. pick
            bestTwoBack = bestOneBack;
            bestOneBack = current;
        }
        return bestOneBack;
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
