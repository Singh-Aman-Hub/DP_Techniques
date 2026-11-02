/**
 * Sum over Subsets — SOS DP, O(n * 2^n)
 *
 * Idea: treat each mask as a point in an n-dimensional grid whose sides have
 *       length 2 (each bit is one coordinate, 0 or 1). "Sum over all submasks"
 *       is then an n-dimensional prefix sum, and a multi-dimensional prefix sum
 *       can be built one dimension at a time: for bit i, every mask that has
 *       bit i set adds in the value of the same mask with bit i cleared.
 *
 * Time complexity:  O(n * 2^n) — n passes (one per bit), each touching 2^n masks with O(1) work.
 * Space complexity: O(2^n) — one array, updated in place.
 *
 * Run: java SosDp.java
 */
import java.util.Arrays;
import java.util.Random;

public class SosDp {

    /** Returns F where F[mask] = sum of values[sub] over every submask sub of mask. */
    static long[] subsetSums(long[] values) {
        int size = values.length;
        int bits = Integer.numberOfTrailingZeros(size);   // size = 2^bits, so this recovers n
        // F[mask] = after the passes for bits 0..i-1: sum of values[sub] where sub is a submask
        //           of mask that may differ from mask ONLY in bits 0..i-1.
        long[] F = Arrays.copyOf(values, size);
        for (int bit = 0; bit < bits; bit++) {
            for (int mask = 0; mask < size; mask++) {
                // (mask & (1 << bit)) != 0 asks "is this bit switched on in mask?"
                if ((mask & (1 << bit)) != 0) {
                    // mask ^ (1 << bit) is mask with this bit switched off.
                    // Its F value does not have this bit, so it is never changed in this pass:
                    // updating in place is safe.
                    F[mask] += F[mask ^ (1 << bit)];
                }
            }
        }
        return F;
    }

    /** Same idea, mirrored: G[mask] = sum of values[sup] over every SUPERmask sup of mask. */
    static long[] supersetSums(long[] values) {
        int size = values.length;
        int bits = Integer.numberOfTrailingZeros(size);
        long[] G = Arrays.copyOf(values, size);
        for (int bit = 0; bit < bits; bit++) {
            for (int mask = 0; mask < size; mask++) {
                // a mask WITHOUT this bit collects from the mask WITH it (mask | bit)
                if ((mask & (1 << bit)) == 0) {
                    G[mask] += G[mask | (1 << bit)];
                }
            }
        }
        return G;
    }

    // Private copy of the O(4^n) method, used as the trusted reference.
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
        check("n=1", subsetSums(new long[]{2, 3}), new long[]{2, 5});
        check("n=2 all ones", subsetSums(new long[]{1, 1, 1, 1}), new long[]{1, 2, 2, 4});
        // Superset sums of the walkthrough example (exercise 1 in walkthrough.md).
        check("superset sums n=3", supersetSums(new long[]{3, 1, 4, 1, 5, 9, 2, 6}),
                new long[]{31, 17, 13, 7, 22, 15, 8, 6});

        Random random = new Random(42);
        boolean allMatch = true;
        for (int trial = 0; trial < 300; trial++) {
            int bits = random.nextInt(10);   // n from 0 to 9
            long[] values = new long[1 << bits];
            for (int i = 0; i < values.length; i++) values[i] = random.nextInt(2001) - 1000;
            if (!Arrays.equals(subsetSums(values), naiveSubsetSums(values))) allMatch = false;
        }
        System.out.println((allMatch ? "PASS" : "FAIL") + "  300 random arrays agree with the O(4^n) version");
        if (!allMatch) System.exit(1);
    }

    private static void check(String label, long[] actual, long[] expected) {
        boolean ok = Arrays.equals(actual, expected);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + Arrays.toString(actual)
                + " (expected " + Arrays.toString(expected) + ")");
        if (!ok) System.exit(1);
    }
}
