/**
 * Memoization — three common bugs, each next to its fix
 *
 * Idea: Memoization code often looks right and returns right answers while
 *       secretly being as slow as brute force, or it silently overflows.
 *         Bug 1  cache re-created inside the recursive function  -> nothing is ever reused.
 *         Bug 2  sentinel 0 when 0 is a real answer            -> zero answers are recomputed.
 *         Bug 3  int cache / int arithmetic                    -> overflow past fib(46).
 *       We count calls to show bugs 1 and 2 (the answers are still "correct"),
 *       and compare values to show bug 3.
 *
 * Time complexity:  buggy versions O(2^n)-like; fixed versions O(n) / O(number of states).
 * Space complexity: O(n) for the caches and recursion depth.
 *
 * Run: java MemoBugs.java
 */
import java.util.Arrays;

public class MemoBugs {

    private static long calls = 0;

    // ---------- Bug 1: the cache is reset on every call ----------
    static long fibResetBug(int n) {
        long[] memo = new long[n + 1];       // BUG: a brand-new empty cache in every call
        Arrays.fill(memo, -1);
        calls++;
        if (n <= 1) return n;
        if (memo[n] != -1) return memo[n];   // never true: this cache was just created
        memo[n] = fibResetBug(n - 1) + fibResetBug(n - 2);
        return memo[n];
    }

    private static long[] sharedMemo;        // FIX: one cache, created once, shared by all calls

    static long fibShared(int n) {
        calls++;
        if (n <= 1) return n;
        if (sharedMemo[n] != -1) return sharedMemo[n];
        sharedMemo[n] = fibShared(n - 1) + fibShared(n - 2);
        return sharedMemo[n];
    }

    // ---------- Bug 2: a sentinel that is also a valid answer ----------
    // jumpWays(n) = number of ordered ways to cover exactly n stairs with jumps of 4 or 6.
    // For an odd n every answer along the way is 0 — a perfectly valid answer.
    private static long[] jumpMemo;

    static long jumpWaysZeroSentinel(int n) {
        calls++;
        if (n < 0) return 0;
        if (n == 0) return 1;
        if (jumpMemo[n] != 0) return jumpMemo[n];     // BUG: 0 means both "unknown" and "zero ways"
        jumpMemo[n] = jumpWaysZeroSentinel(n - 4) + jumpWaysZeroSentinel(n - 6);
        return jumpMemo[n];
    }

    static long jumpWaysMinusOneSentinel(int n) {
        calls++;
        if (n < 0) return 0;
        if (n == 0) return 1;
        if (jumpMemo[n] != -1) return jumpMemo[n];    // FIX: -1 can never be a count
        jumpMemo[n] = jumpWaysMinusOneSentinel(n - 4) + jumpWaysMinusOneSentinel(n - 6);
        return jumpMemo[n];
    }

    // ---------- Bug 3: int overflow ----------
    private static int[] intMemo;

    static int fibInt(int n) {                        // BUG: int holds at most 2,147,483,647
        if (n <= 1) return n;
        if (intMemo[n] != -1) return intMemo[n];
        intMemo[n] = fibInt(n - 1) + fibInt(n - 2);   // silently wraps around to a negative number
        return intMemo[n];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        // Bug 1: same answer, but as many calls as plain recursion (2 * fib(21) - 1)
        calls = 0;
        check("reset-cache fib(20) answer", fibResetBug(20), 6765);
        long resetCalls = calls;
        check("reset-cache fib(20) calls (no reuse at all)", resetCalls, 21891);

        sharedMemo = new long[21];
        Arrays.fill(sharedMemo, -1);
        calls = 0;
        check("shared-cache fib(20) answer", fibShared(20), 6765);
        check("shared-cache fib(20) calls (2n - 1)", calls, 39);

        // Bug 2: n = 41 is odd, so the true answer is 0
        int stairs = 41;
        jumpMemo = new long[stairs + 1];                  // all zeros = the buggy "unknown"
        calls = 0;
        check("zero-sentinel jumpWays(41) answer", jumpWaysZeroSentinel(stairs), 0);
        long zeroSentinelCalls = calls;

        jumpMemo = new long[stairs + 1];
        Arrays.fill(jumpMemo, -1);
        calls = 0;
        check("-1-sentinel jumpWays(41) answer", jumpWaysMinusOneSentinel(stairs), 0);
        long minusOneCalls = calls;
        System.out.println("      calls with sentinel 0: " + zeroSentinelCalls + ", with sentinel -1: " + minusOneCalls);
        check("sentinel 0 makes at least 10x more calls", zeroSentinelCalls >= 10 * minusOneCalls ? 1 : 0, 1);

        jumpMemo = new long[13];
        Arrays.fill(jumpMemo, -1);
        // 12 = 4+4+4 or 6+6 -> 2 ways
        check("jumpWays(12)  [non-zero case]", jumpWaysMinusOneSentinel(12), 2);
        jumpMemo = new long[1];
        Arrays.fill(jumpMemo, -1);
        check("jumpWays(0)  [edge]", jumpWaysMinusOneSentinel(0), 1);

        // Bug 3: fib(46) still fits in int, fib(47) = 2,971,215,073 does not
        intMemo = new int[48];
        Arrays.fill(intMemo, -1);
        check("int fib(46) still correct", fibInt(46), 1836311903);
        check("int fib(47) overflows to a negative number", fibInt(47), -1323752223);
        sharedMemo = new long[48];
        Arrays.fill(sharedMemo, -1);
        check("long fib(47) is correct", fibShared(47), 2971215073L);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
