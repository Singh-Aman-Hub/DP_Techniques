/**
 * 943. Find the Shortest Superstring — Memoization (forward Held-Karp on overlaps)
 *
 * Idea: total length = sum of word lengths - sum of overlaps between neighbours, so maximise the
 * overlap. extra(mask, last) = the most overlap we can still gain by appending every word not in
 * mask after 'last'. choice[mask][last] records the best next word for reconstruction.
 *
 * Time complexity:  O(2^n * n^2 + n^2 * L^2) — n*2^n states with n options; overlap table.
 * Space complexity: O(2^n * n) — memo and choice tables (recursion depth at most n).
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static int n, fullMask;
    private static int[][] overlap;
    private static int[][] memo;     // memo[mask][last] = max extra overlap; -1 = not computed
    private static int[][] choice;   // choice[mask][last] = best next word

    static String shortestSuperstring(String[] words) {
        n = words.length;
        fullMask = (1 << n) - 1;
        overlap = overlaps(words);
        memo = new int[1 << n][n];
        choice = new int[1 << n][n];
        for (int[] row : memo) Arrays.fill(row, -1);
        int bestFirst = 0, bestGain = -1;
        for (int first = 0; first < n; first++) {
            int gain = extra(1 << first, first);
            if (gain > bestGain) { bestGain = gain; bestFirst = first; }
        }
        // rebuild: follow the recorded choices, appending only the non-overlapping tail of each word
        StringBuilder result = new StringBuilder(words[bestFirst]);
        int mask = 1 << bestFirst, last = bestFirst;
        while (mask != fullMask) {
            int next = choice[mask][last];
            result.append(words[next].substring(overlap[last][next]));
            mask |= 1 << next;
            last = next;
        }
        return result.toString();
    }

    private static int extra(int mask, int last) {
        if (mask == fullMask) return 0;                  // nothing left to append
        if (memo[mask][last] != -1) return memo[mask][last];
        int best = -1;
        for (int next = 0; next < n; next++) {
            if ((mask & (1 << next)) != 0) continue;
            int candidate = overlap[last][next] + extra(mask | (1 << next), next);
            if (candidate > best) { best = candidate; choice[mask][last] = next; }
        }
        memo[mask][last] = best;
        return best;
    }

    // overlap[i][j] = longest suffix of words[i] that is also a prefix of words[j]
    static int[][] overlaps(String[] words) {
        int n = words.length;
        int[][] overlap = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                int maxLen = Math.min(words[i].length(), words[j].length());
                for (int len = maxLen; len > 0; len--) {
                    if (words[i].endsWith(words[j].substring(0, len))) { overlap[i][j] = len; break; }
                }
            }
        }
        return overlap;
    }

    // ---------- self-check ----------
    // Any shortest superstring is accepted, so we check: (1) every word is inside, (2) the length.
    public static void main(String[] args) {
        checkSuper("alex/loves/leetcode", new String[]{"alex", "loves", "leetcode"}, 17);
        checkSuper("five DNA-like words", new String[]{"catg", "ctaagt", "gcta", "ttca", "atgcatc"}, 16);
        checkSuper("ab/bc/cd chain", new String[]{"ab", "bc", "cd"}, 4);
        checkSuper("single word (edge)", new String[]{"abc"}, 3);
        // 12 words abc, bcd, cde, ...: each overlaps the next by 2 characters
        String[] chain = new String[12];
        for (int i = 0; i < 12; i++) chain[i] = "" + (char) ('a' + i) + (char) ('a' + i + 1) + (char) ('a' + i + 2);
        checkSuper("12 overlapping 3-letter words", chain, 14);   // "abcdefghijklmn"
    }

    private static void checkSuper(String label, String[] words, int expectedLength) {
        String result = shortestSuperstring(words);
        boolean containsAll = true;
        for (String w : words) if (!result.contains(w)) containsAll = false;
        boolean ok = containsAll && result.length() == expectedLength;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> \"" + result + "\" (expected length "
                + expectedLength + ", contains all = " + containsAll + ")");
        if (!ok) System.exit(1);
    }
}
