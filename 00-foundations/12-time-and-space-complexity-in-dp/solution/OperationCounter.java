/**
 * Time and space complexity in DP — counting the work instead of guessing it
 *
 * Idea: "time = number of states x work per state" is a claim we can measure.
 * This file instruments three problems (Climbing Stairs, grid paths and 0/1
 * knapsack) with counters, and compares
 *   - plain recursion: counts every call, which grows exponentially,
 *   - memoization:     counts how many states are actually computed (cache misses),
 *   - tabulation:      counts how many table cells are filled.
 * The memo and table counts equal "number of states", which is the whole point.
 *
 * Time complexity:  brute force O(2^n)-ish; memo/tab O(states x work per state):
 *                   O(n) stairs, O(rows*cols) grid, O(items*capacity) knapsack.
 * Space complexity: memo/tab use one cell per state, plus the recursion stack for memo.
 *
 * Run: java OperationCounter.java
 */
import java.util.Arrays;

public class OperationCounter {

    // Counters are reset before every measurement.
    private static long calls;   // how many times a recursive function was entered
    private static long computed; // how many states were actually computed (not a cache hit, not a base case)

    // ======================= Climbing Stairs =======================

    static long stairsBrute(int n) {
        calls++;
        if (n <= 1) return 1; // ways(0) = ways(1) = 1
        return stairsBrute(n - 1) + stairsBrute(n - 2);
    }

    static long stairsMemo(int n, long[] memo) {
        calls++;
        if (n <= 1) return 1;
        if (memo[n] != -1) return memo[n]; // cache hit: O(1), no new work
        computed++;
        memo[n] = stairsMemo(n - 1, memo) + stairsMemo(n - 2, memo);
        return memo[n];
    }

    static long stairsTab(int n) {
        // ways[i] = number of ways to reach step i
        long[] ways = new long[Math.max(n + 1, 2)];
        ways[0] = 1;
        ways[1] = 1;
        computed += 2; // base cells are filled too
        for (int i = 2; i <= n; i++) {
            ways[i] = ways[i - 1] + ways[i - 2];
            computed++;
        }
        return ways[n];
    }

    // ======================= Grid paths (right/down) =======================

    static long gridBrute(int r, int c) {
        calls++;
        if (r == 0 || c == 0) return 1;
        return gridBrute(r - 1, c) + gridBrute(r, c - 1);
    }

    static long gridMemo(int r, int c, long[][] memo) {
        calls++;
        if (r == 0 || c == 0) return 1;
        if (memo[r][c] != -1) return memo[r][c];
        computed++;
        memo[r][c] = gridMemo(r - 1, c, memo) + gridMemo(r, c - 1, memo);
        return memo[r][c];
    }

    static long gridTab(int rows, int cols) {
        // paths[r][c] = number of right/down paths from (0,0) to (r,c)
        long[][] paths = new long[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                paths[r][c] = (r == 0 || c == 0) ? 1 : paths[r - 1][c] + paths[r][c - 1];
                computed++;
            }
        }
        return paths[rows - 1][cols - 1];
    }

    // ======================= 0/1 Knapsack =======================

    /** best value using items i..n-1 with remainingCapacity left */
    static int knapBrute(int[] weights, int[] values, int i, int remainingCapacity) {
        calls++;
        if (i == weights.length) return 0;
        int skip = knapBrute(weights, values, i + 1, remainingCapacity);
        int take = Integer.MIN_VALUE;
        if (weights[i] <= remainingCapacity) {
            take = values[i] + knapBrute(weights, values, i + 1, remainingCapacity - weights[i]);
        }
        return Math.max(skip, take);
    }

    static int knapTab(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        // best[i][w] = best value using only the first i items with capacity w
        int[][] best = new int[n + 1][capacity + 1];
        computed += capacity + 1; // row 0 (no items) is all zeros, still part of the table
        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= capacity; w++) {
                best[i][w] = best[i - 1][w];
                if (weights[i - 1] <= w) {
                    best[i][w] = Math.max(best[i][w], values[i - 1] + best[i - 1][w - weights[i - 1]]);
                }
                computed++;
            }
        }
        return best[n][capacity];
    }

    private static void reset() {
        calls = 0;
        computed = 0;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        // --- Climbing Stairs, n = 5 ---
        reset();
        check("stairs brute n=5 answer", stairsBrute(5), 8);
        check("stairs brute n=5 calls", calls, 15);          // 2 * ways(5) - 1
        reset();
        check("stairs brute n=20 answer", stairsBrute(20), 10946);
        check("stairs brute n=20 calls", calls, 21891);       // 2 * 10946 - 1

        reset();
        long[] memo = new long[6];
        Arrays.fill(memo, -1);
        check("stairs memo n=5 answer", stairsMemo(5, memo), 8);
        check("stairs memo n=5 states computed", computed, 4); // states 2,3,4,5
        check("stairs memo n=5 calls", calls, 9);              // 1 + 2 per computed state

        reset();
        check("stairs tab n=5 answer", stairsTab(5), 8);
        check("stairs tab n=5 cells filled", computed, 6);     // cells 0..5

        reset();
        check("stairs tab n=1 (edge) answer", stairsTab(1), 1);

        // --- Grid 3 x 3 ---
        reset();
        check("grid brute 3x3 answer", gridBrute(2, 2), 6);
        check("grid brute 3x3 calls", calls, 11);              // 2 * 6 - 1
        reset();
        gridBrute(2, 6);
        check("grid brute 3x7 calls", calls, 55);              // 2 * 28 - 1

        reset();
        long[][] gridCache = new long[3][3];
        for (long[] row : gridCache) Arrays.fill(row, -1);
        check("grid memo 3x3 answer", gridMemo(2, 2, gridCache), 6);
        check("grid memo 3x3 states computed", computed, 4);   // (1,1) (1,2) (2,1) (2,2)

        reset();
        check("grid tab 3x3 answer", gridTab(3, 3), 6);
        check("grid tab 3x3 cells filled", computed, 9);       // 3 * 3
        reset();
        check("grid tab 1x1 (edge) answer", gridTab(1, 1), 1);

        // --- Knapsack: weights [1,3,4], values [15,20,30], capacity 4 ---
        int[] weights = {1, 3, 4};
        int[] values = {15, 20, 30};
        reset();
        check("knapsack brute answer", knapBrute(weights, values, 0, 4), 35);
        check("knapsack brute calls", calls, 12);
        reset();
        check("knapsack tab answer", knapTab(weights, values, 4), 35);
        check("knapsack tab cells filled", computed, 20);      // (3 + 1) * (4 + 1)
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
