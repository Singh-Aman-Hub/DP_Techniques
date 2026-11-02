/**
 * 357. Count Numbers with Unique Digits — Memoization (digit DP with a "used digits" mask)
 *
 * Idea: Every x with 0 <= x < 10^n can be written as exactly n digits with leading zeros.
 *       Fill those n positions left to right. State = (position, usedMask, started).
 *       usedMask has bit d set when digit d already appears; "started" is false while we are
 *       still writing leading zeros, which do not count as used digits. There is no "tight"
 *       flag because the bound 10^n - 1 = 99...9 never restricts a digit.
 *
 * Time complexity:  O(n * 2^10 * 2 * 10) — states times 10 digit choices (tiny for n <= 8).
 * Space complexity: O(n * 2^10 * 2) for the memo.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static int totalPositions;
    // memo[pos][mask][started] = unique-digit ways to fill positions pos..n-1; -1 = unknown
    private static int[][][] memo;

    static int countNumbersWithUniqueDigits(int n) {
        totalPositions = n;
        memo = new int[n + 1][1 << 10][2];
        for (int[][] plane : memo) for (int[] row : plane) Arrays.fill(row, -1);
        return count(0, 0, 0);
    }

    private static int count(int pos, int usedMask, int started) {
        if (pos == totalPositions) return 1; // one complete number (all leading zeros = the number 0)
        if (memo[pos][usedMask][started] != -1) return memo[pos][usedMask][started];
        int total = 0;
        for (int digit = 0; digit <= 9; digit++) {
            if (started == 0 && digit == 0) {
                total += count(pos + 1, usedMask, 0);           // still a leading zero: nothing used
            } else if ((usedMask & (1 << digit)) == 0) {
                total += count(pos + 1, usedMask | (1 << digit), 1); // digit d is now used
            }
        }
        memo[pos][usedMask][started] = total;
        return total;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 0 (edge: only the number 0)", countNumbersWithUniqueDigits(0), 1);
        check("n = 1", countNumbersWithUniqueDigits(1), 10);
        check("n = 2", countNumbersWithUniqueDigits(2), 91);   // 100 - 9 (11, 22, ..., 99)
        check("n = 3", countNumbersWithUniqueDigits(3), 739);
        check("n = 8", countNumbersWithUniqueDigits(8), 2345851);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
