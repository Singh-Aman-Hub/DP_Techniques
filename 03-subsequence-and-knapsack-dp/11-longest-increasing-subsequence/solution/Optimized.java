/**
 * Longest Increasing Subsequence (LeetCode 300) — O(n log n) "tails" + binary search
 *
 * Idea: tails[k] = the SMALLEST possible last value of an increasing subsequence of
 *       length k + 1 seen so far. tails is always strictly increasing, so for each x
 *       we binary-search the first tails[k] >= x:
 *         - none exists  -> x extends the longest subsequence: append it;
 *         - otherwise    -> replace tails[k] with x (a length-(k+1) subsequence can now
 *                           end with a smaller value, which only helps later elements).
 *       The number of filled slots is the LIS length. tails itself is NOT necessarily
 *       a real subsequence (see the [3,4,5,1] test: tails ends as [1,4,5]).
 *
 * Time complexity:  O(n log n) — n elements, one O(log n) binary search each.
 * Space complexity: O(n) — the tails array.
 *
 * Run: java Optimized.java
 */
import java.util.Arrays;

public class Optimized {

    static int lengthOfLIS(int[] nums) {
        return finalTails(nums).length;
    }

    // Returns the tails array after processing all of nums (useful for teaching/debugging).
    static int[] finalTails(int[] nums) {
        int[] tails = new int[nums.length];   // tails[k] = smallest tail of an increasing subsequence of length k+1
        int size = 0;                          // number of valid entries in tails
        for (int x : nums) {
            // lower bound: first position in tails[0..size) whose value is >= x
            int low = 0, high = size;
            while (low < high) {
                int mid = (low + high) / 2;
                if (tails[mid] < x) low = mid + 1;
                else high = mid;
            }
            tails[low] = x;              // replace, or append when low == size
            if (low == size) size++;
        }
        return Arrays.copyOf(tails, size);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[10,9,2,5,3,7,101,18]", lengthOfLIS(new int[]{10, 9, 2, 5, 3, 7, 101, 18}), 4);
        check("[0,1,0,3,2,3]", lengthOfLIS(new int[]{0, 1, 0, 3, 2, 3}), 4);
        check("[7,7,7,7,7,7,7] (strictly increasing!)", lengthOfLIS(new int[]{7, 7, 7, 7, 7, 7, 7}), 1);
        check("[3,4,5,1]", lengthOfLIS(new int[]{3, 4, 5, 1}), 3);
        check("[5] (edge: single element)", lengthOfLIS(new int[]{5}), 1);

        // tails is not the LIS: [1,4,5] is not even a subsequence of [3,4,5,1]
        int[] tails = finalTails(new int[]{3, 4, 5, 1});
        boolean ok = Arrays.equals(tails, new int[]{1, 4, 5});
        System.out.println((ok ? "PASS" : "FAIL") + "  tails of [3,4,5,1] -> " + Arrays.toString(tails) + " (expected [1, 4, 5])");
        if (!ok) System.exit(1);

        int[] tails2 = finalTails(new int[]{10, 9, 2, 5, 3, 7, 101, 18});
        ok = Arrays.equals(tails2, new int[]{2, 3, 7, 18});
        System.out.println((ok ? "PASS" : "FAIL") + "  tails of [10,9,2,5,3,7,101,18] -> " + Arrays.toString(tails2) + " (expected [2, 3, 7, 18])");
        if (!ok) System.exit(1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
