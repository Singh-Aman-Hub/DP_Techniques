/**
 * LeetCode 132 Palindrome Partitioning II — Front-partition tabulation (O(n^2))
 *
 * Idea: pieces[start] = fewest palindrome pieces covering s[start..n-1]; pieces[n] = 0.
 * pieces[start] reads pieces[end + 1] with end >= start, i.e. positions to its right,
 * so we fill start from n - 1 down to 0. The answer is pieces[0] - 1 cuts.
 *
 * Time complexity:  O(n^2) — the isPal table is O(n^2) and the 1D fill is n x n.
 * Space complexity: O(n^2) for isPal, O(n) for pieces.
 *
 * Run: java MinCutsTabulation.java
 */
public class MinCutsTabulation {

    static long minCut(String s) {
        int n = s.length();
        boolean[][] isPal = new boolean[n][n];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                isPal[i][j] = s.charAt(i) == s.charAt(j) && (j - i < 2 || isPal[i + 1][j - 1]);
            }
        }
        // pieces[start]: min number of palindrome pieces for the suffix s[start..]
        int[] pieces = new int[n + 1];
        pieces[n] = 0;
        for (int start = n - 1; start >= 0; start--) {
            pieces[start] = Integer.MAX_VALUE;
            for (int end = start; end < n; end++) {
                if (isPal[start][end]) {
                    pieces[start] = Math.min(pieces[start], 1 + pieces[end + 1]);
                }
            }
        }
        return pieces[0] - 1; // k pieces need k - 1 cuts
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
