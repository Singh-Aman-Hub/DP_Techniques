/**
 * LeetCode 312 Burst Balloons — Brute force (simulate every bursting order)
 *
 * Idea: try every balloon as the next one to burst, physically remove it from the list,
 * and recurse on what is left. This explores all n! orders. It is correct, but the
 * "remaining balloons" state is an arbitrary subset, not a contiguous range, so it cannot
 * be cached with a small (i, j) table — the reason we switch to "which balloon is LAST".
 *
 * Time complexity:  O(n!) orders, each step copying a list of size up to n.
 * Space complexity: O(n^2) — recursion depth n, each level holding a list copy.
 *
 * Run: java BruteForce.java
 */
import java.util.ArrayList;
import java.util.List;

public class BruteForce {

    static int maxCoins(int[] nums) {
        List<Integer> balloons = new ArrayList<>();
        for (int value : nums) balloons.add(value);
        return best(balloons);
    }

    private static int best(List<Integer> balloons) {
        if (balloons.isEmpty()) return 0;
        int most = 0;
        for (int idx = 0; idx < balloons.size(); idx++) {
            int left = (idx > 0) ? balloons.get(idx - 1) : 1;                  // missing neighbour counts as 1
            int right = (idx + 1 < balloons.size()) ? balloons.get(idx + 1) : 1;
            int gained = left * balloons.get(idx) * right;
            List<Integer> remaining = new ArrayList<>(balloons);
            remaining.remove(idx); // removes by position (idx is an int)
            most = Math.max(most, gained + best(remaining));
        }
        return most;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("nums=[3,1,5,8]", maxCoins(new int[]{3, 1, 5, 8}), 167);
        check("nums=[1,5]", maxCoins(new int[]{1, 5}), 10);
        check("nums=[2,4,3]", maxCoins(new int[]{2, 4, 3}), 33);
        check("edge: one balloon", maxCoins(new int[]{5}), 5);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
