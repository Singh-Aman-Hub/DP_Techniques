/**
 * 152. Maximum Product Subarray — Tabulation (bottom-up)
 *
 * Idea: Fill maxEnd[i] and minEnd[i] left to right from three candidates: start fresh at i,
 * extend the previous max, or extend the previous min (which wins after a negative).
 *
 * Time complexity:  O(n) — one pass, O(1) per index.
 * Space complexity: O(n) — two arrays of length n.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int maxProduct(int[] nums) {
        int n = nums.length;
        int[] maxEnd = new int[n];   // maxEnd[i] = largest product of a subarray ending at i
        int[] minEnd = new int[n];   // minEnd[i] = smallest product of a subarray ending at i
        maxEnd[0] = nums[0];
        minEnd[0] = nums[0];
        int answer = nums[0];
        for (int i = 1; i < n; i++) {
            int fresh = nums[i];
            int extendMax = maxEnd[i - 1] * nums[i];
            int extendMin = minEnd[i - 1] * nums[i];
            maxEnd[i] = Math.max(fresh, Math.max(extendMax, extendMin));
            minEnd[i] = Math.min(fresh, Math.min(extendMax, extendMin));
            answer = Math.max(answer, maxEnd[i]);
        }
        return answer;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[2,3,-2,4]", maxProduct(new int[]{2, 3, -2, 4}), 6);
        check("[-2,0,-1]", maxProduct(new int[]{-2, 0, -1}), 0);
        check("[-2,3,-4]", maxProduct(new int[]{-2, 3, -4}), 24);
        check("[-2] (edge)", maxProduct(new int[]{-2}), -2);
        check("[2,3,-2,4,-1]", maxProduct(new int[]{2, 3, -2, 4, -1}), 48);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
