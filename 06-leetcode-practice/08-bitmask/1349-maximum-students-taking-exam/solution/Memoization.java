/**
 * 1349. Maximum Students Taking Exam — Memoization (row-profile bitmask DP)
 *
 * Idea: conflicts only exist inside a row (left/right) or between consecutive rows (diagonals).
 * Filling row by row, the future depends only on the previous row's seating mask.
 * fill(row, prevMask) = most students in rows row..m-1 given the row above is seated as prevMask.
 *
 * Time complexity:  O(m * 4^n) — m * 2^n states, each tries 2^n masks for its row.
 * Space complexity: O(m * 2^n) — the memo (recursion depth m).
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static int rows, cols;
    private static int[] goodSeats;
    // memo[row][prevMask] = best count for rows row..m-1; -1 = not computed
    private static int[][] memo;

    static int maxStudents(char[][] seats) {
        rows = seats.length;
        cols = seats[0].length;
        goodSeats = goodSeatMasks(seats);
        memo = new int[rows][1 << cols];
        for (int[] row : memo) Arrays.fill(row, -1);
        return fill(0, 0);                       // above row 0 there is nobody: empty mask
    }

    private static int fill(int row, int prevMask) {
        if (row == rows) return 0;               // all rows seated
        if (memo[row][prevMask] != -1) return memo[row][prevMask];
        int best = 0;
        for (int mask = 0; mask < (1 << cols); mask++) {
            if (validRow(mask, goodSeats[row]) && compatible(mask, prevMask)) {
                best = Math.max(best, Integer.bitCount(mask) + fill(row + 1, mask));
            }
        }
        memo[row][prevMask] = best;
        return best;
    }

    // goodSeats[r] has bit j set when seat (r, j) is not broken
    static int[] goodSeatMasks(char[][] seats) {
        int[] good = new int[seats.length];
        for (int r = 0; r < seats.length; r++)
            for (int j = 0; j < seats[0].length; j++)
                if (seats[r][j] == '.') good[r] |= 1 << j;
        return good;
    }

    // only usable seats, and no two students side by side in the same row
    static boolean validRow(int mask, int goodSeats) {
        return (mask & ~goodSeats) == 0 && (mask & (mask >> 1)) == 0;
    }

    // nobody in 'mask' sits diagonally in front of somebody from the row above ('prev')
    static boolean compatible(int mask, int prev) {
        return (mask & (prev << 1)) == 0 && (mask & (prev >> 1)) == 0;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("3x6 example", maxStudents(room("#.##.#", ".####.", "#.##.#")), 4);
        check("5x2 example", maxStudents(room(".#", "##", "#.", "##", ".#")), 3);
        check("5x5 example", maxStudents(room("#...#", ".#.#.", "..#..", ".#.#.", "#...#")), 10);
        check("single broken seat (edge)", maxStudents(room("#")), 0);
        check("single good seat", maxStudents(room(".")), 1);
        String full = "........";
        check("8x8 empty room", maxStudents(room(full, full, full, full, full, full, full, full)), 32);
    }

    // builds the char[][] that LeetCode passes, from readable row strings
    private static char[][] room(String... rows) {
        char[][] seats = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) seats[i] = rows[i].toCharArray();
        return seats;
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
