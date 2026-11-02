/**
 * 935. Knight Dialer — Space-optimized (two arrays of 10)
 *
 * Idea: Each length depends only on the previous length, so roll two 10-element arrays.
 *
 * Time complexity:  O(n) — n steps of constant work (10 digits, <= 3 neighbours).
 * Space complexity: O(1) — 20 counters regardless of n.
 *
 * Run: java Optimized.java
 */
import java.util.Arrays;

public class Optimized {

    private static final int MOD = 1_000_000_007;
    private static final int[][] NEIGHBOURS = {
        {4, 6}, {6, 8}, {7, 9}, {4, 8}, {0, 3, 9}, {}, {0, 1, 7}, {2, 6}, {1, 3}, {2, 4}
    };

    static int knightDialer(int n) {
        long[] endingOn = new long[10];          // endingOn[d] = numbers of the current length ending on d
        Arrays.fill(endingOn, 1);
        for (int len = 2; len <= n; len++) {
            long[] next = new long[10];
            for (int d = 0; d <= 9; d++) {
                for (int prev : NEIGHBOURS[d]) next[d] += endingOn[prev];
                next[d] %= MOD;
            }
            endingOn = next;
        }
        long total = 0;
        for (long c : endingOn) total += c;
        return (int) (total % MOD);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", knightDialer(1), 10);
        check("n = 2", knightDialer(2), 20);
        check("n = 3", knightDialer(3), 46);
        check("n = 3131", knightDialer(3131), 136006598);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
