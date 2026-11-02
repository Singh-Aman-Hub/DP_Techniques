/**
 * Traveling Salesperson Problem — Tabulation (Held–Karp, bottom-up) with tour reconstruction
 *
 * Idea: dp[mask][last] = cheapest path that starts at city 0, visits exactly the
 *       cities in mask (0 and last included), and ends at last.
 *       Push: from (mask, last) go to any unvisited `next`:
 *         dp[mask | 1<<next][next] = min(..., dp[mask][last] + dist[last][next]).
 *       Adding a city makes the mask bigger, so increasing mask order is valid.
 *       Answer = min over last of dp[full][last] + dist[last][0].
 *       parent[mask][last] remembers the city before `last`, so we can walk back
 *       from the best final state and rebuild the tour.
 *
 * Time complexity:  O(2^n * n^2) — 2^n * n states, each pushes to up to n cities.
 * Space complexity: O(2^n * n) — dp and parent tables.
 *
 * Run: java TspTabulation.java
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TspTabulation {

    private static final int INF = Integer.MAX_VALUE / 2;   // halved so INF + distance cannot overflow

    /** Returns the tour as a list of cities starting and ending at 0; its cost is tourCost(...). */
    static List<Integer> bestTour(int[][] dist) {
        int n = dist.length;
        if (n == 1) return List.of(0, 0);
        int full = (1 << n) - 1;

        // dp[mask][last]: see header. parent[mask][last]: the city visited just before last.
        int[][] dp = new int[1 << n][n];
        int[][] parent = new int[1 << n][n];
        for (int[] row : dp) Arrays.fill(row, INF);
        for (int[] row : parent) Arrays.fill(row, -1);
        dp[1][0] = 0;                                     // base case: at city 0, only 0 visited

        for (int mask = 1; mask <= full; mask++) {
            if ((mask & 1) == 0) continue;                // every real path contains the start city 0
            for (int last = 0; last < n; last++) {
                if (dp[mask][last] >= INF) continue;      // state not reachable
                for (int next = 0; next < n; next++) {
                    if ((mask & (1 << next)) != 0) continue;
                    int nextMask = mask | (1 << next);
                    int candidate = dp[mask][last] + dist[last][next];
                    if (candidate < dp[nextMask][next]) {
                        dp[nextMask][next] = candidate;
                        parent[nextMask][next] = last;
                    }
                }
            }
        }

        // Close the cycle: choose the last city that is cheapest to return home from.
        int bestLast = -1, bestCost = INF;
        for (int last = 1; last < n; last++) {
            int cost = dp[full][last] + dist[last][0];
            if (cost < bestCost) { bestCost = cost; bestLast = last; }
        }

        // Walk the parent pointers backwards: (full, bestLast) -> ... -> (1, 0).
        List<Integer> tour = new ArrayList<>();
        tour.add(0);                                      // the return trip home
        int mask = full, city = bestLast;
        while (city != -1) {
            tour.add(city);
            int previous = parent[mask][city];
            mask ^= (1 << city);                          // remove city from the set
            city = previous;
        }
        Collections.reverse(tour);
        return tour;
    }

    static int tourCost(int[][] dist, List<Integer> tour) {
        int cost = 0;
        for (int i = 0; i + 1 < tour.size(); i++) cost += dist[tour.get(i)][tour.get(i + 1)];
        return cost;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] fourCities = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0},
        };
        List<Integer> tour = bestTour(fourCities);
        check("4 cities cost", tourCost(fourCities, tour), 80);
        // 0-1-3-2-0 and its reverse 0-2-3-1-0 both cost 80. The closing loop tries last = 1 first
        // (dp[full][1] + dist[1][0] = 70 + 10 = 80), and walking parents back from city 1 gives 0-2-3-1-0.
        checkTour("4 cities tour", tour, List.of(0, 2, 3, 1, 0));

        int[][] oneWay = {{0, 1, 15}, {6, 0, 7}, {9, 6, 0}};
        List<Integer> oneWayTour = bestTour(oneWay);
        check("3 cities asymmetric cost", tourCost(oneWay, oneWayTour), 17);
        checkTour("3 cities asymmetric tour", oneWayTour, List.of(0, 1, 2, 0));

        check("2 cities", tourCost(new int[][]{{0, 5}, {5, 0}}, bestTour(new int[][]{{0, 5}, {5, 0}})), 10);
        check("1 city",   tourCost(new int[][]{{0}}, bestTour(new int[][]{{0}})), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }

    private static void checkTour(String label, List<Integer> actual, List<Integer> expected) {
        boolean ok = actual.equals(expected);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
