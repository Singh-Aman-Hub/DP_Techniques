/**
 * 788. Rotated Digits — Tabulation over the numbers themselves
 *
 * Idea: Classify every x in 0..n as INVALID (0), SAME (1: valid but rotates to itself) or GOOD (2).
 *       The class of x depends only on the class of x / 10 (all digits but the last) and of
 *       x % 10 (the last digit), both smaller than x, so a left-to-right loop fills the table.
 *       Combine rule: INVALID if either part is INVALID, GOOD if either part is GOOD, else SAME.
 *
 * Time complexity:  O(n) — each of the n+1 cells is computed once in O(1).
 * Space complexity: O(n) — one byte-sized class per number.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static final int INVALID = 0, SAME = 1, GOOD = 2;
    // class of a single digit 0..9 after rotating it by 180 degrees
    static final int[] DIGIT_CLASS = {SAME, SAME, GOOD, INVALID, INVALID, GOOD, GOOD, INVALID, SAME, GOOD};

    static int rotatedDigits(int n) {
        // kind[x] = INVALID, SAME or GOOD for the whole number x
        int[] kind = new int[n + 1];
        int good = 0;
        for (int x = 0; x <= n; x++) {
            if (x < 10) {
                kind[x] = DIGIT_CLASS[x];
            } else {
                int prefix = kind[x / 10], last = kind[x % 10];
                if (prefix == INVALID || last == INVALID) kind[x] = INVALID;
                else if (prefix == GOOD || last == GOOD) kind[x] = GOOD;
                else kind[x] = SAME;
            }
            if (kind[x] == GOOD) good++;
        }
        return good;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 10", rotatedDigits(10), 4);
        check("n = 1 (edge)", rotatedDigits(1), 0);
        check("n = 2", rotatedDigits(2), 1);
        check("n = 857", rotatedDigits(857), 247);
        check("n = 10000", rotatedDigits(10000), 2320);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
