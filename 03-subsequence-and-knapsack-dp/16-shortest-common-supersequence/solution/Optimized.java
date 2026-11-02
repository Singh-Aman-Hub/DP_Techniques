/**
 * Shortest Common Supersequence — LENGTH only in O(min(n, m)) space
 *
 * Idea: |SCS| = n + m - LCS(str1, str2), and the LCS length needs only one rolling row
 *       (topic 12, Optimized.java). Note: printing the actual string needs the full
 *       table (Tabulation.java) or a divide-and-conquer trick (Hirschberg) beyond this topic.
 *
 * Time complexity:  O(n * m) — the LCS computation.
 * Space complexity: O(min(n, m)) — one row over the shorter string.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int scsLength(String str1, String str2) {
        if (str2.length() > str1.length()) { String swap = str1; str1 = str2; str2 = swap; }
        int m = str2.length();
        int[] row = new int[m + 1];   // row[j] = LCS of str1[0..i) and str2[0..j)
        for (int i = 1; i <= str1.length(); i++) {
            int diagonal = 0;
            for (int j = 1; j <= m; j++) {
                int above = row[j];
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) row[j] = diagonal + 1;
                else row[j] = Math.max(above, row[j - 1]);
                diagonal = above;
            }
        }
        return str1.length() + str2.length() - row[m];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("abac / cab", scsLength("abac", "cab"), 5);
        check("aaaaaaaa / aaaaaaaa", scsLength("aaaaaaaa", "aaaaaaaa"), 8);
        check("abc / def (nothing shared)", scsLength("abc", "def"), 6);
        check("geek / eke", scsLength("geek", "eke"), 5);
        check("a / b (edge: single chars)", scsLength("a", "b"), 2);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
