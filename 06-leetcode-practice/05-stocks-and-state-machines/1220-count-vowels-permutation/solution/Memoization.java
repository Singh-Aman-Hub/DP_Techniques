/**
 * 1220. Count Vowels Permutation — Memoization (top-down)
 *
 * Idea: count(remaining, last) = valid ways to append `remaining` more letters after `last`.
 * Which letter may come next depends only on the last letter, so (remaining, last) is the whole
 * state. Sum over the allowed followers and cache.
 *
 * Time complexity:  O(n) — 5n states, at most 4 followers each.
 * Space complexity: O(n) — memo table plus recursion depth n.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static final int MOD = 1_000_000_007;
    // vowels indexed a=0, e=1, i=2, o=3, u=4; FOLLOWERS[v] = letters allowed right after v
    private static final int[][] FOLLOWERS = {
        {1},            // a -> e
        {0, 2},         // e -> a, i
        {0, 1, 3, 4},   // i -> anything except i
        {2, 4},         // o -> i, u
        {0}             // u -> a
    };
    private static long[][] memo;   // memo[r][v] = completions with r letters left after v; -1 = unknown

    static int countVowelPermutation(int n) {
        memo = new long[n][5];
        for (long[] row : memo) Arrays.fill(row, -1);
        long total = 0;
        for (int first = 0; first < 5; first++) total += count(n - 1, first);
        return (int) (total % MOD);
    }

    private static long count(int remaining, int last) {
        if (remaining == 0) return 1;                     // string is complete
        if (memo[remaining][last] != -1) return memo[remaining][last];
        long ways = 0;
        for (int next : FOLLOWERS[last]) ways += count(remaining - 1, next);
        memo[remaining][last] = ways % MOD;
        return memo[remaining][last];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", countVowelPermutation(1), 5);
        check("n = 2", countVowelPermutation(2), 10);
        check("n = 5", countVowelPermutation(5), 68);
        check("n = 144", countVowelPermutation(144), 18208803);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
