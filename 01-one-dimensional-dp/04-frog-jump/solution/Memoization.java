/**
 * Frog Jump (AtCoder EDPC dp_a / dp_b) — Memoization (top-down DP)
 *
 * Idea: the recursion of BruteForce,
 *   minCost(i) = min over j=1..k of  minCost(i-j) + |h[i] - h[i-j]|,
 * with every minCost(i) cached after it is first computed.
 *
 * Time complexity:  O(n*k) — n states, each tries up to k previous stones.
 * Space complexity: O(n) — memo array plus recursion stack of depth up to n.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i] = cheapest total cost to land on stone i; -1 = not computed yet
    private static long[] memo;

    static long frog1(int[] h) {
        return frog2(h, 2);
    }

    static long frog2(int[] h, int k) {
        memo = new long[h.length];
        Arrays.fill(memo, -1);                // costs are >= 0, so -1 never collides
        return minCost(h, h.length - 1, k);
    }

    private static long minCost(int[] h, int i, int k) {
        if (i == 0) return 0;
        if (memo[i] != -1) return memo[i];
        long best = Long.MAX_VALUE;
        for (int j = 1; j <= k && i - j >= 0; j++) {
            best = Math.min(best, minCost(h, i - j, k) + Math.abs(h[i] - h[i - j]));
        }
        memo[i] = best;
        return best;
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
        // large input: brute force would never finish, memoization is instant
        int[] zigzag = new int[2000];
        for (int i = 0; i < zigzag.length; i++) zigzag[i] = (i % 2 == 0) ? 0 : 100;
        // jumping 2 stones at a time stays on height 0 and lands on stone 1998 for free;
        // the last stone (1999, height 100) then needs one 1-jump costing 100
        check("Frog 1: 2000 zigzag stones", frog1(zigzag), 100);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
