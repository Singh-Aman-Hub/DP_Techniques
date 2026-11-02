/**
 * Matrix Chain Multiplication — Tabulation that also prints the optimal parenthesization
 *
 * Idea: while filling dp[i][j] we also record split[i][j] = the k that gave the minimum.
 * The optimal order is then rebuilt recursively: the bracket for (i, j) is
 * "(" + bracket(i, split[i][j]) + bracket(split[i][j] + 1, j) + ")", and a single matrix
 * is printed as its letter (A, B, C, ...).
 *
 * Time complexity:  O(n^3) for the table + O(n) to rebuild the string.
 * Space complexity: O(n^2) — the dp and split tables.
 *
 * Run: java PrintParenthesization.java
 */
public class PrintParenthesization {

    private static int[][] split; // split[i][j]: best last-multiplication point k for A_i..A_j

    static String optimalOrder(int[] dims) {
        int n = dims.length - 1;
        long[][] dp = new long[n + 1][n + 1];
        split = new int[n + 1][n + 1];
        for (int length = 2; length <= n; length++) {
            for (int i = 1; i + length - 1 <= n; i++) {
                int j = i + length - 1;
                dp[i][j] = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    long cost = dp[i][k] + dp[k + 1][j] + (long) dims[i - 1] * dims[k] * dims[j];
                    if (cost < dp[i][j]) { // strict '<' keeps the first (smallest) k on ties
                        dp[i][j] = cost;
                        split[i][j] = k;
                    }
                }
            }
        }
        return bracket(1, n);
    }

    private static String bracket(int i, int j) {
        if (i == j) return String.valueOf((char) ('A' + i - 1));
        int k = split[i][j];
        return "(" + bracket(i, k) + bracket(k + 1, j) + ")";
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("dims=[40,20,30,10,30]", optimalOrder(new int[]{40, 20, 30, 10, 30}), "((A(BC))D)");
        check("dims=[10,30,5,60]", optimalOrder(new int[]{10, 30, 5, 60}), "((AB)C)");
        check("dims=[10,20,30,40,30]", optimalOrder(new int[]{10, 20, 30, 40, 30}), "(((AB)C)D)");
        check("edge: one matrix", optimalOrder(new int[]{10, 20}), "A");
    }

    private static void check(String label, String actual, String expected) {
        String status = actual.equals(expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!actual.equals(expected)) System.exit(1);
    }
}
