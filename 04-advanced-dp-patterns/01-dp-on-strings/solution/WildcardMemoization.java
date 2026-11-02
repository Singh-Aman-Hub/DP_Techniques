/**
 * LeetCode 44 Wildcard Matching — Memoization (top-down)
 *
 * Idea: f(i, j) answers "does the text suffix s[i..] match the pattern suffix p[j..]?".
 * A normal letter or '?' must consume exactly one text character, so we move to f(i+1, j+1).
 * A '*' may match an empty run (skip the star: f(i, j+1)) or swallow one more character
 * and stay on the same star (f(i+1, j)). Repeating the second choice lets one star
 * absorb any number of characters, one at a time.
 *
 * Time complexity:  O(n * m) — there are (n+1)(m+1) states and each does O(1) work once cached.
 * Space complexity: O(n * m) — the memo table, plus O(n + m) recursion depth.
 *
 * Run: java WildcardMemoization.java
 */
public class WildcardMemoization {

    // memo[i][j]: 0 = not computed, 1 = s[i..] matches p[j..], 2 = it does not
    private static byte[][] memo;
    private static String text, pattern;

    static boolean isMatch(String s, String p) {
        text = s;
        pattern = p;
        memo = new byte[s.length() + 1][p.length() + 1];
        return matches(0, 0);
    }

    private static boolean matches(int i, int j) {
        // Pattern used up: we succeed only if the text is used up too.
        if (j == pattern.length()) return i == text.length();
        // Text used up: the rest of the pattern must be all stars (each matches "").
        if (i == text.length()) {
            for (int k = j; k < pattern.length(); k++) {
                if (pattern.charAt(k) != '*') return false;
            }
            return true;
        }
        if (memo[i][j] != 0) return memo[i][j] == 1;

        boolean result;
        char pc = pattern.charAt(j);
        if (pc == '*') {
            // Choice 1: the star matches nothing -> move past the star.
            // Choice 2: the star eats text[i] -> stay on the star, it may eat more.
            result = matches(i, j + 1) || matches(i + 1, j);
        } else if (pc == '?' || pc == text.charAt(i)) {
            result = matches(i + 1, j + 1);
        } else {
            result = false;
        }
        memo[i][j] = (byte) (result ? 1 : 2);
        return result;
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
