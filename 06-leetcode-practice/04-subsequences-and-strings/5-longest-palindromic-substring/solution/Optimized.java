/**
 * 5. Longest Palindromic Substring — Space-optimized DP
 *
 * Idea: expand around each of the 2n-1 centers while the ends match. When the
 * loop stops, left and right are one step outside the palindrome, so its
 * length is right - left - 1.
 *
 * Time complexity:  O(n^2) — 2n-1 centers, each expands at most n/2 times
 * Space complexity: O(1) — a few indices; the answer substring is built once at the end
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static String longestPalindrome(String s) {
        int n = s.length();
        int bestStart = 0, bestLength = 0;
        for (int center = 0; center < 2 * n - 1; center++) {
            int left = center / 2;
            int right = left + center % 2;             // even center = char, odd center = gap
            while (left >= 0 && right < n && s.charAt(left) == s.charAt(right)) {
                left--;
                right++;
            }
            int length = right - left - 1;             // window s[left+1 .. right-1]
            if (length > bestLength) {
                bestStart = left + 1;
                bestLength = length;
            }
        }
        return s.substring(bestStart, bestStart + bestLength);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example cbbd", longestPalindrome("cbbd"), "bb");
        check("racecarxyz (odd length)", longestPalindrome("racecarxyz"), "racecar");
        check("forgeeksskeegfor (even length)", longestPalindrome("forgeeksskeegfor"), "geeksskeeg");
        check("single char (edge)", longestPalindrome("a"), "a");
    }

    private static void check(String label, String actual, String expected) {
        boolean ok = actual.equals(expected);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
