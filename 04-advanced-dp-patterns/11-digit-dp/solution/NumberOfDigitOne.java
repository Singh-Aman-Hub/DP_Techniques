/**
 * Number of Digit One (LeetCode 233) — Digit DP that SUMS a quantity instead of counting numbers
 *
 * Idea: ones(pos, onesSoFar, tight) = total number of digit 1s written across ALL
 *       completions of the current prefix (positions pos..end), where onesSoFar is how
 *       many 1s the prefix already contains. At the end of a number we return onesSoFar,
 *       so every complete number contributes its own count of 1s.
 *       Leading zeros are harmless here: a leading 0 is not a 1, so it never adds anything.
 *
 * Time complexity:  O(L^2 * 2 * 10) — L positions, up to L values of onesSoFar, 2 tight values, 10 digits.
 * Space complexity: O(L^2 * 2) — the memo table.
 *
 * Run: java NumberOfDigitOne.java
 */
import java.util.Arrays;

public class NumberOfDigitOne {

    private static int[] digits;
    // memo[pos][onesSoFar][tight] = ones(pos, onesSoFar, tight); -1 = not computed
    private static long[][][] memo;

    static int countDigitOne(int n) {
        String s = String.valueOf(n);
        digits = new int[s.length()];
        for (int i = 0; i < s.length(); i++) digits[i] = s.charAt(i) - '0';
        memo = new long[s.length()][s.length() + 1][2];
        for (long[][] plane : memo) for (long[] row : plane) Arrays.fill(row, -1);
        return (int) ones(0, 0, 1);
    }

    private static long ones(int pos, int onesSoFar, int tight) {
        if (pos == digits.length) return onesSoFar;       // this finished number has onesSoFar ones
        if (memo[pos][onesSoFar][tight] != -1) return memo[pos][onesSoFar][tight];
        int limit = (tight == 1) ? digits[pos] : 9;
        long total = 0;
        for (int digit = 0; digit <= limit; digit++) {
            int nextTight = (tight == 1 && digit == limit) ? 1 : 0;
            total += ones(pos + 1, onesSoFar + (digit == 1 ? 1 : 0), nextTight);
        }
        memo[pos][onesSoFar][tight] = total;
        return total;
    }

    static int bruteForce(int n) {
        int total = 0;
        for (int x = 1; x <= n; x++) {
            for (int y = x; y > 0; y /= 10) if (y % 10 == 1) total++;
        }
        return total;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 13 (1,10,11,12,13)", countDigitOne(13), 6);
        check("n = 0",                  countDigitOne(0), 0);
        check("n = 99",                 countDigitOne(99), 20);
        check("n = 100",                countDigitOne(100), 21);
        for (int n = 0; n <= 2500; n++) {
            if (countDigitOne(n) != bruteForce(n)) check("cross-check n=" + n, countDigitOne(n), bruteForce(n));
        }
        System.out.println("PASS  cross-check against brute force for n = 0..2500");
        // n = 10^9: each of the 9 low positions holds a 1 in 1/10 of 10^9 numbers -> 9 * 10^8, plus the leading 1.
        check("n = 1,000,000,000", countDigitOne(1_000_000_000), 900_000_001);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
