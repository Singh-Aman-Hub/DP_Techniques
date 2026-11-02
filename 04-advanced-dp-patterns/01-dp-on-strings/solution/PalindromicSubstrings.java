/**
 * LeetCode 647 Palindromic Substrings and LeetCode 5 Longest Palindromic Substring
 * — the isPal[i][j] interval table
 *
 * Idea: s[i..j] is a palindrome when its two end characters are equal AND the inside
 * s[i+1..j-1] is a palindrome (an empty or 1-char inside counts as a palindrome).
 * Because isPal[i][j] needs isPal[i+1][j-1] (a shorter range), we fill i from right to
 * left so the inner range is always ready. One table answers both problems.
 *
 * Time complexity:  O(n^2) — n(n+1)/2 ranges, O(1) work each.
 * Space complexity: O(n^2) — the boolean table.
 *
 * Run: java PalindromicSubstrings.java
 */
public class PalindromicSubstrings {

    // isPal[i][j] is true when s[i..j] (both ends inclusive) reads the same backwards
    static boolean[][] buildTable(String s) {
        int n = s.length();
        boolean[][] isPal = new boolean[n][n];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                // length 1 or 2: equal ends are enough; longer: the inside must be a palindrome too
                isPal[i][j] = s.charAt(i) == s.charAt(j) && (j - i < 2 || isPal[i + 1][j - 1]);
            }
        }
        return isPal;
    }

    static int countSubstrings(String s) { // LC 647
        boolean[][] isPal = buildTable(s);
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            for (int j = i; j < s.length(); j++) {
                if (isPal[i][j]) count++;
            }
        }
        return count;
    }

    static String longestPalindrome(String s) { // LC 5
        boolean[][] isPal = buildTable(s);
        int bestStart = 0, bestLength = s.isEmpty() ? 0 : 1;
        for (int i = 0; i < s.length(); i++) {
            for (int j = i; j < s.length(); j++) {
                if (isPal[i][j] && j - i + 1 > bestLength) {
                    bestStart = i;
                    bestLength = j - i + 1;
                }
            }
        }
        return s.substring(bestStart, bestStart + bestLength);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        checkInt("count aaa", countSubstrings("aaa"), 6);
        checkInt("count abc", countSubstrings("abc"), 3);
        checkInt("count abba", countSubstrings("abba"), 6);
        checkInt("edge: count a", countSubstrings("a"), 1);
        checkStr("longest babad", longestPalindrome("babad"), "bab"); // "aba" is also valid; we keep the first found
        checkStr("longest cbbd", longestPalindrome("cbbd"), "bb");
        checkStr("edge: longest a", longestPalindrome("a"), "a");
    }

    private static void checkInt(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }

    private static void checkStr(String label, String actual, String expected) {
        String status = actual.equals(expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!actual.equals(expected)) System.exit(1);
    }
}
