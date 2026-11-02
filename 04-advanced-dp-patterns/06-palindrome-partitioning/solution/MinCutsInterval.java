/**
 * LeetCode 132 Palindrome Partitioning II — Interval DP version (the slow O(n^3) way)
 *
 * Idea: treat it like matrix chain multiplication. cuts[i][j] = fewest cuts so that every
 * piece of s[i..j] is a palindrome. If s[i..j] is already a palindrome it needs 0 cuts;
 * otherwise try every split point k: cuts[i][k] + 1 + cuts[k+1][j]. Correct but slow,
 * because it solves every substring even though only suffixes (or prefixes) are needed.
 *
 * Time complexity:  O(n^3) — O(n^2) ranges x O(n) split points.
 * Space complexity: O(n^2) — the cuts and isPal tables.
 *
 * Run: java MinCutsInterval.java
 */
public class MinCutsInterval {

    static long minCut(String s) {
        int n = s.length();
        boolean[][] isPal = new boolean[n][n];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                isPal[i][j] = s.charAt(i) == s.charAt(j) && (j - i < 2 || isPal[i + 1][j - 1]);
            }
        }
        // cuts[i][j]: min cuts to split s[i..j] into palindromes
        int[][] cuts = new int[n][n];
        for (int length = 2; length <= n; length++) { // length-1 strings need 0 cuts
            for (int i = 0; i + length - 1 < n; i++) {
                int j = i + length - 1;
                if (isPal[i][j]) {
                    cuts[i][j] = 0;
                    continue;
                }
                cuts[i][j] = Integer.MAX_VALUE;
                for (int k = i; k < j; k++) { // cut between position k and k + 1
                    cuts[i][j] = Math.min(cuts[i][j], cuts[i][k] + 1 + cuts[k + 1][j]);
                }
            }
        }
        return cuts[0][n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("s=aab", minCut("aab"), 1);
        check("s=abccbc", minCut("abccbc"), 2);
        check("s=banana", minCut("banana"), 1);
        check("s=racecar (already a palindrome)", minCut("racecar"), 0);
        check("edge: s=a", minCut("a"), 0);
        check("s=ab", minCut("ab"), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
