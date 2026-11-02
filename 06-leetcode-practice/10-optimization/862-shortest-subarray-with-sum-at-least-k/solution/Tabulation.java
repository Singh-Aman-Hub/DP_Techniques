/**
 * 862. Shortest Subarray with Sum at Least K — Tabulation (prefix sums, O(n^2) "slow DP")
 *
 * Idea: prefix[j] = nums[0] + ... + nums[j-1], so sum(nums[i..j-1]) = prefix[j] - prefix[i].
 *       shortestEndingAt[j] = smallest j - i (i < j) with prefix[j] - prefix[i] >= k.
 *       The answer is the minimum of shortestEndingAt[j] over all j. Scanning i from j-1 down to 0
 *       and stopping at the first hit gives each cell in O(j).
 *
 * Time complexity:  O(n^2) — for each end j, scan up to j starts.
 * Space complexity: O(n) — the prefix-sum array.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int shortestSubarray(int[] nums, int k) {
        int n = nums.length;
        // prefix[j] = sum of the first j numbers; long because sums can exceed int range
        long[] prefix = new long[n + 1];
        for (int j = 0; j < n; j++) prefix[j + 1] = prefix[j] + nums[j];

        int best = Integer.MAX_VALUE;
        for (int j = 1; j <= n; j++) {
            for (int i = j - 1; i >= 0; i--) {     // nearest start first: the first hit is the shortest
                if (prefix[j] - prefix[i] >= k) {
                    best = Math.min(best, j - i);
                    break;
                }
            }
        }
        return best == Integer.MAX_VALUE ? -1 : best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1], k=1", shortestSubarray(new int[]{1}, 1), 1);
        check("[1,2], k=4 (edge: impossible)", shortestSubarray(new int[]{1, 2}, 4), -1);
        check("[2,-1,2], k=3", shortestSubarray(new int[]{2, -1, 2}, 3), 3);
        check("[84,-37,32,40,95], k=167", shortestSubarray(new int[]{84, -37, 32, 40, 95}, 167), 3);
        check("[2,-5,3,1,-1,4], k=5", shortestSubarray(new int[]{2, -5, 3, 1, -1, 4}, 5), 4); // [3,1,-1,4] = 7; no length-3 window reaches 5
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
