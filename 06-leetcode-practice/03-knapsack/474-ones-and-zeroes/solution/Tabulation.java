/**
 * 474. Ones and Zeroes — Tabulation (bottom-up)
 *
 * Idea: dp[i][z][o] = most strings choosable from the first i strings within (z, o). Each layer
 * i is built from layer i-1 by skipping or taking string i-1, exactly like 0/1 knapsack.
 *
 * Time complexity:  O(S + L * m * n) — S characters counted, then L*(m+1)*(n+1) cells in O(1).
 * Space complexity: O(L * m * n) — the 3D table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int findMaxForm(String[] strs, int m, int n) {
        int count = strs.length;
        // dp[i][z][o] = best count using the first i strings with at most z zeros and o ones
        int[][][] dp = new int[count + 1][m + 1][n + 1];
        for (int i = 1; i <= count; i++) {
            int c0 = 0, c1 = 0;
            for (char ch : strs[i - 1].toCharArray()) {
                if (ch == '0') c0++;
                else c1++;
            }
            for (int z = 0; z <= m; z++) {
                for (int o = 0; o <= n; o++) {
                    dp[i][z][o] = dp[i - 1][z][o];                        // skip
                    if (c0 <= z && c1 <= o) {
                        dp[i][z][o] = Math.max(dp[i][z][o], dp[i - 1][z - c0][o - c1] + 1);  // take
                    }
                }
            }
        }
        return dp[count][m][n];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example 1", findMaxForm(new String[]{"10", "0001", "111001", "1", "0"}, 5, 3), 4);
        check("example 2", findMaxForm(new String[]{"10", "0", "1"}, 1, 1), 2);
        check("nothing fits (edge)", findMaxForm(new String[]{"111"}, 1, 1), 0);
        check("all fit", findMaxForm(new String[]{"0", "1", "01"}, 5, 5), 3);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
