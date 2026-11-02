/**
 * 377. Combination Sum IV — Tabulation (bottom-up), with the 518 contrast
 *
 * Idea: dp[t] = sum of dp[t - num]. Looping over amounts OUTSIDE and numbers INSIDE lets every
 * number be the last element at every amount, so different orders are counted separately
 * (permutations). Swapping the loops (numbers outside) counts each multiset once, which is
 * 518. Coin Change II; combinationsLike518 is included only to show that difference.
 * Already a 1D array, so there is no separate Optimized version.
 *
 * Time complexity:  O(T * m) — T amounts times m numbers.
 * Space complexity: O(T) — one array of length T+1.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int combinationSum4(int[] nums, int target) {
        // dp[t] = number of ORDERED sequences from nums that sum to t
        int[] dp = new int[target + 1];
        dp[0] = 1;
        for (int t = 1; t <= target; t++) {            // amount outside -> order matters
            for (int num : nums) {
                if (num <= t) dp[t] += dp[t - num];    // int wrap-around is harmless, see README
            }
        }
        return dp[target];
    }

    /** Same items and target, loops swapped: counts UNORDERED combinations (problem 518). */
    static int combinationsLike518(int[] nums, int target) {
        int[] dp = new int[target + 1];                // dp[t] = multisets summing to t
        dp[0] = 1;
        for (int num : nums) {                         // item outside -> one canonical order
            for (int t = num; t <= target; t++) {
                dp[t] += dp[t - num];
            }
        }
        return dp[target];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,2,3], 4", combinationSum4(new int[]{1, 2, 3}, 4), 7);
        check("[1,2], 3", combinationSum4(new int[]{1, 2}, 3), 3);
        check("[9], 3 (edge: impossible)", combinationSum4(new int[]{9}, 3), 0);
        check("[1], 1", combinationSum4(new int[]{1}, 1), 1);
        check("contrast 518: [1,2], 3", combinationsLike518(new int[]{1, 2}, 3), 2);
        check("contrast 518: [1,2,3], 4", combinationsLike518(new int[]{1, 2, 3}, 4), 4);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
