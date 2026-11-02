/**
 * 2719. Count of Integers — Tabulation (prefix-count difference with a free-suffix table)
 *
 * Idea: Turn the two-sided range into two one-sided counts:
 *       answer = countAtMost(num2) - countAtMost(num1) + (num1 itself is good ? 1 : 0).
 *       (We avoid computing num1 - 1 on a 23-digit string.)
 *       free[r][s] = number of ways to write r digits (leading zeros allowed) whose sum is s.
 *       free[0][0] = 1 and free[r][s] = sum over d = 0..9 of free[r-1][s-d].
 *       countAtMost(X) walks X's digits; at position i, every digit d < X[i] makes the number
 *       smaller than X, so the r = L-1-i digits after it are free and must bring the sum into
 *       [minSum - prefixSum - d, maxSum - prefixSum - d]. Finally X itself is checked.
 *
 * Time complexity:  O(L * S * 10) to build free[][], O(L * 10 * S) for each walk (range sums).
 * Space complexity: O(L * S) for free[][].
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    private static final int MOD = 1_000_000_007;

    static int count(String num1, String num2, int minSum, int maxSum) {
        int length = num2.length();
        // free[r][s] = ways to fill r positions with digits 0..9 so that they sum to exactly s
        long[][] free = new long[length + 1][maxSum + 1];
        free[0][0] = 1;
        for (int r = 1; r <= length; r++) {
            for (int s = 0; s <= maxSum; s++) {
                long ways = 0;
                for (int d = 0; d <= 9 && d <= s; d++) ways += free[r - 1][s - d];
                free[r][s] = ways % MOD;
            }
        }
        long answer = countAtMost(num2, free, minSum, maxSum) - countAtMost(num1, free, minSum, maxSum);
        if (isGood(num1, minSum, maxSum)) answer += 1;
        return (int) (((answer % MOD) + MOD) % MOD);
    }

    // Count x in [0, bound] whose digit sum lies in [minSum, maxSum].
    private static long countAtMost(String bound, long[][] free, int minSum, int maxSum) {
        int length = bound.length();
        long total = 0;
        int prefixSum = 0;
        for (int i = 0; i < length; i++) {
            int boundDigit = bound.charAt(i) - '0';
            int freePositions = length - 1 - i;
            for (int d = 0; d < boundDigit; d++) {
                int lowNeed = Math.max(0, minSum - prefixSum - d);
                int highNeed = maxSum - prefixSum - d;
                for (int s = lowNeed; s <= highNeed; s++) total += free[freePositions][s];
                total %= MOD;
            }
            prefixSum += boundDigit;
            if (prefixSum > maxSum) return total; // the tight prefix is already too heavy
        }
        if (prefixSum >= minSum) total++;          // the bound itself
        return total % MOD;
    }

    private static boolean isGood(String number, int minSum, int maxSum) {
        int sum = 0;
        for (char c : number.toCharArray()) sum += c - '0';
        return sum >= minSum && sum <= maxSum;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1, 12], sums 1..8", count("1", "12", 1, 8), 11);
        check("[1, 5], sums 1..5", count("1", "5", 1, 5), 5);
        check("[7, 7], sums 1..1 (edge: single number, fails)", count("7", "7", 1, 1), 0);
        check("[10, 25], sums 3..5", count("10", "25", 3, 5), 6);
        check("[1, 10^6], sums 10..20", count("1", "1000000", 10, 20), 177192);
        check("[1, 10^22], sums 1..400", count("1", "10000000000000000000000", 1, 400), 490000);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
