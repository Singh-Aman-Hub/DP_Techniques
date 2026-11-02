/**
 * 1312. Minimum Insertion Steps to Make a String Palindrome — Memoization (top-down)
 *
 * Idea: f(i, j) = fewest insertions that make s[i..j] a palindrome. Equal ends
 * mirror each other, so solve the inside. Otherwise give one end an inserted
 * partner (cost 1) and solve the range without that end; take the cheaper.
 *
 * Time complexity:  O(n^2) — about n^2/2 ranges, each solved once in O(1)
 * Space complexity: O(n^2) — memo table; recursion depth up to n
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;
public class Memoization {

    // memo[i][j] = min insertions to make s[i..j] a palindrome; -1 = not computed
    private static int[][] memo;
    private static String text;

    static int minInsertions(String s) {
        text = s;
        int n = s.length();
        memo = new int[n][n];
        for (int[] row : memo) Arrays.fill(row, -1);
        return solve(0, n - 1);
    }

    private static int solve(int i, int j) {
        if (i >= j) return 0;                                 // empty or single char is a palindrome
        if (memo[i][j] != -1) return memo[i][j];
        int result;
        if (text.charAt(i) == text.charAt(j)) {
            result = solve(i + 1, j - 1);                     // ends already mirror each other
        } else {
            result = 1 + Math.min(solve(i + 1, j),           // insert a partner for s[i]
                                  solve(i, j - 1));          // insert a partner for s[j]
        }
        memo[i][j] = result;
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example mbadm", minInsertions("mbadm"), 2);
        check("already a palindrome zzazz", minInsertions("zzazz"), 0);
        check("leetcode", minInsertions("leetcode"), 5);
        check("two different chars ab", minInsertions("ab"), 1);
        check("single char (edge)", minInsertions("a"), 0);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
