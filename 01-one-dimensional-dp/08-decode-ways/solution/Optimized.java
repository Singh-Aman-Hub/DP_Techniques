/**
 * Decode Ways (LeetCode 91) — Space-optimized DP
 *
 * Idea: ways[i] reads only ways[i-1] and ways[i-2], so two variables suffice.
 * A zero count can appear in the middle (e.g. after "00"), and it correctly
 * propagates: once a prefix has no decoding, no longer prefix built on it does.
 *
 * Time complexity:  O(n) — one pass.
 * Space complexity: O(1) — two variables.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int numDecodings(String s) {
        int waysTwoBack = 0;    // ways[i-2]; for i = 1 it would be ways[-1], never used
        int waysOneBack = 1;    // ways[i-1]; starts as ways[0] = 1 (empty prefix)
        for (int i = 1; i <= s.length(); i++) {
            int current = 0;
            if (validSingle(s.charAt(i - 1))) current += waysOneBack;
            if (i >= 2 && validPair(s.charAt(i - 2), s.charAt(i - 1))) current += waysTwoBack;
            waysTwoBack = waysOneBack;
            waysOneBack = current;
        }
        return waysOneBack;
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
