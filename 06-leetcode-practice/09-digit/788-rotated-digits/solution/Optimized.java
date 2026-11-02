/**
 * 788. Rotated Digits — Optimized digit walk (closed-form digit DP)
 *
 * Idea: Walk n's digits from the left. At each position, try every allowed digit smaller than n's
 *       digit; after that the remaining r positions are free. Among the 7 valid digits
 *       {0,1,2,5,6,8,9}, 3 of them {0,1,8} rotate to themselves. So the free suffixes number 7^r in
 *       total, of which 3^r contain no "changing" digit. If the prefix already changed we add 7^r,
 *       otherwise 7^r - 3^r. Then we continue with n's own digit (staying tight) if it is valid.
 *       This is the memoized digit DP with the non-tight states replaced by a formula.
 *
 * Time complexity:  O(D * 10) — D digits, at most 10 candidate digits each.
 * Space complexity: O(1) besides the digit string.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int rotatedDigits(int n) {
        String digits = Integer.toString(n);
        int length = digits.length();
        int total = 0;
        boolean prefixChanged = false; // n's own prefix so far contains 2/5/6/9
        for (int pos = 0; pos < length; pos++) {
            int limit = digits.charAt(pos) - '0';
            int freePositions = length - pos - 1;
            for (int digit = 0; digit < limit; digit++) {
                if (isInvalid(digit)) continue;
                if (prefixChanged || changes(digit)) total += power(7, freePositions);
                else total += power(7, freePositions) - power(3, freePositions);
            }
            if (isInvalid(limit)) return total;  // n's prefix is invalid: nothing tight survives
            if (changes(limit)) prefixChanged = true;
        }
        if (prefixChanged) total++; // n itself is good
        return total;
    }

    private static boolean isInvalid(int d) { return d == 3 || d == 4 || d == 7; }
    private static boolean changes(int d) { return d == 2 || d == 5 || d == 6 || d == 9; }

    private static int power(int base, int exponent) {
        int result = 1;
        for (int i = 0; i < exponent; i++) result *= base;
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 10", rotatedDigits(10), 4);
        check("n = 1 (edge)", rotatedDigits(1), 0);
        check("n = 2", rotatedDigits(2), 1);
        check("n = 857", rotatedDigits(857), 247);
        check("n = 10000", rotatedDigits(10000), 2320);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
