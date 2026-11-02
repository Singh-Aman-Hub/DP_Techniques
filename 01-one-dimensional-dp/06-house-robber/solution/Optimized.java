/**
 * House Robber (LeetCode 198) — Space-optimized DP
 *
 * Idea: maxLoot[i] reads only the previous two cells, so keep two variables.
 *
 * Time complexity:  O(n) — one pass.
 * Space complexity: O(1) — two variables.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int rob(int[] nums) {
        int lootTwoBack = 0;   // best from houses 0..i-2
        int lootOneBack = 0;   // best from houses 0..i-1
        for (int money : nums) {
            int current = Math.max(lootOneBack, money + lootTwoBack); // skip vs. rob
            lootTwoBack = lootOneBack;
            lootOneBack = current;
        }
        return lootOneBack;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[2,7,9,3,1] (walkthrough example)", rob(new int[]{2, 7, 9, 3, 1}), 12);
        check("[1,2,3,1]", rob(new int[]{1, 2, 3, 1}), 4);
        check("[2,1,1,2] (best picks are 3 apart)", rob(new int[]{2, 1, 1, 2}), 4);
        check("[5] (edge: one house)", rob(new int[]{5}), 5);
        check("[0,0] (edge: nothing to steal)", rob(new int[]{0, 0}), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
