/**
 * Climbing Stairs generalized — steps of size 1, 2, ..., k
 *
 * Idea: the last move onto step i has size j for some j in 1..k (as long as
 * j <= i). Each choice of j leads to a different set of climbs, so
 *     ways[i] = ways[i-1] + ways[i-2] + ... + ways[i-k]   (skip negative indices).
 * k = 2 is LeetCode 70, k = 6 is "dice combinations" (CSES 1633).
 *
 * Two versions:
 *   waysTable        — direct O(n*k) tabulation.
 *   waysSlidingWindow — keeps the sum of the last k cells in one variable,
 *                       adding the newest and subtracting the one that leaves
 *                       the window, so each step is O(1).
 *
 * Time complexity:  O(n*k) for waysTable, O(n) for waysSlidingWindow.
 * Space complexity: O(n) for both (the table); an O(k) ring buffer would also work.
 *
 * Run: java GeneralizedKSteps.java
 */
public class GeneralizedKSteps {

    static long waysTable(int n, int k) {
        // ways[i] = number of ordered step sequences (each step 1..k) that sum to i
        long[] ways = new long[n + 1];
        ways[0] = 1;                                   // the empty climb
        for (int i = 1; i <= n; i++) {
            for (int step = 1; step <= k && step <= i; step++) {
                ways[i] += ways[i - step];             // last move had size "step"
            }
        }
        return ways[n];
    }

    static long waysSlidingWindow(int n, int k) {
        long[] ways = new long[n + 1];
        ways[0] = 1;
        long windowSum = 0;          // sum of ways[i-k .. i-1] (only existing indices)
        for (int i = 1; i <= n; i++) {
            windowSum += ways[i - 1];                  // newest cell enters the window
            if (i - 1 - k >= 0) windowSum -= ways[i - 1 - k]; // oldest cell leaves it
            ways[i] = windowSum;
        }
        return ways[n];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] tests = {
            // n, k, expected
            {5, 2, 8},     // LeetCode 70 again
            {4, 3, 7},     // 1,1,2,4,7
            {3, 6, 4},     // 1+1+1, 1+2, 2+1, 3
            {7, 6, 63},    // 1+2+4+8+16+32
            {10, 1, 1},    // edge: only 1-steps -> one way
            {0, 3, 1},     // edge: empty staircase -> one (empty) way
        };
        for (int[] t : tests) {
            String label = "n = " + t[0] + ", k = " + t[1];
            check(label + " (table)", waysTable(t[0], t[1]), t[2]);
            check(label + " (sliding window)", waysSlidingWindow(t[0], t[1]), t[2]);
        }
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
