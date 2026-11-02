/**
 * Transition dependencies and computation order — filling by interval length (preview)
 *
 * Idea: in interval DP a state is a range [i, j] of the input, and it depends on
 * SHORTER ranges inside it, such as [i+1, j-1], [i+1, j] and [i, j-1]. So the
 * safe order is "all ranges of length 1, then all of length 2, then 3, ...".
 * The example is the length of the longest palindromic subsequence (a subsequence
 * keeps the order of characters but may skip some):
 *   lps[i][j] = 2 + lps[i+1][j-1]               if s[i] == s[j]
 *             = max(lps[i+1][j], lps[i][j-1])   otherwise
 * A naive "row i from top, j left to right" order reads lps[i+1][...] before it
 * exists and gives a wrong answer.
 *
 * Time complexity:  O(n^2) — n(n+1)/2 ranges, O(1) work each.
 * Space complexity: O(n^2) — one cell per range [i, j].
 *
 * Run: java IntervalOrderPreview.java
 */
public class IntervalOrderPreview {

    /** Correct: by increasing length, so every shorter inner range is already filled. */
    static int lpsByLength(String s) {
        int n = s.length();
        if (n == 0) return 0;
        // lps[i][j] = length of the longest palindromic subsequence of s[i..j]
        int[][] lps = new int[n][n];
        for (int i = 0; i < n; i++) lps[i][i] = 1; // a single character is a palindrome
        for (int length = 2; length <= n; length++) {
            for (int i = 0; i + length - 1 < n; i++) {
                int j = i + length - 1;
                lps[i][j] = transition(s, lps, i, j);
            }
        }
        return lps[0][n - 1];
    }

    /** Also correct: i from the bottom up, j left to right. [i+1][..] rows are finished first. */
    static int lpsBottomRowFirst(String s) {
        int n = s.length();
        if (n == 0) return 0;
        int[][] lps = new int[n][n];
        for (int i = n - 1; i >= 0; i--) {
            lps[i][i] = 1;
            for (int j = i + 1; j < n; j++) {
                lps[i][j] = transition(s, lps, i, j);
            }
        }
        return lps[0][n - 1];
    }

    /** WRONG: i from the top. Row i+1 has not been computed when row i reads it. */
    static int lpsTopRowFirstWrong(String s) {
        int n = s.length();
        if (n == 0) return 0;
        int[][] lps = new int[n][n];
        for (int i = 0; i < n; i++) {
            lps[i][i] = 1;
            for (int j = i + 1; j < n; j++) {
                lps[i][j] = transition(s, lps, i, j); // lps[i+1][...] is still 0 here
            }
        }
        return lps[0][n - 1];
    }

    /** The recurrence itself, shared by all three orders: only the ORDER differs. */
    private static int transition(String s, int[][] lps, int i, int j) {
        if (s.charAt(i) == s.charAt(j)) {
            // the inner range [i+1, j-1] is empty when j == i + 1
            int inner = (i + 1 <= j - 1) ? lps[i + 1][j - 1] : 0;
            return 2 + inner;
        }
        return Math.max(lps[i + 1][j], lps[i][j - 1]);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("\"bbab\" by length", lpsByLength("bbab"), 3);           // "bab" or "bbb"
        check("\"bbbab\" by length", lpsByLength("bbbab"), 4);         // "bbbb"
        check("\"cbbd\" by length", lpsByLength("cbbd"), 2);           // "bb"
        check("\"a\" (edge) by length", lpsByLength("a"), 1);
        check("\"bbab\" bottom row first", lpsBottomRowFirst("bbab"), 3);
        check("\"bbbab\" bottom row first", lpsBottomRowFirst("bbbab"), 4);
        check("\"bbab\" WRONG top row first gives 2", lpsTopRowFirstWrong("bbab"), 2);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
