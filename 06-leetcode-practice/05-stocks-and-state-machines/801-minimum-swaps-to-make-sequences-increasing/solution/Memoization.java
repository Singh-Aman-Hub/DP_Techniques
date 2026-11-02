/**
 * 801. Minimum Swaps To Make Sequences Increasing — Memoization (top-down)
 *
 * Idea: solve(i, prevSwapped) = fewest swaps for indices i..n-1, given whether index i-1 was
 * swapped. Index i can stay or be swapped, as long as both arrays stay strictly increasing
 * against the (possibly swapped) pair at i-1. Only (i, prevSwapped) matters, so cache it.
 *
 * Time complexity:  O(n) — 2n states, O(1) each.
 * Space complexity: O(n) — memo table plus recursion depth n.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    private static final int INF = Integer.MAX_VALUE / 2;
    private static int[] first, second;
    private static Integer[][] memo;   // memo[i][prevSwapped ? 1 : 0] = min swaps for i..n-1

    static int minSwap(int[] nums1, int[] nums2) {
        first = nums1;
        second = nums2;
        memo = new Integer[nums1.length][2];
        return solve(0, false);
    }

    private static int solve(int i, boolean prevSwapped) {
        if (i == first.length) return 0;
        int key = prevSwapped ? 1 : 0;
        if (memo[i][key] != null) return memo[i][key];
        boolean keepOK = true, swapOK = true;
        if (i > 0) {
            int prevA = prevSwapped ? second[i - 1] : first[i - 1];   // what nums1[i-1] holds now
            int prevB = prevSwapped ? first[i - 1] : second[i - 1];   // what nums2[i-1] holds now
            keepOK = prevA < first[i] && prevB < second[i];
            swapOK = prevA < second[i] && prevB < first[i];
        }
        int best = INF;
        if (keepOK) best = solve(i + 1, false);
        if (swapOK) best = Math.min(best, 1 + solve(i + 1, true));
        memo[i][key] = best;
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example 1", minSwap(new int[]{1, 3, 5, 4}, new int[]{1, 2, 3, 7}), 1);
        check("example 2", minSwap(new int[]{0, 3, 5, 8, 9}, new int[]{2, 1, 4, 6, 9}), 1);
        check("single element (edge)", minSwap(new int[]{1}, new int[]{1}), 0);
        check("swap needed early", minSwap(new int[]{0, 4, 4, 5, 9}, new int[]{0, 1, 6, 8, 10}), 1);
        check("two elements", minSwap(new int[]{3, 2}, new int[]{1, 4}), 1);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
