/**
 * Decode Ways (LeetCode 91) — Tabulation (bottom-up DP)
 *
 * Idea: ways[i] = number of decodings of the first i characters, filled for
 * i = 1..n from ways[0] = 1. Each cell adds ways[i-1] if the last character is
 * a valid single letter, and ways[i-2] if the last two characters are a valid pair.
 *
 * Time complexity:  O(n) — one pass, two O(1) checks per cell.
 * Space complexity: O(n) — the table has n+1 cells.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int numDecodings(String s) {
        int n = s.length();
        // ways[i] = number of ways to decode the prefix s[0..i-1]; ways[0] = empty prefix
        int[] ways = new int[n + 1];
        ways[0] = 1;
        for (int i = 1; i <= n; i++) {
            if (validSingle(s.charAt(i - 1))) {
                ways[i] += ways[i - 1];
            }
            if (i >= 2 && validPair(s.charAt(i - 2), s.charAt(i - 1))) {
                ways[i] += ways[i - 2];
            }
        }
        return ways[n];
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
        check("forty 1s", numDecodings("1".repeat(40)), 165580141);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
