/**
 * Climbing Stairs — Memoization, produced by the 4-step recipe
 *
 * Idea: apply the recipe to the brute force mechanically.
 *   1. Changing parameter: n (one int, range 0..n)        -> 1D cache
 *   2. Cache sized by the range: long[n + 1], filled with -1 ("not computed")
 *   3. Check the cache right after the base cases
 *   4. Store the result in the cache before returning it
 * A second version shows the "negative index" case: a recursion that may step
 * below 0 must reject the negative index BEFORE touching the cache.
 *
 * Time complexity:  O(n) — n + 1 states, each computed once with O(1) work.
 * Space complexity: O(n) — the cache (n + 1 cells) plus up to n stack frames.
 *
 * Run: java ClimbingStairsMemoization.java
 */
import java.util.Arrays;

public class ClimbingStairsMemoization {

    // memo[i] = number of ways to reach step i; -1 means "not computed yet"
    private static long[] memo;

    static long climbStairs(int n) {
        memo = new long[n + 1];          // step 2: one cell per possible value of i (0..n)
        Arrays.fill(memo, -1);           // -1 can never be a real answer (counts are >= 1)
        return ways(n);
    }

    private static long ways(int i) {
        if (i <= 1) return 1;                    // base cases stay exactly as in the brute force
        if (memo[i] != -1) return memo[i];       // step 3: check
        long result = ways(i - 1) + ways(i - 2); // the original recurrence, unchanged
        memo[i] = result;                        // step 4: store
        return result;
    }

    // ---------- variant: the recursion can reach a negative index ----------

    // memoNeg[i] = ways to reach step i using steps of size 1, 2 or 3; -1 = not computed
    private static long[] memoNeg;

    /**
     * Steps of size 1, 2 or 3. Writing the base cases as "i == 0 -> 1, i < 0 -> 0"
     * means ways3(i - 3) can be called with i - 3 = -1 or -2. Those calls must be
     * answered by the base case before the cache is read, or memoNeg[-1] would throw
     * ArrayIndexOutOfBoundsException.
     */
    static long climbWithThreeSizes(int n) {
        memoNeg = new long[n + 1];
        Arrays.fill(memoNeg, -1);
        return ways3(n);
    }

    private static long ways3(int i) {
        if (i < 0) return 0;               // overshot the ground: not a valid plan (checked FIRST)
        if (i == 0) return 1;              // exactly at the ground: one (empty) plan
        if (memoNeg[i] != -1) return memoNeg[i];
        memoNeg[i] = ways3(i - 1) + ways3(i - 2) + ways3(i - 3);
        return memoNeg[i];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 5", climbStairs(5), 8);
        check("n = 10", climbStairs(10), 89);
        check("n = 45 (brute force would need ~3.7 billion calls)", climbStairs(45), 1_836_311_903L);
        check("n = 1 (edge)", climbStairs(1), 1);
        check("n = 0 (edge)", climbStairs(0), 1);

        // Cross-check against the brute force on every small n.
        for (int n = 0; n <= 20; n++) {
            check("memo == brute force for n = " + n, climbStairs(n), bruteForce(n));
        }

        // Steps of 1, 2 or 3: 1, 1, 2, 4, 7, 13 for n = 0..5 (each is the sum of the previous three).
        check("sizes {1,2,3}, n = 5", climbWithThreeSizes(5), 13);
        check("sizes {1,2,3}, n = 1 (calls reach -1 and -2)", climbWithThreeSizes(1), 1);
        check("sizes {1,2,3}, n = 4", climbWithThreeSizes(4), 7);
    }

    /** The original recursion, kept here only to cross-check the memoized answers. */
    private static long bruteForce(int n) {
        if (n <= 1) return 1;
        return bruteForce(n - 1) + bruteForce(n - 2);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
