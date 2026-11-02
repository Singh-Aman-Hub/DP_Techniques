/**
 * 673. Number of Longest Increasing Subsequence — Memoization (top-down)
 *
 * Idea: for each index i compute two numbers: the longest strictly increasing
 * subsequence ending at nums[i] and how many such subsequences reach that
 * length. A strictly longer predecessor replaces the count; an equal one adds.
 *
 * Time complexity:  O(n^2) — n states, each scans its up to n predecessors once
 * Space complexity: O(n) — two memo arrays; recursion depth up to n
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // length[i] = longest LIS ending at nums[i]; 0 = not computed yet
    private static int[] length;
    // count[i] = number of increasing subsequences ending at nums[i] with length[i] elements
    private static int[] count;
    private static int[] values;

    static int findNumberOfLIS(int[] nums) {
        values = nums;
        int n = nums.length;
        length = new int[n];
        count = new int[n];
        int longest = 0;
        for (int i = 0; i < n; i++) longest = Math.max(longest, solve(i));
        int total = 0;
        for (int i = 0; i < n; i++) {
            if (length[i] == longest) total += count[i];   // every ending index that reaches the max
        }
        return total;
    }

    private static int solve(int i) {
        if (length[i] != 0) return length[i];
        int bestLength = 1, ways = 1;                     // nums[i] on its own
        for (int j = 0; j < i; j++) {
            if (values[j] >= values[i]) continue;          // must be strictly increasing
            int candidate = solve(j) + 1;
            if (candidate > bestLength) {
                bestLength = candidate;
                ways = count[j];                           // longer: old ways are no longer longest
            } else if (candidate == bestLength) {
                ways += count[j];                          // tie: these ways also reach the best length
            }
        }
        length[i] = bestLength;
        count[i] = ways;
        return bestLength;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example [1,3,5,4,7]", findNumberOfLIS(new int[]{1, 3, 5, 4, 7}), 2);
        check("all equal [2,2,2,2,2]", findNumberOfLIS(new int[]{2, 2, 2, 2, 2}), 5);
        check("[1,2,4,3,5,4,7,2]", findNumberOfLIS(new int[]{1, 2, 4, 3, 5, 4, 7, 2}), 3);
        check("single element (edge)", findNumberOfLIS(new int[]{1}), 1);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
