/**
 * 1048. Longest String Chain — Tabulation (bottom-up)
 *
 * Idea: sort words by length so every possible predecessor (one letter shorter)
 * is processed first. best[word] = 1 + max best[word minus one letter] over the
 * deletions that appear in the map.
 *
 * Time complexity:  O(n log n + n * L^2) — sort, then L deletions of O(L) cost per word
 * Space complexity: O(n * L) — the map from word to chain length
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
public class Tabulation {

    static int longestStrChain(String[] words) {
        String[] sorted = words.clone();
        Arrays.sort(sorted, Comparator.comparingInt(String::length));   // shorter words first
        // best.get(word) = longest chain ending with word
        Map<String, Integer> best = new HashMap<>();
        int answer = 0;
        for (String word : sorted) {
            int chain = 1;
            for (int k = 0; k < word.length(); k++) {
                String shorter = word.substring(0, k) + word.substring(k + 1);
                Integer previous = best.get(shorter);       // already final: it is shorter
                if (previous != null) chain = Math.max(chain, previous + 1);
            }
            best.put(word, chain);
            answer = Math.max(answer, chain);
        }
        return answer;
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
