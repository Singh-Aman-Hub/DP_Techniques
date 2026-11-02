/**
 * Shortest Common Supersequence (LeetCode 1092) — Memoization of the LENGTH
 *
 * Idea: Building strings inside the memo is wasteful. Instead memoize only the length:
 *       len(i, j) = shortest supersequence length of str1[0..i) and str2[0..j).
 *         match:    1 + len(i-1, j-1)
 *         mismatch: 1 + min(len(i-1, j), len(i, j-1))
 *       This equals n + m - LCS(n, m). Tabulation.java then rebuilds the actual string.
 *
 * Time complexity:  O(n * m) — each state once, O(1) work.
 * Space complexity: O(n * m) for the memo + O(n + m) recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i][j] = SCS length of str1[0..i) and str2[0..j); -1 = not computed yet
    private static int[][] memo;

    static int scsLength(String str1, String str2) {
        memo = new int[str1.length() + 1][str2.length() + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return len(str1, str2, str1.length(), str2.length());
    }

    private static int len(String str1, String str2, int i, int j) {
        if (i == 0) return j;
        if (j == 0) return i;
        if (memo[i][j] != -1) return memo[i][j];
        if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
            memo[i][j] = 1 + len(str1, str2, i - 1, j - 1);
        } else {
            memo[i][j] = 1 + Math.min(len(str1, str2, i - 1, j), len(str1, str2, i, j - 1));
        }
        return memo[i][j];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("abac / cab", scsLength("abac", "cab"), 5);
        check("aaaaaaaa / aaaaaaaa", scsLength("aaaaaaaa", "aaaaaaaa"), 8);
        check("abc / def (nothing shared)", scsLength("abc", "def"), 6);
        check("geek / eke", scsLength("geek", "eke"), 5);
        check("a / b (edge: single chars)", scsLength("a", "b"), 2);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
