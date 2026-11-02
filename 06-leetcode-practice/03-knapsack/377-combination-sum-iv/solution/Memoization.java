/**
 * 377. Combination Sum IV — Memoization (top-down)
 *
 * Idea: count(t) = number of ordered sequences summing to t. The last number can be any num <= t,
 * and the rest is an ordered sequence summing to t - num. Different last numbers give different
 * sequences, so add them. Cache by remaining sum.
 *
 * Time complexity:  O(T * m) — T+1 states, m numbers tried in each.
 * Space complexity: O(T) — memo array plus recursion depth up to T.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    private static int[] nums;
    // memo[t] = ordered sequences summing to t; null = unknown (int may wrap, so no -1 sentinel)
    private static Integer[] memo;

    static int combinationSum4(int[] numbers, int target) {
        nums = numbers;
        memo = new Integer[target + 1];
        return count(target);
    }

    private static int count(int remaining) {
        if (remaining == 0) return 1;                      // the empty sequence
        if (memo[remaining] != null) return memo[remaining];
        int total = 0;
        for (int num : nums) {
            if (num <= remaining) total += count(remaining - num);   // num is the LAST element
        }
        memo[remaining] = total;
        return total;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,2,3], 4", combinationSum4(new int[]{1, 2, 3}, 4), 7);
        check("[1,2], 3", combinationSum4(new int[]{1, 2}, 3), 3);
        check("[9], 3 (edge: impossible)", combinationSum4(new int[]{9}, 3), 0);
        check("[1], 1", combinationSum4(new int[]{1}, 1), 1);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
