/**
 * 790. Domino and Tromino Tiling — Space-optimized
 *
 * Idea: The two-state recurrence reads only the last two full values and the last two part
 * values, so keep rolling variables. closedForm() checks the derived identity
 * full[n] = 2*full[n-1] + full[n-3] against it.
 *
 * Time complexity:  O(n) — one loop.
 * Space complexity: O(1) — a handful of variables.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    private static final int MOD = 1_000_000_007;

    static int numTilings(int n) {
        long fullPrev2 = 0, fullPrev1 = 1;   // full[i-2], full[i-1]  (start: full[-1] = 0, full[0] = 1)
        long partPrev2 = 0, partPrev1 = 0;   // part[i-2], part[i-1]
        for (int i = 1; i <= n; i++) {
            long fullNow = (fullPrev1 + fullPrev2 + 2 * partPrev2) % MOD;
            long partNow = (partPrev1 + fullPrev1) % MOD;
            fullPrev2 = fullPrev1;
            fullPrev1 = fullNow;
            partPrev2 = partPrev1;
            partPrev1 = partNow;
        }
        return (int) fullPrev1;
    }

    /** full[n] = 2*full[n-1] + full[n-3], with full[0] = 1, full[1] = 1, full[2] = 2. */
    static int closedForm(int n) {
        if (n <= 1) return 1;
        if (n == 2) return 2;
        long a = 1, b = 1, c = 2;            // full[i-3], full[i-2], full[i-1]
        for (int i = 3; i <= n; i++) {
            long next = (2 * c + a) % MOD;
            a = b;
            b = c;
            c = next;
        }
        return (int) c;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", numTilings(1), 1);
        check("n = 2", numTilings(2), 2);
        check("n = 3", numTilings(3), 5);
        check("n = 4", numTilings(4), 11);
        check("n = 5", numTilings(5), 24);
        check("n = 30", numTilings(30), 312342182);
        check("closed form n = 5", closedForm(5), 24);
        check("closed form n = 30", closedForm(30), 312342182);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
