/**
 * Matching (AtCoder DP O) — counting perfect matchings with dp[mask]
 *
 * Idea: man i (in order 0, 1, ...) must be paired with a compatible, still-free woman.
 *       ways[mask] = number of ways to pair men 0..bitCount(mask)-1 with exactly the
 *       women in mask. Same shape as LeetCode 1879, with "+" (count) instead of "min".
 *
 * Time complexity:  O(2^n * n) — every mask tries every woman once.
 * Space complexity: O(2^n) — one count per mask.
 *
 * Run: java MatchingCount.java
 */
public class MatchingCount {

    private static final long MOD = 1_000_000_007L;

    /** compatible[i][j] == 1 when man i and woman j may be paired. */
    static long countMatchings(int[][] compatible) {
        int n = compatible.length;
        // ways[mask] = number of valid pairings of the first bitCount(mask) men with the women in mask
        long[] ways = new long[1 << n];
        ways[0] = 1;                                     // pairing nobody: exactly one (empty) way

        for (int mask = 0; mask < (1 << n); mask++) {
            if (ways[mask] == 0) continue;               // unreachable state, nothing to extend
            int man = Integer.bitCount(mask);
            if (man == n) continue;                      // everyone is already paired
            for (int woman = 0; woman < n; woman++) {
                if ((mask & (1 << woman)) != 0 || compatible[man][woman] == 0) continue;
                int next = mask | (1 << woman);
                ways[next] = (ways[next] + ways[mask]) % MOD;
            }
        }
        return ways[(1 << n) - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("sample 1", countMatchings(new int[][]{{0, 1, 1}, {1, 0, 1}, {1, 1, 1}}), 3);
        check("sample 2: nobody compatible", countMatchings(new int[4][4]), 0);
        check("n = 1, compatible", countMatchings(new int[][]{{1}}), 1);
        int[][] allOnes = new int[4][4];
        for (int[] row : allOnes) java.util.Arrays.fill(row, 1);
        check("4x4 all compatible = 4!", countMatchings(allOnes), 24);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
