/**
 * 902. Numbers At Most N Given Digit Set — Tabulation (right-to-left suffix table)
 *
 * Idea: Let L = number of digits of n and D = size of the digit set.
 *       (a) Numbers shorter than n are always smaller: D^1 + D^2 + ... + D^(L-1).
 *       (b) For numbers with exactly L digits, let tightWays[i] = how many ways to fill positions
 *           i..L-1 so that this suffix is <= n's suffix, given positions 0..i-1 equal n's.
 *           tightWays[L] = 1 (empty suffix equals itself), and
 *           tightWays[i] = (#set digits < n[i]) * D^(L-1-i)  +  (n[i] in set ? tightWays[i+1] : 0).
 *       Answer = (a) + tightWays[0].
 *
 * Time complexity:  O(L * D) — one pass over n's digits, scanning the set at each.
 * Space complexity: O(L) for the table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int atMostNGivenDigitSet(String[] digits, int n) {
        String bound = Integer.toString(n);
        int length = bound.length(), setSize = digits.length;

        long[] powerOfSet = new long[length + 1]; // powerOfSet[r] = D^r free fillings of r positions
        powerOfSet[0] = 1;
        for (int r = 1; r <= length; r++) powerOfSet[r] = powerOfSet[r - 1] * setSize;

        long shorter = 0;
        for (int len = 1; len < length; len++) shorter += powerOfSet[len];

        // tightWays[i] = fillings of positions i..L-1 that keep the number <= n, prefix equal to n's
        long[] tightWays = new long[length + 1];
        tightWays[length] = 1;
        for (int i = length - 1; i >= 0; i--) {
            int boundDigit = bound.charAt(i) - '0';
            int smaller = 0;
            boolean equalAvailable = false;
            for (String d : digits) {
                int digit = d.charAt(0) - '0';
                if (digit < boundDigit) smaller++;
                if (digit == boundDigit) equalAvailable = true;
            }
            tightWays[i] = smaller * powerOfSet[length - 1 - i] + (equalAvailable ? tightWays[i + 1] : 0);
        }
        return (int) (shorter + tightWays[0]);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("{1,3,5,7}, n = 100", atMostNGivenDigitSet(new String[]{"1", "3", "5", "7"}, 100), 20);
        check("{1,4,9}, n = 1e9", atMostNGivenDigitSet(new String[]{"1", "4", "9"}, 1000000000), 29523);
        check("{7}, n = 8", atMostNGivenDigitSet(new String[]{"7"}, 8), 1);
        check("{9}, n = 8 (edge: nothing fits)", atMostNGivenDigitSet(new String[]{"9"}, 8), 0);
        check("{1,3,5,7}, n = 357", atMostNGivenDigitSet(new String[]{"1", "3", "5", "7"}, 357), 48);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
