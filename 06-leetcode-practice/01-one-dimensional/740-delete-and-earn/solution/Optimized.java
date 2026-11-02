/**
 * 740. Delete and Earn — Space-optimized
 *
 * Idea: The House Robber recurrence only reads dp[v-1] and dp[v-2], so two rolling
 * variables replace the dp array. The bucket array is still needed.
 *
 * Time complexity:  O(n + M) — bucketing plus one pass over values.
 * Space complexity: O(M) — only for the points buckets; the DP itself is O(1).
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int deleteAndEarn(int[] nums) {
        int maxValue = 0;
        for (int x : nums) maxValue = Math.max(maxValue, x);
        int[] points = new int[maxValue + 1];   // points[v] = v * count(v)
        for (int x : nums) points[x] += x;

        int bestUpToPrev2 = 0;                  // dp[v-2]
        int bestUpToPrev = 0;                   // dp[v-1]
        for (int v = 0; v <= maxValue; v++) {
            int current = Math.max(bestUpToPrev, bestUpToPrev2 + points[v]);
            bestUpToPrev2 = bestUpToPrev;
            bestUpToPrev = current;
        }
        return bestUpToPrev;
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
