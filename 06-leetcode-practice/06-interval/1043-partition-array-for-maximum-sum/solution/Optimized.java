/**
 * 1043. Partition Array for Maximum Sum — Space-optimized DP
 *
 * Idea: dp[i] reads only dp[i-1] .. dp[i-k], so a ring buffer of k+1 slots is
 * enough: dp[x] lives in ring[x % (k+1)]. The new value is built in a local
 * variable and written last, because its slot still holds dp[i-k-1].
 *
 * Time complexity:  O(n * k) — same loops as tabulation
 * Space complexity: O(k) — the ring buffer
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int maxSumAfterPartitioning(int[] arr, int k) {
        int n = arr.length;
        int size = k + 1;
        // ring[x % size] = dp[x] for the last k+1 prefixes; ring[0] = dp[0] = 0
        int[] ring = new int[size];
        for (int i = 1; i <= n; i++) {
            int bestHere = 0, blockMax = 0;
            for (int len = 1; len <= Math.min(k, i); len++) {
                blockMax = Math.max(blockMax, arr[i - len]);
                bestHere = Math.max(bestHere, ring[(i - len) % size] + len * blockMax);
            }
            ring[i % size] = bestHere;                         // overwrite dp[i-k-1], no longer needed
        }
        return ring[n % size];
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
