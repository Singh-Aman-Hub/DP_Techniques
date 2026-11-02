/**
 * 664. Strange Printer — Memoization (top-down)
 *
 * Idea: f(i, j) = fewest turns to print s[i..j]. The stroke that prints s[i]
 * either serves only s[i] (1 + f(i+1, j)) or also prints a later equal
 * character s[k]; then the inside s[i+1..k-1] is printed on top
 * (f(i+1, k-1)) and s[i] rides for free on the stroke counted in f(k, j).
 *
 * Time complexity:  O(n^3) — n^2/2 ranges, each tries up to n merge partners k
 * Space complexity: O(n^2) — memo table; recursion depth up to n
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;
public class Memoization {

    // memo[i][j] = min turns to print s[i..j]; -1 = not computed
    private static int[][] memo;
    private static String text;

    static int strangePrinter(String s) {
        text = s;
        int n = s.length();
        memo = new int[n][n];
        for (int[] row : memo) Arrays.fill(row, -1);
        return turns(0, n - 1);
    }

    private static int turns(int i, int j) {
        if (i > j) return 0;                                  // empty range
        if (memo[i][j] != -1) return memo[i][j];
        int best = 1 + turns(i + 1, j);                       // s[i] gets its own stroke
        for (int k = i + 1; k <= j; k++) {
            if (text.charAt(k) == text.charAt(i)) {           // one stroke prints both s[i] and s[k]
                best = Math.min(best, turns(i + 1, k - 1) + turns(k, j));
            }
        }
        memo[i][j] = best;
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example abab", strangePrinter("abab"), 3);
        check("aaabbb", strangePrinter("aaabbb"), 2);
        check("aba", strangePrinter("aba"), 2);
        check("tbgtgb", strangePrinter("tbgtgb"), 4);
        check("single char (edge)", strangePrinter("a"), 1);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
