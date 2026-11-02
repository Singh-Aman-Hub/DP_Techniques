/**
 * 152. Maximum Product Subarray — Memoization (top-down)
 *
 * Idea: For each index i compute the largest AND smallest product of a subarray ending at i.
 * A negative nums[i] turns the smallest previous product into the largest, so both are needed.
 * The answer is the maximum of maxEnd(i) over all i.
 *
 * Time complexity:  O(n) — 2n cached values, each computed once in O(1).
 * Space complexity: O(n) — two memo arrays plus a recursion chain of depth n.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    private static int[] nums;
    private static Integer[] maxMemo;   // maxMemo[i] = largest product of a subarray ending at i
    private static Integer[] minMemo;   // minMemo[i] = smallest product of a subarray ending at i

    static int maxProduct(int[] input) {
        nums = input;
        maxMemo = new Integer[nums.length];
        minMemo = new Integer[nums.length];
        int answer = nums[0];
        for (int i = 0; i < nums.length; i++) {
            answer = Math.max(answer, maxEnd(i));
        }
        return answer;
    }

    private static int maxEnd(int i) {
        if (maxMemo[i] == null) fill(i);
        return maxMemo[i];
    }

    private static int minEnd(int i) {
        if (minMemo[i] == null) fill(i);
        return minMemo[i];
    }

    // computes both extremes for index i together, since they share the same three candidates
    private static void fill(int i) {
        if (i == 0) {
            maxMemo[0] = nums[0];
            minMemo[0] = nums[0];
            return;
        }
        int fresh = nums[i];
        int extendMax = maxEnd(i - 1) * nums[i];
        int extendMin = minEnd(i - 1) * nums[i];
        maxMemo[i] = Math.max(fresh, Math.max(extendMax, extendMin));
        minMemo[i] = Math.min(fresh, Math.min(extendMax, extendMin));
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
