/**
 * 354. Russian Doll Envelopes — Tabulation (bottom-up)
 *
 * Idea: same reduction (sort width asc, height desc on ties), then the classic
 * bottom-up O(n^2) LIS over heights: chain[i] = 1 + max chain[j] for j < i
 * with a smaller height.
 *
 * Time complexity:  O(n^2) — double loop over pairs (j, i) after an O(n log n) sort
 * Space complexity: O(n) — the chain array
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;
public class Tabulation {

    static int maxEnvelopes(int[][] envelopes) {
        int[][] sorted = sortForLis(envelopes);
        int n = sorted.length;
        // chain[i] = most envelopes in a nesting whose outermost envelope is sorted[i]
        int[] chain = new int[n];
        int best = 0;
        for (int i = 0; i < n; i++) {
            chain[i] = 1;
            for (int j = 0; j < i; j++) {
                if (sorted[j][1] < sorted[i][1]) chain[i] = Math.max(chain[i], chain[j] + 1);
            }
            best = Math.max(best, chain[i]);
        }
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
