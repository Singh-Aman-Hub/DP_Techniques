/**
 * 1048. Longest String Chain — Memoization (top-down)
 *
 * Idea: f(word) = longest chain ending with word. Its predecessor must be word
 * with one letter deleted, so try all len(word) deletions that exist in the
 * list and take 1 + the best of them. Results are cached per word.
 *
 * Time complexity:  O(n * L^2) — n words, L deletions each, each deletion builds/hashes an O(L) string
 * Space complexity: O(n * L) — the dictionary and memo map; recursion depth at most L
 *
 * Run: java Memoization.java
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
public class Memoization {

    // memo.get(word) = longest chain ending with word (absent = not computed yet)
    private static Map<String, Integer> memo;
    private static Set<String> dictionary;

    static int longestStrChain(String[] words) {
        dictionary = new HashSet<>();
        for (String word : words) dictionary.add(word);
        memo = new HashMap<>();
        int best = 0;
        for (String word : words) best = Math.max(best, chainEndingAt(word));
        return best;
    }

    private static int chainEndingAt(String word) {
        Integer cached = memo.get(word);
        if (cached != null) return cached;
        int best = 1;                                         // the word by itself
        for (int k = 0; k < word.length(); k++) {
            String shorter = word.substring(0, k) + word.substring(k + 1);
            if (dictionary.contains(shorter)) {
                best = Math.max(best, 1 + chainEndingAt(shorter));
            }
        }
        memo.put(word, best);
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example a..bdca", longestStrChain(new String[]{"a", "b", "ba", "bca", "bda", "bdca"}), 4);
        check("xb..pcxbcf", longestStrChain(new String[]{"xbc", "pcxbcf", "xb", "cxbc", "pcxbc"}), 5);
        check("no valid link", longestStrChain(new String[]{"abcd", "dbqca"}), 1);
        check("single word (edge)", longestStrChain(new String[]{"a"}), 1);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
