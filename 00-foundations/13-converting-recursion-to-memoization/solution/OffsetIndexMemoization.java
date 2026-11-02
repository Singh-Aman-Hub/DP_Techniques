/**
 * Memoizing a state with NEGATIVE values — the offset trick (Target Sum style)
 *
 * Idea: put a + or - in front of every number and count the sign choices whose
 * total equals target. The recursion's state is (i, sum): the index of the next
 * number and the running sum so far. The running sum can be negative, e.g. -5,
 * and Java arrays have no index -5. Two standard fixes:
 *   (a) OFFSET: the sum always lies in [-total, +total], where total = sum of
 *       |nums|. Store state (i, sum) at memo[i][sum + total]; the shifted index
 *       lies in [0, 2 * total].
 *   (b) HASHMAP: use the pair (i, sum) as a key. Simpler, slower, more memory.
 *
 * Time complexity:  O(n * total) — n + 1 values of i times 2 * total + 1 sums, O(1) each.
 * Space complexity: O(n * total) for the cache + O(n) recursion depth.
 *
 * Run: java OffsetIndexMemoization.java
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OffsetIndexMemoization {

    // ---------- the "before": plain recursion ----------

    static long bruteForce(int[] nums, int i, int sum, int target) {
        if (i == nums.length) return (sum == target) ? 1 : 0;
        return bruteForce(nums, i + 1, sum + nums[i], target)    // put '+' in front of nums[i]
             + bruteForce(nums, i + 1, sum - nums[i], target);   // put '-' in front of nums[i]
    }

    // ---------- (a) array cache with an offset ----------

    // memo[i][sum + offset] = number of ways to finish from index i when the running sum is `sum`;
    // -1 means "not computed yet"
    private static long[][] memo;
    private static int offset;

    static long countWithOffset(int[] nums, int target) {
        int total = 0;
        for (int x : nums) total += Math.abs(x);
        offset = total;                                   // smallest possible sum is -total -> index 0
        memo = new long[nums.length + 1][2 * total + 1];  // sums -total..+total -> 2*total + 1 slots
        for (long[] row : memo) Arrays.fill(row, -1);
        return ways(nums, 0, 0, target);
    }

    private static long ways(int[] nums, int i, int sum, int target) {
        if (i == nums.length) return (sum == target) ? 1 : 0;
        int column = sum + offset;                        // shift: the real sum may be negative
        if (memo[i][column] != -1) return memo[i][column];
        long result = ways(nums, i + 1, sum + nums[i], target)
                    + ways(nums, i + 1, sum - nums[i], target);
        memo[i][column] = result;
        return result;
    }

    // ---------- (b) HashMap cache keyed by the pair (i, sum) ----------

    // cache key "i,sum" -> number of ways to finish from that state
    private static Map<String, Long> cache;

    static long countWithHashMap(int[] nums, int target) {
        cache = new HashMap<>();
        return waysMap(nums, 0, 0, target);
    }

    private static long waysMap(int[] nums, int i, int sum, int target) {
        if (i == nums.length) return (sum == target) ? 1 : 0;
        String key = i + "," + sum;                       // negative sums are fine inside a key
        Long saved = cache.get(key);
        if (saved != null) return saved;
        long result = waysMap(nums, i + 1, sum + nums[i], target)
                    + waysMap(nums, i + 1, sum - nums[i], target);
        cache.put(key, result);
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[] fiveOnes = {1, 1, 1, 1, 1};
        // Exactly one '-' among five 1s gives 3: five ways to choose which one.
        check("[1,1,1,1,1], target 3, offset", countWithOffset(fiveOnes, 3), 5);
        check("[1,1,1,1,1], target 3, hashmap", countWithHashMap(fiveOnes, 3), 5);
        check("[1,1,1,1,1], target 3, brute", bruteForce(fiveOnes, 0, 0, 3), 5);

        // +1+2-3 = 0 and -1-2+3 = 0
        check("[1,2,3], target 0, offset", countWithOffset(new int[]{1, 2, 3}, 0), 2);
        // -1-1-1-1-1 = -5: a negative target works because only the running sum is shifted
        check("[1,1,1,1,1], target -5, offset", countWithOffset(fiveOnes, -5), 1);
        check("[1], target 1 (edge: one number)", countWithOffset(new int[]{1}, 1), 1);
        check("[1], target 2 (edge: impossible)", countWithOffset(new int[]{1}, 2), 0);

        // Cross-check all three methods on a slightly larger input and several targets.
        int[] mixed = {2, 1, 3, 1, 2};
        for (int target = -10; target <= 10; target++) {
            long expected = bruteForce(mixed, 0, 0, target);
            check("[2,1,3,1,2] target " + target + " offset == brute", countWithOffset(mixed, target), expected);
            check("[2,1,3,1,2] target " + target + " hashmap == brute", countWithHashMap(mixed, target), expected);
        }
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
