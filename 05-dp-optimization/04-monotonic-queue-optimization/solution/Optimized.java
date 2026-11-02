/**
 * LeetCode 1696 — Jump Game VI — monotonic deque optimization, O(n)
 *
 * Idea: dp[i] = nums[i] + max(dp[i-k .. i-1]). The window [i-k, i-1] slides right by one
 *       each step, exactly like Sliding Window Maximum. Keep a deque of indices whose dp values
 *       are strictly decreasing; its front is always the best index inside the window.
 *       Order per step: (1) expire the front if it left the window, (2) read the front to compute
 *       dp[i], (3) pop dominated indices from the back, (4) push i.
 *
 * Time complexity:  O(n) — each index enters the deque once and leaves at most once.
 * Space complexity: O(n) for dp (the deque holds at most k+1 indices).
 *
 * Run: java Optimized.java
 */
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class Optimized {

    static long maxResult(int[] nums, int k) {
        int n = nums.length;
        // dp[i] = maximum score of a path from index 0 that ends at index i
        long[] dp = new long[n];
        dp[0] = nums[0];
        // candidates: indices with strictly decreasing dp values, all inside the current window
        Deque<Integer> candidates = new ArrayDeque<>();
        candidates.offerLast(0);
        for (int i = 1; i < n; i++) {
            // (1) index i-k-1 and older can no longer jump to i
            while (candidates.peekFirst() < i - k) candidates.pollFirst();
            // (2) the front holds max(dp[i-k .. i-1])
            dp[i] = nums[i] + dp[candidates.peekFirst()];
            // (3) an older index with dp <= dp[i] will never beat i again: discard it
            while (!candidates.isEmpty() && dp[candidates.peekLast()] <= dp[i]) candidates.pollLast();
            // (4) i is a candidate for the next k positions
            candidates.offerLast(i);
        }
        return dp[n - 1];
    }

    /** Private copy of the O(n·k) version, used only for the randomized cross-check. */
    private static long slowTable(int[] nums, int k) {
        long[] dp = new long[nums.length];
        dp[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            long bestPrevious = Long.MIN_VALUE;
            for (int j = Math.max(0, i - k); j < i; j++) bestPrevious = Math.max(bestPrevious, dp[j]);
            dp[i] = nums[i] + bestPrevious;
        }
        return dp[nums.length - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example [1,-1,-2,4,-7,3] k=2", maxResult(new int[]{1, -1, -2, 4, -7, 3}, 2), 7);
        check("[10,-5,-2,4,0,3] k=3", maxResult(new int[]{10, -5, -2, 4, 0, 3}, 3), 17);
        check("[1,-5,-20,4,-1,3,-6,-3] k=2", maxResult(new int[]{1, -5, -20, 4, -1, 3, -6, -3}, 2), 0);
        check("single element (edge)", maxResult(new int[]{5}, 1), 5);
        check("k=1 forces every step: 1+2+3", maxResult(new int[]{1, 2, 3}, 1), 6);

        Random random = new Random(1696);
        int mismatches = 0;
        for (int trial = 0; trial < 1000; trial++) {
            int n = 1 + random.nextInt(15);
            int k = 1 + random.nextInt(n);
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = random.nextInt(21) - 10;
            if (maxResult(nums, k) != slowTable(nums, k)) mismatches++;
        }
        check("1000 random cases: mismatches vs slow", mismatches, 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
