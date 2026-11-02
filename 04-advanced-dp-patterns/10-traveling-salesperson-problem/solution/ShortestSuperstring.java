/**
 * Find the Shortest Superstring (LeetCode 943) — TSP in disguise
 *
 * Idea: placing word j right after word i saves overlap[i][j] characters, where
 *       overlap[i][j] = longest suffix of words[i] that is also a prefix of words[j].
 *       Total length = sum of word lengths - sum of overlaps along the chosen order.
 *       So we want the ORDER of words (a path through all "cities") with the largest
 *       total overlap: Held–Karp with max instead of min, and no return trip.
 *       dp[mask][last] = max overlap of an order that uses the words in mask and ends with last.
 *
 * Time complexity:  O(2^n * n^2 + n^2 * L^2) — the Held–Karp table, plus computing overlaps
 *                   by trying every suffix length (L = longest word length).
 * Space complexity: O(2^n * n) — dp and parent tables.
 *
 * Run: java ShortestSuperstring.java
 */
import java.util.Arrays;

public class ShortestSuperstring {

    static String shortestSuperstring(String[] words) {
        int n = words.length;
        int[][] overlap = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (i != j) overlap[i][j] = suffixPrefixOverlap(words[i], words[j]);

        int full = (1 << n) - 1;
        // dp[mask][last] = best total overlap using exactly the words in mask, ending with words[last]
        int[][] dp = new int[1 << n][n];
        int[][] parent = new int[1 << n][n];
        for (int[] row : dp) Arrays.fill(row, -1);        // -1 = state not reachable
        for (int[] row : parent) Arrays.fill(row, -1);
        for (int i = 0; i < n; i++) dp[1 << i][i] = 0;    // any word can come first; no overlap yet

        for (int mask = 1; mask <= full; mask++) {
            for (int last = 0; last < n; last++) {
                if (dp[mask][last] < 0) continue;
                for (int next = 0; next < n; next++) {
                    if ((mask & (1 << next)) != 0) continue;
                    int nextMask = mask | (1 << next);
                    int candidate = dp[mask][last] + overlap[last][next];
                    if (candidate > dp[nextMask][next]) {
                        dp[nextMask][next] = candidate;
                        parent[nextMask][next] = last;
                    }
                }
            }
        }

        int bestLast = 0;
        for (int last = 1; last < n; last++) if (dp[full][last] > dp[full][bestLast]) bestLast = last;

        // Rebuild the order backwards, then glue the words, skipping each overlap.
        int[] order = new int[n];
        int mask = full, word = bestLast;
        for (int pos = n - 1; pos >= 0; pos--) {
            order[pos] = word;
            int previous = parent[mask][word];
            mask ^= (1 << word);
            word = previous;
        }
        StringBuilder result = new StringBuilder(words[order[0]]);
        for (int pos = 1; pos < n; pos++) {
            int skip = overlap[order[pos - 1]][order[pos]];
            result.append(words[order[pos]].substring(skip));
        }
        return result.toString();
    }

    /** Longest k such that the last k characters of a equal the first k characters of b. */
    private static int suffixPrefixOverlap(String a, String b) {
        for (int k = Math.min(a.length(), b.length()); k > 0; k--) {
            if (a.endsWith(b.substring(0, k))) return k;
        }
        return 0;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        // Any shortest answer is accepted, so check the LENGTH and that every word appears.
        checkSuperstring(new String[]{"alex", "loves", "leetcode"}, 17);
        checkSuperstring(new String[]{"catg", "ctaagt", "gcta", "ttca", "atgcatc"}, 16);
        checkSuperstring(new String[]{"abc"}, 3);
        checkSuperstring(new String[]{"ab", "ba"}, 3);
    }

    private static void checkSuperstring(String[] words, int expectedLength) {
        String answer = shortestSuperstring(words);
        boolean ok = answer.length() == expectedLength;
        for (String w : words) if (!answer.contains(w)) ok = false;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + Arrays.toString(words) + " -> \"" + answer
                + "\" length " + answer.length() + " (expected length " + expectedLength + ")");
        if (!ok) System.exit(1);
    }
}
