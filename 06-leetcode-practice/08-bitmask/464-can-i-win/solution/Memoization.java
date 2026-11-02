/**
 * 464. Can I Win — Memoization (game DP over a bitmask)
 *
 * Idea: a position is the set of used numbers (bitmask); the running total is implied by it.
 * The player to move wins if some unused number either reaches the target now, or leaves the
 * opponent in a losing position. Positions repeat across move orders, so cache by mask.
 *
 * Time complexity:  O(2^n * n) worst case — each mask is solved once, trying n numbers.
 * Space complexity: O(2^n) — the memo (recursion depth is at most n).
 *
 * Run: java Memoization.java
 */
public class Memoization {

    private static int maxNumber;
    // memo[mask] = can the player to move force a win? null = not computed yet
    private static Boolean[] memo;

    static boolean canIWin(int maxChoosableInteger, int desiredTotal) {
        if (desiredTotal <= 0) return true;                      // target already reached
        int allSum = maxChoosableInteger * (maxChoosableInteger + 1) / 2;
        if (allSum < desiredTotal) return false;                 // nobody can ever win
        maxNumber = maxChoosableInteger;
        memo = new Boolean[1 << maxChoosableInteger];
        return canWin(0, desiredTotal);
    }

    // 'remaining' is a function of usedMask, passed along only to avoid recomputing it
    private static boolean canWin(int usedMask, int remaining) {
        if (memo[usedMask] != null) return memo[usedMask];
        boolean result = false;
        for (int number = 1; number <= maxNumber && !result; number++) {
            int bit = 1 << (number - 1);
            if ((usedMask & bit) != 0) continue;
            // win now, or move to a position where the opponent (now to move) loses
            if (number >= remaining || !canWin(usedMask | bit, remaining - number)) result = true;
        }
        memo[usedMask] = result;
        return result;
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
