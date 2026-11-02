/**
 * 526. Beautiful Arrangement — Memoization (bitmask DP)
 *
 * Idea: fill positions 1..n left to right. The future depends only on WHICH numbers are used,
 * so the state is a bitmask (bit k-1 set = number k used). The position being filled is
 * bitCount(mask) + 1. ways(mask) = sum of ways(mask + k) over unused k compatible with that position.
 *
 * Time complexity:  O(2^n * n) — 2^n masks, each tries n numbers.
 * Space complexity: O(2^n) — the memo array (recursion depth is only n).
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static int n;
    private static int fullMask;
    // memo[mask] = number of ways to fill the remaining positions; -1 = not computed
    private static int[] memo;

    static int countArrangement(int count) {
        n = count;
        fullMask = (1 << n) - 1;
        memo = new int[1 << n];
        Arrays.fill(memo, -1);
        return ways(0);
    }

    private static int ways(int usedMask) {
        if (usedMask == fullMask) return 1;            // all positions filled beautifully
        if (memo[usedMask] != -1) return memo[usedMask];
        int position = Integer.bitCount(usedMask) + 1; // positions are filled in order
        int total = 0;
        for (int number = 1; number <= n; number++) {
            boolean unused = (usedMask & (1 << (number - 1))) == 0;
            if (unused && (number % position == 0 || position % number == 0)) {
                total += ways(usedMask | (1 << (number - 1)));
            }
        }
        memo[usedMask] = total;
        return total;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 2", countArrangement(2), 2);
        check("n = 3", countArrangement(3), 3);
        check("n = 1 (edge)", countArrangement(1), 1);
        check("n = 4", countArrangement(4), 8);
        check("n = 15 (largest input)", countArrangement(15), 24679);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
