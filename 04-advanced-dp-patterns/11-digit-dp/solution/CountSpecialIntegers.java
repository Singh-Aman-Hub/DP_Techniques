/**
 * Count Special Integers (LeetCode 2376) — Digit DP with a used-digits mask and a `started` flag
 *
 * Idea: a number is special when all its digits are distinct. Build it digit by digit
 *       and remember which digits are already used (a 10-bit mask).
 *       Leading zeros are a trap: "007" is really 7, and its two zeros must NOT be
 *       marked as used. So we carry `started` = "have we placed a non-zero digit yet?".
 *       Before the number starts, a 0 is just "no digit here" and changes nothing.
 *       special(pos, usedMask, tight, started) counts valid completions; the all-zero
 *       string (never started) is the number 0, which is not counted (range is 1..n).
 *
 * Time complexity:  O(L * 2^10 * 2 * 2 * 10) — positions x masks x flags x digits (~ 4 * 10^5 for L = 10).
 * Space complexity: O(L * 2^10 * 2 * 2) — the memo table.
 *
 * Run: java CountSpecialIntegers.java
 */
import java.util.Arrays;

public class CountSpecialIntegers {

    private static int[] digits;
    // memo[pos][usedMask][tight][started]; -1 = not computed
    private static int[][][][] memo;

    static int countSpecialNumbers(int n) {
        String s = String.valueOf(n);
        digits = new int[s.length()];
        for (int i = 0; i < s.length(); i++) digits[i] = s.charAt(i) - '0';
        memo = new int[s.length()][1 << 10][2][2];
        for (int[][][] a : memo) for (int[][] b : a) for (int[] c : b) Arrays.fill(c, -1);
        return special(0, 0, 1, 0);
    }

    private static int special(int pos, int usedMask, int tight, int started) {
        if (pos == digits.length) return started;         // count it only if it is a real number (>= 1)
        if (memo[pos][usedMask][tight][started] != -1) return memo[pos][usedMask][tight][started];

        int limit = (tight == 1) ? digits[pos] : 9;
        int total = 0;
        for (int digit = 0; digit <= limit; digit++) {
            int nextTight = (tight == 1 && digit == limit) ? 1 : 0;
            if (started == 0 && digit == 0) {
                // still a leading zero: the number has not begun, nothing is used
                total += special(pos + 1, usedMask, nextTight, 0);
            } else {
                if ((usedMask & (1 << digit)) != 0) continue;   // digit already used -> not special
                total += special(pos + 1, usedMask | (1 << digit), nextTight, 1);
            }
        }
        memo[pos][usedMask][tight][started] = total;
        return total;
    }

    static int bruteForce(int n) {
        int count = 0;
        for (int x = 1; x <= n; x++) {
            int seen = 0;
            boolean distinct = true;
            for (int y = x; y > 0; y /= 10) {
                int digit = y % 10;
                if ((seen & (1 << digit)) != 0) { distinct = false; break; }
                seen |= 1 << digit;
            }
            if (distinct) count++;
        }
        return count;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 20 (all but 11)", countSpecialNumbers(20), 19);
        check("n = 5",               countSpecialNumbers(5), 5);
        check("n = 135",             countSpecialNumbers(135), 110);
        check("n = 1 (edge)",        countSpecialNumbers(1), 1);
        for (int n = 1; n <= 3000; n++) {
            if (countSpecialNumbers(n) != bruteForce(n)) check("cross-check n=" + n, countSpecialNumbers(n), bruteForce(n));
        }
        System.out.println("PASS  cross-check against brute force for n = 1..3000");
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
