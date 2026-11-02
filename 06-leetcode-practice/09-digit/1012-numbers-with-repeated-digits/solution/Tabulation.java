/**
 * 1012. Numbers With Repeated Digits — Tabulation (counting distinct-digit numbers with permutations)
 *
 * Idea: Same complement: answer = n - (# of x in [1, n] with all-distinct digits).
 *       The distinct count is built from a small table instead of recursion:
 *       perm[a][b] = a * (a-1) * ... * (a-b+1) = ways to fill b positions with distinct digits from a.
 *       (a) Numbers with fewer digits than n (length k < L): 9 * perm[9][k-1].
 *       (b) Numbers with L digits: walk n's digits; at position i try every unused digit d < n[i]
 *           (no 0 at i = 0); the remaining L-1-i positions take distinct digits from the
 *           10-(i+1) still unused: perm[10-(i+1)][L-1-i]. Then fix n[i]; if it repeats, stop.
 *           If the walk finishes, n itself has distinct digits: add 1.
 *
 * Time complexity:  O(L * 10) — L positions, up to 10 candidate digits each (table is 11 x 11).
 * Space complexity: O(1) — the permutation table has a fixed size.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int numDupDigitsAtMostN(int n) {
        // perm[a][b] = number of ordered ways to choose b distinct digits out of a available
        int[][] perm = new int[11][11];
        for (int a = 0; a <= 10; a++) {
            perm[a][0] = 1;
            for (int b = 1; b <= a; b++) perm[a][b] = perm[a][b - 1] * (a - b + 1);
        }

        String bound = Integer.toString(n);
        int length = bound.length();
        int distinct = 0;
        for (int k = 1; k < length; k++) distinct += 9 * perm[9][k - 1]; // first digit 1..9, the rest distinct

        int usedMask = 0;
        boolean boundStillDistinct = true;
        for (int i = 0; i < length && boundStillDistinct; i++) {
            int boundDigit = bound.charAt(i) - '0';
            for (int digit = (i == 0 ? 1 : 0); digit < boundDigit; digit++) {
                if ((usedMask & (1 << digit)) != 0) continue;
                distinct += perm[10 - (i + 1)][length - 1 - i]; // lower positions are now free
            }
            if ((usedMask & (1 << boundDigit)) != 0) boundStillDistinct = false; // n's prefix repeats
            usedMask |= 1 << boundDigit;
        }
        if (boundStillDistinct) distinct++; // n itself
        return n - distinct;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 20", numDupDigitsAtMostN(20), 1);
        check("n = 100", numDupDigitsAtMostN(100), 10);
        check("n = 1000", numDupDigitsAtMostN(1000), 262);
        check("n = 1 (edge)", numDupDigitsAtMostN(1), 0);
        check("n = 1e9", numDupDigitsAtMostN(1000000000), 994388230);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
