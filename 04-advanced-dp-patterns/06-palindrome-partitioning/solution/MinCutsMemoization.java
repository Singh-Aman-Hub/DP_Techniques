/**
 * LeetCode 132 Palindrome Partitioning II — Front-partition memoization (O(n^2))
 *
 * Idea: only the starting index changes. f(start) = fewest palindrome PIECES that cover the
 * suffix s[start..]. Choose the first piece s[start..end] (it must be a palindrome), then
 * solve the rest: f(start) = min over valid end of 1 + f(end + 1). Cuts = pieces - 1.
 *
 * Time complexity:  O(n^2) — n states, each scanning up to n ends with O(1) isPal checks.
 * Space complexity: O(n^2) — the isPal table (memo itself is O(n)).
 *
 * Run: java MinCutsMemoization.java
 */
import java.util.Arrays;

public class MinCutsMemoization {

    private static boolean[][] isPal;
    private static int[] memo; // memo[start]: min pieces for s[start..]; -1 = unknown

    static long minCut(String s) {
        int n = s.length();
        isPal = new boolean[n][n];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                isPal[i][j] = s.charAt(i) == s.charAt(j) && (j - i < 2 || isPal[i + 1][j - 1]);
            }
        }
        memo = new int[n + 1];
        Arrays.fill(memo, -1);
        return minPieces(s, 0) - 1;
    }

    private static int minPieces(String s, int start) {
        if (start == s.length()) return 0; // nothing left: zero pieces
        if (memo[start] != -1) return memo[start];
        int best = Integer.MAX_VALUE;
        for (int end = start; end < s.length(); end++) {
            if (isPal[start][end]) {
                best = Math.min(best, 1 + minPieces(s, end + 1));
            }
        }
        return memo[start] = best;
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
