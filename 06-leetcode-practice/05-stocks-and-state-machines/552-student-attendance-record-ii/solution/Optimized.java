/**
 * 552. Student Attendance Record II — Space-optimized (6 rolling counters)
 *
 * Idea: Length i+1 depends only on length i, so keep one 2 x 3 array for the current length
 * and build the next one from it.
 *
 * Time complexity:  O(n) — constant work (6 states x 3 moves) per character.
 * Space complexity: O(1) — two 2 x 3 arrays.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    private static final int MOD = 1_000_000_007;

    static int checkRecord(int n) {
        long[][] current = new long[2][3];   // current[a][l] for the current length
        current[0][0] = 1;
        for (int i = 0; i < n; i++) {
            long[][] next = new long[2][3];
            for (int a = 0; a <= 1; a++) {
                for (int l = 0; l <= 2; l++) {
                    long count = current[a][l];
                    next[a][0] = (next[a][0] + count) % MOD;                    // P
                    if (a == 0) next[1][0] = (next[1][0] + count) % MOD;        // A
                    if (l < 2) next[a][l + 1] = (next[a][l + 1] + count) % MOD; // L
                }
            }
            current = next;
        }
        long total = 0;
        for (int a = 0; a <= 1; a++) for (int l = 0; l <= 2; l++) total += current[a][l];
        return (int) (total % MOD);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", checkRecord(1), 3);
        check("n = 2", checkRecord(2), 8);
        check("n = 3", checkRecord(3), 19);
        check("n = 10101", checkRecord(10101), 183236316);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
