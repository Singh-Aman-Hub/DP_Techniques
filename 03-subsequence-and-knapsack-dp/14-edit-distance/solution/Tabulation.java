/**
 * Edit Distance (LeetCode 72) — Tabulation + listing the actual operations
 *
 * Idea: dp[i][j] = edit distance between word1[0..i) and word2[0..j).
 *       Column 0: dp[i][0] = i (delete everything). Row 0: dp[0][j] = j (insert everything).
 *       Each inner cell reads its diagonal, upper and left neighbours, so rows are filled
 *       top to bottom, left to right. Walking back from dp[n][m] and asking "which
 *       neighbour produced this value?" lists one optimal sequence of operations.
 *
 * Time complexity:  O(n * m) — (n+1)(m+1) cells, O(1) each; the walk back is O(n + m).
 * Space complexity: O(n * m) — the full table.
 *
 * Run: java Tabulation.java
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tabulation {

    // dp[i][j] = fewest operations turning word1[0..i) into word2[0..j)
    static int[][] buildTable(String word1, String word2) {
        int n = word1.length(), m = word2.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) dp[i][0] = i;   // delete all i characters
        for (int j = 0; j <= m; j++) dp[0][j] = j;   // insert all j characters
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],            // replace
                                   Math.min(dp[i - 1][j],                // delete
                                            dp[i][j - 1]));              // insert
                }
            }
        }
        return dp;
    }

    static int minDistance(String word1, String word2) {
        return buildTable(word1, word2)[word1.length()][word2.length()];
    }

    // One optimal list of operations, in left-to-right order.
    static List<String> operations(String word1, String word2) {
        int[][] dp = buildTable(word1, word2);
        List<String> ops = new ArrayList<>();
        int i = word1.length(), j = word2.length();
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && word1.charAt(i - 1) == word2.charAt(j - 1) && dp[i][j] == dp[i - 1][j - 1]) {
                i--; j--;                                                   // match: no operation
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                ops.add("replace '" + word1.charAt(i - 1) + "' with '" + word2.charAt(j - 1) + "'");
                i--; j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                ops.add("delete '" + word1.charAt(i - 1) + "'");
                i--;
            } else {
                ops.add("insert '" + word2.charAt(j - 1) + "'");
                j--;
            }
        }
        Collections.reverse(ops);
        return ops;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("horse -> ros", minDistance("horse", "ros"), 3);
        check("intention -> execution", minDistance("intention", "execution"), 5);
        check("\"\" -> abc (edge: insert all)", minDistance("", "abc"), 3);
        check("abc -> \"\" (edge: delete all)", minDistance("abc", ""), 3);
        check("abc -> abc (already equal)", minDistance("abc", "abc"), 0);

        checkOps("operations horse -> ros", "horse", "ros",
                List.of("replace 'h' with 'r'", "delete 'r'", "delete 'e'"));
        checkOps("operations \"\" -> abc", "", "abc", List.of("insert 'a'", "insert 'b'", "insert 'c'"));
        checkOps("operations abc -> abc", "abc", "abc", List.of());
    }

    private static void checkOps(String label, String word1, String word2, List<String> expected) {
        List<String> actual = operations(word1, word2);
        boolean ok = actual.equals(expected) && actual.size() == minDistance(word1, word2);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual);
        if (!ok) System.exit(1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
