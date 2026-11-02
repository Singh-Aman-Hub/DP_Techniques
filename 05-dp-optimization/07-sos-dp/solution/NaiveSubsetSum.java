/**
 * Sum over Subsets — Naive O(4^n) version (the "slow DP" we start from)
 *
 * Idea: for every mask, scan ALL 2^n masks and keep the ones that are subsets
 *       of it. A candidate "sub" is a subset of "mask" exactly when every bit
 *       of sub is also a bit of mask, i.e. (sub & mask) == sub.
 *
 * Time complexity:  O(4^n) — 2^n outer masks times 2^n candidates each = (2^n)^2 = 4^n.
 * Space complexity: O(2^n) — only the answer array F of size 2^n.
 *
 * Run: java NaiveSubsetSum.java
 */
import java.util.Arrays;

public class NaiveSubsetSum {

    /**
     * values[mask] is the number stored at mask (values.length must be 2^n).
     * Returns subsetSum where subsetSum[mask] = sum of values[sub] over all sub that are subsets of mask.
     */
    static long[] subsetSums(long[] values) {
        int size = values.length;               // size = 2^n
        long[] subsetSum = new long[size];      // subsetSum[mask] = sum over every submask of mask
        for (int mask = 0; mask < size; mask++) {
            for (int sub = 0; sub < size; sub++) {
                // (sub & mask) keeps only the bits sub shares with mask.
                // If nothing was lost, every bit of sub is inside mask -> sub is a subset.
                if ((sub & mask) == sub) {
                    subsetSum[mask] += values[sub];
                }
            }
        }
        return subsetSum;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        // Walkthrough example (n = 3), answers computed by hand in walkthrough.md section 3.
        check("n=3 walkthrough example", subsetSums(new long[]{3, 1, 4, 1, 5, 9, 2, 6}),
                new long[]{3, 4, 7, 9, 8, 18, 14, 31});
        // Edge case n = 0: the only mask is 0, whose only subset is itself.
        check("n=0 single value", subsetSums(new long[]{5}), new long[]{5});
        // n = 1: F[0] = 2, F[1] = 2 + 3.
        check("n=1", subsetSums(new long[]{2, 3}), new long[]{2, 5});
        // All ones: F[mask] = number of subsets = 2^popcount(mask).
        check("n=2 all ones", subsetSums(new long[]{1, 1, 1, 1}), new long[]{1, 2, 2, 4});
    }

    private static void check(String label, long[] actual, long[] expected) {
        boolean ok = Arrays.equals(actual, expected);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + Arrays.toString(actual)
                + " (expected " + Arrays.toString(expected) + ")");
        if (!ok) System.exit(1);
    }
}
