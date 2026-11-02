/**
 * What is Dynamic Programming? — Demo: naive vs. cached Fibonacci
 *
 * Idea: Both functions compute the n-th Fibonacci number with the same rule
 *       fib(n) = fib(n - 1) + fib(n - 2). The naive version recomputes the same
 *       values again and again. The cached version writes every answer into a
 *       "notebook" (an array) the first time and simply reads it afterwards.
 *       That one change, "remember what you already computed", is the heart of DP.
 *
 * Time complexity:  naive  O(2^n) — every call spawns two more calls (roughly doubling).
 *                   cached O(n)   — each value fib(0..n) is computed once, O(1) work each.
 * Space complexity: naive  O(n)   — the deepest chain of unfinished calls has n frames.
 *                   cached O(n)   — the notebook has n + 1 cells, plus the O(n) call stack.
 *
 * Run: java Demo.java
 */
public class Demo {

    // Counts how many times each function body runs, so the difference is visible
    // even on a fast machine where the timing alone might look small.
    private static long naiveCalls = 0;
    private static long cachedCalls = 0;

    /** Plain recursion: solves the same smaller problems many times. */
    static long naiveFib(int n) {
        naiveCalls++;
        if (n <= 1) return n;                         // fib(0) = 0, fib(1) = 1
        return naiveFib(n - 1) + naiveFib(n - 2);
    }

    // notebook[i] holds fib(i) once it is known; -1 means "not written down yet".
    private static long[] notebook;

    /** Same recursion, but it looks in the notebook before doing any work. */
    static long cachedFib(int n) {
        cachedCalls++;
        if (n <= 1) return n;
        if (notebook[n] != -1) return notebook[n];    // already solved: just read it
        notebook[n] = cachedFib(n - 1) + cachedFib(n - 2);
        return notebook[n];
    }

    static long cachedFibFresh(int n) {
        notebook = new long[n + 1];
        java.util.Arrays.fill(notebook, -1);
        return cachedFib(n);
    }

    // ---------- self-check + timing ----------
    public static void main(String[] args) {
        // Correctness: both versions must give the same, hand-checked answers.
        check("naive  fib(0)", naiveFib(0), 0);
        check("naive  fib(1)", naiveFib(1), 1);
        check("naive  fib(10)", naiveFib(10), 55);
        check("cached fib(10)", cachedFibFresh(10), 55);
        check("cached fib(50)", cachedFibFresh(50), 12586269025L);

        // Timing: n = 35 is slow enough to notice for the naive version, still quick to finish.
        int n = 35;
        naiveCalls = 0;
        long start = System.nanoTime();
        long slow = naiveFib(n);
        long naiveMillis = (System.nanoTime() - start) / 1_000_000;

        cachedCalls = 0;
        start = System.nanoTime();
        long fast = cachedFibFresh(n);
        long cachedMicros = (System.nanoTime() - start) / 1_000;

        System.out.println();
        System.out.println("fib(" + n + ") naive : " + slow + "  calls = " + naiveCalls + "  time ~ " + naiveMillis + " ms");
        System.out.println("fib(" + n + ") cached: " + fast + "  calls = " + cachedCalls + "  time ~ " + cachedMicros + " microseconds");
        System.out.println();

        // The call counts are exact, so we can test them. Naive calls for fib(n) = 2*fib(n+1) - 1.
        check("naive calls for fib(35)", naiveCalls, 2L * 14930352L - 1);   // fib(36) = 14930352
        // Cached: fib(n) is entered once for each n..2 (computing), plus one extra
        // look-up call per value, giving 2n - 1 calls in total.
        check("cached calls for fib(35)", cachedCalls, 2L * n - 1);
        check("both agree on fib(35)", fast, slow);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
