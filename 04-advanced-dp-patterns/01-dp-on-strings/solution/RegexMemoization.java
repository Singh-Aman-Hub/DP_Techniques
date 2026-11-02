/**
 * LeetCode 10 Regular Expression Matching — Memoization (top-down)
 *
 * Idea: f(i, j) answers "does s[i..] match p[j..]?". The key difference from wildcards is
 * that '*' is not a token on its own: it belongs to the character BEFORE it. So at every
 * step we first look ahead: if p[j+1] == '*', the pair "x*" means "zero or more x".
 *   - zero copies: skip the pair            -> f(i, j+2)
 *   - one more copy: s[i] must match x, then stay on the pair -> f(i+1, j)
 * Otherwise p[j] is a single letter or '.', which must match s[i] -> f(i+1, j+1).
 *
 * Time complexity:  O(n * m) — (n+1)(m+1) states, O(1) work each after caching.
 * Space complexity: O(n * m) — memo table plus O(n + m) recursion depth.
 *
 * Run: java RegexMemoization.java
 */
public class RegexMemoization {

    // memo[i][j]: 0 = unknown, 1 = s[i..] matches p[j..], 2 = it does not
    private static byte[][] memo;
    private static String text, pattern;

    static boolean isMatch(String s, String p) {
        text = s;
        pattern = p;
        memo = new byte[s.length() + 1][p.length() + 1];
        return matches(0, 0);
    }

    private static boolean matches(int i, int j) {
        if (j == pattern.length()) return i == text.length();
        if (memo[i][j] != 0) return memo[i][j] == 1;

        // Does the current pattern character match the current text character?
        // (Only possible if there is text left.)
        boolean firstMatches = i < text.length()
                && (pattern.charAt(j) == '.' || pattern.charAt(j) == text.charAt(i));

        boolean result;
        if (j + 1 < pattern.length() && pattern.charAt(j + 1) == '*') {
            // "x*": use zero copies of x, OR use one copy now and keep the pair alive.
            result = matches(i, j + 2) || (firstMatches && matches(i + 1, j));
        } else {
            result = firstMatches && matches(i + 1, j + 1);
        }
        memo[i][j] = (byte) (result ? 1 : 2);
        return result;
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
