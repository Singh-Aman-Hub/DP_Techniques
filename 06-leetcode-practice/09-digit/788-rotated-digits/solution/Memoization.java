/**
 * 788. Rotated Digits — Memoization (top-down digit DP)
 *
 * Idea: Build the numbers 0..n digit by digit from the most significant digit.
 *       State = (position, tight, changed). "tight" says the prefix so far equals n's prefix,
 *       so the next digit may not exceed n's digit. "changed" says we already used one of
 *       2/5/6/9, the digits that look different after rotation. Digits 3/4/7 are forbidden.
 *       Leading zeros are harmless: 0 is a valid digit that does not set "changed",
 *       and the number 0 itself is never counted because it is never "changed".
 *
 * Time complexity:  O(D * 2 * 2 * 10) — D digits, 4 flag combinations, 10 digit choices each.
 * Space complexity: O(D * 2 * 2) for the memo plus O(D) recursion depth.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static int[] digitsOfN;
    // memo[pos][tight][changed] = how many ways to fill positions pos.. that give a good number; -1 = unknown
    private static int[][][] memo;

    static int rotatedDigits(int n) {
        String text = Integer.toString(n);
        digitsOfN = new int[text.length()];
        for (int i = 0; i < text.length(); i++) digitsOfN[i] = text.charAt(i) - '0';
        memo = new int[text.length()][2][2];
        for (int[][] plane : memo) for (int[] row : plane) Arrays.fill(row, -1);
        return count(0, 1, 0);
    }

    private static int count(int pos, int tight, int changed) {
        if (pos == digitsOfN.length) return changed; // a complete number is good only if something rotated differently
        if (memo[pos][tight][changed] != -1) return memo[pos][tight][changed];

        int limit = (tight == 1) ? digitsOfN[pos] : 9; // a tight prefix may not exceed n's digit here
        int total = 0;
        for (int digit = 0; digit <= limit; digit++) {
            if (digit == 3 || digit == 4 || digit == 7) continue; // these become invalid after rotation
            int nextTight = (tight == 1 && digit == limit) ? 1 : 0;
            int nextChanged = (changed == 1 || digit == 2 || digit == 5 || digit == 6 || digit == 9) ? 1 : 0;
            total += count(pos + 1, nextTight, nextChanged);
        }
        memo[pos][tight][changed] = total;
        return total;
    }

    // Direct definition, used only to cross-check small n.
    static int bruteForce(int n) {
        int good = 0;
        for (int x = 1; x <= n; x++) {
            boolean valid = true, changed = false;
            for (int rest = x; rest > 0; rest /= 10) {
                int d = rest % 10;
                if (d == 3 || d == 4 || d == 7) valid = false;
                if (d == 2 || d == 5 || d == 6 || d == 9) changed = true;
            }
            if (valid && changed) good++;
        }
        return good;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 10", rotatedDigits(10), 4);        // 2, 5, 6, 9
        check("n = 1 (edge)", rotatedDigits(1), 0);    // 1 rotates to itself
        check("n = 2", rotatedDigits(2), 1);
        check("n = 857", rotatedDigits(857), 247);
        check("n = 10000", rotatedDigits(10000), bruteForce(10000));
        for (int n = 1; n <= 2000; n++) {
            if (rotatedDigits(n) != bruteForce(n)) check("cross-check n = " + n, rotatedDigits(n), bruteForce(n));
        }
        System.out.println("PASS  digit DP agrees with brute force for n = 1..2000");
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
