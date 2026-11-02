/**
 * 357. Count Numbers with Unique Digits — Space-optimized tabulation
 *
 * Idea: exactly[k] only needs exactly[k-1], so keep one running product instead of an array.
 *
 * Time complexity:  O(n) — one multiplication per length.
 * Space complexity: O(1) — two integers.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int countNumbersWithUniqueDigits(int n) {
        int answer = 1;          // the number 0
        int exactlyK = 9;        // count of 1-digit unique numbers (1..9)
        for (int k = 1; k <= n; k++) {
            if (k > 1) exactlyK *= (11 - k); // the k-th digit avoids the k-1 digits already used
            answer += exactlyK;
        }
        return answer;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 0 (edge)", countNumbersWithUniqueDigits(0), 1);
        check("n = 1", countNumbersWithUniqueDigits(1), 10);
        check("n = 2", countNumbersWithUniqueDigits(2), 91);
        check("n = 3", countNumbersWithUniqueDigits(3), 739);
        check("n = 8", countNumbersWithUniqueDigits(8), 2345851);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
