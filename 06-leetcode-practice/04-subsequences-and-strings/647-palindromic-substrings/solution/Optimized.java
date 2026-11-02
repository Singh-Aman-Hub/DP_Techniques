/**
 * 647. Palindromic Substrings — Space-optimized DP
 *
 * Idea: expand around center. Every palindrome has a center: one of the n
 * characters or one of the n-1 gaps. Grow outward from each center while the
 * ends match; every successful step is one more palindromic substring.
 *
 * Time complexity:  O(n^2) — 2n-1 centers, each expands at most n/2 times
 * Space complexity: O(1) — only a few indices, no table
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int countSubstrings(String s) {
        int n = s.length();
        int count = 0;
        for (int center = 0; center < 2 * n - 1; center++) {
            int left = center / 2;
            int right = left + center % 2;             // even center = single char, odd = gap
            while (left >= 0 && right < n && s.charAt(left) == s.charAt(right)) {
                count++;                               // s[left..right] is a palindrome
                left--;
                right++;
            }
        }
        return count;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example abba", countSubstrings("abba"), 6);
        check("abc (only singles)", countSubstrings("abc"), 3);
        check("aaa (overlapping)", countSubstrings("aaa"), 6);
        check("single char (edge)", countSubstrings("a"), 1);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
