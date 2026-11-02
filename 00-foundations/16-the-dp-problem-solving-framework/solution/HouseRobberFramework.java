/**
 * The DP problem-solving framework — House Robber (LeetCode 198), all four stages
 *
 * Idea: the 7 questions give the state "rob(i) = most money from houses 0..i",
 * the choices "skip house i" or "rob house i (then house i-1 is off limits)",
 * and the recurrence rob(i) = max(rob(i-1), nums[i] + rob(i-2)). The same
 * recurrence is then written as recursion, memoization, tabulation and finally
 * with two variables, because it only reads the last two states.
 *
 * Time complexity:  brute force O(2^n) — two branches per house; the others O(n) —
 *                   n states, O(1) work each.
 * Space complexity: brute force O(n) stack; memo O(n) cache + O(n) stack;
 *                   table O(n); two variables O(1).
 *
 * Run: java HouseRobberFramework.java
 */
import java.util.Arrays;

public class HouseRobberFramework {

    // ---------- 1. brute force ----------
    static int bruteForce(int[] nums, int i) {
        if (i < 0) return 0;              // no houses left
        if (i == 0) return nums[0];       // one house: rob it
        int skip = bruteForce(nums, i - 1);
        int take = nums[i] + bruteForce(nums, i - 2);
        return Math.max(skip, take);
    }

    // ---------- 2. memoization ----------
    static int memo(int[] nums, int i, int[] cache) {
        if (i < 0) return 0;
        if (i == 0) return nums[0];
        if (cache[i] != -1) return cache[i];
        cache[i] = Math.max(memo(nums, i - 1, cache), nums[i] + memo(nums, i - 2, cache));
        return cache[i];
    }

    static int memoization(int[] nums) {
        // cache[i] = most money from houses 0..i; -1 means "not computed yet" (money is never negative)
        int[] cache = new int[nums.length];
        Arrays.fill(cache, -1);
        return memo(nums, nums.length - 1, cache);
    }

    // ---------- 3. tabulation ----------
    static int tabulation(int[] nums) {
        int n = nums.length;
        // rob[i] = most money from houses 0..i without robbing two neighbours
        int[] rob = new int[n];
        rob[0] = nums[0];
        if (n > 1) rob[1] = Math.max(nums[0], nums[1]);
        for (int i = 2; i < n; i++) {
            rob[i] = Math.max(rob[i - 1], nums[i] + rob[i - 2]);
        }
        return rob[n - 1];
    }

    // ---------- 4. two variables ----------
    static int optimized(int[] nums) {
        int twoBack = 0;   // rob[i-2] (0 before the first house)
        int oneBack = 0;   // rob[i-1]
        for (int money : nums) {
            int current = Math.max(oneBack, money + twoBack);
            twoBack = oneBack;   // move the older value first
            oneBack = current;
        }
        return oneBack;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] inputs = {{2, 7, 9, 3, 1}, {1, 2, 3, 1}, {5}, {2, 1, 1, 2}};
        int[] expected = {12, 4, 5, 4};
        for (int k = 0; k < inputs.length; k++) {
            int[] nums = inputs[k];
            String label = Arrays.toString(nums);
            check("brute force " + label, bruteForce(nums, nums.length - 1), expected[k]);
            check("memoization " + label, memoization(nums), expected[k]);
            check("tabulation  " + label, tabulation(nums), expected[k]);
            check("optimized   " + label, optimized(nums), expected[k]);
        }
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
