/**
 * Time and space complexity in DP — the hidden cost of recursion: stack depth
 *
 * Idea: memoized Climbing Stairs uses O(n) memory for the cache AND O(n) memory
 * for the call stack, because ways(n) calls ways(n-1), which calls ways(n-2), ...
 * all the way down before anything returns. Java gives each thread a fixed-size
 * stack; when the chain of calls is too deep the JVM throws StackOverflowError.
 * Tabulation has no recursion, so it has no such limit.
 *
 * This demo runs the memoized recursion inside a Thread whose stack size we choose
 * (new Thread(group, task, name, stackSizeInBytes)), so the result does not depend
 * on the default stack size or on the -Xss flag of the JVM that launches it.
 *
 * Time complexity:  O(n) for both versions — n states, O(1) work each.
 * Space complexity: memo O(n) cache + O(n) stack frames; tabulation O(n) table, O(1) stack.
 *
 * Run: java StackDepthDemo.java
 */
public class StackDepthDemo {

    private static final long MOD = 1_000_000_007L; // keep numbers small so they never overflow a long

    /** Memoized ways(n) — recursion depth is about n. */
    static long waysMemo(int n, long[] memo) {
        if (n <= 1) return 1;
        if (memo[n] != 0) return memo[n];
        memo[n] = (waysMemo(n - 1, memo) + waysMemo(n - 2, memo)) % MOD;
        return memo[n];
    }

    /** Tabulated ways(n) — a plain loop, recursion depth 1. */
    static long waysTab(int n) {
        // ways[i] = number of ways to reach step i (mod 1e9+7)
        long[] ways = new long[Math.max(n + 1, 2)];
        ways[0] = 1;
        ways[1] = 1;
        for (int i = 2; i <= n; i++) {
            ways[i] = (ways[i - 1] + ways[i - 2]) % MOD;
        }
        return ways[n];
    }

    /**
     * Runs waysMemo(n) on a new thread with the given stack size.
     * Returns the answer, or -1 if the thread ran out of stack.
     */
    static long memoOnStack(int n, long stackBytes) {
        long[] result = {-1};
        Runnable task = () -> {
            try {
                result[0] = waysMemo(n, new long[n + 1]);
            } catch (StackOverflowError e) {
                result[0] = -1; // the call chain was deeper than this stack can hold
            }
        };
        Thread worker = new Thread(null, task, "dp-worker", stackBytes);
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return result[0];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        long smallStack = 256L * 1024;       // 256 KB
        long bigStack = 256L * 1024 * 1024;  // 256 MB

        check("memo n=10 on 256 KB stack", memoOnStack(10, smallStack), 89);
        check("memo n=45 on 256 KB stack (1836311903 mod 1e9+7)", memoOnStack(45, smallStack), 836_311_896);
        check("memo n=1 (edge) on 256 KB stack", memoOnStack(1, smallStack), 1);

        // One million nested calls cannot fit in 256 KB: expect a StackOverflowError (reported as -1).
        check("memo n=1,000,000 on 256 KB stack overflows", memoOnStack(1_000_000, smallStack), -1);

        // Tabulation has no recursion, so it handles a million steps on the normal main thread.
        long tab = waysTab(1_000_000);
        // The same memoized code works if we give it a much bigger stack; both must agree.
        long memoBig = memoOnStack(200_000, bigStack);
        check("memo n=200,000 on 256 MB stack equals tabulation", memoBig, waysTab(200_000));
        check("tabulation n=1,000,000 finished (result in [0, MOD))", (tab >= 0 && tab < MOD) ? 1 : 0, 1);
        check("tabulation n=45 equals memo", waysTab(45), 836_311_896);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
