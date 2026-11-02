/**
 * 486. Predict the Winner — Brute force
 *
 * Idea: classic minimax with an explicit turn flag. The score is P1 - P2;
 * player 1 maximizes it and player 2 minimizes it. Exponential, shown only to
 * compare with the single "negamax" recurrence used by the DP versions.
 *
 * Time complexity:  O(2^n) — two choices per turn, n turns
 * Space complexity: O(n) — recursion depth
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static boolean predictTheWinner(int[] nums) {
        return scoreDifference(nums) >= 0;
    }

    static int scoreDifference(int[] nums) {
        return minimax(nums, 0, nums.length - 1, true);
    }

    // returns final (P1 - P2) from range [i..j] when it is P1's turn if p1Turn
    private static int minimax(int[] nums, int i, int j, boolean p1Turn) {
        if (i > j) return 0;
        if (p1Turn) {
            return Math.max(nums[i] + minimax(nums, i + 1, j, false),
                            nums[j] + minimax(nums, i, j - 1, false));
        }
        return Math.min(-nums[i] + minimax(nums, i + 1, j, true),     // P2's points count against P1
                        -nums[j] + minimax(nums, i, j - 1, true));
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example [1,5,2] lead", scoreDifference(new int[]{1, 5, 2}), -2);
        check("[1,5,233,7] lead", scoreDifference(new int[]{1, 5, 233, 7}), 222);
        check("tie [1,1] lead", scoreDifference(new int[]{1, 1}), 0);
        check("single number (edge) lead", scoreDifference(new int[]{7}), 7);
        check("P1 wins [1,5,2]? (0 = false)", predictTheWinner(new int[]{1, 5, 2}) ? 1 : 0, 0);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
