/**
 * 2140. Solving Questions With Brainpower — Tabulation (bottom-up)
 *
 * Idea: Fill dp[i] = max(dp[i+1], points[i] + dp[i + brainpower[i] + 1]) from the last question
 * backward, because every state reads only larger indices. No O(1) version exists: the jump
 * may land on any later index, so the whole array is needed.
 *
 * Time complexity:  O(n) — one backward pass, O(1) per question.
 * Space complexity: O(n) — the dp array.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static long mostPoints(int[][] questions) {
        int n = questions.length;
        // dp[i] = best points from questions i..n-1; dp[n] = 0 (nothing left)
        long[] dp = new long[n + 1];
        for (int i = n - 1; i >= 0; i--) {
            int landing = Math.min(n, i + questions[i][1] + 1);   // clamp jumps past the end
            long skip = dp[i + 1];
            long solve = questions[i][0] + dp[landing];
            dp[i] = Math.max(skip, solve);
        }
        return dp[0];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example 1", mostPoints(new int[][]{{3, 2}, {4, 3}, {4, 4}, {2, 5}}), 5);
        check("example 2", mostPoints(new int[][]{{1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}}), 7);
        check("single question (edge)", mostPoints(new int[][]{{1, 10}}), 1);
        check("brainpower 0", mostPoints(new int[][]{{10, 0}, {10, 0}}), 20);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
