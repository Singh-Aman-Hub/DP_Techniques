/**
 * LeetCode 44 Wildcard Matching — Space-optimized tabulation (two rows)
 *
 * Idea: dp[i][j] only reads row i - 1 (up and diagonal) and row i (left). So we keep
 * the previous row 'prev' and build the current row 'curr' left to right.
 *
 * Time complexity:  O(n * m) — same cells as the full table.
 * Space complexity: O(m) — two rows of length m + 1.
 *
 * Run: java WildcardOptimized.java
 */
public class WildcardOptimized {

    static boolean isMatch(String s, String p) {
        int n = s.length(), m = p.length();
        // prev[j]: does the previous text prefix match p's first j chars?
        boolean[] prev = new boolean[m + 1];
        prev[0] = true;
        for (int j = 1; j <= m; j++) prev[j] = prev[j - 1] && p.charAt(j - 1) == '*';
        for (int i = 1; i <= n; i++) {
            boolean[] curr = new boolean[m + 1]; // curr[0] = false: non-empty text vs empty pattern
            for (int j = 1; j <= m; j++) {
                char pc = p.charAt(j - 1);
                if (pc == '*') {
                    curr[j] = curr[j - 1] || prev[j];   // left (star = "") or up (star eats s[i-1])
                } else if (pc == '?' || pc == s.charAt(i - 1)) {
                    curr[j] = prev[j - 1];              // diagonal
                }
            }
            prev = curr;
        }
        return prev[m];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("s=adceb p=*a*b", isMatch("adceb", "*a*b"), true);
        check("s=aa p=a", isMatch("aa", "a"), false);
        check("s=cb p=?a", isMatch("cb", "?a"), false);
        check("s=acdcb p=a*c?b", isMatch("acdcb", "a*c?b"), false);
        check("s=abcd p=a*d", isMatch("abcd", "a*d"), true);
        check("edge: s= p=*", isMatch("", "*"), true);
        check("edge: s= p=?", isMatch("", "?"), false);
    }

    private static void check(String label, boolean actual, boolean expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
