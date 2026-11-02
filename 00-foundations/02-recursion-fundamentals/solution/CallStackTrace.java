/**
 * Recursion Fundamentals — watching the call stack
 *
 * Idea: Each time a function is called, Java pushes a "frame" (its own copy of
 *       the parameters and local variables) onto the call stack. The frame is
 *       popped when the call returns. This program prints an indented line on
 *       every push ("call") and every pop ("return"), so you can see the stack
 *       grow and shrink. It also measures the deepest the stack gets, and shows
 *       what happens when a base case is missing (StackOverflowError).
 *
 * Time complexity:  O(n) for the traced factorial, O(2^n) for the traced fib.
 * Space complexity: O(n) — the maximum stack depth, which this program measures.
 *
 * Run: java CallStackTrace.java
 */
public class CallStackTrace {

    private static int currentDepth = 0;  // how many frames are on the stack right now
    private static int maxDepth = 0;      // the largest value currentDepth ever reached
    private static boolean printTrace = true;

    private static void enter(String call) {
        currentDepth++;
        maxDepth = Math.max(maxDepth, currentDepth);
        if (printTrace) System.out.println("  ".repeat(currentDepth - 1) + "call   " + call);
    }

    private static void leave(String call, long result) {
        if (printTrace) System.out.println("  ".repeat(currentDepth - 1) + "return " + call + " = " + result);
        currentDepth--;
    }

    static long factorial(int n) {
        enter("factorial(" + n + ")");
        long result = (n == 0) ? 1 : n * factorial(n - 1);
        leave("factorial(" + n + ")", result);
        return result;
    }

    static long fib(int n) {
        enter("fib(" + n + ")");
        long result = (n <= 1) ? n : fib(n - 1) + fib(n - 2);
        leave("fib(" + n + ")", result);
        return result;
    }

    /** Deliberately broken: no base case, so it never stops calling itself. */
    static long brokenCountdown(int n) {
        return 1 + brokenCountdown(n - 1);
    }

    private static void reset(boolean print) {
        currentDepth = 0;
        maxDepth = 0;
        printTrace = print;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        System.out.println("Trace of factorial(4):");
        reset(true);
        check("factorial(4)", factorial(4), 24);
        // factorial(4) -> (3) -> (2) -> (1) -> (0): five frames stacked at once
        check("max stack depth for factorial(4)", maxDepth, 5);

        System.out.println();
        System.out.println("Trace of fib(4):");
        reset(true);
        check("fib(4)", fib(4), 3);
        // longest chain: fib(4) -> fib(3) -> fib(2) -> fib(1): four frames
        check("max stack depth for fib(4)", maxDepth, 4);

        System.out.println();
        reset(false);
        check("factorial(0)  [edge: only the base case runs]", factorial(0), 1);
        check("max stack depth for factorial(0)", maxDepth, 1);

        reset(false);
        fib(20);
        // fib(n) goes down the "n-1" side first: fib(20) -> fib(19) -> ... -> fib(1) = 20 frames
        check("max stack depth for fib(20)", maxDepth, 20);
        check("stack is empty again after returning", currentDepth, 0);

        // Missing base case: Java eventually runs out of stack space.
        boolean overflowed = false;
        try {
            brokenCountdown(10);
        } catch (StackOverflowError error) {
            overflowed = true;
        }
        System.out.println("brokenCountdown(10) threw StackOverflowError: " + overflowed);
        check("missing base case overflows the stack", overflowed ? 1 : 0, 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
