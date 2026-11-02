/**
 * LeetCode 10 Regular Expression Matching — Tabulation (bottom-up)
 *
 * Idea: dp[i][j] is true when the first i characters of s match the first j characters
 * of p. When p[j-1] == '*', the pair p[j-2..j-1] = "x*" is handled as one unit:
 *   - zero copies of x:  dp[i][j-2]                         (look two columns left)
 *   - one more copy:     x matches s[i-1] AND dp[i-1][j]    (look one row up, same column)
 * Otherwise a letter or '.' must match s[i-1] one-for-one: dp[i-1][j-1].
 *
 * Time complexity:  O(n * m) — each of the (n+1)(m+1) cells takes O(1).
 * Space complexity: O(n * m) — the table.
 *
 * Run: java RegexTabulation.java
 */
public class RegexTabulation {

    static boolean isMatch(String s, String p) {
        int n = s.length(), m = p.length();
        // dp[i][j]: does s[0..i-1] match p[0..j-1]?
        boolean[][] dp = new boolean[n + 1][m + 1];
        dp[0][0] = true;
        // Empty text can match patterns like "a*", "a*b*", ".*c*" — every pair used zero times.
        for (int j = 2; j <= m; j++) {
            dp[0][j] = p.charAt(j - 1) == '*' && dp[0][j - 2];
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                char pc = p.charAt(j - 1);
                if (pc == '*') {
                    char starred = p.charAt(j - 2); // the character the star repeats
                    boolean zeroCopies = dp[i][j - 2];
                    boolean oneMoreCopy = (starred == '.' || starred == s.charAt(i - 1)) && dp[i - 1][j];
                    dp[i][j] = zeroCopies || oneMoreCopy;
                } else if (pc == '.' || pc == s.charAt(i - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }
        return dp[n][m];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("s=aab p=c*a*b", isMatch("aab", "c*a*b"), true);
        check("s=aa p=a", isMatch("aa", "a"), false);
        check("s=aa p=a*", isMatch("aa", "a*"), true);
        check("s=ab p=.*", isMatch("ab", ".*"), true);
        check("s=ab p=.*c", isMatch("ab", ".*c"), false);
        check("s=mississippi p=mis*is*p*.", isMatch("mississippi", "mis*is*p*."), false);
        check("edge: s= p=a*", isMatch("", "a*"), true);
        check("edge: s= p=", isMatch("", ""), true);
    }

    private static void check(String label, boolean actual, boolean expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
