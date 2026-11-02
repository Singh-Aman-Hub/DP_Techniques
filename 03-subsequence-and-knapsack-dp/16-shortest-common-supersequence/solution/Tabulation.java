/**
 * Shortest Common Supersequence (LeetCode 1092) — LCS table + walk back to build the string
 *
 * Idea: Characters of the LCS can be shared by both strings; every other character
 *       must be written separately. So |SCS| = n + m - LCS. To build the string, fill
 *       the LCS table (topic 12) and walk back from (n, m):
 *         - characters equal         -> write it once, move diagonally;
 *         - LCS came from above      -> write str1[i-1], move up;
 *         - otherwise (from the left)-> write str2[j-1], move left;
 *       then write whatever prefix of either string remains, and reverse.
 *
 * Time complexity:  O(n * m) — filling the table; the walk is O(n + m).
 * Space complexity: O(n * m) — the full LCS table is needed for the walk.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static String shortestCommonSupersequence(String str1, String str2) {
        int n = str1.length(), m = str2.length();
        int[][] lcs = new int[n + 1][m + 1];   // lcs[i][j] = LCS length of str1[0..i) and str2[0..j)
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) lcs[i][j] = lcs[i - 1][j - 1] + 1;
                else lcs[i][j] = Math.max(lcs[i - 1][j], lcs[i][j - 1]);
            }
        }

        StringBuilder reversed = new StringBuilder();
        int i = n, j = m;
        while (i > 0 && j > 0) {
            if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                reversed.append(str1.charAt(i - 1));   // shared LCS character: write once
                i--; j--;
            } else if (lcs[i - 1][j] >= lcs[i][j - 1]) {
                reversed.append(str1.charAt(i - 1));   // str1[i-1] is not in the LCS path: write it
                i--;
            } else {
                reversed.append(str2.charAt(j - 1));
                j--;
            }
        }
        while (i > 0) { reversed.append(str1.charAt(i - 1)); i--; }   // leftover prefix of str1
        while (j > 0) { reversed.append(str2.charAt(j - 1)); j--; }   // leftover prefix of str2
        return reversed.reverse().toString();
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        checkScs("abac", "cab", "cabac");
        checkScs("aaaaaaaa", "aaaaaaaa", "aaaaaaaa");
        checkScs("abc", "def", "defabc");
        checkScs("geek", "eke", "gekek");
        checkScs("a", "b", "ba");
    }

    // Checks the exact string AND that it is a valid supersequence of the expected length.
    private static void checkScs(String str1, String str2, String expected) {
        String actual = shortestCommonSupersequence(str1, str2);
        boolean ok = actual.equals(expected) && isSubsequence(str1, actual) && isSubsequence(str2, actual);
        System.out.println((ok ? "PASS" : "FAIL") + "  SCS(" + str1 + ", " + str2 + ") -> \"" + actual
                + "\" (expected \"" + expected + "\")");
        if (!ok) System.exit(1);
    }

    // true if `small` can be obtained from `big` by deleting characters
    private static boolean isSubsequence(String small, String big) {
        int k = 0;
        for (int i = 0; i < big.length() && k < small.length(); i++) {
            if (big.charAt(i) == small.charAt(k)) k++;
        }
        return k == small.length();
    }
}
