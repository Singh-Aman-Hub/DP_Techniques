/**
 * 943. Find the Shortest Superstring — Tabulation (Held-Karp with parent pointers)
 *
 * Idea: gain[mask][last] = max total overlap of an ordering of exactly the words in mask that
 * ends with 'last'. It extends gain[mask without last][prev] by overlap[prev][last]. Smaller
 * masks come first numerically, so iterate masks upward. parent[][] rebuilds the order.
 *
 * Time complexity:  O(2^n * n^2 + n^2 * L^2) — states x predecessors, plus the overlap table.
 * Space complexity: O(2^n * n) — gain and parent tables.
 *
 * Run: java Tabulation.java
 */
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class Tabulation {

    static String shortestSuperstring(String[] words) {
        int n = words.length;
        int[][] overlap = overlaps(words);
        int[][] gain = new int[1 << n][n];     // gain[mask][last] = best overlap; -1 = impossible
        int[][] parent = new int[1 << n][n];   // parent[mask][last] = word placed just before 'last'
        for (int[] row : gain) Arrays.fill(row, -1);
        for (int[] row : parent) Arrays.fill(row, -1);
        for (int i = 0; i < n; i++) gain[1 << i][i] = 0;   // a single word overlaps nothing

        for (int mask = 1; mask < (1 << n); mask++) {
            for (int last = 0; last < n; last++) {
                if ((mask & (1 << last)) == 0 || mask == (1 << last)) continue;
                int previousMask = mask ^ (1 << last);
                for (int prev = 0; prev < n; prev++) {
                    if ((previousMask & (1 << prev)) == 0 || gain[previousMask][prev] < 0) continue;
                    int candidate = gain[previousMask][prev] + overlap[prev][last];
                    if (candidate > gain[mask][last]) { gain[mask][last] = candidate; parent[mask][last] = prev; }
                }
            }
        }
        int full = (1 << n) - 1, last = 0;
        for (int i = 1; i < n; i++) if (gain[full][i] > gain[full][last]) last = i;

        // walk the parents back from the end, collecting the order in reverse
        Deque<Integer> order = new ArrayDeque<>();
        int mask = full;
        while (last != -1) {
            order.addFirst(last);
            int prev = parent[mask][last];
            mask ^= 1 << last;
            last = prev;
        }
        StringBuilder result = new StringBuilder();
        int previous = -1;
        for (int word : order) {
            result.append(previous == -1 ? words[word] : words[word].substring(overlap[previous][word]));
            previous = word;
        }
        return result.toString();
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
