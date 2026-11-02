/**
 * 1130. Minimum Cost Tree From Leaf Values — Tabulation (bottom-up)
 *
 * Idea: dp[i][j] = min over split k of dp[i][k] + dp[k+1][j] +
 * maxLeaf[i][k] * maxLeaf[k+1][j]. Every range depends only on shorter
 * ranges, so fill by increasing length; dp[i][i] = 0.
 *
 * Time complexity:  O(n^3) — length x start x split point
 * Space complexity: O(n^2) — dp and maxLeaf tables
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int mctFromLeafValues(int[] arr) {
        int n = arr.length;
        int[][] maxLeaf = buildMaxLeaf(arr);
        // dp[i][j] = min sum of internal node values for a tree over leaves arr[i..j]
        int[][] dp = new int[n][n];                           // dp[i][i] = 0 already
        for (int length = 2; length <= n; length++) {
            for (int i = 0; i + length - 1 < n; i++) {
                int j = i + length - 1;
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    int cost = dp[i][k] + dp[k + 1][j] + maxLeaf[i][k] * maxLeaf[k + 1][j];
                    dp[i][j] = Math.min(dp[i][j], cost);
                }
            }
        }
        return dp[0][n - 1];
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
