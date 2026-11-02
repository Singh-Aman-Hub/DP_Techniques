/**
 * CSES 1633 Dice Combinations — memoization (top-down)
 *
 * Idea: same recurrence as the brute force, ways(s) = sum of ways(s - face)
 * for face = 1..6, but each ways(s) is stored the first time it is computed.
 * Answers are taken modulo 1_000_000_007 as the problem requires.
 * Warning (topic 12): the recursion is about n calls deep. CSES allows n up to
 * 10^6, which overflows Java's default stack; prefer the tabulated version there.
 *
 * Time complexity:  O(6n) = O(n) — n+1 states, 6 terms each.
 * Space complexity: O(n) cache + O(n) recursion stack.
 *
 * Run: java DiceCombinationsMemoization.java
 */
import java.util.Arrays;

public class DiceCombinationsMemoization {

    private static final long MOD = 1_000_000_007L;

    // memo[s] = number of throw sequences summing to s (mod MOD); -1 means "not computed yet"
    private static long[] memo;

    static long ways(int sum) {
        if (sum == 0) return 1;
        if (sum < 0) return 0;
        if (memo[sum] != -1) return memo[sum];
        long total = 0;
        for (int face = 1; face <= 6; face++) {
            total = (total + ways(sum - face)) % MOD;
        }
        memo[sum] = total;
        return total;
    }

    static long solve(int n) {
        memo = new long[n + 1];
        Arrays.fill(memo, -1);
        return ways(n);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", solve(1), 1);
        check("n = 3", solve(3), 4);
        check("n = 4", solve(4), 8);
        check("n = 7", solve(7), 63);
        check("n = 10", solve(10), 492);
        check("n = 50 (mod 1e9+7)", solve(50), 660_641_036);   // cross-checked with tabulation
        check("n = 1000 (mod 1e9+7)", solve(1000), 937_196_411);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
