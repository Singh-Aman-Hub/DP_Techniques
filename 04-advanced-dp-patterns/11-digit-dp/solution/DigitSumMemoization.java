/**
 * Digit Sum (AtCoder DP S) — Digit DP, memoization (top-down)
 *
 * Idea: build the number digit by digit, left to right, as a string of the same
 *       length as K (shorter numbers simply get leading zeros, which add 0 to the sum).
 *       ways(pos, sumMod, tight) = how many ways to fill positions pos..end so that
 *       the total digit sum is divisible by D, given the digits so far sum to sumMod (mod D)
 *       and `tight` says whether every digit so far EQUALS K's digit (so the next
 *       digit may not exceed K's digit at pos). The count includes 0 ("000...0"),
 *       so we subtract 1 at the end to count 1..K.
 *
 * Time complexity:  O(L * D * 2 * 10) — L positions, D remainders, 2 tight values, 10 digits each.
 * Space complexity: O(L * D * 2) — the memo table (plus O(L) recursion depth).
 *
 * Run: java DigitSumMemoization.java
 */
import java.util.Arrays;

public class DigitSumMemoization {

    private static final long MOD = 1_000_000_007L;

    private static int[] digits;      // digits of K, most significant first
    private static int divisor;
    // memo[pos][sumMod][tight] = ways(pos, sumMod, tight); -1 = not computed
    private static long[][][] memo;

    static long countUpTo(String k, int d) {
        digits = new int[k.length()];
        for (int i = 0; i < k.length(); i++) digits[i] = k.charAt(i) - '0';
        divisor = d;
        memo = new long[k.length()][d][2];
        for (long[][] plane : memo) for (long[] row : plane) Arrays.fill(row, -1);

        long withZero = ways(0, 0, 1);
        return (withZero - 1 + MOD) % MOD;                // remove the number 0
    }

    private static long ways(int pos, int sumMod, int tight) {
        if (pos == digits.length) return sumMod == 0 ? 1 : 0;   // a complete number: does it qualify?
        if (memo[pos][sumMod][tight] != -1) return memo[pos][sumMod][tight];

        // While tight, we may not go above K's digit here; otherwise any digit 0..9 is fine.
        int limit = (tight == 1) ? digits[pos] : 9;
        long total = 0;
        for (int digit = 0; digit <= limit; digit++) {
            // We stay tight only if we were tight AND we copied K's digit exactly.
            int nextTight = (tight == 1 && digit == limit) ? 1 : 0;
            total += ways(pos + 1, (sumMod + digit) % divisor, nextTight);
        }
        total %= MOD;
        memo[pos][sumMod][tight] = total;
        return total;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("K=30, D=4",                 countUpTo("30", 4), 6);
        check("K=235, D=1",                countUpTo("235", 1), 235);
        check("K=1, D=2",                  countUpTo("1", 2), 0);
        check("K=100, D=10",               countUpTo("100", 10), 9);
        check("sample: K=1000000009, D=1", countUpTo("1000000009", 1), 2);
        check("sample: 20-digit K, D=58",  countUpTo("98765432109876543210", 58), 635270834);

        // Cross-check against the brute force for every K up to 600 and a few divisors.
        for (int d = 1; d <= 7; d++) {
            long expected = 0;
            for (int k = 1; k <= 600; k++) {
                if (digitSum(k) % d == 0) expected++;
                if (countUpTo(String.valueOf(k), d) != expected) {
                    check("cross-check K=" + k + " D=" + d, countUpTo(String.valueOf(k), d), expected);
                }
            }
        }
        System.out.println("PASS  cross-check against brute force for K <= 600, D = 1..7");
    }

    /** Plain digit sum, used only by the brute-force cross-check (same as in DigitSumBruteForce). */
    private static int digitSum(long x) {
        int sum = 0;
        while (x > 0) { sum += (int) (x % 10); x /= 10; }
        return sum;
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
