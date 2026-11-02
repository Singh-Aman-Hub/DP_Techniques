/**
 * 354. Russian Doll Envelopes — Space-optimized DP
 *
 * Idea: after the same sort, run the O(n log n) LIS on heights. tails[k] is the
 * smallest possible height ending a nesting of k+1 envelopes. Each new height
 * replaces the first tail >= it (lower bound, because nesting is strict) or
 * extends the array. The array length is the answer.
 *
 * Time complexity:  O(n log n) — the sort plus one binary search per envelope
 * Space complexity: O(n) — the tails array
 *
 * Run: java Optimized.java
 */
import java.util.Arrays;
public class Optimized {

    static int maxEnvelopes(int[][] envelopes) {
        int[][] sorted = sortForLis(envelopes);
        // tails[k] = smallest outermost height over all nestings of k+1 envelopes seen so far
        int[] tails = new int[sorted.length];
        int size = 0;
        for (int[] envelope : sorted) {
            int height = envelope[1];
            int lo = 0, hi = size;                            // lower bound: first tails[pos] >= height
            while (lo < hi) {
                int mid = (lo + hi) / 2;
                if (tails[mid] < height) lo = mid + 1;
                else hi = mid;
            }
            tails[lo] = height;                               // better (smaller) ending for length lo+1
            if (lo == size) size++;                           // extended the longest nesting
        }
        return size;
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
