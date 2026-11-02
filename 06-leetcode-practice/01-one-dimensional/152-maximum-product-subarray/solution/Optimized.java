/**
 * 152. Maximum Product Subarray — Space-optimized
 *
 * Idea: Only the previous (max, min) pair is ever read, so keep two variables. Compute all
 * three candidates from the OLD values before overwriting either of them.
 *
 * Time complexity:  O(n) — one pass.
 * Space complexity: O(1) — curMax, curMin and the answer.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int maxProduct(int[] nums) {
        int curMax = nums[0];   // largest product of a subarray ending at the current index
        int curMin = nums[0];   // smallest product of a subarray ending at the current index
        int answer = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int fresh = nums[i];
            int extendMax = curMax * nums[i];   // computed from old values on purpose
            int extendMin = curMin * nums[i];
            curMax = Math.max(fresh, Math.max(extendMax, extendMin));
            curMin = Math.min(fresh, Math.min(extendMax, extendMin));
            answer = Math.max(answer, curMax);
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
