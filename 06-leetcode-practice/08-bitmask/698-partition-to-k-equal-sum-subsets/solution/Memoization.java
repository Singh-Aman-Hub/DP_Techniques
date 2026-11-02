/**
 * 698. Partition to K Equal Sum Subsets — Memoization (bitmask DP)
 *
 * Idea: fill buckets one at a time, each up to target = sum / k. The fill of the current bucket
 * is sum(mask) % target, so the mask of used numbers is the whole state. canFinish(mask) tries
 * every unused number that fits in the current bucket.
 *
 * Time complexity:  O(2^n * n) — each mask is solved once and tries n numbers.
 * Space complexity: O(2^n) — the memo (recursion depth at most n).
 *
 * Run: java Memoization.java
 */
public class Memoization {

    private static int[] values;
    private static int target, fullMask;
    // memo[mask] = can the unused numbers complete all buckets? null = not computed yet
    private static Boolean[] memo;

    static boolean canPartitionKSubsets(int[] nums, int k) {
        int total = 0, largest = 0;
        for (int x : nums) { total += x; largest = Math.max(largest, x); }
        if (total % k != 0) return false;
        target = total / k;
        if (largest > target) return false;           // that number fits in no bucket
        values = nums;
        fullMask = (1 << nums.length) - 1;
        memo = new Boolean[1 << nums.length];
        return canFinish(0, 0);
    }

    // fill = sum(usedMask) % target, passed along so we don't recompute it
    private static boolean canFinish(int usedMask, int fill) {
        if (usedMask == fullMask) return true;        // every bucket closed at exactly target
        if (memo[usedMask] != null) return memo[usedMask];
        boolean result = false;
        for (int i = 0; i < values.length && !result; i++) {
            if ((usedMask & (1 << i)) != 0 || fill + values[i] > target) continue;
            // when the bucket hits target, % wraps the fill to 0: the next bucket starts
            result = canFinish(usedMask | (1 << i), (fill + values[i]) % target);
        }
        memo[usedMask] = result;
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[4,3,2,3,5,2,1], k=4", canPartitionKSubsets(new int[]{4, 3, 2, 3, 5, 2, 1}, 4), true);
        check("[1,2,3,4], k=3 (sum not divisible)", canPartitionKSubsets(new int[]{1, 2, 3, 4}, 3), false);
        check("[1,2,3], k=2", canPartitionKSubsets(new int[]{1, 2, 3}, 2), true);
        check("[10,1,1], k=2 (number above target)", canPartitionKSubsets(new int[]{10, 1, 1}, 2), false);
        check("[5], k=1 (edge)", canPartitionKSubsets(new int[]{5}, 1), true);
        // target 5 but no partner for 4 (would need a 1)
        check("[2,2,2,2,3,4,5], k=4", canPartitionKSubsets(new int[]{2, 2, 2, 2, 3, 4, 5}, 4), false);
        check("16 ones, k=8", canPartitionKSubsets(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, 8), true);
    }

    private static void check(String label, boolean actual, boolean expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
