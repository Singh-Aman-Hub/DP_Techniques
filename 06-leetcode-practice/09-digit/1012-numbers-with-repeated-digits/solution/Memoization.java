/**
 * 1012. Numbers With Repeated Digits — Memoization (complement + digit DP with a used-digit mask)
 *
 * Idea: "At least one repeated digit" is hard to track directly (which digit repeated? when?).
 *       Its complement, "all digits distinct", only needs the set of digits used so far.
 *       So answer = n - countDistinct(n), where countDistinct(n) counts x in [1, n] with unique digits.
 *       countDistinct is a digit DP with state (position, usedMask, tight, started).
 *       Only non-tight, started states are memoized: those are the ones reached many times.
 *
 * Time complexity:  O(L * 2^10 * 10) — L <= 10 positions, 1024 masks, 10 digit choices.
 * Space complexity: O(L * 2^10) memo plus O(L) recursion depth.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static int[] boundDigits;
    // memo[pos][mask] = distinct-digit ways to fill positions pos.. when NOT tight and already started; -1 = unknown
    private static int[][] memo;

    static int numDupDigitsAtMostN(int n) {
        String text = Integer.toString(n);
        boundDigits = new int[text.length()];
        for (int i = 0; i < text.length(); i++) boundDigits[i] = text.charAt(i) - '0';
        memo = new int[text.length()][1 << 10];
        for (int[] row : memo) Arrays.fill(row, -1);
        int distinct = countDistinct(0, 0, true, false);
        return n - distinct;
    }

    private static int countDistinct(int pos, int usedMask, boolean tight, boolean started) {
        if (pos == boundDigits.length) return started ? 1 : 0; // the empty number 0 is not in [1, n]
        boolean cacheable = !tight && started;
        if (cacheable && memo[pos][usedMask] != -1) return memo[pos][usedMask];
        int limit = tight ? boundDigits[pos] : 9;
        int total = 0;
        for (int digit = 0; digit <= limit; digit++) {
            boolean nextTight = tight && digit == limit;
            if (!started && digit == 0) {
                total += countDistinct(pos + 1, usedMask, nextTight, false); // leading zero: uses nothing
            } else if ((usedMask & (1 << digit)) == 0) {
                total += countDistinct(pos + 1, usedMask | (1 << digit), nextTight, true);
            }
        }
        if (cacheable) memo[pos][usedMask] = total;
        return total;
    }

    static int bruteForce(int n) {
        int repeated = 0;
        for (int x = 1; x <= n; x++) {
            int seen = 0;
            boolean dup = false;
            for (int rest = x; rest > 0; rest /= 10) {
                int bit = 1 << (rest % 10);
                if ((seen & bit) != 0) dup = true;
                seen |= bit;
            }
            if (dup) repeated++;
        }
        return repeated;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 20", numDupDigitsAtMostN(20), 1);       // only 11
        check("n = 100", numDupDigitsAtMostN(100), 10);    // 11,22,...,99 and 100
        check("n = 1000", numDupDigitsAtMostN(1000), 262);
        check("n = 1 (edge)", numDupDigitsAtMostN(1), 0);
        check("n = 1e9", numDupDigitsAtMostN(1000000000), 994388230);
        for (int n = 1; n <= 3000; n++) {
            if (numDupDigitsAtMostN(n) != bruteForce(n)) check("cross-check n = " + n, numDupDigitsAtMostN(n), bruteForce(n));
        }
        System.out.println("PASS  digit DP agrees with brute force for n = 1..3000");
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
