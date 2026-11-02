/**
 * 474. Ones and Zeroes — Space-optimized (one 2D layer)
 *
 * Idea: Drop the string index. For each string, update dp[z][o] with BOTH budgets iterated
 * downward, so dp[z-c0][o-c1] still holds the value from before this string: each string is
 * used at most once (the 0/1 knapsack rule, on two axes).
 *
 * Time complexity:  O(S + L * m * n) — same cells visited as tabulation.
 * Space complexity: O(m * n) — a single (m+1) x (n+1) table.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int findMaxForm(String[] strs, int m, int n) {
        int[][] dp = new int[m + 1][n + 1];   // dp[z][o] = best count so far within budget (z, o)
        for (String s : strs) {
            int c0 = 0, c1 = 0;
            for (char ch : s.toCharArray()) {
                if (ch == '0') c0++;
                else c1++;
            }
            for (int z = m; z >= c0; z--) {            // downward: never reuse this string
                for (int o = n; o >= c1; o--) {
                    dp[z][o] = Math.max(dp[z][o], dp[z - c0][o - c1] + 1);
                }
            }
        }
        return dp[m][n];
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
