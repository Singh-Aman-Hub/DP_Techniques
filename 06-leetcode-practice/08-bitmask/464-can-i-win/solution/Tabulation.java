/**
 * 464. Can I Win — Tabulation (game DP over all masks, largest first)
 *
 * Idea: win[mask] depends on win[mask | bit] (a larger number), so fill masks from the full
 * set down to 0. sum[mask] is built incrementally from the mask without its lowest bit.
 * Masks whose sum already reaches the target are never consulted (the "number >= remaining"
 * test fires first), so their values do not matter.
 *
 * Time complexity:  O(2^n * n) — every mask tries every number.
 * Space complexity: O(2^n) — win[] and sum[].
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static boolean canIWin(int maxChoosableInteger, int desiredTotal) {
        if (desiredTotal <= 0) return true;
        int n = maxChoosableInteger;
        if (n * (n + 1) / 2 < desiredTotal) return false;
        int states = 1 << n;
        // sum[mask] = total of the numbers in mask
        int[] sum = new int[states];
        for (int mask = 1; mask < states; mask++) {
            int lowestBitIndex = Integer.numberOfTrailingZeros(mask);   // bit i stands for number i+1
            sum[mask] = sum[mask & (mask - 1)] + lowestBitIndex + 1;    // mask & (mask-1) clears that bit
        }
        // win[mask] = player to move (numbers in mask used) can force a win
        boolean[] win = new boolean[states];
        for (int mask = states - 1; mask >= 0; mask--) {
            int remaining = desiredTotal - sum[mask];
            for (int number = 1; number <= n; number++) {
                int bit = 1 << (number - 1);
                if ((mask & bit) != 0) continue;
                if (number >= remaining || !win[mask | bit]) { win[mask] = true; break; }
            }
        }
        return win[0];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("max 10, desired 11", canIWin(10, 11), false);
        check("max 10, desired 0 (edge: already reached)", canIWin(10, 0), true);
        check("max 10, desired 1", canIWin(10, 1), true);
        check("max 3, desired 5", canIWin(3, 5), true);
        check("max 3, desired 4", canIWin(3, 4), false);
        check("max 5, desired 50 (sum too small)", canIWin(5, 50), false);
        // 1+...+20 = 210 and any 19 numbers sum to < 210, so the 20th pick (player 2) wins
        check("max 20, desired 210 (full 2^20 search)", canIWin(20, 210), false);
    }

    private static void check(String label, boolean actual, boolean expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
