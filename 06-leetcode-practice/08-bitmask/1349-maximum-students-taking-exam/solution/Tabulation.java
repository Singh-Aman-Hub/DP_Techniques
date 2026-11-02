/**
 * 1349. Maximum Students Taking Exam — Tabulation (row by row over seating masks)
 *
 * Idea: best[row][mask] = most students in rows 0..row with row seated exactly as mask.
 * It adds bitCount(mask) to the best compatible pattern of the row above. -1 marks patterns
 * that are impossible (broken seat or side-by-side students).
 *
 * Time complexity:  O(m * 4^n) — every row checks every (mask, previous mask) pair.
 * Space complexity: O(m * 2^n) — the full table.
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;

public class Tabulation {

    static int maxStudents(char[][] seats) {
        int rows = seats.length, cols = seats[0].length, masks = 1 << cols;
        int[] goodSeats = goodSeatMasks(seats);
        // best[row][mask] = max students in rows 0..row when row is seated as mask; -1 = impossible
        int[][] best = new int[rows][masks];
        for (int[] row : best) Arrays.fill(row, -1);
        for (int mask = 0; mask < masks; mask++) {
            if (validRow(mask, goodSeats[0])) best[0][mask] = Integer.bitCount(mask);   // no row above
        }
        for (int row = 1; row < rows; row++) {
            for (int mask = 0; mask < masks; mask++) {
                if (!validRow(mask, goodSeats[row])) continue;
                for (int prev = 0; prev < masks; prev++) {
                    if (best[row - 1][prev] < 0 || !compatible(mask, prev)) continue;
                    best[row][mask] = Math.max(best[row][mask], best[row - 1][prev] + Integer.bitCount(mask));
                }
            }
        }
        int answer = 0;
        for (int mask = 0; mask < masks; mask++) answer = Math.max(answer, best[rows - 1][mask]);
        return answer;
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
