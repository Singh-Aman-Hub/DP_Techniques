/**
 * 698. Partition to K Equal Sum Subsets — Tabulation (reachable masks)
 *
 * Idea: fill[mask] = sum(mask) % target if the numbers in mask can be placed as complete
 * buckets plus one partial bucket without overflow, else -1. From each reachable mask, add an
 * unused number that fits. Adding a bit increases the mask, so iterate masks upward.
 *
 * Time complexity:  O(2^n * n) — every mask tries every number.
 * Space complexity: O(2^n) — one int per mask.
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;

public class Tabulation {

    static boolean canPartitionKSubsets(int[] nums, int k) {
        int total = 0, largest = 0;
        for (int x : nums) { total += x; largest = Math.max(largest, x); }
        if (total % k != 0) return false;
        int target = total / k;
        if (largest > target) return false;
        int n = nums.length;
        // fill[mask] = amount in the current (partial) bucket, or -1 if mask is unreachable
        int[] fill = new int[1 << n];
        Arrays.fill(fill, -1);
        fill[0] = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            if (fill[mask] == -1) continue;
            for (int i = 0; i < n; i++) {
                int next = mask | (1 << i);
                if (next == mask || fill[mask] + nums[i] > target) continue;
                fill[next] = (fill[mask] + nums[i]) % target;
            }
        }
        return fill[(1 << n) - 1] != -1;
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
