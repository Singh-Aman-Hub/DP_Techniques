/**
 * 1130. Minimum Cost Tree From Leaf Values — Memoization (top-down)
 *
 * Idea: the root of a subtree over leaves arr[i..j] splits them at some k into
 * [i..k] and [k+1..j]; its value is max(left) * max(right). f(i, j) = min over
 * k of f(i, k) + f(k+1, j) + that product, cached per range.
 *
 * Time complexity:  O(n^3) — n^2/2 ranges, each tries up to n split points once
 * Space complexity: O(n^2) — memo and maxLeaf tables; recursion depth up to n
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;
public class Memoization {

    // memo[i][j] = min sum of internal nodes for a tree over leaves arr[i..j]; -1 = not computed
    private static int[][] memo;
    private static int[][] maxLeaf;

    static int mctFromLeafValues(int[] arr) {
        int n = arr.length;
        maxLeaf = buildMaxLeaf(arr);
        memo = new int[n][n];
        for (int[] row : memo) Arrays.fill(row, -1);
        return minCost(0, n - 1);
    }

    private static int minCost(int i, int j) {
        if (i == j) return 0;                                 // a single leaf has no internal node
        if (memo[i][j] != -1) return memo[i][j];
        int best = Integer.MAX_VALUE;
        for (int k = i; k < j; k++) {                         // root splits leaves into [i..k] and [k+1..j]
            int cost = minCost(i, k) + minCost(k + 1, j) + maxLeaf[i][k] * maxLeaf[k + 1][j];
            best = Math.min(best, cost);
        }
        memo[i][j] = best;
        return best;
    }

    // maxLeaf[i][j] = largest value in arr[i..j], so each split costs O(1)
    private static int[][] buildMaxLeaf(int[] arr) {
        int n = arr.length;
        int[][] maxLeaf = new int[n][n];
        for (int i = 0; i < n; i++) {
            maxLeaf[i][i] = arr[i];
            for (int j = i + 1; j < n; j++) maxLeaf[i][j] = Math.max(maxLeaf[i][j - 1], arr[j]);
        }
        return maxLeaf;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example [6,2,4]", mctFromLeafValues(new int[]{6, 2, 4}), 32);
        check("[3,1,5,2]", mctFromLeafValues(new int[]{3, 1, 5, 2}), 28);
        check("increasing [1,2,3,4]", mctFromLeafValues(new int[]{1, 2, 3, 4}), 20);
        check("two leaves (edge)", mctFromLeafValues(new int[]{4, 11}), 44);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
