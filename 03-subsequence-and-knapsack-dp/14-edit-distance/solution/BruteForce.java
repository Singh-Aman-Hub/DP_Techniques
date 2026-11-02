/**
 * Edit Distance (LeetCode 72) — Brute force recursion
 *
 * Idea: dist(i, j) = fewest operations to turn the prefix word1[0..i) into word2[0..j).
 *       Compare the LAST characters. If equal, they already line up: dist(i-1, j-1).
 *       Otherwise try all three operations on the end of word1 and keep the cheapest:
 *         replace word1[i-1] by word2[j-1] -> 1 + dist(i-1, j-1)
 *         delete  word1[i-1]               -> 1 + dist(i-1, j)
 *         insert  word2[j-1] at the end    -> 1 + dist(i, j-1)
 *
 * Time complexity:  O(3^(n+m)) — up to three branches per call.
 * Space complexity: O(n + m) — recursion depth.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int minDistance(String word1, String word2) {
        return dist(word1, word2, word1.length(), word2.length());
    }

    private static int dist(String word1, String word2, int i, int j) {
        if (i == 0) return j;   // build word2[0..j) from nothing: j insertions
        if (j == 0) return i;   // turn word1[0..i) into nothing: i deletions
        if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
            return dist(word1, word2, i - 1, j - 1);       // free: last characters already match
        }
        int replace = dist(word1, word2, i - 1, j - 1);
        int delete = dist(word1, word2, i - 1, j);
        int insert = dist(word1, word2, i, j - 1);
        return 1 + Math.min(replace, Math.min(delete, insert));
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("horse -> ros", minDistance("horse", "ros"), 3);
        check("intention -> execution", minDistance("intention", "execution"), 5);
        check("\"\" -> abc (edge: insert all)", minDistance("", "abc"), 3);
        check("abc -> \"\" (edge: delete all)", minDistance("abc", ""), 3);
        check("abc -> abc (already equal)", minDistance("abc", "abc"), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
