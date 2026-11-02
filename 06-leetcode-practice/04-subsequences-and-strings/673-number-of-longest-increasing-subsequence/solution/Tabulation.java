/**
 * 673. Number of Longest Increasing Subsequence — Tabulation (bottom-up)
 *
 * Idea: the O(n^2) LIS table extended with a count array. For each i scan every
 * j < i with nums[j] < nums[i]; a longer length replaces the count, an equal
 * length adds count[j]. Finally sum counts at indices of maximum length.
 *
 * Time complexity:  O(n^2) — double loop over pairs (j, i)
 * Space complexity: O(n) — the length and count arrays
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int findNumberOfLIS(int[] nums) {
        int n = nums.length;
        int[] length = new int[n];   // length[i] = longest increasing subsequence ending at nums[i]
        int[] count = new int[n];    // count[i]  = how many of those reach length[i]
        int longest = 0;
        for (int i = 0; i < n; i++) {
            length[i] = 1;
            count[i] = 1;
            for (int j = 0; j < i; j++) {
                if (nums[j] >= nums[i]) continue;
                if (length[j] + 1 > length[i]) {
                    length[i] = length[j] + 1;
                    count[i] = count[j];
                } else if (length[j] + 1 == length[i]) {
                    count[i] += count[j];
                }
            }
            longest = Math.max(longest, length[i]);
        }
        int total = 0;
        for (int i = 0; i < n; i++) {
            if (length[i] == longest) total += count[i];
        }
        return total;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example [1,3,5,4,7]", findNumberOfLIS(new int[]{1, 3, 5, 4, 7}), 2);
        check("all equal [2,2,2,2,2]", findNumberOfLIS(new int[]{2, 2, 2, 2, 2}), 5);
        check("[1,2,4,3,5,4,7,2]", findNumberOfLIS(new int[]{1, 2, 4, 3, 5, 4, 7, 2}), 3);
        check("single element (edge)", findNumberOfLIS(new int[]{1}), 1);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
