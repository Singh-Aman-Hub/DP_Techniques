/**
 * 486. Predict the Winner — Tabulation (bottom-up)
 *
 * Idea: diff[i][j] = max(nums[i] - diff[i+1][j], nums[j] - diff[i][j-1]),
 * with diff[i][i] = nums[i]. Shorter ranges are needed first, so i runs from
 * right to left and j from i+1 rightwards; the first player wins when the lead is >= 0.
 *
 * Time complexity:  O(n^2) — upper triangle of the table, O(1) per cell
 * Space complexity: O(n^2) — the n x n table
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static boolean predictTheWinner(int[] nums) {
        return scoreDifference(nums) >= 0;
    }

    static int scoreDifference(int[] nums) {
        int n = nums.length;
        // diff[i][j] = best (mover - opponent) lead on nums[i..j]
        int[][] diff = new int[n][n];
        for (int i = n - 1; i >= 0; i--) {
            diff[i][i] = nums[i];
            for (int j = i + 1; j < n; j++) {
                diff[i][j] = Math.max(nums[i] - diff[i + 1][j], nums[j] - diff[i][j - 1]);
            }
        }
        return diff[0][n - 1];
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
