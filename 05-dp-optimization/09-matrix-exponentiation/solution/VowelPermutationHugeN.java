/**
 * Count Vowels Permutation (LC 1220) for huge n — O(n) rolling DP vs 5x5 matrix power
 *
 * Idea: count[c] = number of valid strings of the current length ending in vowel c.
 * The rules say which vowel may follow which, and they never change with the length,
 * so one step is a fixed linear map: next = T · count, with T[to][from] = 1 when
 * "from" may be followed by "to". Length n means applying T (n - 1) times to the
 * all-ones vector of length-1 strings, i.e. computing T^(n-1).
 *
 * Time complexity:  O(5·5·n) = O(n) for the rolling DP;
 *                   O(5³·log n) = O(log n) for the matrix version.
 * Space complexity: O(1) for both (5 counters / a few 5x5 matrices).
 *
 * Run: java VowelPermutationHugeN.java
 */
import java.util.Random;

public class VowelPermutationHugeN {

    private static final long MOD = 1_000_000_007L;

    // Vowel order: a=0, e=1, i=2, o=3, u=4.
    // CAN_FOLLOW[from][to] = 1 if vowel "to" may come right after vowel "from":
    //   a -> e;  e -> a, i;  i -> a, e, o, u (anything but i);  o -> i, u;  u -> a.
    private static final int[][] CAN_FOLLOW = {
        {0, 1, 0, 0, 0},
        {1, 0, 1, 0, 0},
        {1, 1, 0, 1, 1},
        {0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0}
    };

    /** O(n) rolling DP: two vectors of 5 counters. */
    static long countSlow(long n) {
        long[] count = {1, 1, 1, 1, 1}; // length 1: one string per vowel
        for (long length = 2; length <= n; length++) {
            long[] next = new long[5];
            for (int from = 0; from < 5; from++)
                for (int to = 0; to < 5; to++)
                    if (CAN_FOLLOW[from][to] == 1) next[to] = (next[to] + count[from]) % MOD;
            count = next;
        }
        long total = 0;
        for (long c : count) total = (total + c) % MOD;
        return total;
    }

    static long[][] multiply(long[][] a, long[][] b) {
        int size = a.length;
        long[][] product = new long[size][size];
        for (int i = 0; i < size; i++)
            for (int k = 0; k < size; k++) {
                if (a[i][k] == 0) continue;
                for (int j = 0; j < size; j++)
                    product[i][j] = (product[i][j] + a[i][k] * b[k][j]) % MOD;
            }
        return product;
    }

    static long[][] power(long[][] base, long exponent) {
        int size = base.length;
        long[][] result = new long[size][size];
        for (int i = 0; i < size; i++) result[i][i] = 1;
        long[][] square = base;
        while (exponent > 0) {
            if ((exponent & 1) == 1) result = multiply(result, square);
            square = multiply(square, square);
            exponent >>= 1;
        }
        return result;
    }

    /** O(log n): total = sum of all entries of T^(n-1) (starting vector is all ones). */
    static long countFast(long n) {
        long[][] transition = new long[5][5];
        // transition[to][from] = CAN_FOLLOW[from][to], so that next = transition · count
        for (int from = 0; from < 5; from++)
            for (int to = 0; to < 5; to++) transition[to][from] = CAN_FOLLOW[from][to];
        long[][] steps = power(transition, n - 1);
        long total = 0;
        for (long[] row : steps)
            for (long value : row) total = (total + value) % MOD;
        return total;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n=1 edge (5 single vowels)", countFast(1), 5);
        check("n=2 (10 allowed pairs)", countFast(2), 10);
        check("n=5 slow", countSlow(5), 68);
        check("n=5 fast", countFast(5), 68);
        check("n=20000 fast == slow", countFast(20000), countSlow(20000));

        Random random = new Random(1220);
        boolean allAgree = true;
        for (int trial = 0; trial < 200 && allAgree; trial++) {
            long n = 1 + random.nextInt(3000);
            allAgree = countFast(n) == countSlow(n);
        }
        check("200 random n agree", allAgree ? 1 : 0, 1);

        // n = 10^18 is far beyond the O(n) loop; the matrix version needs ~60 squarings.
        long huge = countFast(1_000_000_000_000_000_000L);
        check("n=10^18 runs and returns a residue in [0, MOD)", (huge >= 0 && huge < MOD) ? 1 : 0, 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
