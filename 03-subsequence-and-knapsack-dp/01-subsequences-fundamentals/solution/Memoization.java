/**
 * Count Subsequences With Sum K — Memoization (top-down DP)
 *
 * Idea: the brute-force call count(index, sumSoFar) depends only on those two numbers,
 * not on WHICH elements produced sumSoFar. Different paths that reach the same
 * (index, sumSoFar) therefore have the same answer, so we cache it. Elements must be
 * non-negative: then a sumSoFar above K can never come back down, and we prune it,
 * which keeps sumSoFar inside 0..K and lets us use a plain 2D array as the cache.
 *
 * Time complexity:  O(n * K) — at most (n + 1) * (K + 1) distinct states, each computed once with O(1) work.
 * Space complexity: O(n * K) for the memo table, plus O(n) recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[index][sumSoFar] = number of ways to finish from this state; -1 means "not computed yet"
    private static long[][] memo;

    static long countWithSum(int[] arr, int k) {
        memo = new long[arr.length + 1][k + 1];
        for (long[] row : memo) Arrays.fill(row, -1);
        return count(arr, 0, 0, k);
    }

    private static long count(int[] arr, int index, int sumSoFar, int k) {
        if (sumSoFar > k) return 0;                       // non-negative elements: overshoot is final
        if (index == arr.length) return (sumSoFar == k) ? 1 : 0;
        if (memo[index][sumSoFar] != -1) return memo[index][sumSoFar];

        long take = count(arr, index + 1, sumSoFar + arr[index], k);
        long skip = count(arr, index + 1, sumSoFar, k);
        memo[index][sumSoFar] = take + skip;
        return memo[index][sumSoFar];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,2,3], K=3", countWithSum(new int[]{1, 2, 3}, 3), 2);
        check("[1,2,1], K=2", countWithSum(new int[]{1, 2, 1}, 2), 2);
        check("[1,1,1,1], K=2", countWithSum(new int[]{1, 1, 1, 1}, 2), 6);
        check("[0,1], K=1", countWithSum(new int[]{0, 1}, 1), 2);
        check("[1,2,3], K=0 (edge: empty subsequence)", countWithSum(new int[]{1, 2, 3}, 0), 1);
        check("[1,2,3], K=7", countWithSum(new int[]{1, 2, 3}, 7), 0);
        // 30 ones, K = 15: C(30,15) = 155117520 — far too many paths for brute force, instant here
        check("30 ones, K=15 = C(30,15)", countWithSum(filled(30, 1), 15), 155117520L);
    }

    private static int[] filled(int n, int value) {
        int[] a = new int[n];
        Arrays.fill(a, value);
        return a;
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
