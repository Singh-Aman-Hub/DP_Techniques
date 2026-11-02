/**
 * Count Vowels Permutation (LeetCode 1220) — state machine with 5 states (the last vowel)
 *
 * Idea: the rules say which vowel may FOLLOW which:
 *         a -> e;  e -> a, i;  i -> a, e, o, u;  o -> i, u;  u -> a.
 *       endsWith[v] = number of valid strings of the current length ending in vowel v.
 *       A string ending in v grows by one letter along every allowed edge v -> w.
 *       Reading the rules backwards gives "who can come before w":
 *         a <- e, i, u;  e <- a, i;  i <- e, o;  o <- i;  u <- i, o.
 *       bruteForce enumerates all 5^n strings for small n as a cross-check.
 *
 * Time complexity:  O(n) — n-1 steps, each with a constant 8 edge additions.
 * Space complexity: O(1) — five counters.
 *
 * Run: java CountVowelsPermutation.java
 */
public class CountVowelsPermutation {

    private static final long MOD = 1_000_000_007L;

    static int countVowelPermutation(int n) {
        // a, e, i, o, u = number of valid strings of the current length ending in that vowel
        long a = 1, e = 1, i = 1, o = 1, u = 1;          // length 1: each vowel alone
        for (int length = 2; length <= n; length++) {
            long newA = (e + i + u) % MOD;                // a may follow e, i, u
            long newE = (a + i) % MOD;                    // e may follow a, i
            long newI = (e + o) % MOD;                    // i may follow e, o
            long newO = i % MOD;                          // o may follow i
            long newU = (i + o) % MOD;                    // u may follow i, o
            a = newA; e = newE; i = newI; o = newO; u = newU;
        }
        return (int) ((a + e + i + o + u) % MOD);
    }

    // allowedNext[v] lists the vowels that may follow vowel v (0=a, 1=e, 2=i, 3=o, 4=u)
    private static final int[][] ALLOWED_NEXT = {{1}, {0, 2}, {0, 1, 3, 4}, {2, 4}, {0}};

    static long bruteForce(int n) {
        long total = 0;
        for (int first = 0; first < 5; first++) total += extend(first, n - 1);
        return total;
    }

    private static long extend(int last, int remaining) {
        if (remaining == 0) return 1;
        long total = 0;
        for (int next : ALLOWED_NEXT[last]) total += extend(next, remaining - 1);
        return total;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1", countVowelPermutation(1), 5);
        check("n = 2", countVowelPermutation(2), 10);
        check("n = 5", countVowelPermutation(5), 68);
        for (int n = 1; n <= 10; n++) check("n = " + n + " vs brute", countVowelPermutation(n), bruteForce(n));
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
