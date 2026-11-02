/**
 * Shortest Common Supersequence (LeetCode 1092) — Brute force recursion
 *
 * Idea: scs(i, j) = a shortest string that contains both str1[0..i) and str2[0..j) as
 *       subsequences. If the last characters match, write that character once after
 *       scs(i-1, j-1). Otherwise the final character of the answer must be one of the
 *       two last characters: try scs(i-1, j) + str1[i-1] and scs(i, j-1) + str2[j-1]
 *       and keep the shorter (the first one on ties).
 *
 * Time complexity:  O(2^(n+m) * (n+m)) — two branches per mismatch, and each call
 *                   builds strings of length up to n + m.
 * Space complexity: O((n + m)^2) — recursion depth n + m, each frame holding a string.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static String shortestCommonSupersequence(String str1, String str2) {
        return scs(str1, str2, str1.length(), str2.length());
    }

    private static String scs(String str1, String str2, int i, int j) {
        if (i == 0) return str2.substring(0, j);   // only str2's prefix is left to cover
        if (j == 0) return str1.substring(0, i);
        if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
            return scs(str1, str2, i - 1, j - 1) + str1.charAt(i - 1);   // shared character, written once
        }
        String endWithStr1 = scs(str1, str2, i - 1, j) + str1.charAt(i - 1);
        String endWithStr2 = scs(str1, str2, i, j - 1) + str2.charAt(j - 1);
        return (endWithStr1.length() <= endWithStr2.length()) ? endWithStr1 : endWithStr2;
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
