/**
 * House Robber II (LeetCode 213) — Tabulation (bottom-up DP)
 *
 * Idea: run the House Robber table on nums[0..n-2] and on nums[1..n-1];
 * the answer is the larger of the two final cells.
 *
 * Time complexity:  O(n) — two linear passes.
 * Space complexity: O(n) — one table per pass.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int rob(int[] nums) {
        int n = nums.length;
        if (n == 1) return nums[0];
        return Math.max(robRange(nums, 0, n - 2), robRange(nums, 1, n - 1));
    }

    // straight-line House Robber on nums[lo..hi]
    private static int robRange(int[] nums, int lo, int hi) {
        int len = hi - lo + 1;
        // best[k] = maximum loot from houses lo..lo+k (k counts positions inside the range)
        int[] best = new int[len];
        for (int k = 0; k < len; k++) {
            int money = nums[lo + k];
            int skip = (k >= 1) ? best[k - 1] : 0;
            int robIt = money + ((k >= 2) ? best[k - 2] : 0);
            best[k] = Math.max(skip, robIt);
        }
        return best[len - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[2,7,9,3,1] (walkthrough example)", rob(new int[]{2, 7, 9, 3, 1}), 11);
        check("[2,3,2] (first and last are neighbours)", rob(new int[]{2, 3, 2}), 3);
        check("[1,2,3,1]", rob(new int[]{1, 2, 3, 1}), 4);
        check("[200,3,140,20,10] (best plan avoids the last house)", rob(new int[]{200, 3, 140, 20, 10}), 340);
        check("[5] (edge: one house is its own neighbour-free circle)", rob(new int[]{5}), 5);
        check("[1,1] (edge: two houses)", rob(new int[]{1, 1}), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
