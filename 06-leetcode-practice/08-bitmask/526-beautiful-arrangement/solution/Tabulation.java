/**
 * 526. Beautiful Arrangement — Tabulation (bitmask DP, push style)
 *
 * Idea: count[mask] = ways to fill the first bitCount(mask) positions using exactly the numbers
 * in mask. From each mask, place a compatible unused number at the next position. Adding a bit
 * makes the mask larger, so looping masks in increasing order computes every state in time.
 *
 * Time complexity:  O(2^n * n) — each mask pushes to at most n larger masks.
 * Space complexity: O(2^n) — one counter per mask.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int countArrangement(int n) {
        // count[mask] = beautiful ways to fill positions 1..bitCount(mask) with the numbers in mask
        int[] count = new int[1 << n];
        count[0] = 1;                                   // nothing placed yet: one (empty) way
        for (int mask = 0; mask < (1 << n); mask++) {
            if (count[mask] == 0) continue;            // unreachable prefix
            int position = Integer.bitCount(mask) + 1;
            for (int number = 1; number <= n; number++) {
                int bit = 1 << (number - 1);
                if ((mask & bit) == 0 && (number % position == 0 || position % number == 0)) {
                    count[mask | bit] += count[mask];
                }
            }
        }
        return count[(1 << n) - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 2", countArrangement(2), 2);
        check("n = 3", countArrangement(3), 3);
        check("n = 1 (edge)", countArrangement(1), 1);
        check("n = 4", countArrangement(4), 8);
        check("n = 15 (largest input)", countArrangement(15), 24679);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
