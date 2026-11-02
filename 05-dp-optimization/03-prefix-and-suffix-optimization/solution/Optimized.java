/**
 * AtCoder DP Contest M — Candies — prefix-sum optimization, O(N·K)
 *
 * Idea: the slow transition is cur[j] = prev[j-a] + ... + prev[j], a contiguous RANGE of the
 *       previous row. Build prefix[t] = prev[0] + ... + prev[t-1] once per row (O(K)); then
 *       every range sum is prefix[j+1] - prefix[max(0, j-a)], an O(1) lookup.
 *       All arithmetic is modulo 1e9+7, so the subtraction adds MOD back before taking %.
 *
 * Time complexity:  O(N·K) — per child: O(K) to build the prefix array + O(K) cells × O(1).
 * Space complexity: O(K) — the previous row, its prefix array and the current row.
 *
 * Run: java Optimized.java
 */
import java.util.Random;

public class Optimized {

    static final long MOD = 1_000_000_007L;

    static long countWays(int[] limits, int totalCandies) {
        // prev[j] = ways to give exactly j candies to the children processed so far
        long[] prev = new long[totalCandies + 1];
        prev[0] = 1;
        // prefix[t] = (prev[0] + ... + prev[t-1]) mod MOD; prefix[0] = 0 means "empty sum"
        long[] prefix = new long[totalCandies + 2];
        for (int limit : limits) {
            prefix[0] = 0;
            for (int t = 0; t <= totalCandies; t++) {
                prefix[t + 1] = (prefix[t] + prev[t]) % MOD;
            }
            long[] cur = new long[totalCandies + 1];
            for (int j = 0; j <= totalCandies; j++) {
                int lo = Math.max(0, j - limit);          // smallest index of prev we may use
                // sum prev[lo..j] = prefix[j+1] - prefix[lo]; + MOD keeps it non-negative
                cur[j] = (prefix[j + 1] - prefix[lo] + MOD) % MOD;
            }
            prev = cur;
        }
        return prev[totalCandies];
    }

    /** Private copy of the O(N·K²) version, used only for the randomized cross-check. */
    private static long slowTable(int[] limits, int totalCandies) {
        long[] prev = new long[totalCandies + 1];
        prev[0] = 1;
        for (int limit : limits) {
            long[] cur = new long[totalCandies + 1];
            for (int j = 0; j <= totalCandies; j++) {
                long sum = 0;
                for (int x = 0; x <= Math.min(limit, j); x++) sum += prev[j - x];
                cur[j] = sum % MOD;
            }
            prev = cur;
        }
        return prev[totalCandies];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("limits=[1,2,3] K=4", countWays(new int[]{1, 2, 3}, 4), 5);
        check("limits=[9] K=10 (impossible)", countWays(new int[]{9}, 10), 0);
        check("limits=[0,0] K=0 (edge)", countWays(new int[]{0, 0}, 0), 1);
        check("limits=[1,1] K=1", countWays(new int[]{1, 1}, 1), 2);
        // AtCoder sample 4: far too big for the slow version (4 · 10^5 · 10^5 steps)
        check("limits=[100000 x4] K=100000",
              countWays(new int[]{100000, 100000, 100000, 100000}, 100000), 665683269);

        // randomized cross-check against the slow version (fixed seed => reproducible)
        Random random = new Random(20240503);
        int mismatches = 0;
        for (int trial = 0; trial < 500; trial++) {
            int n = 1 + random.nextInt(6);
            int k = random.nextInt(15);
            int[] limits = new int[n];
            for (int i = 0; i < n; i++) limits[i] = random.nextInt(15);
            if (countWays(limits, k) != slowTable(limits, k)) mismatches++;
        }
        check("500 random cases: mismatches vs slow", mismatches, 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
