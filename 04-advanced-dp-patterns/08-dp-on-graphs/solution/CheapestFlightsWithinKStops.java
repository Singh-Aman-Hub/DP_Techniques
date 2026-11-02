/**
 * Cheapest Flights Within K Stops (LeetCode 787) — Bellman-Ford as DP over "edges used"
 *
 * Idea: the flight graph may contain cycles, so "cost to reach v" alone is not a DAG state.
 *       Adding the number of flights used makes it one:
 *         cost[t][v] = cheapest price to reach v from src using AT MOST t flights.
 *       cost[t][v] = min(cost[t-1][v], min over flights u -> v of cost[t-1][u] + price).
 *       Every transition goes from layer t-1 to layer t, so layers are an acyclic order.
 *       k stops means at most k + 1 flights. We keep only two rows (previous and current).
 *       A brute-force DFS over all routes with at most k + 1 flights cross-checks the DP.
 *
 * Time complexity:  O((k + 1) * (n + F)) — k + 1 rounds, each copies n cells and relaxes all F flights.
 * Space complexity: O(n) — two rows of n costs.
 *
 * Run: java CheapestFlightsWithinKStops.java
 */
import java.util.Arrays;

public class CheapestFlightsWithinKStops {

    private static final int UNREACHED = Integer.MAX_VALUE;

    static int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        // previous[v] = cheapest cost to v with at most t-1 flights (row t-1 of the table)
        int[] previous = new int[n];
        Arrays.fill(previous, UNREACHED);
        previous[src] = 0;                                // base case: 0 flights, we are at src for free

        for (int t = 1; t <= k + 1; t++) {
            // current starts as a COPY: "at most t flights" includes "at most t-1 flights"
            int[] current = previous.clone();
            for (int[] f : flights) {
                int from = f[0], to = f[1], price = f[2];
                if (previous[from] == UNREACHED) continue;
                // read from previous (not current) so each round adds at most ONE flight
                current[to] = Math.min(current[to], previous[from] + price);
            }
            previous = current;
        }
        return previous[dst] == UNREACHED ? -1 : previous[dst];
    }

    // ---------- brute force: try every route with at most k + 1 flights ----------
    static int bruteForce(int n, int[][] flights, int src, int dst, int k) {
        long best = search(flights, src, dst, k + 1);
        return best >= UNREACHED ? -1 : (int) best;
    }

    private static long search(int[][] flights, int at, int dst, int flightsLeft) {
        if (at == dst) return 0;
        if (flightsLeft == 0) return UNREACHED;
        long best = UNREACHED;
        for (int[] f : flights) {
            if (f[0] == at) best = Math.min(best, f[2] + search(flights, f[1], dst, flightsLeft - 1));
        }
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] fourCities = {{0, 1, 100}, {1, 2, 100}, {2, 0, 100}, {1, 3, 600}, {2, 3, 200}};
        int[][] triangle = {{0, 1, 100}, {1, 2, 100}, {0, 2, 500}};

        check("4 cities, k=1",       findCheapestPrice(4, fourCities, 0, 3, 1), 700);
        check("4 cities, k=1 brute", bruteForce(4, fourCities, 0, 3, 1), 700);
        check("4 cities, k=2",       findCheapestPrice(4, fourCities, 0, 3, 2), 400);
        check("4 cities, k=2 brute", bruteForce(4, fourCities, 0, 3, 2), 400);
        check("triangle, k=1",       findCheapestPrice(3, triangle, 0, 2, 1), 200);
        check("triangle, k=0",       findCheapestPrice(3, triangle, 0, 2, 0), 500);
        check("unreachable",         findCheapestPrice(3, new int[][]{{0, 1, 100}}, 0, 2, 1), -1);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
