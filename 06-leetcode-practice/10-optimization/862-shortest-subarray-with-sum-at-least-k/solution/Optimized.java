/**
 * 862. Shortest Subarray with Sum at Least K — Monotonic deque on prefix sums, cross-checked
 *
 * Idea: Keep a deque of start indices whose prefix sums are strictly increasing.
 *       (1) Front pops: while prefix[j] - prefix[front] >= k, the front is a valid start for j;
 *           record j - front and drop it — any later end would only give a longer subarray.
 *       (2) Back pops: while prefix[back] >= prefix[j], the old index is useless: j is later
 *           (shorter subarrays) AND has a smaller-or-equal prefix (larger sums). Drop it.
 *       Then push j. Every index enters and leaves the deque at most once.
 *
 * Time complexity:  O(n) — each index is pushed once and popped at most once.
 * Space complexity: O(n) — prefix sums plus the deque.
 *
 * Run: java Optimized.java
 */
import java.util.Random;

public class Optimized {

    static int shortestSubarrayFast(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int j = 0; j < n; j++) prefix[j + 1] = prefix[j] + nums[j];

        int[] deque = new int[n + 1]; // indices into prefix, prefix values strictly increasing front -> back
        int head = 0, tail = 0;       // live part is deque[head..tail-1]
        int best = Integer.MAX_VALUE;
        for (int j = 0; j <= n; j++) {
            while (head < tail && prefix[j] - prefix[deque[head]] >= k) {
                best = Math.min(best, j - deque[head]);
                head++;               // this start can never give a shorter answer later
            }
            while (head < tail && prefix[deque[tail - 1]] >= prefix[j]) {
                tail--;               // dominated: j is later and no larger
            }
            deque[tail++] = j;
        }
        return best == Integer.MAX_VALUE ? -1 : best;
    }

    // The O(n^2) prefix-sum DP from Tabulation.java, kept for cross-checking.
    static int shortestSubarraySlow(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int j = 0; j < n; j++) prefix[j + 1] = prefix[j] + nums[j];
        int best = Integer.MAX_VALUE;
        for (int j = 1; j <= n; j++)
            for (int i = j - 1; i >= 0; i--)
                if (prefix[j] - prefix[i] >= k) { best = Math.min(best, j - i); break; }
        return best == Integer.MAX_VALUE ? -1 : best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] inputs = {{1}, {1, 2}, {2, -1, 2}, {84, -37, 32, 40, 95}, {2, -5, 3, 1, -1, 4}};
        int[] targets = {1, 4, 3, 167, 5};
        int[] expected = {1, -1, 3, 3, 4};
        for (int t = 0; t < inputs.length; t++) {
            check("fast, " + java.util.Arrays.toString(inputs[t]) + " k=" + targets[t], shortestSubarrayFast(inputs[t], targets[t]), expected[t]);
            check("slow, " + java.util.Arrays.toString(inputs[t]) + " k=" + targets[t], shortestSubarraySlow(inputs[t], targets[t]), expected[t]);
        }
        Random random = new Random(862);
        for (int trial = 0; trial < 5000; trial++) {
            int n = 1 + random.nextInt(14);
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = random.nextInt(21) - 8; // mix of negatives and positives
            int k = 1 + random.nextInt(25);
            int slow = shortestSubarraySlow(nums, k), fast = shortestSubarrayFast(nums, k);
            if (slow != fast) check("random " + java.util.Arrays.toString(nums) + " k=" + k, fast, slow);
        }
        System.out.println("PASS  deque solution agrees with O(n^2) DP on 5000 random inputs");
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
