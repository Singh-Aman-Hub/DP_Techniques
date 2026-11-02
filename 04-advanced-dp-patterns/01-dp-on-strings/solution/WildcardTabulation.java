/**
 * LeetCode 44 Wildcard Matching — Tabulation (bottom-up)
 *
 * Idea: dp[i][j] is true when the first i text characters match the first j pattern
 * characters (prefix form, the same shape as LCS / edit distance). For a '*' at p[j-1]:
 * either it matches the empty string (dp[i][j-1]) or it matches s[i-1] plus whatever it
 * already matched before (dp[i-1][j]). Row 0 is true only while the pattern is all stars.
 *
 * Time complexity:  O(n * m) — every one of the (n+1)(m+1) cells is filled once with O(1) work.
 * Space complexity: O(n * m) — the full table (one row is enough; see the walkthrough).
 *
 * Run: java WildcardTabulation.java
 */
public class WildcardTabulation {

    static boolean isMatch(String s, String p) {
        int n = s.length(), m = p.length();
        // dp[i][j]: does s[0..i-1] (first i chars) match p[0..j-1] (first j chars)?
        boolean[][] dp = new boolean[n + 1][m + 1];
        dp[0][0] = true; // empty text matches empty pattern
        for (int j = 1; j <= m; j++) {
            // empty text matches a pattern prefix only if that prefix is all stars
            dp[0][j] = dp[0][j - 1] && p.charAt(j - 1) == '*';
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                char pc = p.charAt(j - 1);
                if (pc == '*') {
                    // star matches "" (look left) OR star absorbs s[i-1] (look up)
                    dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
                } else if (pc == '?' || pc == s.charAt(i - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // one-for-one match: look diagonally
                }
                // otherwise dp[i][j] stays false
            }
        }
        return dp[n][m];
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
        check("edge: s= p=", isMatch("", ""), true);
    }

    private static void check(String label, boolean actual, boolean expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
