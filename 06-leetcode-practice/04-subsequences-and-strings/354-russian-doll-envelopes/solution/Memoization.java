/**
 * 354. Russian Doll Envelopes — Memoization (top-down)
 *
 * Idea: after sorting (width asc, height desc on ties) a nesting is a strictly
 * increasing subsequence of heights. f(i) = most envelopes in a nesting whose
 * outermost envelope is sorted index i; cached per index (O(n^2) LIS).
 *
 * Time complexity:  O(n^2) — n states, each scans up to n earlier envelopes once (plus O(n log n) sort)
 * Space complexity: O(n) — memo array; recursion depth up to n
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;
public class Memoization {

    // memo[i] = longest nesting whose outermost envelope is sorted index i; 0 = not computed
    private static int[] memo;
    private static int[] heights;

    static int maxEnvelopes(int[][] envelopes) {
        int[][] sorted = sortForLis(envelopes);
        int n = sorted.length;
        heights = new int[n];
        for (int i = 0; i < n; i++) heights[i] = sorted[i][1];
        memo = new int[n];
        int best = 0;
        for (int i = 0; i < n; i++) best = Math.max(best, chainEndingAt(i));
        return best;
    }

    private static int chainEndingAt(int i) {
        if (memo[i] != 0) return memo[i];
        int best = 1;                                         // envelope i alone
        for (int j = 0; j < i; j++) {
            if (heights[j] < heights[i]) {                    // the sort already handles widths
                best = Math.max(best, 1 + chainEndingAt(j));
            }
        }
        memo[i] = best;
        return best;
    }

    // Sort by width ascending; equal widths by height DESCENDING so that a strictly
    // increasing run of heights can never use two envelopes of the same width.
    private static int[][] sortForLis(int[][] envelopes) {
        int[][] sorted = envelopes.clone();
        Arrays.sort(sorted, (first, second) -> first[0] != second[0]
                ? Integer.compare(first[0], second[0])
                : Integer.compare(second[1], first[1]));
        return sorted;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example", maxEnvelopes(new int[][]{{5, 4}, {6, 4}, {6, 7}, {2, 3}}), 3);
        check("chain of four", maxEnvelopes(new int[][]{{4, 5}, {4, 6}, {6, 7}, {2, 3}, {1, 1}}), 4);
        check("same width needs descending tie-break", maxEnvelopes(new int[][]{{1, 2}, {1, 3}, {1, 4}}), 1);
        check("identical envelopes", maxEnvelopes(new int[][]{{1, 1}, {1, 1}, {1, 1}}), 1);
        check("single envelope (edge)", maxEnvelopes(new int[][]{{1, 1}}), 1);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
