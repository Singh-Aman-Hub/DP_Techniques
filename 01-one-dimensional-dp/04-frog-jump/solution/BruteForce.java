/**
 * Frog Jump (AtCoder EDPC dp_a "Frog 1" and dp_b "Frog 2") — Brute force recursion
 *
 * Idea: minCost(i) = cheapest total cost for the frog to land on stone i.
 * The frog's last jump came from some stone i-j (j = 1..k), costing
 * |h[i] - h[i-j]|. Plain recursion tries every sequence of jumps.
 *
 * Time complexity:  O(k^n) in the worst case (O(2^n) for Frog 1) — every call
 *                   branches into up to k calls and nothing is reused.
 * Space complexity: O(n) — recursion depth up to n.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static long frog1(int[] h) {
        return minCost(h, h.length - 1, 2);   // Frog 1 is Frog 2 with k = 2
    }

    static long frog2(int[] h, int k) {
        return minCost(h, h.length - 1, k);
    }

    private static long minCost(int[] h, int i, int k) {
        if (i == 0) return 0;                 // the frog starts on stone 0 for free
        long best = Long.MAX_VALUE;
        for (int j = 1; j <= k && i - j >= 0; j++) {
            long viaStone = minCost(h, i - j, k) + Math.abs(h[i] - h[i - j]);
            best = Math.min(best, viaStone);
        }
        return best;
    }

    // ---------- self-check (small inputs only: this is exponential) ----------
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
