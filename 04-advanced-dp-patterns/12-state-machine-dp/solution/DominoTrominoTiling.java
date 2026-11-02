/**
 * Domino and Tromino Tiling (LeetCode 790) — state machine over the shape of the tiled edge
 *
 * Idea: tile a 2 x n board from left to right. At any moment the right edge of the
 *       tiled part has one of three shapes ("states"):
 *         full[i]   = columns 0..i-1 completely covered, nothing sticks out
 *         top[i]    = columns 0..i-1 covered AND the top cell of column i covered
 *         bottom[i] = columns 0..i-1 covered AND the bottom cell of column i covered
 *       Pull transitions (which last piece completes the shape):
 *         full[i]   = full[i-1]                 (vertical domino in column i-1)
 *                   + full[i-2]                 (two horizontal dominoes in columns i-2, i-1)
 *                   + top[i-2] + bottom[i-2]    (a tromino filling the empty cell of column i-2
 *                                                and both cells of column i-1)
 *         top[i]    = full[i-1]   (tromino: both cells of column i-1 + top of column i)
 *                   + bottom[i-1] (horizontal domino: top of column i-1 + top of column i)
 *         bottom[i] = full[i-1] + top[i-1]   (mirror image of top)
 *       The answer is full[n]. A second method uses the known closed recurrence
 *       f(n) = 2 f(n-1) + f(n-3) as a cross-check.
 *
 * Time complexity:  O(n) — three states updated per column.
 * Space complexity: O(n) for the arrays (only the last two columns are really needed, so O(1) is possible).
 *
 * Run: java DominoTrominoTiling.java
 */
public class DominoTrominoTiling {

    private static final long MOD = 1_000_000_007L;

    static int numTilings(int n) {
        long[] full = new long[n + 1];
        long[] top = new long[n + 1];
        long[] bottom = new long[n + 1];
        full[0] = 1;                                      // empty board: one way (place nothing)
        for (int i = 1; i <= n; i++) {
            full[i] = full[i - 1];                        // vertical domino in column i-1
            if (i >= 2) full[i] += full[i - 2];           // two stacked horizontal dominoes in columns i-2, i-1
            if (i >= 2) full[i] += top[i - 2] + bottom[i - 2];   // tromino closing a one-cell overhang
            full[i] %= MOD;
            top[i] = (full[i - 1] + bottom[i - 1]) % MOD;     // tromino from flat, or top domino from a bottom overhang
            bottom[i] = (full[i - 1] + top[i - 1]) % MOD;     // mirror image
        }
        return (int) full[n];
    }

    /** Reference: the known recurrence f(n) = 2 f(n-1) + f(n-3), with f(0)=1, f(1)=1, f(2)=2. */
    static int closedRecurrence(int n) {
        long[] f = new long[Math.max(3, n + 1)];
        f[0] = 1; f[1] = 1; f[2] = 2;
        for (int i = 3; i <= n; i++) f[i] = (2 * f[i - 1] + f[i - 3]) % MOD;
        return (int) f[n];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1", numTilings(1), 1);
        check("n = 2", numTilings(2), 2);
        check("n = 3", numTilings(3), 5);
        check("n = 4", numTilings(4), 11);
        check("n = 5", numTilings(5), 24);
        for (int n = 1; n <= 1000; n++) {
            if (numTilings(n) != closedRecurrence(n)) check("n = " + n + " vs recurrence", numTilings(n), closedRecurrence(n));
        }
        System.out.println("PASS  state machine matches f(n) = 2f(n-1) + f(n-3) for n = 1..1000");
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
