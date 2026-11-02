/**
 * 1220. Count Vowels Permutation — Tabulation (bottom-up)
 *
 * Idea: dp[len][v] = valid strings of length len ending in vowel v. Each entry sums the previous
 * length's counts over the letters allowed to come BEFORE v (the rule graph with edges reversed).
 *
 * Time complexity:  O(n) — n lengths x 5 vowels x at most 3 predecessors.
 * Space complexity: O(n) — the n x 5 table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    private static final int MOD = 1_000_000_007;
    private static final int A = 0, E = 1, I = 2, O = 3, U = 4;

    static int countVowelPermutation(int n) {
        // dp[len][v] = number of valid strings of length len that end with vowel v
        long[][] dp = new long[n + 1][5];
        for (int v = 0; v < 5; v++) dp[1][v] = 1;
        for (int len = 2; len <= n; len++) {
            long[] prev = dp[len - 1];
            dp[len][A] = (prev[E] + prev[I] + prev[U]) % MOD;   // a may follow e, i, u
            dp[len][E] = (prev[A] + prev[I]) % MOD;             // e may follow a, i
            dp[len][I] = (prev[E] + prev[O]) % MOD;             // i may follow e, o
            dp[len][O] = prev[I] % MOD;                         // o may follow i
            dp[len][U] = (prev[I] + prev[O]) % MOD;             // u may follow i, o
        }
        long total = 0;
        for (int v = 0; v < 5; v++) total += dp[n][v];
        return (int) (total % MOD);
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
