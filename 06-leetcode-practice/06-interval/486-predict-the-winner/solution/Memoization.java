/**
 * 486. Predict the Winner — Memoization (top-down)
 *
 * Idea: diff(i, j) = best lead (own points minus opponent's points) that the
 * player to move can force on the range [i..j]. Taking an end gives that value
 * minus the opponent's best lead on what remains; the first player wins when the lead is >= 0.
 *
 * Time complexity:  O(n^2) — n(n+1)/2 ranges, each solved once with O(1) work
 * Space complexity: O(n^2) — memo table; recursion depth up to n
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[i][j] = best lead for the player to move on [i..j]; null = not computed
    // (Integer, not int: a lead can be 0 or negative, so no numeric sentinel is safe)
    private static Integer[][] memo;
    private static int[] values;

    static boolean predictTheWinner(int[] nums) {
        return scoreDifference(nums) >= 0;
    }

    static int scoreDifference(int[] nums) {
        values = nums;
        memo = new Integer[nums.length][nums.length];
        return lead(0, nums.length - 1);
    }

    private static int lead(int i, int j) {
        if (i == j) return values[i];                         // last item goes to the mover
        if (memo[i][j] != null) return memo[i][j];
        int takeLeft = values[i] - lead(i + 1, j);            // opponent then moves on [i+1..j]
        int takeRight = values[j] - lead(i, j - 1);           // opponent then moves on [i..j-1]
        memo[i][j] = Math.max(takeLeft, takeRight);
        return memo[i][j];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example [1,5,2] lead", scoreDifference(new int[]{1, 5, 2}), -2);
        check("[1,5,233,7] lead", scoreDifference(new int[]{1, 5, 233, 7}), 222);
        check("tie [1,1] lead", scoreDifference(new int[]{1, 1}), 0);
        check("single number (edge) lead", scoreDifference(new int[]{7}), 7);
        check("P1 wins [1,5,2]? (0 = false)", predictTheWinner(new int[]{1, 5, 2}) ? 1 : 0, 0);
        check("P1 wins tie [1,1]? (1 = true)", predictTheWinner(new int[]{1, 1}) ? 1 : 0, 1);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
