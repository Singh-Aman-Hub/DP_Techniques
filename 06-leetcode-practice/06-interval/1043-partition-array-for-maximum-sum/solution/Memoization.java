/**
 * 1043. Partition Array for Maximum Sum — Memoization (top-down)
 *
 * Idea: blocks are scored independently, so only the prefix length matters.
 * best(i) = max over the last block length len (1..k) of best(i - len) +
 * len * max(last len elements). The block max is kept as a running maximum.
 *
 * Time complexity:  O(n * k) — n states, each tries k lengths in O(1)
 * Space complexity: O(n) — memo array plus recursion depth up to n
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;
public class Memoization {

    // memo[i] = best total for the prefix arr[0..i-1]; -1 = not computed
    private static int[] memo;
    private static int[] values;
    private static int maxBlock;

    static int maxSumAfterPartitioning(int[] arr, int k) {
        values = arr;
        maxBlock = k;
        memo = new int[arr.length + 1];
        Arrays.fill(memo, -1);
        return best(arr.length);
    }

    private static int best(int i) {
        if (i == 0) return 0;                                  // empty prefix
        if (memo[i] != -1) return memo[i];
        int result = 0, blockMax = 0;
        for (int len = 1; len <= Math.min(maxBlock, i); len++) {
            blockMax = Math.max(blockMax, values[i - len]);    // max of arr[i-len .. i-1]
            result = Math.max(result, best(i - len) + len * blockMax);
        }
        memo[i] = result;
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example k=3", maxSumAfterPartitioning(new int[]{1, 15, 7, 9, 2, 5, 10}, 3), 84);
        check("eleven numbers k=4", maxSumAfterPartitioning(new int[]{1, 4, 1, 5, 7, 3, 6, 1, 9, 9, 3}, 4), 83);
        check("one block [5,1] k=2", maxSumAfterPartitioning(new int[]{5, 1}, 2), 10);
        check("single element (edge)", maxSumAfterPartitioning(new int[]{1}, 1), 1);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
