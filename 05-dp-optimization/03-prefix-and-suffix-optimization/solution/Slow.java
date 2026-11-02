/**
 * AtCoder DP Contest M — Candies — the SLOW versions (brute force + O(N·K²) tabulation)
 *
 * Idea: dp[i][j] = number of ways to hand out exactly j candies to the first i children,
 *       where child i may receive 0..limits[i-1] candies. To fill one cell we loop over
 *       every possible amount x given to child i and add dp[i-1][j-x]. That inner loop
 *       is a RANGE SUM over the previous row, which is exactly what the optimized version
 *       replaces with a prefix-sum lookup.
 *
 * Time complexity:  bruteForce: exponential — it tries every amount for every child.
 *                   slowTable:  O(N·K·min(K, maxLimit)) = O(N·K²) worst case — N·(K+1) cells,
 *                   each summing up to K+1 previous cells.
 * Space complexity: bruteForce: O(N) recursion depth. slowTable: O(K) — two rows of length K+1.
 *
 * Run: java Slow.java
 */
public class Slow {

    static final long MOD = 1_000_000_007L;

    /** Plain recursion: ways to distribute `remaining` candies among children child..n-1. */
    static long bruteForce(int[] limits, int child, int remaining) {
        if (child == limits.length) {
            return remaining == 0 ? 1 : 0;   // valid only if every candy was handed out
        }
        long ways = 0;
        // Try every legal amount for this child; never give more than what is left.
        for (int give = 0; give <= Math.min(limits[child], remaining); give++) {
            ways += bruteForce(limits, child + 1, remaining - give);
        }
        return ways % MOD;
    }

    /** Bottom-up DP whose inner loop re-adds a whole range of the previous row: O(N·K²). */
    static long slowTable(int[] limits, int totalCandies) {
        // prev[j] = number of ways to give exactly j candies to the children processed so far
        long[] prev = new long[totalCandies + 1];
        prev[0] = 1;                          // zero children, zero candies: one (empty) way
        for (int limit : limits) {
            long[] cur = new long[totalCandies + 1];
            for (int j = 0; j <= totalCandies; j++) {
                long sum = 0;
                // this child takes x candies, the earlier children took j - x
                for (int x = 0; x <= Math.min(limit, j); x++) {
                    sum += prev[j - x];
                }
                cur[j] = sum % MOD;
            }
            prev = cur;
        }
        return prev[totalCandies];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        // limits {1,2,3}, K = 4: (0,1,3),(0,2,2),(1,0,3),(1,1,2),(1,2,1) -> 5
        check("brute  limits=[1,2,3] K=4", bruteForce(new int[]{1, 2, 3}, 0, 4), 5);
        check("table  limits=[1,2,3] K=4", slowTable(new int[]{1, 2, 3}, 4), 5);
        // one child can hold at most 9, so 10 candies is impossible
        check("table  limits=[9] K=10 (impossible)", slowTable(new int[]{9}, 10), 0);
        // edge case: nobody can take anything and there is nothing to give -> exactly one way
        check("table  limits=[0,0] K=0 (edge)", slowTable(new int[]{0, 0}, 0), 1);
        // limits {1,1}, K = 1: (1,0) or (0,1) -> 2
        check("brute  limits=[1,1] K=1", bruteForce(new int[]{1, 1}, 0, 1), 2);
        check("table  limits=[1,1] K=1", slowTable(new int[]{1, 1}, 1), 2);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
