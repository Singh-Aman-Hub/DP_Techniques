/**
 * Counting Numbers (CSES 2220) — Digit DP on a range [a, b] with count(b) - count(a - 1)
 *
 * Idea: count the integers in [a, b] with no two adjacent equal digits.
 *       count(x) = how many valid numbers lie in [0, x]; then the answer is
 *       count(b) - count(a - 1), with count(-1) = 0.
 *       State: (pos, previous digit, tight, started). `started` matters here: in "0012"
 *       the two leading zeros are not real digits, so they must not count as "adjacent equal".
 *
 * Time complexity:  O(L * 11 * 2 * 2 * 10) per call — L <= 19 digits for values up to 10^18.
 * Space complexity: O(L * 11 * 2 * 2) — the memo table (previous digit 10 = "none yet").
 *
 * Run: java CountingNumbers.java
 */
import java.util.Arrays;

public class CountingNumbers {

    private static int[] digits;
    // memo[pos][previousDigit][tight][started]; previousDigit 10 means "no digit yet"; -1 = unknown
    private static long[][][][] memo;

    static long countInRange(long a, long b) {
        return countUpTo(b) - countUpTo(a - 1);
    }

    /** Number of valid integers in [0, x]. */
    static long countUpTo(long x) {
        if (x < 0) return 0;
        String s = String.valueOf(x);
        digits = new int[s.length()];
        for (int i = 0; i < s.length(); i++) digits[i] = s.charAt(i) - '0';
        memo = new long[s.length()][11][2][2];
        for (long[][][] a : memo) for (long[][] b : a) for (long[] c : b) Arrays.fill(c, -1);
        return valid(0, 10, 1, 0);
    }

    private static long valid(int pos, int previous, int tight, int started) {
        if (pos == digits.length) return 1;               // every complete string here is valid (all zeros = the number 0)
        if (memo[pos][previous][tight][started] != -1) return memo[pos][previous][tight][started];
        int limit = (tight == 1) ? digits[pos] : 9;
        long total = 0;
        for (int digit = 0; digit <= limit; digit++) {
            int nextTight = (tight == 1 && digit == limit) ? 1 : 0;
            if (started == 0 && digit == 0) {
                total += valid(pos + 1, 10, nextTight, 0);    // still leading zeros: no previous digit
            } else {
                if (started == 1 && digit == previous) continue;   // two equal neighbours: invalid
                total += valid(pos + 1, digit, nextTight, 1);
            }
        }
        memo[pos][previous][tight][started] = total;
        return total;
    }

    static long bruteForce(long a, long b) {
        long count = 0;
        for (long x = a; x <= b; x++) {
            String s = String.valueOf(x);
            boolean ok = true;
            for (int i = 1; i < s.length(); i++) if (s.charAt(i) == s.charAt(i - 1)) { ok = false; break; }
            if (ok) count++;
        }
        return count;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("sample [123, 321]",  countInRange(123, 321), 171);
        check("[0, 9] single digits", countInRange(0, 9), 10);
        check("[10, 20] (all but 11)", countInRange(10, 20), 10);
        check("[0, 0] edge",          countInRange(0, 0), 1);
        check("[11, 11] edge",        countInRange(11, 11), 0);
        for (long a = 0; a <= 300; a += 37) {
            for (long b = a; b <= 1500; b += 53) {
                if (countInRange(a, b) != bruteForce(a, b)) check("cross-check [" + a + "," + b + "]", countInRange(a, b), bruteForce(a, b));
            }
        }
        System.out.println("PASS  cross-check against brute force on many ranges inside [0, 1500]");
        // Every d-digit number has 9 choices for the first digit and 9 for each next one: 9^d.
        // [0, 10^18 - 1] = {0} + sum over d = 1..18 of 9^d = 1 + (9^19 - 9) / 8.
        long expected = 1;
        long power = 1;
        for (int d = 1; d <= 18; d++) { power *= 9; expected += power; }
        check("[0, 10^18 - 1]", countInRange(0, 999_999_999_999_999_999L), expected);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
