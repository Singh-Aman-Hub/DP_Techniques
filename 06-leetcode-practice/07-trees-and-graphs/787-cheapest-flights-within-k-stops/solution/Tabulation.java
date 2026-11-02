/**
 * 787. Cheapest Flights Within K Stops — Tabulation (layered Bellman-Ford)
 *
 * Idea: cost[t][v] = cheapest price from src to v using at most t flights. Layer t starts as a
 * copy of layer t-1 (using fewer flights is allowed) and then relaxes every flight u->v using
 * cost[t-1][u]. Reading only the previous layer guarantees each layer adds at most one flight.
 *
 * Time complexity:  O((k+1) * (n + E)) — k+1 layers, each copies n cells and relaxes E flights.
 * Space complexity: O(n * k) — the full (k+2) x n table.
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;

public class Tabulation {

    private static final int INF = Integer.MAX_VALUE / 2;

    static int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        // cost[t][v] = cheapest price from src to v using at most t flights
        int[][] cost = new int[k + 2][n];
        Arrays.fill(cost[0], INF);
        cost[0][src] = 0;                                  // zero flights: only src is reachable
        for (int t = 1; t <= k + 1; t++) {
            cost[t] = cost[t - 1].clone();
            for (int[] f : flights) {
                int from = f[0], to = f[1], price = f[2];
                if (cost[t - 1][from] < INF) {             // read the PREVIOUS layer only
                    cost[t][to] = Math.min(cost[t][to], cost[t - 1][from] + price);
                }
            }
        }
        return cost[k + 1][dst] >= INF ? -1 : cost[k + 1][dst];
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
