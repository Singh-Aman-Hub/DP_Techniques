/**
 * 787. Cheapest Flights Within K Stops — Memoization
 *
 * Idea: cheapest(city, flightsLeft) = minimum price from city to dst with at most flightsLeft
 * flights. Try every outgoing flight and recurse with one fewer flight. Because flightsLeft
 * strictly decreases, the (city, flightsLeft) states form a DAG even if the flight graph has
 * cycles, so caching is safe.
 *
 * Time complexity:  O((k+1) * (n + E)) — n*(k+2) states; each scans its city's flights once.
 * Space complexity: O(n * k) — the memo table, plus recursion depth up to k+1.
 *
 * Run: java Memoization.java
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Memoization {

    private static final int INF = Integer.MAX_VALUE / 2;   // "unreachable", safe to add a price to
    private static List<List<int[]>> outgoing;             // outgoing.get(u) = list of {to, price}
    private static int destination;
    // memo[city][f] = cheapest(city, f); -1 = not computed yet
    private static int[][] memo;

    static int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        outgoing = new ArrayList<>();
        for (int i = 0; i < n; i++) outgoing.add(new ArrayList<>());
        for (int[] f : flights) outgoing.get(f[0]).add(new int[]{f[1], f[2]});
        destination = dst;
        memo = new int[n][k + 2];
        for (int[] row : memo) Arrays.fill(row, -1);
        int answer = cheapest(src, k + 1);                 // k stops = k + 1 flights
        return answer >= INF ? -1 : answer;
    }

    private static int cheapest(int city, int flightsLeft) {
        if (city == destination) return 0;                 // arrived: nothing more to pay
        if (flightsLeft == 0) return INF;                  // stuck away from dst
        if (memo[city][flightsLeft] != -1) return memo[city][flightsLeft];
        int best = INF;
        for (int[] flight : outgoing.get(city)) {
            int rest = cheapest(flight[0], flightsLeft - 1);
            if (rest < INF) best = Math.min(best, flight[1] + rest);
        }
        memo[city][flightsLeft] = best;
        return best;
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
