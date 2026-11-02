/**
 * Space optimization basics — Climbing Stairs with two variables
 *
 * Idea: ways[i] = ways[i-1] + ways[i-2] only ever reads the two cells just
 * before i. Once ways[i] is computed, ways[i-2] is never read again, so the
 * whole array can be replaced by two variables that "slide" forward:
 *   twoBack = ways[i-2], oneBack = ways[i-1].
 * The table version is kept here so the two can be compared on the same tests.
 *
 * Time complexity:  O(n) — both versions compute each of the n+1 states once with O(1) work.
 * Space complexity: table O(n) — one cell per step; optimized O(1) — two variables.
 *
 * Run: java StairsTwoVariables.java
 */
public class StairsTwoVariables {

    /** Full table, for comparison. */
    static long waysTable(int n) {
        // ways[i] = number of ways to reach step i using 1- or 2-steps
        long[] ways = new long[Math.max(n + 1, 2)];
        ways[0] = 1;
        ways[1] = 1;
        for (int i = 2; i <= n; i++) {
            ways[i] = ways[i - 1] + ways[i - 2];
        }
        return ways[n];
    }

    /** Two variables: only the last two states are ever read. */
    static long waysTwoVariables(int n) {
        long twoBack = 1; // ways[i-2], starts as ways[0]
        long oneBack = 1; // ways[i-1], starts as ways[1]
        if (n <= 1) return 1;
        for (int i = 2; i <= n; i++) {
            long current = oneBack + twoBack; // ways[i]
            // slide the window one step right; order matters: move oneBack into twoBack first
            twoBack = oneBack;
            oneBack = current;
        }
        return oneBack; // after the loop, oneBack holds ways[n]
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[] inputs = {5, 10, 1, 0, 45};
        long[] expected = {8, 89, 1, 1, 1836311903L};
        for (int k = 0; k < inputs.length; k++) {
            int n = inputs[k];
            check("table n=" + n, waysTable(n), expected[k]);
            check("two variables n=" + n, waysTwoVariables(n), expected[k]);
        }
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
