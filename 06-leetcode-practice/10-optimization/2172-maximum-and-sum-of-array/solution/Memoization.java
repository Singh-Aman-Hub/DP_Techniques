/**
 * 2172. Maximum AND Sum of Array — Memoization (base-3 mask: how full is each slot?)
 *
 * Idea: Place nums[0], nums[1], ... one at a time. What matters about earlier placements is only
 *       how many numbers each slot holds: 0, 1 or 2. Store that as a base-3 number:
 *       digit s of `fill` = count in slot s+1. State = (index, fill).
 *       The index is implied by the total of all digits, but keeping it explicit is clearer.
 *       Choice: put nums[index] into any slot with count < 2, earning nums[index] AND slotNumber.
 *
 * Time complexity:  O(3^S * S) — at most 3^S fill patterns (S <= 9 → 19683), S slot choices each.
 * Space complexity: O(3^S) memo (index is determined by fill) plus O(n) recursion depth.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;
import java.util.Random;

public class Memoization {

    private static int[] nums;
    private static int slots;
    private static int[] powerOf3;
    // memo[fill] = best AND sum for placing the remaining numbers, given slot counts `fill`; -1 = unknown
    private static int[] memo;

    static int maximumANDSum(int[] numbers, int numSlots) {
        nums = numbers;
        slots = numSlots;
        powerOf3 = new int[numSlots + 1];
        powerOf3[0] = 1;
        for (int s = 1; s <= numSlots; s++) powerOf3[s] = powerOf3[s - 1] * 3;
        memo = new int[powerOf3[numSlots]];
        Arrays.fill(memo, -1);
        return solve(0, 0);
    }

    private static int solve(int index, int fill) {
        if (index == nums.length) return 0;          // everyone is placed
        if (memo[fill] != -1) return memo[fill];
        int best = 0;
        for (int s = 0; s < slots; s++) {
            int countInSlot = (fill / powerOf3[s]) % 3;  // base-3 digit s
            if (countInSlot == 2) continue;              // slot is full
            int gain = nums[index] & (s + 1);            // slots are numbered from 1
            best = Math.max(best, gain + solve(index + 1, fill + powerOf3[s]));
        }
        memo[fill] = best;
        return best;
    }

    // Try every assignment (only for tiny inputs) to cross-check.
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
        System.out.println("PASS  base-3 memoization agrees with brute force on 300 random inputs");
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
