/**
 * 902. Numbers At Most N Given Digit Set — Memoization (digit DP with tight + started)
 *
 * Idea: Build numbers digit by digit over n's length. State = (position, tight, started).
 *       While not started we may skip a position (a leading "blank"), which is how shorter numbers
 *       are produced. Once started, every position takes one digit from the allowed set; a tight
 *       prefix may not use a digit larger than n's digit at that position.
 *
 * Time complexity:  O(L * 2 * 2 * |digits|) — L = number of digits of n.
 * Space complexity: O(L * 4) memo plus O(L) recursion depth.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static int[] allowed;
    private static int[] boundDigits;
    // memo[pos][tight][started] = ways to finish from here; -1 = unknown
    private static long[][][] memo;

    static int atMostNGivenDigitSet(String[] digits, int n) {
        allowed = new int[digits.length];
        for (int i = 0; i < digits.length; i++) allowed[i] = digits[i].charAt(0) - '0';
        String text = Integer.toString(n);
        boundDigits = new int[text.length()];
        for (int i = 0; i < text.length(); i++) boundDigits[i] = text.charAt(i) - '0';
        memo = new long[text.length()][2][2];
        for (long[][] plane : memo) for (long[] row : plane) Arrays.fill(row, -1);
        return (int) count(0, 1, 0);
    }

    private static long count(int pos, int tight, int started) {
        if (pos == boundDigits.length) return started; // count only real (non-empty) numbers
        if (memo[pos][tight][started] != -1) return memo[pos][tight][started];
        long total = 0;
        if (started == 0) total += count(pos + 1, 0, 0); // leave this position blank: a shorter number, never above n
        int limit = (tight == 1) ? boundDigits[pos] : 9;
        for (int digit : allowed) {
            if (digit > limit) continue;                  // would exceed n
            int nextTight = (tight == 1 && digit == limit) ? 1 : 0;
            total += count(pos + 1, nextTight, 1);
        }
        memo[pos][tight][started] = total;
        return total;
    }

    static int bruteForce(String[] digits, int n) {
        int good = 0;
        for (int x = 1; x <= n; x++) {
            boolean ok = true;
            for (char c : Integer.toString(x).toCharArray()) {
                boolean found = false;
                for (String d : digits) if (d.charAt(0) == c) found = true;
                if (!found) ok = false;
            }
            if (ok) good++;
        }
        return good;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("{1,3,5,7}, n = 100", atMostNGivenDigitSet(new String[]{"1", "3", "5", "7"}, 100), 20);
        check("{1,4,9}, n = 1e9", atMostNGivenDigitSet(new String[]{"1", "4", "9"}, 1000000000), 29523);
        check("{7}, n = 8", atMostNGivenDigitSet(new String[]{"7"}, 8), 1);
        check("{9}, n = 8 (edge: nothing fits)", atMostNGivenDigitSet(new String[]{"9"}, 8), 0);
        check("{1,3,5,7}, n = 357", atMostNGivenDigitSet(new String[]{"1", "3", "5", "7"}, 357), 48); // 4 + 16 shorter, 16 + 8 + 4 three-digit
        String[] set = {"2", "3", "8"};
        for (int n = 1; n <= 3000; n++) {
            if (atMostNGivenDigitSet(set, n) != bruteForce(set, n)) check("cross-check n = " + n, atMostNGivenDigitSet(set, n), bruteForce(set, n));
        }
        System.out.println("PASS  digit DP agrees with brute force for {2,3,8}, n = 1..3000");
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
