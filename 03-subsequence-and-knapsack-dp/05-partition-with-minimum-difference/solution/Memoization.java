/**
 * Partition With Minimum Difference — Memoization
 *
 * Idea: explore(i, s1) depends only on the index and the group-1 sum so far, and s1 lies in
 * 0..total for non-negative input. Cache the best final difference reachable from each
 * (i, s1). This is the "index + accumulated quantity" state from topic 01.
 *
 * Time complexity:  O(n * total) — (n + 1) * (total + 1) states, O(1) each.
 * Space complexity: O(n * total) for the memo, plus O(n) recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i][s1] = smallest |s1_final - s2_final| reachable from this state; -1 = not computed
    private static int[][] memo;

    static int minDifference(int[] arr) {
        int total = 0;
        for (int x : arr) total += x;
        memo = new int[arr.length + 1][total + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return explore(arr, 0, 0, total);
    }

    private static int explore(int[] arr, int i, int s1, int total) {
        if (i == arr.length) return Math.abs(total - 2 * s1);
        if (memo[i][s1] != -1) return memo[i][s1];
        int putInGroup1 = explore(arr, i + 1, s1 + arr[i], total);
        int putInGroup2 = explore(arr, i + 1, s1, total);
        memo[i][s1] = Math.min(putInGroup1, putInGroup2);
        return memo[i][s1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,6,11,5] -> {1,11}=12 vs {6,5}=11", minDifference(new int[]{1, 6, 11, 5}), 1);
        check("[3,9,7,3] -> {3,7}=10 vs {9,3}=12", minDifference(new int[]{3, 9, 7, 3}), 2);
        check("[1,2,3,9] -> {1,2,3}=6 vs {9}=9", minDifference(new int[]{1, 2, 3, 9}), 3);
        check("[1,1] -> perfect split", minDifference(new int[]{1, 1}), 0);
        check("[5] (edge: one element, other side empty)", minDifference(new int[]{5}), 5);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
