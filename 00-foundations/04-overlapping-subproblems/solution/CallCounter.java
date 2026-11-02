/**
 * Overlapping Subproblems — counting how often each subproblem is solved
 *
 * Idea: Add a counter array to plain recursion: callsFor[k] = how many times
 *       the function was called with argument k. For Fibonacci the counts are
 *       large (fib(1) is computed fib(n) times) — the subproblems OVERLAP.
 *       For merge sort we count calls per index range (lo, hi): every range is
 *       sorted exactly once — the subproblems do NOT overlap, so caching would
 *       not help there.
 *
 * Time complexity:  fib O(2^n) (the point of the demo); merge sort O(n log n).
 * Space complexity: O(n) counters + O(n) stack for fib; O(n^2) range counters
 *                   for merge sort (fine for the tiny demo arrays).
 *
 * Run: java CallCounter.java
 */
public class CallCounter {

    // callsFor[k] = number of times fib(k) was entered
    private static long[] callsFor;

    static long countedFib(int n) {
        callsFor[n]++;
        if (n <= 1) return n;
        return countedFib(n - 1) + countedFib(n - 2);
    }

    /** Runs fib(n) with fresh counters and returns the counter array. */
    static long[] fibCallCounts(int n) {
        callsFor = new long[n + 1];
        countedFib(n);
        return callsFor;
    }

    // rangeCalls[lo][hi] = how many times mergeSort was asked to sort values[lo..hi]
    private static int[][] rangeCalls;

    static void mergeSort(int[] values, int lo, int hi) {
        rangeCalls[lo][hi]++;
        if (lo >= hi) return;                  // 0 or 1 element: already sorted
        int mid = (lo + hi) / 2;
        mergeSort(values, lo, mid);            // left half and right half share
        mergeSort(values, mid + 1, hi);        // no elements: disjoint subproblems
        merge(values, lo, mid, hi);
    }

    private static void merge(int[] values, int lo, int mid, int hi) {
        int[] merged = new int[hi - lo + 1];
        int left = lo, right = mid + 1, out = 0;
        while (left <= mid && right <= hi) merged[out++] = (values[left] <= values[right]) ? values[left++] : values[right++];
        while (left <= mid) merged[out++] = values[left++];
        while (right <= hi) merged[out++] = values[right++];
        System.arraycopy(merged, 0, values, lo, merged.length);
    }

    /** Largest number of times any single range was sorted. */
    static int maxCallsPerRange(int[] values) {
        int n = values.length;
        rangeCalls = new int[n][n];
        mergeSort(values, 0, n - 1);
        int most = 0, distinctRanges = 0;
        for (int[] row : rangeCalls) for (int count : row) {
            most = Math.max(most, count);
            if (count > 0) distinctRanges++;
        }
        System.out.println("merge sort on " + n + " elements: " + distinctRanges + " distinct ranges, each sorted at most " + most + " time(s)");
        return most;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        long[] counts = fibCallCounts(6);
        System.out.println("fib(6): how many times each subproblem is computed");
        System.out.println("  k  | calls to fib(k)");
        System.out.println(" ----+----------------");
        long total = 0;
        for (int k = 6; k >= 0; k--) {
            System.out.printf("  %d  | %d%n", k, counts[k]);
            total += counts[k];
        }
        System.out.println("  total calls = " + total);
        System.out.println();

        check("calls to fib(6)", counts[6], 1);
        check("calls to fib(5)", counts[5], 1);
        check("calls to fib(4)", counts[4], 2);
        check("calls to fib(3)", counts[3], 3);
        check("calls to fib(2)", counts[2], 5);
        check("calls to fib(1)", counts[1], 8);
        check("calls to fib(0)", counts[0], 5);
        check("total calls for fib(6)", total, 25);

        long[] bigger = fibCallCounts(20);
        check("calls to fib(1) inside fib(20) = fib(20)", bigger[1], 6765);

        long[] tiny = fibCallCounts(1);
        check("fib(1) [edge]: computed exactly once", tiny[1], 1);

        int[] data = {5, 2, 9, 1, 7, 3, 8, 6};
        check("merge sort: no range is sorted twice", maxCallsPerRange(data), 1);
        check("merge sort actually sorted the data (first)", data[0], 1);
        check("merge sort actually sorted the data (last)", data[7], 9);
        check("merge sort, one element [edge]", maxCallsPerRange(new int[]{42}), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
