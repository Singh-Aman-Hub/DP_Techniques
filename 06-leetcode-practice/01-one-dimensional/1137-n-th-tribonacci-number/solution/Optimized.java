/**
 * 1137. N-th Tribonacci Number — Space-optimized
 *
 * Idea: The tabulation only ever reads the last three cells, so keep just three rolling
 * variables (a = T(i-3), b = T(i-2), c = T(i-1)) and slide them forward each step.
 *
 * Time complexity:  O(n) — one pass, O(1) work per index.
 * Space complexity: O(1) — three variables regardless of n.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static long tribonacci(int n) {
        if (n == 0) return 0;
        if (n <= 2) return 1;

        long a = 0, b = 1, c = 1;             // T0, T1, T2
        for (int i = 3; i <= n; i++) {
            long next = a + b + c;            // compute before sliding, or a value is lost
            a = b;
            b = c;
            c = next;
        }
        return c;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 0 (edge)", tribonacci(0), 0);
        check("n = 2 (edge)", tribonacci(2), 1);
        check("n = 4", tribonacci(4), 4);
        check("n = 6", tribonacci(6), 13);
        check("n = 25", tribonacci(25), 1389537);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
