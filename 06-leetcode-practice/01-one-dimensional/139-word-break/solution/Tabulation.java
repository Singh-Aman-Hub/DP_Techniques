/**
 * 139. Word Break — Tabulation (bottom-up)
 *
 * Idea: dp[i] is true when the first i characters can be split. The last word of such a split
 * is s[j..i) for some j < i, so dp[i] = OR over j of (dp[j] && s[j..i) is a word).
 *
 * Time complexity:  O(n^3) — O(n^2) pairs (j, i), each building/hashing a substring of length O(n).
 * Space complexity: O(n + W*L) — the dp array plus the dictionary hash set.
 *
 * Run: java Tabulation.java
 */
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tabulation {

    static boolean wordBreak(String s, List<String> wordDict) {
        Set<String> dictionary = new HashSet<>(wordDict);
        int n = s.length();
        // dp[i] = can the prefix s[0..i) be split into dictionary words?
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;                                     // empty prefix needs zero words
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && dictionary.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;                                // one valid split is enough
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
