/**
 * 1137. N-th Tribonacci Number — Memoization (top-down)
 *
 * Idea: Write the definition T(n) = T(n-1) + T(n-2) + T(n-3) as recursion, and cache
 * every answer so each index is computed only once instead of exponentially many times.
 *
 * Time complexity:  O(n) — n+1 distinct states, each does O(1) work after its children are cached.
 * Space complexity: O(n) — the memo array plus a recursion stack of depth n.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i] stores T(i); -1 means "not computed yet" (every real value is >= 0)
    private static long[] memo;

    static long tribonacci(int n) {
        memo = new long[n + 1];
        Arrays.fill(memo, -1);
        return solve(n);
    }

    private static long solve(int i) {
        if (i == 0) return 0;
        if (i <= 2) return 1;                 // T1 = T2 = 1
        if (memo[i] != -1) return memo[i];    // cache hit: skip the whole subtree
        memo[i] = solve(i - 1) + solve(i - 2) + solve(i - 3);
        return memo[i];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 0 (edge)", tribonacci(0), 0);
        check("n = 2 (edge)", tribonacci(2), 1);
        check("n = 4", tribonacci(4), 4);
        check("n = 6", tribonacci(6), 13);
        check("n = 25", tribonacci(25), 1389537);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
