/**
 * 357. Count Numbers with Unique Digits — Tabulation by length
 *
 * Idea: exactly[k] = how many k-digit numbers (no leading zero) have all-unique digits.
 *       exactly[1] = 9 (digits 1..9), and going from k-1 to k digits we append one digit that
 *       differs from the k-1 already used: 10 - (k-1) = 11 - k choices.
 *       So exactly[k] = exactly[k-1] * (11 - k). The answer adds 1 for the number 0 plus all
 *       exactly[k] for k = 1..n.
 *
 * Time complexity:  O(n) — one multiplication per length.
 * Space complexity: O(n) for the table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int countNumbersWithUniqueDigits(int n) {
        if (n == 0) return 1;
        // exactly[k] = count of k-digit numbers whose digits are all different
        int[] exactly = new int[n + 1];
        exactly[1] = 9;
        int answer = 1 + exactly[1]; // the number 0, plus 1..9
        for (int k = 2; k <= n; k++) {
            exactly[k] = exactly[k - 1] * (11 - k);
            answer += exactly[k];
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
