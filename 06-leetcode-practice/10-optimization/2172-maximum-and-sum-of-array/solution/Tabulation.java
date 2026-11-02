/**
 * 2172. Maximum AND Sum of Array — Tabulation (two "seats" per slot → ordinary bitmask DP)
 *
 * Idea: Split every slot into two seats: seat 2t and seat 2t+1 both belong to slot t+1.
 *       Now each seat holds at most one number, so the set of taken seats is a plain bitmask
 *       of 2*S bits. If a mask has c bits set, exactly the first c numbers are seated.
 *       dp[mask] = best AND sum when the seats in `mask` hold nums[0..c-1].
 *       dp[mask] = max over seats b in mask of dp[mask without b] + (nums[c-1] AND slotOf(b)):
 *       the last-placed number nums[c-1] sat in seat b.
 *
 * Time complexity:  O(4^S * 2S) — 2^(2S) masks (S <= 9 → 262144), 2S seats checked per mask.
 * Space complexity: O(4^S) for dp[].
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;
import java.util.Random;

public class Tabulation {

    static int maximumANDSum(int[] nums, int numSlots) {
        int seats = 2 * numSlots;
        // dp[mask] = best sum when exactly the seats in mask are filled by nums[0..bitCount(mask)-1]; -1 = unreachable
        int[] dp = new int[1 << seats];
        Arrays.fill(dp, -1);
        dp[0] = 0;
        int best = 0;
        for (int mask = 1; mask < (1 << seats); mask++) {
            int placed = Integer.bitCount(mask);
            if (placed > nums.length) continue;           // more seats than numbers: impossible
            int lastNumber = nums[placed - 1];
            for (int seat = 0; seat < seats; seat++) {
                if ((mask & (1 << seat)) == 0) continue;
                int before = dp[mask ^ (1 << seat)];       // smaller mask: already computed
                if (before < 0) continue;
                int slotNumber = seat / 2 + 1;
                dp[mask] = Math.max(dp[mask], before + (lastNumber & slotNumber));
            }
            if (placed == nums.length) best = Math.max(best, dp[mask]);
        }
        return best;
    }

    static int bruteForce(int[] numbers, int numSlots) {
        return tryAll(numbers, 0, new int[numSlots + 1], numSlots);
    }

    private static int tryAll(int[] numbers, int index, int[] used, int numSlots) {
        if (index == numbers.length) return 0;
        int best = 0;
        for (int slot = 1; slot <= numSlots; slot++) {
            if (used[slot] == 2) continue;
            used[slot]++;
            best = Math.max(best, (numbers[index] & slot) + tryAll(numbers, index + 1, used, numSlots));
            used[slot]--;
        }
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,2,3,4,5,6], slots=3", maximumANDSum(new int[]{1, 2, 3, 4, 5, 6}, 3), 9);
        check("[1,3,10,4,7,1], slots=9", maximumANDSum(new int[]{1, 3, 10, 4, 7, 1}, 9), 24);
        check("[1], slots=1 (edge)", maximumANDSum(new int[]{1}, 1), 1);
        check("[8,8], slots=1 (edge: nothing matches)", maximumANDSum(new int[]{8, 8}, 1), 0);
        Random random = new Random(2172);
        for (int trial = 0; trial < 300; trial++) {
            int numSlots = 1 + random.nextInt(4);
            int[] numbers = new int[1 + random.nextInt(2 * numSlots)];
            for (int i = 0; i < numbers.length; i++) numbers[i] = 1 + random.nextInt(15);
            int expected = bruteForce(numbers, numSlots);
            if (maximumANDSum(numbers, numSlots) != expected) check("random " + Arrays.toString(numbers), maximumANDSum(numbers, numSlots), expected);
        }
        System.out.println("PASS  two-seat bitmask DP agrees with brute force on 300 random inputs");
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
