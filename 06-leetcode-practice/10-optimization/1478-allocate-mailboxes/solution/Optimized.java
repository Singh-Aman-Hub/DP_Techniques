/**
 * 1478. Allocate Mailboxes — Divide & Conquer optimization, cross-checked against the O(n^2 k) DP
 *
 * Idea: In row b, let opt[j] be the split i that achieves dp[b][j]. Because the median cost
 *       satisfies the quadrangle inequality (adding a far house never makes an earlier split
 *       better), opt[j] is non-decreasing in j. So compute the middle column mid first, find its
 *       best split, and recurse: columns left of mid only search splits <= opt[mid],
 *       columns right of mid only search splits >= opt[mid].
 *
 * Time complexity:  O(k * n log n) — each row is a recursion of depth log n, O(n) work per level.
 * Space complexity: O(n^2) for the cost table, O(n) for the rows.
 *
 * Run: java Optimized.java
 */
import java.util.Arrays;
import java.util.Random;

public class Optimized {

    private static final int INF = Integer.MAX_VALUE / 2;
    private static int[][] cost;
    private static int[] previous, current;

    static int minDistanceFast(int[] housesInput, int k) {
        int[] houses = housesInput.clone();
        Arrays.sort(houses);
        int n = houses.length;
        cost = medianCosts(houses);
        previous = new int[n + 1];
        Arrays.fill(previous, INF);
        previous[0] = 0;
        for (int b = 1; b <= k; b++) {
            current = new int[n + 1];
            Arrays.fill(current, INF);
            current[0] = 0;
            computeRow(1, n, 0, n - 1);
            previous = current;
        }
        return previous[n];
    }

    // Fill current[lo..hi], knowing every optimal split for these columns lies in [optLo, optHi].
    private static void computeRow(int lo, int hi, int optLo, int optHi) {
        if (lo > hi) return;
        int mid = (lo + hi) / 2;
        int bestValue = INF, bestSplit = optLo;
        for (int i = optLo; i <= Math.min(optHi, mid - 1); i++) {
            if (previous[i] == INF) continue;
            int candidate = previous[i] + cost[i][mid - 1];
            if (candidate < bestValue) {
                bestValue = candidate;
                bestSplit = i;
            }
        }
        current[mid] = bestValue;
        computeRow(lo, mid - 1, optLo, bestSplit);   // left columns: splits cannot pass opt[mid]
        computeRow(mid + 1, hi, bestSplit, optHi);   // right columns: splits cannot go before opt[mid]
    }

    // The plain O(n^2 * k) tabulation, kept for cross-checking.
    static int minDistanceSlow(int[] housesInput, int k) {
        int[] houses = housesInput.clone();
        Arrays.sort(houses);
        int n = houses.length;
        int[][] localCost = medianCosts(houses);
        int[] prev = new int[n + 1];
        Arrays.fill(prev, INF);
        prev[0] = 0;
        for (int b = 1; b <= k; b++) {
            int[] cur = new int[n + 1];
            Arrays.fill(cur, INF);
            cur[0] = 0;
            for (int j = 1; j <= n; j++)
                for (int i = 0; i < j; i++)
                    if (prev[i] != INF) cur[j] = Math.min(cur[j], prev[i] + localCost[i][j - 1]);
            prev = cur;
        }
        return prev[n];
    }

    static int[][] medianCosts(int[] houses) {
        int n = houses.length;
        int[][] table = new int[n][n];
        for (int length = 2; length <= n; length++) {
            for (int i = 0; i + length - 1 < n; i++) {
                int j = i + length - 1;
                table[i][j] = (i + 1 <= j - 1 ? table[i + 1][j - 1] : 0) + houses[j] - houses[i];
            }
        }
        return table;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] inputs = {{1, 4, 8, 10, 20}, {2, 3, 5, 12, 18}, {7, 4, 6, 1}, {3, 6, 14, 10}, {5}};
        int[] boxes = {3, 2, 1, 4, 1};
        int[] expected = {5, 9, 8, 0, 0};
        for (int t = 0; t < inputs.length; t++) {
            check("fast, " + Arrays.toString(inputs[t]) + " k=" + boxes[t], minDistanceFast(inputs[t], boxes[t]), expected[t]);
            check("slow, " + Arrays.toString(inputs[t]) + " k=" + boxes[t], minDistanceSlow(inputs[t], boxes[t]), expected[t]);
        }
        Random random = new Random(1478);
        for (int trial = 0; trial < 3000; trial++) {
            int n = 1 + random.nextInt(15), k = 1 + random.nextInt(n);
            int[] houses = new int[n];
            for (int i = 0; i < n; i++) houses[i] = 1 + random.nextInt(60); // duplicates are allowed here on purpose
            int slow = minDistanceSlow(houses, k), fast = minDistanceFast(houses, k);
            if (slow != fast) check("random " + Arrays.toString(houses) + " k=" + k, fast, slow);
        }
        System.out.println("PASS  D&C-optimized DP agrees with O(n^2 k) DP on 3000 random inputs");
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
