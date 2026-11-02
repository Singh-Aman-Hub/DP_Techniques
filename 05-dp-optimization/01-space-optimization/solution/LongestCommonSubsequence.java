/**
 * Longest Common Subsequence (LC 1143) — diagonal dependency and a saved "prev" variable
 *
 * Idea: dp[i][j] = dp[i-1][j-1] + 1 if the characters match, otherwise
 * max(dp[i-1][j], dp[i][j-1]). The footprint is up, left AND up-left (diagonal).
 * With one array swept left to right, "up" is the old row[j] and "left" is the new
 * row[j-1], but the diagonal (old row[j-1]) has already been overwritten. So we save
 * it in a variable "prev" just before overwriting row[j-1].
 *
 * This file also shows WHEN YOU CANNOT space-optimize: rebuilding the actual
 * subsequence walks backwards through the whole table, so it needs all rows.
 *
 * Time complexity:  O(n·m) for every version — n·m cells, O(1) work each
 *                   (reconstruction adds O(n + m) steps).
 * Space complexity: O(n·m) for the table and the reconstruction, O(m) for the one-row length.
 *
 * Run: java LongestCommonSubsequence.java
 */
import java.util.Random;

public class LongestCommonSubsequence {

    /** Full table: dp[i][j] = LCS length of a[0..i) and b[0..j). */
    static int[][] buildTable(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1]; // row 0 and column 0 are "empty prefix" = 0
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1] + 1;
                else dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        return dp;
    }

    static int lcsTable(String a, String b) {
        return buildTable(a, b)[a.length()][b.length()];
    }

    /** One row plus one saved diagonal value. */
    static int lcsOneRow(String a, String b) {
        int m = b.length();
        int[] row = new int[m + 1]; // row[j] = dp[i-1][j] before update, dp[i][j] after
        for (int i = 1; i <= a.length(); i++) {
            int prev = 0; // dp[i-1][0], the diagonal for j = 1; column 0 is always 0
            for (int j = 1; j <= m; j++) {
                int above = row[j]; // dp[i-1][j], about to be overwritten
                if (a.charAt(i - 1) == b.charAt(j - 1)) row[j] = prev + 1;
                else row[j] = Math.max(above, row[j - 1]);
                prev = above; // becomes the diagonal dp[i-1][j] for the next column j+1
            }
        }
        return row[m];
    }

    /** Reconstruction needs the whole table: we walk back from (n, m) and look at earlier rows. */
    static String lcsString(String a, String b) {
        int[][] dp = buildTable(a, b);
        StringBuilder reversed = new StringBuilder();
        int i = a.length(), j = b.length();
        while (i > 0 && j > 0) {
            if (a.charAt(i - 1) == b.charAt(j - 1)) { reversed.append(a.charAt(i - 1)); i--; j--; }
            else if (dp[i - 1][j] >= dp[i][j - 1]) i--;
            else j--;
        }
        return reversed.reverse().toString();
    }

    private static boolean isSubsequence(String small, String big) {
        int k = 0;
        for (int t = 0; t < big.length() && k < small.length(); t++)
            if (big.charAt(t) == small.charAt(k)) k++;
        return k == small.length();
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("table   abcde/ace", lcsTable("abcde", "ace"), 3);
        check("one row abcde/ace", lcsOneRow("abcde", "ace"), 3);
        check("one row abc/abc", lcsOneRow("abc", "abc"), 3);
        check("one row abc/def", lcsOneRow("abc", "def"), 0);
        check("one row empty edge", lcsOneRow("", "abc"), 0);
        String rebuilt = lcsString("abcde", "ace");
        check("reconstructed string is \"ace\"", rebuilt.equals("ace") ? 1 : 0, 1);

        Random random = new Random(42);
        boolean allAgree = true;
        for (int trial = 0; trial < 1000 && allAgree; trial++) {
            String a = randomString(random, random.nextInt(9));
            String b = randomString(random, random.nextInt(9));
            int len = lcsTable(a, b);
            String s = lcsString(a, b);
            allAgree = len == lcsOneRow(a, b) && s.length() == len
                    && isSubsequence(s, a) && isSubsequence(s, b);
        }
        check("1000 random string pairs agree", allAgree ? 1 : 0, 1);
    }

    private static String randomString(Random random, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) sb.append((char) ('a' + random.nextInt(3)));
        return sb.toString();
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
