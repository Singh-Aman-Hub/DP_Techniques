/**
 * Longest Increasing Subsequence (LeetCode 300) — O(n^2) tabulation + printing the LIS
 *
 * Idea: dp[i] = length of the longest increasing subsequence that ENDS at index i.
 *       Such a subsequence is either nums[i] alone (length 1) or some increasing
 *       subsequence ending at an earlier j with nums[j] < nums[i], extended by nums[i].
 *       parent[i] remembers which j gave the best extension, so following parent
 *       pointers from the best ending index rebuilds one LIS (backwards).
 *
 * Time complexity:  O(n^2) — for each i we scan all j < i.
 * Space complexity: O(n) — dp and parent arrays (rebuilding the LIS adds O(n)).
 *
 * Run: java Tabulation.java
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Tabulation {

    static int lengthOfLIS(int[] nums) {
        return longestIncreasingSubsequence(nums).size();
    }

    static List<Integer> longestIncreasingSubsequence(int[] nums) {
        int n = nums.length;
        List<Integer> lis = new ArrayList<>();
        if (n == 0) return lis;

        int[] dp = new int[n];       // dp[i] = LIS length among subsequences ending exactly at i
        int[] parent = new int[n];   // parent[i] = previous index in that best subsequence (-1 = none)
        Arrays.fill(dp, 1);          // nums[i] alone is always an increasing subsequence
        Arrays.fill(parent, -1);

        int bestEnd = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                // strict '>' on dp keeps the FIRST j that achieves the best length
                if (nums[j] < nums[i] && dp[j] + 1 > dp[i]) {
                    dp[i] = dp[j] + 1;
                    parent[i] = j;
                }
            }
            if (dp[i] > dp[bestEnd]) bestEnd = i;
        }

        for (int k = bestEnd; k != -1; k = parent[k]) lis.add(nums[k]);   // collected backwards
        Collections.reverse(lis);
        return lis;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[10,9,2,5,3,7,101,18]", lengthOfLIS(new int[]{10, 9, 2, 5, 3, 7, 101, 18}), 4);
        check("[0,1,0,3,2,3]", lengthOfLIS(new int[]{0, 1, 0, 3, 2, 3}), 4);
        check("[7,7,7,7,7,7,7] (strictly increasing!)", lengthOfLIS(new int[]{7, 7, 7, 7, 7, 7, 7}), 1);
        check("[3,4,5,1]", lengthOfLIS(new int[]{3, 4, 5, 1}), 3);
        check("[5] (edge: single element)", lengthOfLIS(new int[]{5}), 1);

        checkList("print LIS of [10,9,2,5,3,7,101,18]",
                longestIncreasingSubsequence(new int[]{10, 9, 2, 5, 3, 7, 101, 18}), List.of(2, 5, 7, 101));
        checkList("print LIS of [0,1,0,3,2,3]",
                longestIncreasingSubsequence(new int[]{0, 1, 0, 3, 2, 3}), List.of(0, 1, 2, 3));
        checkList("print LIS of [3,4,5,1]",
                longestIncreasingSubsequence(new int[]{3, 4, 5, 1}), List.of(3, 4, 5));
    }

    private static void checkList(String label, List<Integer> actual, List<Integer> expected) {
        boolean ok = actual.equals(expected);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
