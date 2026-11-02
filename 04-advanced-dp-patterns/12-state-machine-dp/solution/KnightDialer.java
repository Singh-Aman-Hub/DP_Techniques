/**
 * Knight Dialer (LeetCode 935) — state machine whose states are the 10 phone keys
 *
 * Idea: a chess knight stands on a phone keypad and dials a number of length n by making
 *       n - 1 knight moves. The state is the key it stands on; the allowed transitions are
 *       the knight moves between keys (key 5 has none).
 *       count[key] = number of distinct numbers of the current length that end on key.
 *       bruteForce walks every sequence of moves for small n as a cross-check.
 *
 * Time complexity:  O(n * 20) = O(n) — 20 directed knight moves on the keypad per step.
 * Space complexity: O(1) — two arrays of 10 counters.
 *
 * Run: java KnightDialer.java
 */
public class KnightDialer {

    private static final long MOD = 1_000_000_007L;

    // MOVES[key] = keys a knight can jump to from key
    //   1 2 3
    //   4 5 6
    //   7 8 9
    //     0
    private static final int[][] MOVES = {
        {4, 6},     // 0
        {6, 8},     // 1
        {7, 9},     // 2
        {4, 8},     // 3
        {0, 3, 9},  // 4
        {},         // 5: no knight move lands on the keypad
        {0, 1, 7},  // 6
        {2, 6},     // 7
        {1, 3},     // 8
        {2, 4},     // 9
    };

    static int knightDialer(int n) {
        long[] count = new long[10];
        java.util.Arrays.fill(count, 1);                  // length 1: start on any key
        for (int length = 2; length <= n; length++) {
            long[] next = new long[10];
            for (int key = 0; key < 10; key++) {
                for (int target : MOVES[key]) next[target] = (next[target] + count[key]) % MOD;
            }
            count = next;
        }
        long total = 0;
        for (long c : count) total += c;
        return (int) (total % MOD);
    }

    static long bruteForce(int n) {
        long total = 0;
        for (int key = 0; key < 10; key++) total += walk(key, n - 1);
        return total;
    }

    private static long walk(int key, int movesLeft) {
        if (movesLeft == 0) return 1;
        long total = 0;
        for (int target : MOVES[key]) total += walk(target, movesLeft - 1);
        return total;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1",    knightDialer(1), 10);
        check("n = 2",    knightDialer(2), 20);
        check("n = 3",    knightDialer(3), 46);
        check("n = 3131", knightDialer(3131), 136006598);
        for (int n = 1; n <= 12; n++) check("n = " + n + " vs brute", knightDialer(n), bruteForce(n));
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
