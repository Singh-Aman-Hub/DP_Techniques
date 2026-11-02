/**
 * Digit Sum (AtCoder DP S) — Brute force: check every number from 1 to K
 *
 * Idea: loop over every number, add up its digits, and test divisibility by D.
 *       Correct, but K can have 10 000 digits in the real problem, so this only
 *       works for small K. It is the reference the digit DP is checked against.
 *
 * Time complexity:  O(K * digits) — K numbers, each split into its digits.
 * Space complexity: O(1).
 *
 * Run: java DigitSumBruteForce.java
 */
public class DigitSumBruteForce {

    static long countUpTo(long k, int d) {
        long count = 0;
        for (long x = 1; x <= k; x++) {
            if (digitSum(x) % d == 0) count++;
        }
        return count;
    }

    static int digitSum(long x) {
        int sum = 0;
        while (x > 0) {
            sum += (int) (x % 10);   // last digit
            x /= 10;                 // drop the last digit
        }
        return sum;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("K=30, D=4 (4,8,13,17,22,26)", countUpTo(30, 4), 6);
        check("K=235, D=1 (every number)",   countUpTo(235, 1), 235);
        check("K=1, D=2 (digit sum 1 is odd)", countUpTo(1, 2), 0);
        check("K=100, D=10 (19,28,...,91)",  countUpTo(100, 10), 9);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
