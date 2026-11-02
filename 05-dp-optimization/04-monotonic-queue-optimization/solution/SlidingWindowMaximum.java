/**
 * LeetCode 239 — Sliding Window Maximum — the monotonic deque, taught on its own (no DP yet)
 *
 * Idea: keep a deque of INDICES whose values are strictly decreasing from front to back.
 *       - Before pushing index i, pop from the back every index whose value is <= nums[i]:
 *         such an element is older AND not larger, so it can never be a window maximum again.
 *       - Pop from the front any index that has slid out of the window (index <= i - k).
 *       - The front is then the index of the maximum of the current window.
 *
 * Time complexity:  brute: O(n·k) — every window is rescanned.
 *                   deque: O(n) — each index is pushed once and popped at most once, so the
 *                   total number of deque operations over the whole run is at most 2n.
 * Space complexity: O(k) for the deque (it never holds more than one window), plus the output.
 *
 * Run: java SlidingWindowMaximum.java
 */
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;

public class SlidingWindowMaximum {

    static int[] bruteForce(int[] nums, int k) {
        int[] result = new int[nums.length - k + 1];
        for (int start = 0; start + k <= nums.length; start++) {
            int best = Integer.MIN_VALUE;
            for (int i = start; i < start + k; i++) best = Math.max(best, nums[i]);
            result[start] = best;
        }
        return result;
    }

    static int[] withDeque(int[] nums, int k) {
        int[] result = new int[nums.length - k + 1];
        // window holds indices; nums[window] is strictly decreasing from front (oldest) to back (newest)
        Deque<Integer> window = new ArrayDeque<>();
        for (int i = 0; i < nums.length; i++) {
            // 1. drop the front if it fell out of the window [i-k+1 .. i]
            if (!window.isEmpty() && window.peekFirst() <= i - k) window.pollFirst();
            // 2. drop dominated elements: older and not bigger than nums[i] -> useless forever
            while (!window.isEmpty() && nums[window.peekLast()] <= nums[i]) window.pollLast();
            // 3. the newcomer always enters (it may become the maximum after older ones expire)
            window.offerLast(i);
            // 4. once the first full window is formed, the front is its maximum
            if (i >= k - 1) result[i - k + 1] = nums[window.peekFirst()];
        }
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[] example = {1, 3, -1, -3, 5, 3, 6, 7};
        check("brute example k=3", bruteForce(example, 3), new int[]{3, 3, 5, 5, 6, 7});
        check("deque example k=3", withDeque(example, 3), new int[]{3, 3, 5, 5, 6, 7});
        check("deque single element (edge)", withDeque(new int[]{1}, 1), new int[]{1});
        check("deque decreasing k=2", withDeque(new int[]{9, 8, 7, 6}, 2), new int[]{9, 8, 7});
        check("deque whole array k=n", withDeque(new int[]{1, 2, 3, 4}, 4), new int[]{4});
        check("deque duplicates k=2", withDeque(new int[]{5, 5, 5}, 2), new int[]{5, 5});

        Random random = new Random(239);
        int mismatches = 0;
        for (int trial = 0; trial < 500; trial++) {
            int n = 1 + random.nextInt(12);
            int k = 1 + random.nextInt(n);
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = random.nextInt(11) - 5;
            if (!Arrays.equals(bruteForce(nums, k), withDeque(nums, k))) mismatches++;
        }
        String status = mismatches == 0 ? "PASS" : "FAIL";
        System.out.println(status + "  500 random arrays: mismatches brute vs deque -> " + mismatches + " (expected 0)");
        if (mismatches != 0) System.exit(1);
    }

    private static void check(String label, int[] actual, int[] expected) {
        boolean ok = Arrays.equals(actual, expected);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + Arrays.toString(actual)
                + " (expected " + Arrays.toString(expected) + ")");
        if (!ok) System.exit(1);
    }
}
