/**
 * LeetCode 131 Palindrome Partitioning — Backtracking with a precomputed isPal table
 *
 * Idea: build the partition from the front. At position 'start', try every end such that
 * s[start..end] is a palindrome, add that piece, recurse from end + 1, then remove the
 * piece (backtrack). Precomputing isPal[i][j] in O(n^2) turns each palindrome check into O(1).
 *
 * Time complexity:  O(n * 2^n) in the worst case ("aaaa...": every one of the 2^(n-1) ways
 *                   to cut is valid, and copying each answer costs O(n)). The output itself
 *                   can be that large, so no algorithm can do better in general.
 * Space complexity: O(n^2) for isPal + O(n) recursion depth (excluding the output).
 *
 * Run: java PalindromePartitioning.java
 */
import java.util.ArrayList;
import java.util.List;

public class PalindromePartitioning {

    static List<List<String>> partition(String s) {
        int n = s.length();
        // isPal[i][j]: s[i..j] is a palindrome (same table as LC 647, filled right to left)
        boolean[][] isPal = new boolean[n][n];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                isPal[i][j] = s.charAt(i) == s.charAt(j) && (j - i < 2 || isPal[i + 1][j - 1]);
            }
        }
        List<List<String>> result = new ArrayList<>();
        backtrack(s, 0, isPal, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(String s, int start, boolean[][] isPal,
                                  List<String> pieces, List<List<String>> result) {
        if (start == s.length()) {           // the whole string is cut into palindromes
            result.add(new ArrayList<>(pieces));
            return;
        }
        for (int end = start; end < s.length(); end++) {
            if (!isPal[start][end]) continue; // the first piece must be a palindrome
            pieces.add(s.substring(start, end + 1));
            backtrack(s, end + 1, isPal, pieces, result);
            pieces.remove(pieces.size() - 1); // undo the choice before trying a longer piece
        }
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("s=aab", partition("aab").toString(), "[[a, a, b], [aa, b]]");
        check("s=abba", partition("abba").toString(), "[[a, b, b, a], [a, bb, a], [abba]]");
        check("s=aaa", partition("aaa").toString(), "[[a, a, a], [a, aa], [aa, a], [aaa]]");
        check("edge: s=a", partition("a").toString(), "[[a]]");
    }

    private static void check(String label, String actual, String expected) {
        String status = actual.equals(expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!actual.equals(expected)) System.exit(1);
    }
}
