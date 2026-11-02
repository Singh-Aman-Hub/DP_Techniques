/**
 * Partition With Minimum Difference — Tabulation (read the last row of subset sum)
 *
 * Idea: build the subset-sum table dp[i][s] for s in 0..total/2. The LAST row says which
 * sums are reachable using any of the elements. Scan it from total/2 downwards: the first
 * reachable s1 is the group-1 sum closest to half, and the difference is total - 2 * s1.
 * We only need s1 <= total/2 because the smaller group of any split is at most half.
 *
 * Time complexity:  O(n * total) — n * (total/2 + 1) cells, plus an O(total) scan.
 * Space complexity: O(n * total) — the full boolean table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int minDifference(int[] arr) {
        int total = 0;
        for (int x : arr) total += x;
        int half = total / 2;
        int n = arr.length;
        // dp[i][s] = can a subset of arr[0..i-1] sum to exactly s?
        boolean[][] dp = new boolean[n + 1][half + 1];
        dp[0][0] = true;
        for (int i = 1; i <= n; i++) {
            int value = arr[i - 1];
            for (int s = 0; s <= half; s++) {
                dp[i][s] = dp[i - 1][s] || (value <= s && dp[i - 1][s - value]);
            }
        }
        for (int s1 = half; s1 >= 0; s1--) {
            if (dp[n][s1]) return total - 2 * s1;    // s1 <= half, so this is never negative
        }
        return total;                                // unreachable: dp[n][0] is always true
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
