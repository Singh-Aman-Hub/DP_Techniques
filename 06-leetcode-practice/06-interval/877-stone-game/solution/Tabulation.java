/**
 * 877. Stone Game — Tabulation (bottom-up)
 *
 * Idea: diff[i][j] = max(nums[i] - diff[i+1][j], nums[j] - diff[i][j-1]),
 * with diff[i][i] = nums[i]. Shorter ranges are needed first, so i runs from
 * right to left and j from i+1 rightwards; the first player wins when the lead is > 0.
 *
 * Time complexity:  O(n^2) — upper triangle of the table, O(1) per cell
 * Space complexity: O(n^2) — the n x n table
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static boolean stoneGame(int[] nums) {
        return scoreDifference(nums) > 0;
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
        check("example [5,3,4,5] lead", scoreDifference(new int[]{5, 3, 4, 5}), 1);
        check("[3,7,2,3] lead", scoreDifference(new int[]{3, 7, 2, 3}), 5);
        check("[2,8,3,1] lead", scoreDifference(new int[]{2, 8, 3, 1}), 4);
        check("two piles (edge) lead", scoreDifference(new int[]{1, 2}), 1);
        check("Alice wins [5,3,4,5] (1 = true)", stoneGame(new int[]{5, 3, 4, 5}) ? 1 : 0, 1);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
