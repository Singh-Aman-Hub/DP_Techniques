/**
 * 139. Word Break — Memoization (top-down)
 *
 * Idea: canSplit(start) asks whether s[start..] can be cut into dictionary words. Try every
 * dictionary word that begins at start, and recurse on what remains. There are only n+1
 * distinct suffixes, so caching each answer removes the exponential blow-up.
 *
 * Time complexity:  O(n^3) — n+1 states, up to n end positions each, O(n) per substring/hash.
 * Space complexity: O(n + W*L) — memo + recursion stack, plus the dictionary hash set.
 *
 * Run: java Memoization.java
 */
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Memoization {

    private static String text;
    private static Set<String> dictionary;
    private static Boolean[] memo;   // memo[start] = can text[start..] be split? null = unknown

    static boolean wordBreak(String s, List<String> wordDict) {
        text = s;
        dictionary = new HashSet<>(wordDict);
        memo = new Boolean[s.length() + 1];
        return canSplit(0);
    }

    private static boolean canSplit(int start) {
        if (start == text.length()) return true;          // empty suffix: zero more words needed
        if (memo[start] != null) return memo[start];
        boolean result = false;
        for (int end = start + 1; end <= text.length() && !result; end++) {
            if (dictionary.contains(text.substring(start, end)) && canSplit(end)) {
                result = true;
            }
        }
        memo[start] = result;
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("leetcode", wordBreak("leetcode", List.of("leet", "code")), true);
        check("applepenapple", wordBreak("applepenapple", List.of("apple", "pen")), true);
        check("catsandog", wordBreak("catsandog", List.of("cats", "dog", "sand", "and", "cat")), false);
        check("a with [b] (edge)", wordBreak("a", List.of("b")), false);
        check("aaaaaaa with [aaaa, aaa]", wordBreak("aaaaaaa", List.of("aaaa", "aaa")), true);
    }

    private static void check(String label, boolean actual, boolean expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
