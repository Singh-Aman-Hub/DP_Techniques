/**
 * 474. Ones and Zeroes — Memoization (top-down)
 *
 * Idea: 0/1 knapsack where each string costs (zeros, ones) and is worth 1. best(i, z, o) =
 * most strings choosable from strs[i..] with z zeros and o ones left: skip string i, or take it
 * if it fits. Cache every (i, z, o).
 *
 * Time complexity:  O(S + L * m * n) — S characters counted once; L*(m+1)*(n+1) states, O(1) each.
 * Space complexity: O(L * m * n) — the memo cube plus recursion depth L.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    private static int[] zeros, ones;     // zeros[i] / ones[i] = digit counts of string i
    private static Integer[][][] memo;    // memo[i][z][o] = best count from strings i.. with budget (z, o)

    static int findMaxForm(String[] strs, int m, int n) {
        int count = strs.length;
        zeros = new int[count];
        ones = new int[count];
        for (int i = 0; i < count; i++) {
            for (char ch : strs[i].toCharArray()) {
                if (ch == '0') zeros[i]++;
                else ones[i]++;
            }
        }
        memo = new Integer[count][m + 1][n + 1];
        return best(0, m, n);
    }

    private static int best(int i, int zerosLeft, int onesLeft) {
        if (i == zeros.length) return 0;                   // no strings left to consider
        if (memo[i][zerosLeft][onesLeft] != null) return memo[i][zerosLeft][onesLeft];
        int skip = best(i + 1, zerosLeft, onesLeft);
        int take = 0;
        if (zeros[i] <= zerosLeft && ones[i] <= onesLeft) {
            take = 1 + best(i + 1, zerosLeft - zeros[i], onesLeft - ones[i]);
        }
        memo[i][zerosLeft][onesLeft] = Math.max(skip, take);
        return memo[i][zerosLeft][onesLeft];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example 1", findMaxForm(new String[]{"10", "0001", "111001", "1", "0"}, 5, 3), 4);
        check("example 2", findMaxForm(new String[]{"10", "0", "1"}, 1, 1), 2);
        check("nothing fits (edge)", findMaxForm(new String[]{"111"}, 1, 1), 0);
        check("all fit", findMaxForm(new String[]{"0", "1", "01"}, 5, 5), 3);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
