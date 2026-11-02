/**
 * Sum over Subsets — Submask enumeration, O(3^n)
 *
 * Idea: instead of testing all 2^n candidates, walk ONLY the submasks of each
 *       mask with the trick  sub = (sub - 1) & mask, which jumps from one
 *       submask straight to the next smaller one. A mask with c set bits has
 *       exactly 2^c submasks, and summing 2^c over all masks gives 3^n.
 *
 * Time complexity:  O(3^n) — sum over masks of 2^popcount(mask) = (1 + 2)^n by the binomial theorem.
 * Space complexity: O(2^n) — the answer array.
 *
 * Run: java SubmaskEnumeration.java
 */
import java.util.Arrays;
import java.util.Random;

public class SubmaskEnumeration {

    /** subsetSum[mask] = sum of values[sub] over every submask sub of mask. */
    static long[] subsetSums(long[] values) {
        int size = values.length;
        long[] subsetSum = new long[size];
        for (int mask = 0; mask < size; mask++) {
            // Start with the largest submask (mask itself) and go down.
            // (sub - 1) turns the lowest 1-bit of sub into 0 and every bit below it into 1;
            // "& mask" then throws away bits that are not in mask.
            // The result is the next smaller submask, so we never visit a non-submask.
            for (int sub = mask; ; sub = (sub - 1) & mask) {
                subsetSum[mask] += values[sub];
                if (sub == 0) break;   // 0 is the last submask; (0 - 1) & mask would wrap back to mask
            }
        }
        return subsetSum;
    }

    /** Lists the submasks of mask in the order the trick visits them (used in the walkthrough). */
    static int[] submasksOf(int mask) {
        int count = 1 << Integer.bitCount(mask);   // a mask with c set bits has 2^c submasks
        int[] result = new int[count];
        int pos = 0;
        for (int sub = mask; ; sub = (sub - 1) & mask) {
            result[pos++] = sub;
            if (sub == 0) break;
        }
        return result;
    }

    // Private copy of the O(4^n) method so this file runs on its own.
    private static long[] naiveSubsetSums(long[] values) {
        long[] subsetSum = new long[values.length];
        for (int mask = 0; mask < values.length; mask++)
            for (int sub = 0; sub < values.length; sub++)
                if ((sub & mask) == sub) subsetSum[mask] += values[sub];
        return subsetSum;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n=3 walkthrough example", subsetSums(new long[]{3, 1, 4, 1, 5, 9, 2, 6}),
                new long[]{3, 4, 7, 9, 8, 18, 14, 31});
        check("n=0 single value", subsetSums(new long[]{5}), new long[]{5});
        check("n=2 all ones", subsetSums(new long[]{1, 1, 1, 1}), new long[]{1, 2, 2, 4});
        // 13 = 1101 in binary: submasks 1101, 1100, 1001, 1000, 0101, 0100, 0001, 0000.
        check("submasks of 13", toLong(submasksOf(13)), new long[]{13, 12, 9, 8, 5, 4, 1, 0});

        // Randomized cross-check against the naive version (fixed seed => reproducible).
        Random random = new Random(7);
        boolean allMatch = true;
        for (int trial = 0; trial < 200; trial++) {
            int bits = random.nextInt(9);   // n from 0 to 8
            long[] values = new long[1 << bits];
            for (int i = 0; i < values.length; i++) values[i] = random.nextInt(201) - 100;
            if (!Arrays.equals(subsetSums(values), naiveSubsetSums(values))) allMatch = false;
        }
        System.out.println((allMatch ? "PASS" : "FAIL") + "  200 random arrays agree with the O(4^n) version");
        if (!allMatch) System.exit(1);
    }

    private static long[] toLong(int[] a) {
        long[] r = new long[a.length];
        for (int i = 0; i < a.length; i++) r[i] = a[i];
        return r;
    }

    private static void check(String label, long[] actual, long[] expected) {
        boolean ok = Arrays.equals(actual, expected);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + Arrays.toString(actual)
                + " (expected " + Arrays.toString(expected) + ")");
        if (!ok) System.exit(1);
    }
}
