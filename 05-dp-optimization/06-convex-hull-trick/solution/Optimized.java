/**
 * Frog 3 (AtCoder DP Contest, Task Z) — Convex Hull Trick with a monotone deque, O(N)
 *
 * Idea: expand dp[j] + (h[i] - h[j])^2 + C into
 *     (-2*h[j]) * h[i]  +  (dp[j] + h[j]^2)   +   h[i]^2 + C
 *      slope m_j   x        intercept b_j          same for every j
 * so dp[i] = h[i]^2 + C + min over j of (m_j * x + b_j) evaluated at x = h[i].
 * Every earlier stone is a LINE; we need the lowest line at x. Heights strictly increase, so
 * slopes are added in decreasing order and queries x arrive in increasing order: a deque of
 * the useful lines (the lower envelope) answers each query in amortized O(1).
 *
 * Time complexity:  O(N) — each line is pushed once and popped at most once.
 * Space complexity: O(N) — the deque of lines and the dp array.
 *
 * Overflow: b_j = dp[j] + h[j]^2 <= (h_max - h_min)^2 + C + h_max^2 <= 3e12 for h <= 1e6, C <= 1e12
 * (dp[j] is at most the cost of one direct jump from stone 0),
 * and slope differences are <= 2e6, so the cross products in isUseless() are < 6e18,
 * below Long.MAX_VALUE ~ 9.22e18. Plain long arithmetic is safe.
 *
 * Run: java Optimized.java
 */
import java.util.Random;

public class Optimized {

    // The deque stores lines; line t is y = slope[t] * x + intercept[t].
    private static long[] slope;
    private static long[] intercept;

    private static long valueAt(int line, long x) {
        return slope[line] * x + intercept[line];
    }

    /**
     * With slopes s1 > s2 > s3, the middle line is never strictly the lowest when
     * line 3 overtakes line 1 no later than line 2 does:
     *     x(1,3) <= x(1,2)   where x(a,b) = (b_b - b_a) / (s_a - s_b).
     * Both denominators are positive, so we cross-multiply instead of dividing.
     */
    private static boolean isUseless(int first, int middle, int last) {
        long left = (intercept[last] - intercept[first]) * (slope[first] - slope[middle]);
        long right = (intercept[middle] - intercept[first]) * (slope[first] - slope[last]);
        return left <= right;
    }

    static long minFrogCost(long[] heights, long jumpCost) {
        int n = heights.length;
        long[] dp = new long[n]; // dp[i] = cheapest total cost to stand on stone i
        slope = new long[n];
        intercept = new long[n];
        int head = 0, tail = 0; // deque = indices head..tail-1 of slope/intercept

        // stone 0 becomes the first line
        slope[tail] = -2 * heights[0];
        intercept[tail] = dp[0] + heights[0] * heights[0];
        tail++;

        for (int i = 1; i < n; i++) {
            long x = heights[i];
            // queries only move right, so a front line beaten at x stays beaten forever
            while (tail - head >= 2 && valueAt(head + 1, x) <= valueAt(head, x)) head++;
            dp[i] = valueAt(head, x) + x * x + jumpCost;

            // add the line for stone i, dropping back lines that can never be lowest again
            long newSlope = -2 * heights[i];
            long newIntercept = dp[i] + heights[i] * heights[i];
            slope[tail] = newSlope;
            intercept[tail] = newIntercept;
            while (tail - head >= 2 && isUseless(tail - 2, tail - 1, tail)) {
                // remove the middle line by sliding the new line one slot left
                slope[tail - 1] = newSlope;
                intercept[tail - 1] = newIntercept;
                tail--;
            }
            tail++;
        }
        return dp[n - 1];
    }

    // ---------- private copy of the slow O(N^2) DP, used only for cross-checking ----------
    private static long slowMinFrogCost(long[] heights, long jumpCost) {
        int n = heights.length;
        long[] dp = new long[n];
        for (int i = 1; i < n; i++) {
            dp[i] = Long.MAX_VALUE;
            for (int j = 0; j < i; j++) {
                long d = heights[i] - heights[j];
                dp[i] = Math.min(dp[i], dp[j] + d * d + jumpCost);
            }
        }
        return dp[n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("sample 1: h=1..5, C=6", minFrogCost(new long[]{1, 2, 3, 4, 5}, 6), 20);
        check("sample 2: two far stones, C=1e12",
                minFrogCost(new long[]{500000, 1000000}, 1000000000000L), 1250000000000L);
        check("walkthrough example, C=5",
                minFrogCost(new long[]{1, 3, 4, 5, 10, 11, 12, 13}, 5), 62);
        check("edge: one stone, nothing to do", minFrogCost(new long[]{7}, 100), 0);

        // largest legal magnitudes (h up to 1e6, C up to 1e12) on a short array, so the slow DP is fast
        long[] extreme = {1, 2, 500000, 999999, 1000000};
        check("extreme magnitudes agree with slow DP",
                minFrogCost(extreme, 1000000000000L), slowMinFrogCost(extreme, 1000000000000L));

        // randomized cross-check: strictly increasing heights, as the problem guarantees
        Random random = new Random(26);
        int trials = 500;
        for (int t = 0; t < trials; t++) {
            int n = 1 + random.nextInt(40);
            long[] heights = new long[n];
            long current = 1 + random.nextInt(5);
            for (int i = 0; i < n; i++) {
                heights[i] = current;
                current += 1 + random.nextInt(random.nextBoolean() ? 3 : 50);
            }
            long jumpCost = 1 + random.nextInt(200);
            long fast = minFrogCost(heights, jumpCost);
            long slow = slowMinFrogCost(heights, jumpCost);
            if (fast != slow) {
                System.out.println("FAIL  random trial " + t + ": fast=" + fast + " slow=" + slow
                        + " heights=" + java.util.Arrays.toString(heights) + " C=" + jumpCost);
                System.exit(1);
            }
        }
        System.out.println("PASS  " + trials + " random inputs: convex hull trick == slow DP");
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
