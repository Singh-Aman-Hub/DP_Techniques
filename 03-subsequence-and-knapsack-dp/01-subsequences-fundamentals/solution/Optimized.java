/**
 * Count Subsequences With Sum K — Space-optimized tabulation (two rows)
 *
 * Idea: row i of the table only reads row i-1. So we keep just two arrays: "prev" (row i-1)
 * and "curr" (row i), and swap them after each element. (Topic 02 shows how to shrink this
 * to ONE array, and why the loop direction then matters.)
 *
 * Time complexity:  O(n * K) — same cells as the full table, each O(1).
 * Space complexity: O(K) — two arrays of length K + 1.
 *
 * Run: java Optimized.java
 */
import java.util.Arrays;

public class Optimized {

    static long countWithSum(int[] arr, int k) {
        // prev[s] = number of subsequences of the elements processed so far with sum s
        long[] prev = new long[k + 1];
        prev[0] = 1;                                  // no elements yet: only the empty subsequence
        for (int value : arr) {
            long[] curr = new long[k + 1];
            for (int s = 0; s <= k; s++) {
                curr[s] = prev[s] + ((s >= value) ? prev[s - value] : 0);
            }
            prev = curr;                              // row i becomes "previous" for the next element
        }
        return prev[k];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,2,3], K=3", countWithSum(new int[]{1, 2, 3}, 3), 2);
        check("[1,2,1], K=2", countWithSum(new int[]{1, 2, 1}, 2), 2);
        check("[1,1,1,1], K=2", countWithSum(new int[]{1, 1, 1, 1}, 2), 6);
        check("[0,1], K=1", countWithSum(new int[]{0, 1}, 1), 2);
        check("[1,2,3], K=0 (edge: empty subsequence)", countWithSum(new int[]{1, 2, 3}, 0), 1);
        check("[] , K=0 (edge: empty array)", countWithSum(new int[]{}, 0), 1);
        int[] ones = new int[30];
        Arrays.fill(ones, 1);
        check("30 ones, K=15 = C(30,15)", countWithSum(ones, 15), 155117520L);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
