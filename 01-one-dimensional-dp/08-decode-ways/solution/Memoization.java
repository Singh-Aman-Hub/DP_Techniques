/**
 * Decode Ways (LeetCode 91) — Memoization (top-down DP)
 *
 * Idea: the BruteForce recursion with ways(i) cached.
 *   ways(i) = [s[i-1] is 1..9] * ways(i-1) + [s[i-2..i-1] is 10..26] * ways(i-2)
 *
 * Time complexity:  O(n) — n+1 states, O(1) work each.
 * Space complexity: O(n) — memo plus recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i] = number of decodings of the first i characters; -1 = not computed
    // (0 is a real answer here, e.g. for "0", so 0 cannot mean "unknown")
    private static int[] memo;

    static int numDecodings(String s) {
        memo = new int[s.length() + 1];
        Arrays.fill(memo, -1);
        return ways(s, s.length());
    }

    private static int ways(String s, int i) {
        if (i == 0) return 1;
        if (memo[i] != -1) return memo[i];
        int count = 0;
        if (validSingle(s.charAt(i - 1))) count += ways(s, i - 1);
        if (i >= 2 && validPair(s.charAt(i - 2), s.charAt(i - 1))) count += ways(s, i - 2);
        memo[i] = count;
        return count;
    }

    // a single character decodes to a letter iff it is 1..9
    private static boolean validSingle(char c) {
        return c != '0';
    }

    // two characters decode to a letter iff they form 10..26 (no leading zero)
    private static boolean validPair(char first, char second) {
        int value = (first - '0') * 10 + (second - '0');
        return first != '0' && value >= 10 && value <= 26;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("\"1226\" (walkthrough example)", numDecodings("1226"), 5);
        check("\"226\"", numDecodings("226"), 3);
        check("\"12\"", numDecodings("12"), 2);
        check("\"27\" (27 is not a letter)", numDecodings("27"), 1);
        check("\"10\" (0 only as part of 10)", numDecodings("10"), 1);
        check("\"2101\"", numDecodings("2101"), 1);
        check("\"11106\"", numDecodings("11106"), 2);
        check("\"0\" (edge: lone zero)", numDecodings("0"), 0);
        check("\"06\" (edge: leading zero)", numDecodings("06"), 0);
        check("\"100\" (edge: 00 cannot be split)", numDecodings("100"), 0);
        check("\"30\" (edge: 30 is too big, 0 alone is invalid)", numDecodings("30"), 0);
        check("forty 1s (Fibonacci-sized answer)", numDecodings("1".repeat(40)), 165580141);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
