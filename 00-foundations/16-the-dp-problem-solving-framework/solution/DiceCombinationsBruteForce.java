/**
 * CSES 1633 Dice Combinations — brute force (plain recursion)
 *
 * Idea: ways(s) = number of ordered sequences of dice throws (each 1..6) that
 * add up to exactly s. The LAST throw is some d in 1..6, and before it the
 * throws added up to s - d, so ways(s) = ways(s-1) + ... + ways(s-6).
 * ways(0) = 1 (the empty sequence), ways(negative) = 0 (overshot the target).
 * Without a cache the same sums are recomputed again and again.
 *
 * Time complexity:  O(6^n) as a simple bound — up to 6 branches per call, depth up to n.
 * Space complexity: O(n) — the recursion stack is at most n calls deep.
 *
 * Run: java DiceCombinationsBruteForce.java
 */
public class DiceCombinationsBruteForce {

    static long ways(int sum) {
        if (sum == 0) return 1;  // exactly reached the target: one valid sequence
        if (sum < 0) return 0;   // overshot: not a valid sequence
        long total = 0;
        for (int face = 1; face <= 6; face++) {
            total += ways(sum - face); // last throw shows `face`
        }
        return total;
    }

    // ---------- self-check (small inputs only: this is exponential) ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", ways(1), 1);
        check("n = 3", ways(3), 4);
        check("n = 4", ways(4), 8);
        check("n = 6", ways(6), 32);
        check("n = 7", ways(7), 63);
        check("n = 10", ways(10), 492);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
