/**
 * 2719. Count of Integers — Memoization (digit DP with BOTH bounds in one pass)
 *
 * Idea: Pad num1 with leading zeros to num2's length. Build x digit by digit.
 *       State = (position, digitSum, tightLow, tightHigh).
 *       tightLow  = prefix equals num1's prefix, so the next digit may not go BELOW num1's digit.
 *       tightHigh = prefix equals num2's prefix, so the next digit may not go ABOVE num2's digit.
 *       Leading zeros add 0 to the digit sum, so they need no special flag here.
 *       Prune as soon as the running sum exceeds maxSum.
 *
 * Time complexity:  O(L * S * 4 * 10) — L <= 23 positions, S <= 400 sums, 4 flag pairs, 10 digits.
 * Space complexity: O(L * S * 4) memo plus O(L) recursion depth.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static final int MOD = 1_000_000_007;
    private static int[] low, high;
    private static int minSum, maxSum;
    // memo[pos][sum][tightLow][tightHigh] = valid ways to finish; -1 = unknown
    private static int[][][][] memo;

    static int count(String num1, String num2, int minSumValue, int maxSumValue) {
        int length = num2.length();
        String padded = "0".repeat(length - num1.length()) + num1; // align num1 under num2
        low = new int[length];
        high = new int[length];
        for (int i = 0; i < length; i++) {
            low[i] = padded.charAt(i) - '0';
            high[i] = num2.charAt(i) - '0';
        }
        minSum = minSumValue;
        maxSum = maxSumValue;
        memo = new int[length][maxSum + 1][2][2];
        for (int[][][] cube : memo) for (int[][] plane : cube) for (int[] row : plane) Arrays.fill(row, -1);
        return solve(0, 0, 1, 1);
    }

    private static int solve(int pos, int sum, int tightLow, int tightHigh) {
        if (sum > maxSum) return 0;                         // digits never decrease the sum: dead branch
        if (pos == low.length) return (sum >= minSum) ? 1 : 0;
        if (memo[pos][sum][tightLow][tightHigh] != -1) return memo[pos][sum][tightLow][tightHigh];
        int from = (tightLow == 1) ? low[pos] : 0;
        int to = (tightHigh == 1) ? high[pos] : 9;
        long total = 0;
        for (int digit = from; digit <= to; digit++) {
            int nextLow = (tightLow == 1 && digit == from) ? 1 : 0;
            int nextHigh = (tightHigh == 1 && digit == to) ? 1 : 0;
            total += solve(pos + 1, sum + digit, nextLow, nextHigh);
        }
        memo[pos][sum][tightLow][tightHigh] = (int) (total % MOD);
        return memo[pos][sum][tightLow][tightHigh];
    }

    static int bruteForce(int from, int to, int minSumValue, int maxSumValue) {
        int good = 0;
        for (int x = from; x <= to; x++) {
            int s = 0;
            for (int rest = x; rest > 0; rest /= 10) s += rest % 10;
            if (s >= minSumValue && s <= maxSumValue) good++;
        }
        return good;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1, 12], sums 1..8", count("1", "12", 1, 8), 11);  // all but 9
        check("[1, 5], sums 1..5", count("1", "5", 1, 5), 5);
        check("[7, 7], sums 1..1 (edge: single number, fails)", count("7", "7", 1, 1), 0);
        check("[10, 25], sums 3..5", count("10", "25", 3, 5), 6);  // 12, 13, 14, 21, 22, 23
        check("[1, 10^6], sums 10..20", count("1", "1000000", 10, 20), 177192);
        check("[1, 10^22], sums 1..400", count("1", "10000000000000000000000", 1, 400), 490000); // every x qualifies: 10^22 mod p
        for (int a = 1; a <= 300; a += 7) {
            for (int b = a; b <= 1200; b += 113) {
                int expected = bruteForce(a, b, 4, 11);
                if (count(Integer.toString(a), Integer.toString(b), 4, 11) != expected)
                    check("cross-check [" + a + ", " + b + "]", count(Integer.toString(a), Integer.toString(b), 4, 11), expected);
            }
        }
        System.out.println("PASS  two-bound digit DP agrees with brute force on 400+ ranges");
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
