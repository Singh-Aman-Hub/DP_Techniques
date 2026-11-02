/**
 * Digit Sum (AtCoder DP S) — Digit DP, tabulation (bottom-up) and the two-row version
 *
 * Idea: walk the positions of K from left to right and keep, for every
 *       (tight, sumMod) pair, how many prefixes are in that situation.
 *         count[pos][tight][sumMod] = number of ways to choose the first pos digits so that
 *         they sum to sumMod (mod D) and are tight (equal to K's first pos digits) or not.
 *       Start: count[0][tight=1][0] = 1 (the empty prefix equals K's empty prefix).
 *       Each prefix is extended by every allowed digit. At the end, prefixes with
 *       sumMod = 0 are the numbers we want (including 0, which we subtract).
 *       Row pos+1 only reads row pos, so countUpToTwoRows keeps just two rows.
 *
 * Time complexity:  O(L * 2 * D * 10) — every position, state and digit once.
 * Space complexity: O(L * 2 * D) for the full table; O(2 * D) for the two-row version.
 *
 * Run: java DigitSumTabulation.java
 */
public class DigitSumTabulation {

    private static final long MOD = 1_000_000_007L;
    private static final int LOOSE = 0, TIGHT = 1;

    static long countUpTo(String k, int d) {
        int length = k.length();
        // count[pos][tight][sumMod]: see header
        long[][][] count = new long[length + 1][2][d];
        count[0][TIGHT][0] = 1;

        for (int pos = 0; pos < length; pos++) {
            int kDigit = k.charAt(pos) - '0';
            for (int tight = 0; tight <= 1; tight++) {
                for (int sumMod = 0; sumMod < d; sumMod++) {
                    long ways = count[pos][tight][sumMod];
                    if (ways == 0) continue;
                    int limit = (tight == TIGHT) ? kDigit : 9;
                    for (int digit = 0; digit <= limit; digit++) {
                        int nextTight = (tight == TIGHT && digit == kDigit) ? TIGHT : LOOSE;
                        int nextMod = (sumMod + digit) % d;
                        count[pos + 1][nextTight][nextMod] = (count[pos + 1][nextTight][nextMod] + ways) % MOD;
                    }
                }
            }
        }
        long withZero = (count[length][LOOSE][0] + count[length][TIGHT][0]) % MOD;
        return (withZero - 1 + MOD) % MOD;
    }

    /** Same DP, keeping only the current row and the next row. */
    static long countUpToTwoRows(String k, int d) {
        long[][] current = new long[2][d];
        current[TIGHT][0] = 1;
        for (int pos = 0; pos < k.length(); pos++) {
            int kDigit = k.charAt(pos) - '0';
            long[][] next = new long[2][d];
            for (int tight = 0; tight <= 1; tight++) {
                for (int sumMod = 0; sumMod < d; sumMod++) {
                    long ways = current[tight][sumMod];
                    if (ways == 0) continue;
                    int limit = (tight == TIGHT) ? kDigit : 9;
                    for (int digit = 0; digit <= limit; digit++) {
                        int nextTight = (tight == TIGHT && digit == kDigit) ? TIGHT : LOOSE;
                        int nextMod = (sumMod + digit) % d;
                        next[nextTight][nextMod] = (next[nextTight][nextMod] + ways) % MOD;
                    }
                }
            }
            current = next;
        }
        return ((current[LOOSE][0] + current[TIGHT][0]) % MOD - 1 + MOD) % MOD;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        String[] ks = {"30", "235", "1", "100", "1000000009", "98765432109876543210"};
        int[] ds = {4, 1, 2, 10, 1, 58};
        long[] expected = {6, 235, 0, 9, 2, 635270834};
        for (int t = 0; t < ks.length; t++) {
            check("K=" + ks[t] + ", D=" + ds[t] + " table",    countUpTo(ks[t], ds[t]), expected[t]);
            check("K=" + ks[t] + ", D=" + ds[t] + " two rows", countUpToTwoRows(ks[t], ds[t]), expected[t]);
        }
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
