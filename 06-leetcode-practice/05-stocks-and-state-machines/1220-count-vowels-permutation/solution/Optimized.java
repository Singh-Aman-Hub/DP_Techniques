/**
 * 1220. Count Vowels Permutation — Space-optimized (five variables)
 *
 * Idea: Each length depends only on the previous one. Keep five counters and compute all five
 * new values from the OLD ones before assigning any of them.
 *
 * Time complexity:  O(n) — n steps of constant work.
 * Space complexity: O(1) — five counters.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    private static final int MOD = 1_000_000_007;

    static int countVowelPermutation(int n) {
        long a = 1, e = 1, i = 1, o = 1, u = 1;   // strings of the current length ending in each vowel
        for (int len = 2; len <= n; len++) {
            long newA = (e + i + u) % MOD;
            long newE = (a + i) % MOD;
            long newI = (e + o) % MOD;
            long newO = i;
            long newU = (i + o) % MOD;
            a = newA;
            e = newE;
            i = newI;
            o = newO;
            u = newU;
        }
        return (int) ((a + e + i + o + u) % MOD);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", countVowelPermutation(1), 5);
        check("n = 2", countVowelPermutation(2), 10);
        check("n = 5", countVowelPermutation(5), 68);
        check("n = 144", countVowelPermutation(144), 18208803);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
