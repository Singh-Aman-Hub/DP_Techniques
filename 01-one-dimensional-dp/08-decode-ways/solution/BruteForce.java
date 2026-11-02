/**
 * Decode Ways (LeetCode 91) — Brute force recursion
 *
 * Idea: ways(i) = number of ways to decode the first i characters s[0..i-1].
 * The LAST letter of a decoding used either the last character alone (valid
 * if it is 1..9) or the last two characters (valid if they form 10..26).
 * The two groups never overlap, so their counts are added.
 *
 * Time complexity:  O(2^n) — up to two recursive calls per character.
 * Space complexity: O(n) — recursion depth.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int numDecodings(String s) {
        return ways(s, s.length());
    }

    private static int ways(String s, int i) {
        if (i == 0) return 1;                                  // empty prefix: one (empty) decoding
        int count = 0;
        if (validSingle(s.charAt(i - 1))) {
            count += ways(s, i - 1);                           // last letter = s[i-1]
        }
        if (i >= 2 && validPair(s.charAt(i - 2), s.charAt(i - 1))) {
            count += ways(s, i - 2);                           // last letter = s[i-2..i-1]
        }
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
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
