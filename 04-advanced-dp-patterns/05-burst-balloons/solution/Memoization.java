/**
 * LeetCode 312 Burst Balloons — Memoization (choose the LAST balloon in an open interval)
 *
 * Idea: pad the array with 1 on both sides: a = [1, nums..., 1]. f(i, j) = most coins from
 * bursting every balloon strictly between i and j, while i and j are still standing.
 * If k is the LAST balloon burst in (i, j), then at that moment its neighbours are exactly
 * a[i] and a[j], so it pays a[i] * a[k] * a[j]; before that, (i, k) and (k, j) were cleared
 * independently because k stood between them as a wall.
 *
 * Time complexity:  O(n^3) — O(n^2) intervals x O(n) choices of k.
 * Space complexity: O(n^2) — memo table; recursion depth O(n).
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static int[] padded;  // padded[0] = padded[n+1] = 1, padded[1..n] = nums
    private static int[][] memo;  // memo[i][j]: best coins for the open interval (i, j); -1 = unknown

    static int maxCoins(int[] nums) {
        int n = nums.length;
        padded = new int[n + 2];
        padded[0] = 1;
        padded[n + 1] = 1;
        for (int idx = 0; idx < n; idx++) padded[idx + 1] = nums[idx];
        memo = new int[n + 2][n + 2];
        for (int[] row : memo) Arrays.fill(row, -1);
        return best(0, n + 1);
    }

    private static int best(int i, int j) {
        if (j - i < 2) return 0; // no balloon strictly between i and j
        if (memo[i][j] != -1) return memo[i][j];
        int most = 0;
        for (int k = i + 1; k < j; k++) { // k = the last balloon to burst inside (i, j)
            int coins = best(i, k) + best(k, j) + padded[i] * padded[k] * padded[j];
            most = Math.max(most, coins);
        }
        return memo[i][j] = most;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("nums=[3,1,5,8]", maxCoins(new int[]{3, 1, 5, 8}), 167);
        check("nums=[1,5]", maxCoins(new int[]{1, 5}), 10);
        check("nums=[2,4,3]", maxCoins(new int[]{2, 4, 3}), 33);
        check("edge: one balloon", maxCoins(new int[]{5}), 5);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
