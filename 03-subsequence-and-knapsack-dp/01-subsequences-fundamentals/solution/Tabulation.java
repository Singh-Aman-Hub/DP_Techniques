/**
 * Count Subsequences With Sum K — Tabulation (bottom-up DP)
 *
 * Idea: dp[i][s] = number of subsequences of the FIRST i elements whose sum is exactly s.
 * Element i-1 is either left out (dp[i-1][s] ways) or included (dp[i-1][s - arr[i-1]] ways,
 * only if s >= arr[i-1]). Row 0 (no elements) has exactly one subsequence: the empty one, sum 0.
 * Elements must be non-negative so that sums stay inside 0..K.
 *
 * Time complexity:  O(n * K) — (n + 1) * (K + 1) cells, each filled with O(1) work.
 * Space complexity: O(n * K) — the full table.
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;

public class Tabulation {

    static long countWithSum(int[] arr, int k) {
        int n = arr.length;
        // dp[i][s] = number of subsequences of arr[0..i-1] with sum exactly s
        long[][] dp = new long[n + 1][k + 1];
        dp[0][0] = 1;                             // the empty subsequence
        for (int i = 1; i <= n; i++) {
            int value = arr[i - 1];
            for (int s = 0; s <= k; s++) {
                long skip = dp[i - 1][s];
                long take = (s >= value) ? dp[i - 1][s - value] : 0;
                dp[i][s] = skip + take;
            }
        }
        return dp[n][k];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,2,3], K=3", countWithSum(new int[]{1, 2, 3}, 3), 2);
        check("[1,2,1], K=2", countWithSum(new int[]{1, 2, 1}, 2), 2);
        check("[1,1,1,1], K=2", countWithSum(new int[]{1, 1, 1, 1}, 2), 6);
        check("[0,1], K=1", countWithSum(new int[]{0, 1}, 1), 2);
        check("[1,2,3], K=0 (edge: empty subsequence)", countWithSum(new int[]{1, 2, 3}, 0), 1);
        check("[1,2,3], K=7", countWithSum(new int[]{1, 2, 3}, 7), 0);
        int[] ones = new int[30];
        Arrays.fill(ones, 1);
        check("30 ones, K=15 = C(30,15)", countWithSum(ones, 15), 155117520L);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
