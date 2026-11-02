/**
 * Partition to K Equal Sum Subsets (LeetCode 698) — dp[mask] over "which numbers are used"
 *
 * Idea: target = total / k. Fill buckets one at a time. If we know WHICH numbers are
 *       used (mask), we know their sum, and so how full the current bucket is:
 *       fill = sum(mask) % target. A number fits if fill + num <= target.
 *       reachable[mask] = the numbers in mask can be placed so that every bucket
 *       is either full or is the single bucket currently being filled.
 *       The answer is reachable[full mask].
 *       A plain backtracking search (bucketSearch) cross-checks the DP on small inputs.
 *
 * Time complexity:  O(2^n * n) — every mask tries every unused number once.
 * Space complexity: O(2^n) — reachable[] and the per-mask sums.
 *
 * Run: java PartitionKEqualSumSubsets.java
 */
public class PartitionKEqualSumSubsets {

    static boolean canPartitionKSubsets(int[] nums, int k) {
        int n = nums.length;
        int total = 0;
        for (int x : nums) total += x;
        if (total % k != 0) return false;
        int target = total / k;
        for (int x : nums) if (x > target) return false;   // a number bigger than a bucket can never fit

        // reachable[mask] = we can place exactly the numbers in mask, buckets filled in order
        boolean[] reachable = new boolean[1 << n];
        // maskSum[mask] = sum of the numbers in mask (built from a smaller mask: drop the lowest bit)
        int[] maskSum = new int[1 << n];
        for (int mask = 1; mask < (1 << n); mask++) {
            int lowest = Integer.numberOfTrailingZeros(mask);
            maskSum[mask] = maskSum[mask & (mask - 1)] + nums[lowest];   // mask & (mask-1) clears the lowest bit
        }

        reachable[0] = true;
        for (int mask = 0; mask < (1 << n); mask++) {
            if (!reachable[mask]) continue;
            int fill = maskSum[mask] % target;            // how full the bucket being filled is
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) != 0) continue;     // already used
                if (fill + nums[j] > target) continue;    // would overflow the current bucket
                reachable[mask | (1 << j)] = true;
            }
        }
        return reachable[(1 << n) - 1];
    }

    // ---------- brute force: put each number into one of k buckets ----------
    static boolean bucketSearch(int[] nums, int k) {
        int total = 0;
        for (int x : nums) total += x;
        if (total % k != 0) return false;
        return place(nums, 0, new int[k], total / k);
    }

    private static boolean place(int[] nums, int i, int[] buckets, int target) {
        if (i == nums.length) {
            for (int b : buckets) if (b != target) return false;
            return true;
        }
        for (int b = 0; b < buckets.length; b++) {
            if (buckets[b] + nums[i] > target) continue;
            buckets[b] += nums[i];
            if (place(nums, i + 1, buckets, target)) return true;
            buckets[b] -= nums[i];
        }
        return false;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] inputs = {{4, 3, 2, 3, 5, 2, 1}, {1, 2, 3, 4}, {2, 2, 2, 2, 3, 4, 5}, {5}, {1, 1, 1, 1}};
        int[] ks = {4, 3, 4, 1, 2};
        boolean[] expected = {true, false, false, true, true};
        String[] labels = {"[4,3,2,3,5,2,1] k=4", "[1,2,3,4] k=3", "[2,2,2,2,3,4,5] k=4",
                           "single [5] k=1", "[1,1,1,1] k=2"};
        for (int t = 0; t < inputs.length; t++) {
            check(labels[t] + " dp",    canPartitionKSubsets(inputs[t], ks[t]), expected[t]);
            check(labels[t] + " brute", bucketSearch(inputs[t], ks[t]), expected[t]);
        }
    }

    private static void check(String label, boolean actual, boolean expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
