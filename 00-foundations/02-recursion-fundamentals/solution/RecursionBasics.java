/**
 * Recursion Fundamentals — four small recursive functions
 *
 * Idea: Every recursive function has the same two parts:
 *         1. a base case  — a tiny input whose answer we know directly;
 *         2. a recursive case — solve a smaller version of the same problem
 *            and use its answer to build ours ("trust the recursion").
 *       The four examples go from one recursive call (factorial, array sum)
 *       to two calls (Fibonacci) to "one call per choice" (grid paths).
 *
 * Time complexity:
 *   factorial(n)      O(n)     — one call per value n, n-1, ..., 0.
 *   sumArray(a)       O(n)     — one call per index 0..n.
 *   fib(n)            O(2^n)   — each call makes two more; topic 03 counts them exactly.
 *   gridPaths(r, c)   O(2^(r+c)) — two choices (right/down) at every cell on the way.
 * Space complexity:
 *   O(depth of recursion): n for factorial/sum/fib, r + c for gridPaths — that many
 *   calls can be "waiting" on the call stack at the same moment.
 *
 * Run: java RecursionBasics.java
 */
public class RecursionBasics {

    /** n! = n * (n-1) * ... * 1, and 0! = 1. */
    static long factorial(int n) {
        if (n == 0) return 1;                 // base case: the empty product is 1
        return n * factorial(n - 1);          // trust that factorial(n-1) is correct
    }

    /** Sum of values[index..end]. Call with index = 0 for the whole array. */
    static int sumArray(int[] values, int index) {
        if (index == values.length) return 0; // base case: nothing left to add
        // my element + the sum of everything to my right (a smaller problem)
        return values[index] + sumArray(values, index + 1);
    }

    /** fib(0) = 0, fib(1) = 1, fib(n) = fib(n-1) + fib(n-2). */
    static long fib(int n) {
        if (n <= 1) return n;                 // two base cases: fib(0) and fib(1)
        return fib(n - 1) + fib(n - 2);       // two recursive calls
    }

    /**
     * Number of ways to walk from the top-left cell of a rows x cols grid to the
     * bottom-right cell, moving only right or down.
     * We stand at the top-left of a grid with `rows` rows and `cols` columns left.
     */
    static long gridPaths(int rows, int cols) {
        // base case: a single row or a single column leaves exactly one path (go straight)
        if (rows == 1 || cols == 1) return 1;
        // choice 1: step down  -> one fewer row remains
        // choice 2: step right -> one fewer column remains
        // The choices lead to different paths, so the counts simply add up.
        return gridPaths(rows - 1, cols) + gridPaths(rows, cols - 1);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("factorial(0)  [edge: base case]", factorial(0), 1);
        check("factorial(5)", factorial(5), 120);
        check("factorial(10)", factorial(10), 3628800);

        check("sumArray([])  [edge: empty]", sumArray(new int[]{}, 0), 0);
        check("sumArray([3, 1, 4, 1, 5])", sumArray(new int[]{3, 1, 4, 1, 5}, 0), 14);
        check("sumArray([-2, 7])", sumArray(new int[]{-2, 7}, 0), 5);

        check("fib(0)  [edge]", fib(0), 0);
        check("fib(1)  [edge]", fib(1), 1);
        check("fib(6)", fib(6), 8);
        check("fib(10)", fib(10), 55);

        check("gridPaths(1, 1)  [edge: start = end]", gridPaths(1, 1), 1);
        check("gridPaths(2, 2)", gridPaths(2, 2), 2);
        check("gridPaths(2, 3)", gridPaths(2, 3), 3);
        check("gridPaths(3, 3)", gridPaths(3, 3), 6);
        check("gridPaths(3, 7)", gridPaths(3, 7), 28);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
