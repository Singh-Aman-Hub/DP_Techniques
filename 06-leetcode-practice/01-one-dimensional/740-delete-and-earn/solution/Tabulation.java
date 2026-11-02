/**
 * 740. Delete and Earn — Tabulation (bottom-up)
 *
 * Idea: After bucketing (points[v] = v * count(v)), fill dp[v] = max(dp[v-1], dp[v-2] + points[v])
 * from small values upward, exactly like House Robber.
 *
 * Time complexity:  O(n + M) — bucketing is O(n), the loop visits M values once.
 * Space complexity: O(M) — the points and dp arrays.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int deleteAndEarn(int[] nums) {
        int maxValue = 0;
        for (int x : nums) maxValue = Math.max(maxValue, x);
        // points[v] = total earned by taking every copy of value v
        int[] points = new int[Math.max(maxValue + 1, 2)];
        for (int x : nums) points[x] += x;

        // dp[v] = best total using only values 0..v
        int[] dp = new int[points.length];
        dp[0] = points[0];
        dp[1] = Math.max(points[0], points[1]);
        for (int v = 2; v < points.length; v++) {
            dp[v] = Math.max(dp[v - 1], dp[v - 2] + points[v]);
        }
        return dp[points.length - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[3,4,2]", deleteAndEarn(new int[]{3, 4, 2}), 6);
        check("[2,2,3,3,3,4]", deleteAndEarn(new int[]{2, 2, 3, 3, 3, 4}), 9);
        check("[1] (edge)", deleteAndEarn(new int[]{1}), 1);
        check("[1,1,1,2,4,5,5,5,6]", deleteAndEarn(new int[]{1, 1, 1, 2, 4, 5, 5, 5, 6}), 18);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
