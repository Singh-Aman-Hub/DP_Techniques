/**
 * Edit Distance (LeetCode 72) — Memoization (top-down DP)
 *
 * Idea: Same recursion as BruteForce.java; the state (i, j) has (n+1)(m+1) values and
 *       is reached through many different operation orders, so cache it.
 *
 * Time complexity:  O(n * m) — each state once, O(1) work (three lookups).
 * Space complexity: O(n * m) for the memo + O(n + m) recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i][j] = edit distance between word1[0..i) and word2[0..j); -1 = not computed yet
    private static int[][] memo;

    static int minDistance(String word1, String word2) {
        memo = new int[word1.length() + 1][word2.length() + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return dist(word1, word2, word1.length(), word2.length());
    }

    private static int dist(String word1, String word2, int i, int j) {
        if (i == 0) return j;
        if (j == 0) return i;
        if (memo[i][j] != -1) return memo[i][j];
        if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
            memo[i][j] = dist(word1, word2, i - 1, j - 1);
        } else {
            int replace = dist(word1, word2, i - 1, j - 1);
            int delete = dist(word1, word2, i - 1, j);
            int insert = dist(word1, word2, i, j - 1);
            memo[i][j] = 1 + Math.min(replace, Math.min(delete, insert));
        }
        return memo[i][j];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("horse -> ros", minDistance("horse", "ros"), 3);
        check("intention -> execution", minDistance("intention", "execution"), 5);
        check("\"\" -> abc (edge: insert all)", minDistance("", "abc"), 3);
        check("abc -> \"\" (edge: delete all)", minDistance("abc", ""), 3);
        check("abc -> abc (already equal)", minDistance("abc", "abc"), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
