/**
 * 1411. Number of Ways to Paint N x 3 Grid — Memoization (top-down, 12 row patterns)
 *
 * Idea: A row is one of 12 valid 3-cell patterns. Whether a row may sit below another depends
 * only on those two patterns (no equal colour in the same column), so ways(row, prevPattern)
 * is the whole state. The patterns and the compatibility table are generated, not hard-coded.
 *
 * Time complexity:  O(n * 144) — 12n states, 12 candidate next patterns each.
 * Space complexity: O(n * 12) — memo table plus recursion depth n.
 *
 * Run: java Memoization.java
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Memoization {

    private static final int MOD = 1_000_000_007;
    private static int rows;
    private static int[][] patterns;          // patterns[p] = the 3 colours of row pattern p
    private static boolean[][] compatible;    // compatible[p][q] = q may be painted directly below p
    private static long[][] memo;             // memo[row][p] = ways to paint rows row..n-1 under p; -1 = unknown

    static int numOfWays(int n) {
        rows = n;
        buildPatterns();
        memo = new long[n][patterns.length];
        for (long[] line : memo) Arrays.fill(line, -1);
        long total = 0;
        for (int p = 0; p < patterns.length; p++) total += ways(1, p);   // row 0 painted with p
        return (int) (total % MOD);
    }

    private static long ways(int row, int prev) {
        if (row == rows) return 1;                         // every row is painted
        if (memo[row][prev] != -1) return memo[row][prev];
        long total = 0;
        for (int next = 0; next < patterns.length; next++) {
            if (compatible[prev][next]) total += ways(row + 1, next);
        }
        memo[row][prev] = total % MOD;
        return memo[row][prev];
    }

    private static void buildPatterns() {
        List<int[]> list = new ArrayList<>();
        for (int a = 0; a < 3; a++)
            for (int b = 0; b < 3; b++)
                for (int c = 0; c < 3; c++)
                    if (a != b && b != c) list.add(new int[]{a, b, c});   // no equal horizontal neighbours
        patterns = list.toArray(new int[0][]);
        compatible = new boolean[patterns.length][patterns.length];
        for (int p = 0; p < patterns.length; p++)
            for (int q = 0; q < patterns.length; q++)
                compatible[p][q] = patterns[p][0] != patterns[q][0]
                                && patterns[p][1] != patterns[q][1]
                                && patterns[p][2] != patterns[q][2];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", numOfWays(1), 12);
        check("n = 2", numOfWays(2), 54);
        check("n = 3", numOfWays(3), 246);
        check("n = 5000", numOfWays(5000), 30228214);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
