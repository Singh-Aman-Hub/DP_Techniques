/**
 * 712. Minimum ASCII Delete Sum for Two Strings — Memoization (top-down)
 *
 * Idea: f(i, j) = cheapest ASCII cost to make s1[0..i-1] and s2[0..j-1] equal.
 * Equal last characters are kept for free; otherwise pay for deleting the last
 * character of s1 or of s2, whichever leads to the lower total.
 *
 * Time complexity:  O(n * m) — (n+1)*(m+1) states, O(1) work each (base sums come from prefix arrays)
 * Space complexity: O(n * m) — memo table plus recursion depth up to n + m
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;
public class Memoization {

    // memo[i][j] = min ASCII delete cost for prefixes of length i and j; -1 = not computed
    private static int[][] memo;
    private static String first, second;
    // prefixFirst[i] = ASCII sum of s1[0..i-1]; used for the "other string is empty" base case
    private static int[] prefixFirst, prefixSecond;

    static int minimumDeleteSum(String s1, String s2) {
        first = s1;
        second = s2;
        prefixFirst = prefixSums(s1);
        prefixSecond = prefixSums(s2);
        memo = new int[s1.length() + 1][s2.length() + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return minCost(s1.length(), s2.length());
    }

    private static int[] prefixSums(String s) {
        int[] sums = new int[s.length() + 1];
        for (int k = 0; k < s.length(); k++) sums[k + 1] = sums[k] + s.charAt(k);
        return sums;
    }

    private static int minCost(int i, int j) {
        if (i == 0) return prefixSecond[j];           // delete all of s2[0..j-1]
        if (j == 0) return prefixFirst[i];            // delete all of s1[0..i-1]
        if (memo[i][j] != -1) return memo[i][j];
        char last1 = first.charAt(i - 1), last2 = second.charAt(j - 1);
        int result;
        if (last1 == last2) {
            result = minCost(i - 1, j - 1);
        } else {
            result = Math.min(last1 + minCost(i - 1, j),   // pay for deleting from s1
                              last2 + minCost(i, j - 1));  // pay for deleting from s2
        }
        memo[i][j] = result;
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example sea / eat", minimumDeleteSum("sea", "eat"), 231);
        check("delete / leet", minimumDeleteSum("delete", "leet"), 403);
        check("identical strings", minimumDeleteSum("abc", "abc"), 0);
        check("nothing in common (edge)", minimumDeleteSum("a", "b"), 195);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
