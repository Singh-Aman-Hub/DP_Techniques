/**
 * Frog Jump (AtCoder EDPC dp_a / dp_b) — Space-optimized DP
 *
 * Idea: Frog 1 reads only the last two cells, so two variables suffice.
 * Frog 2 reads the last k cells, so a ring buffer of k+1 slots suffices:
 * stone i is stored in slot i % (k+1), overwriting a stone that is now more
 * than k positions back and can never be reached in one jump again.
 * (Heights are still needed for every stone, so the input array stays.)
 *
 * Time complexity:  O(n) for Frog 1, O(n*k) for Frog 2.
 * Space complexity: O(1) extra for Frog 1, O(min(n, k)) extra for Frog 2.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static long frog1(int[] h) {
        long twoBack = 0;            // minCost(i-2); unused until i >= 2
        long oneBack = 0;            // minCost(i-1), starts as minCost(0) = 0
        for (int i = 1; i < h.length; i++) {
            long fromOne = oneBack + Math.abs(h[i] - h[i - 1]);
            long fromTwo = (i >= 2) ? twoBack + Math.abs(h[i] - h[i - 2]) : Long.MAX_VALUE;
            long current = Math.min(fromOne, fromTwo);
            twoBack = oneBack;
            oneBack = current;
        }
        return oneBack;
    }

    static long frog2(int[] h, int k) {
        int n = h.length;
        int size = Math.min(n, k + 1);                 // never need more slots than stones
        long[] ring = new long[size];                  // ring[i % size] = minCost(i) for the last 'size' stones
        ring[0] = 0;
        for (int i = 1; i < n; i++) {
            long best = Long.MAX_VALUE;
            for (int j = 1; j <= k && i - j >= 0; j++) {
                best = Math.min(best, ring[(i - j) % size] + Math.abs(h[i] - h[i - j]));
            }
            ring[i % size] = best;                     // safe: slot held stone i-size, which is > k back
        }
        return ring[(n - 1) % size];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        // Frog 1 (jumps of 1 or 2)
        check("Frog 1: [10,30,40,20] (walkthrough example)", frog1(new int[]{10, 30, 40, 20}), 30);
        check("Frog 1: [10,10] (edge: equal heights)", frog1(new int[]{10, 10}), 0);
        check("Frog 1: [30,10,60,10,60,50]", frog1(new int[]{30, 10, 60, 10, 60, 50}), 40);
        check("Frog 1: [7] (edge: already on the last stone)", frog1(new int[]{7}), 0);
        // Frog 2 (jumps of 1..k)
        check("Frog 2: k=3 [10,30,40,50,20]", frog2(new int[]{10, 30, 40, 50, 20}, 3), 30);
        check("Frog 2: k=1 [10,20,10] (edge: only 1-jumps)", frog2(new int[]{10, 20, 10}, 1), 20);
        check("Frog 2: k=100 [10,10] (edge: k larger than n)", frog2(new int[]{10, 10}, 100), 0);
        check("Frog 2: k=4 [40,10,20,70,80,10,20,70,80,60]", frog2(new int[]{40, 10, 20, 70, 80, 10, 20, 70, 80, 60}, 4), 40);
        check("Frog 2 with k=2 equals Frog 1", frog2(new int[]{30, 10, 60, 10, 60, 50}, 2), 40);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
