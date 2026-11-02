/**
 * 583. Delete Operation for Two Strings — Memoization (top-down)
 *
 * Idea: f(i, j) = fewest deletions that make word1[0..i-1] and word2[0..j-1]
 * equal. Equal last characters are kept for free; otherwise delete the last
 * character of one string (cost 1) and take the cheaper option.
 *
 * Time complexity:  O(n * m) — (n+1)*(m+1) states, O(1) work each
 * Space complexity: O(n * m) — memo table plus recursion depth up to n + m
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;
public class Memoization {

    // memo[i][j] = min deletions for prefixes of length i and j; -1 = not computed
    private static int[][] memo;
    private static String first, second;

    static int minDistance(String word1, String word2) {
        first = word1;
        second = word2;
        memo = new int[word1.length() + 1][word2.length() + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return minDeletions(word1.length(), word2.length());
    }

    private static int minDeletions(int i, int j) {
        if (i == 0) return j;                         // delete every remaining char of word2
        if (j == 0) return i;                         // delete every remaining char of word1
        if (memo[i][j] != -1) return memo[i][j];
        int result;
        if (first.charAt(i - 1) == second.charAt(j - 1)) {
            result = minDeletions(i - 1, j - 1);      // matching pair is kept, no cost
        } else {
            result = 1 + Math.min(minDeletions(i - 1, j), minDeletions(i, j - 1));
        }
        memo[i][j] = result;
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example sea / eat", minDistance("sea", "eat"), 2);
        check("leetcode / etco", minDistance("leetcode", "etco"), 4);
        check("identical strings", minDistance("abc", "abc"), 0);
        check("nothing in common (edge)", minDistance("a", "b"), 2);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
