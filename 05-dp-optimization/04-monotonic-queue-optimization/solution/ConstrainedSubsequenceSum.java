/**
 * LeetCode 1425 — Constrained Subsequence Sum — slow O(n·k) DP and monotonic-deque O(n) DP
 *
 * Idea: dp[i] = largest sum of a valid non-empty subsequence whose LAST chosen element is nums[i].
 *       The previous chosen element is at most k positions back, or there is none (start fresh):
 *           dp[i] = nums[i] + max(0, max(dp[i-k .. i-1])).
 *       The "max(0, ...)" is the only difference from Jump Game VI: a negative best previous
 *       subsequence is simply not used. The window max comes from a monotonic deque.
 *       The answer is the maximum dp[i] over all i (the subsequence may end anywhere).
 *
 * Time complexity:  slow: O(n·k) — a window scan per index. fast: O(n) — amortized deque.
 * Space complexity: O(n) for dp; the deque holds at most k+1 indices.
 *
 * Run: java ConstrainedSubsequenceSum.java
 */
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class ConstrainedSubsequenceSum {

    static long slow(int[] nums, int k) {
        int n = nums.length;
        long[] dp = new long[n];   // dp[i] = best sum of a valid subsequence ending at index i
        long answer = Long.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            long bestPrevious = 0;   // 0 = "start a new subsequence at i"
            for (int j = Math.max(0, i - k); j < i; j++) bestPrevious = Math.max(bestPrevious, dp[j]);
            dp[i] = nums[i] + bestPrevious;
            answer = Math.max(answer, dp[i]);
        }
        return answer;
    }

    static long fast(int[] nums, int k) {
        int n = nums.length;
        long[] dp = new long[n];
        Deque<Integer> candidates = new ArrayDeque<>();   // indices, dp strictly decreasing
        long answer = Long.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            if (!candidates.isEmpty() && candidates.peekFirst() < i - k) candidates.pollFirst();
            long bestPrevious = candidates.isEmpty() ? 0 : Math.max(0, dp[candidates.peekFirst()]);
            dp[i] = nums[i] + bestPrevious;
            answer = Math.max(answer, dp[i]);
            while (!candidates.isEmpty() && dp[candidates.peekLast()] <= dp[i]) candidates.pollLast();
            candidates.offerLast(i);
        }
        return answer;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("slow [10,2,-10,5,20] k=2", slow(new int[]{10, 2, -10, 5, 20}, 2), 37);   // 10+2+5+20
        check("fast [10,2,-10,5,20] k=2", fast(new int[]{10, 2, -10, 5, 20}, 2), 37);
        check("fast all negative [-1,-2,-3] k=1 (edge)", fast(new int[]{-1, -2, -3}, 1), -1);
        check("fast [10,-2,-10,-5,20] k=2", fast(new int[]{10, -2, -10, -5, 20}, 2), 23);  // 10-2-5+20
        check("fast single element", fast(new int[]{4}, 1), 4);

        Random random = new Random(1425);
        int mismatches = 0;
        for (int trial = 0; trial < 1000; trial++) {
            int n = 1 + random.nextInt(15);
            int k = 1 + random.nextInt(n);
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = random.nextInt(21) - 10;
            if (slow(nums, k) != fast(nums, k)) mismatches++;
        }
        check("1000 random cases: mismatches slow vs fast", mismatches, 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
