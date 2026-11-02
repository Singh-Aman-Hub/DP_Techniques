/**
 * 600. Non-negative Integers without Consecutive Ones — Memoization (binary digit DP)
 *
 * Idea: Digit DP in base 2. Walk n's bits from the most significant one.
 *       State = (position, previousBit, tight). We may place a 1 only if the previous bit is 0
 *       (no "11"), and, while tight, only if n has a 1 at this position.
 *       Leading zeros are fine here: zeros never create "11", and 0 itself is counted.
 *
 * Time complexity:  O(B * 2 * 2 * 2) — B <= 31 bits, 4 states per position, 2 choices each.
 * Space complexity: O(B * 4) memo plus O(B) recursion depth.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static int[] bits;     // n's bits, most significant first
    // memo[pos][prevBit][tight] = valid ways to fill bits pos..end; -1 = unknown
    private static int[][][] memo;

    static int findIntegers(int n) {
        String binary = Integer.toBinaryString(n);
        bits = new int[binary.length()];
        for (int i = 0; i < binary.length(); i++) bits[i] = binary.charAt(i) - '0';
        memo = new int[bits.length][2][2];
        for (int[][] plane : memo) for (int[] row : plane) Arrays.fill(row, -1);
        return count(0, 0, 1);
    }

    private static int count(int pos, int prevBit, int tight) {
        if (pos == bits.length) return 1; // a complete valid number
        if (memo[pos][prevBit][tight] != -1) return memo[pos][prevBit][tight];
        int limit = (tight == 1) ? bits[pos] : 1;
        int total = 0;
        for (int bit = 0; bit <= limit; bit++) {
            if (bit == 1 && prevBit == 1) continue;         // would create "11"
            int nextTight = (tight == 1 && bit == limit) ? 1 : 0;
            total += count(pos + 1, bit, nextTight);
        }
        memo[pos][prevBit][tight] = total;
        return total;
    }

    static int bruteForce(int n) {
        int good = 0;
        for (int x = 0; x <= n; x++) if ((x & (x >> 1)) == 0) good++; // x & (x>>1) is nonzero exactly when two adjacent bits are 1
        return good;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 5", findIntegers(5), 5);        // 0, 1, 2, 4, 5
        check("n = 1 (edge)", findIntegers(1), 2);  // 0, 1
        check("n = 2", findIntegers(2), 3);
        check("n = 8", findIntegers(8), 6);        // 0,1,2,4,5,8
        check("n = 1e9", findIntegers(1000000000), 2178309);
        for (int n = 1; n <= 5000; n++) {
            if (findIntegers(n) != bruteForce(n)) check("cross-check n = " + n, findIntegers(n), bruteForce(n));
        }
        System.out.println("PASS  digit DP agrees with brute force for n = 1..5000");
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
