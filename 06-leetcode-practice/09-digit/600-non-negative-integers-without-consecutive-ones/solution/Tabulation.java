/**
 * 600. Non-negative Integers without Consecutive Ones — Tabulation (Fibonacci table + bit walk)
 *
 * Idea: free[k] = number of k-bit strings (leading zeros allowed) with no "11".
 *       A string either starts with 0 (then any free[k-1] string follows) or with "10"
 *       (then any free[k-2] string follows): free[k] = free[k-1] + free[k-2], free[0]=1, free[1]=2.
 *       Walk n's bits from the top. At every 1-bit at position i (i bits below it), putting a 0
 *       there makes the number smaller than n, so the lower i bits are free: add free[i].
 *       If we meet two adjacent 1-bits in n, every further tight number would contain "11": stop.
 *       If the walk finishes, n itself is valid: add 1.
 *
 * Time complexity:  O(B) — B = 31 bits, one table cell and one step per bit.
 * Space complexity: O(B) for the free[] table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int findIntegers(int n) {
        // free[k] = count of k-bit strings without two adjacent ones
        int[] free = new int[32];
        free[0] = 1;
        free[1] = 2;
        for (int k = 2; k < 32; k++) free[k] = free[k - 1] + free[k - 2];

        int total = 0, previousBit = 0;
        for (int i = 30; i >= 0; i--) {
            if (((n >> i) & 1) == 1) {
                total += free[i];            // put 0 here, the i lower bits are unrestricted by n
                if (previousBit == 1) return total; // n has "11": the tight branch dies here
                previousBit = 1;
            } else {
                previousBit = 0;
            }
        }
        return total + 1; // n itself has no "11"
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 5", findIntegers(5), 5);
        check("n = 1 (edge)", findIntegers(1), 2);
        check("n = 2", findIntegers(2), 3);
        check("n = 8", findIntegers(8), 6);
        check("n = 1e9", findIntegers(1000000000), 2178309);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
