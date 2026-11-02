/**
 * 1349. Maximum Students Taking Exam — Optimized (rolling arrays + precomputed valid masks)
 *
 * Idea: row r reads only row r-1, so keep two arrays of size 2^n and swap them. Also list the
 * valid masks of each row once, so the double loop skips impossible patterns (for n = 8 only
 * 55 of 256 masks have no two adjacent bits).
 *
 * Time complexity:  O(m * 4^n) worst case — far fewer pairs in practice thanks to the valid lists.
 * Space complexity: O(2^n) — two rows of the table plus the valid-mask list.
 *
 * Run: java Optimized.java
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Optimized {

    static int maxStudents(char[][] seats) {
        int rows = seats.length, cols = seats[0].length, masks = 1 << cols;
        int[] goodSeats = goodSeatMasks(seats);
        // previous[mask] = best count up to the previous row with that row seated as mask; -1 = impossible
        int[] previous = new int[masks];
        Arrays.fill(previous, -1);
        previous[0] = 0;                                  // imaginary empty row above row 0
        List<Integer> previousValid = new ArrayList<>();
        previousValid.add(0);
        for (int row = 0; row < rows; row++) {
            int[] current = new int[masks];
            Arrays.fill(current, -1);
            List<Integer> currentValid = new ArrayList<>();
            for (int mask = 0; mask < masks; mask++) {
                if (!validRow(mask, goodSeats[row])) continue;
                currentValid.add(mask);
                for (int prev : previousValid) {
                    if (compatible(mask, prev)) {
                        current[mask] = Math.max(current[mask], previous[prev] + Integer.bitCount(mask));
                    }
                }
            }
            previous = current;
            previousValid = currentValid;
        }
        int answer = 0;
        for (int mask : previousValid) answer = Math.max(answer, previous[mask]);
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
