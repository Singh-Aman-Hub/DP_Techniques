/**
 * 787. Cheapest Flights Within K Stops — Optimized (two rolling arrays)
 *
 * Idea: layer t of the table reads only layer t-1, so keep just two arrays: prev (t-1) and
 * next (t). Updating a single array in place would be wrong, because a price improved in this
 * round could be extended again in the same round, using two flights in one layer.
 *
 * Time complexity:  O((k+1) * (n + E)) — k+1 rounds of copying and relaxing.
 * Space complexity: O(n) — two arrays of size n.
 *
 * Run: java Optimized.java
 */
import java.util.Arrays;

public class Optimized {

    private static final int INF = Integer.MAX_VALUE / 2;

    static int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        // prev[v] = cheapest price to reach v using at most (round - 1) flights
        int[] prev = new int[n];
        Arrays.fill(prev, INF);
        prev[src] = 0;
        for (int round = 1; round <= k + 1; round++) {
            int[] next = prev.clone();
            for (int[] f : flights) {
                if (prev[f[0]] < INF) next[f[1]] = Math.min(next[f[1]], prev[f[0]] + f[2]);
            }
            prev = next;
        }
        return prev[dst] >= INF ? -1 : prev[dst];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] triangle = {{0, 1, 100}, {1, 2, 100}, {0, 2, 500}};
        check("triangle, k = 1", findCheapestPrice(3, triangle, 0, 2, 1), 200);
        check("triangle, k = 0", findCheapestPrice(3, triangle, 0, 2, 0), 500);
        int[][] withCycle = {{0, 1, 100}, {1, 2, 100}, {2, 0, 100}, {1, 3, 600}, {2, 3, 200}};
        check("graph with a cycle, k = 1", findCheapestPrice(4, withCycle, 0, 3, 1), 700);
        check("graph with a cycle, k = 2", findCheapestPrice(4, withCycle, 0, 3, 2), 400);
        check("unreachable (edge)", findCheapestPrice(3, new int[][]{{0, 1, 100}}, 0, 2, 1), -1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
