/**
 * 139. Word Break — Optimized (bounded last-word length)
 *
 * Idea: Same prefix DP as Tabulation, but the last word s[j..i) can be at most maxLen long,
 * so only j in [i - maxLen, i) can possibly succeed. This cuts the inner loop from O(n) to O(L).
 *
 * Time complexity:  O(n * L^2) — n positions, L candidate starts, O(L) per substring.
 * Space complexity: O(n + W*L) — the dp array plus the dictionary hash set.
 *
 * Run: java Optimized.java
 */
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Optimized {

    static boolean wordBreak(String s, List<String> wordDict) {
        Set<String> dictionary = new HashSet<>(wordDict);
        int maxLen = 0;
        for (String w : wordDict) maxLen = Math.max(maxLen, w.length());

        int n = s.length();
        boolean[] dp = new boolean[n + 1];   // dp[i] = can s[0..i) be split?
        dp[0] = true;
        for (int i = 1; i <= n; i++) {
            // a word longer than maxLen cannot exist, so start no further back than i - maxLen
            for (int j = Math.max(0, i - maxLen); j < i; j++) {
                if (dp[j] && dictionary.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[n];
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
