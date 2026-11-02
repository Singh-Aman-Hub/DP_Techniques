/**
 * Numbers At Most N Given Digit Set (LeetCode 902) — Digit DP with a restricted digit alphabet
 *
 * Idea: same skeleton as every digit DP, but the allowed digits are only those in the set.
 *       Shorter numbers are handled with `started`: before the number starts we may
 *       place a "blank" (a leading zero that is not a real digit), even though 0 is
 *       never in the set. The empty number (all blanks) is not counted.
 *       build(pos, tight, started) = how many valid numbers complete this prefix.
 *
 * Time complexity:  O(L * 2 * 2 * |digits|) — L <= 10 positions, 4 flag combinations, |digits| <= 9 choices.
 * Space complexity: O(L * 2 * 2) — the memo table.
 *
 * Run: java NumbersAtMostNGivenDigitSet.java
 */
import java.util.Arrays;

public class NumbersAtMostNGivenDigitSet {

    private static int[] limitDigits;
    private static int[] allowed;
    // memo[pos][tight][started]; -1 = not computed
    private static long[][][] memo;

    static int atMostNGivenDigitSet(String[] digitSet, int n) {
        allowed = new int[digitSet.length];
        for (int i = 0; i < digitSet.length; i++) allowed[i] = Integer.parseInt(digitSet[i]);
        String s = String.valueOf(n);
        limitDigits = new int[s.length()];
        for (int i = 0; i < s.length(); i++) limitDigits[i] = s.charAt(i) - '0';
        memo = new long[s.length()][2][2];
        for (long[][] plane : memo) for (long[] row : plane) Arrays.fill(row, -1);
        return (int) build(0, 1, 0);
    }

    private static long build(int pos, int tight, int started) {
        if (pos == limitDigits.length) return started;    // a real number was written
        if (memo[pos][tight][started] != -1) return memo[pos][tight][started];
        int limit = (tight == 1) ? limitDigits[pos] : 9;
        long total = 0;

        if (started == 0) {
            // Option 1: keep the number shorter, leave this position blank.
            // A blank is smaller than n's digit (n has no leading zero), so we are no longer tight.
            total += build(pos + 1, 0, 0);
        }
        for (int digit : allowed) {
            if (digit > limit) continue;                   // would exceed n
            int nextTight = (tight == 1 && digit == limit) ? 1 : 0;
            total += build(pos + 1, nextTight, 1);
        }
        memo[pos][tight][started] = total;
        return total;
    }

    static int bruteForce(String[] digitSet, int n) {
        String set = String.join("", digitSet);
        int count = 0;
        for (int x = 1; x <= n; x++) {
            boolean ok = true;
            for (char c : String.valueOf(x).toCharArray()) if (set.indexOf(c) < 0) { ok = false; break; }
            if (ok) count++;
        }
        return count;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("{1,3,5,7}, n=100",  atMostNGivenDigitSet(new String[]{"1", "3", "5", "7"}, 100), 20);
        check("{1,4,9}, n=10^9",   atMostNGivenDigitSet(new String[]{"1", "4", "9"}, 1_000_000_000), 29523);
        check("{7}, n=8",          atMostNGivenDigitSet(new String[]{"7"}, 8), 1);
        check("{5}, n=4 (edge)",   atMostNGivenDigitSet(new String[]{"5"}, 4), 0);
        String[][] sets = {{"1", "3", "5", "7"}, {"2", "8"}, {"1", "2", "3", "4", "5", "6", "7", "8", "9"}};
        for (String[] set : sets) {
            for (int n = 1; n <= 2000; n++) {
                int fast = atMostNGivenDigitSet(set, n), slow = bruteForce(set, n);
                if (fast != slow) check("cross-check " + Arrays.toString(set) + " n=" + n, fast, slow);
            }
        }
        System.out.println("PASS  cross-check against brute force for n = 1..2000 on three digit sets");
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
