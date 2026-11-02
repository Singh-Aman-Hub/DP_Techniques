/**
 * Traveling Salesperson Problem — Memoization (Held–Karp, top-down)
 *
 * Idea: in the brute force, the remaining work depends only on (a) the SET of visited
 *       cities and (b) the city we stand in now. Not on the order of earlier visits.
 *       finish(mask, last) = cheapest way to visit every city NOT in mask, starting
 *       at `last`, and then return to city 0. Cache it in memo[mask][last].
 *
 * Time complexity:  O(2^n * n^2) — 2^n * n states, each tries up to n next cities.
 * Space complexity: O(2^n * n) — the memo table (plus O(n) recursion depth).
 *
 * Run: java TspMemoization.java
 */
import java.util.Arrays;

public class TspMemoization {

    // memo[mask][last] = cheapest cost to finish the tour from state (mask, last); -1 = unknown
    private static int[][] memo;
    private static int[][] dist;

    static int shortestTour(int[][] distances) {
        dist = distances;
        int n = dist.length;
        memo = new int[1 << n][n];
        for (int[] row : memo) Arrays.fill(row, -1);
        return finish(1, 0);                              // start: only city 0 visited, standing at 0
    }

    private static int finish(int mask, int last) {
        int n = dist.length;
        if (mask == (1 << n) - 1) return dist[last][0];   // everything visited: return home
        if (memo[mask][last] != -1) return memo[mask][last];

        int best = Integer.MAX_VALUE;
        for (int next = 0; next < n; next++) {
            if ((mask & (1 << next)) != 0) continue;      // already visited
            best = Math.min(best, dist[last][next] + finish(mask | (1 << next), next));
        }
        memo[mask][last] = best;
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] fourCities = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0},
        };
        int[][] oneWay = {
            {0, 1, 15},
            {6, 0, 7},
            {9, 6, 0},
        };
        check("4 cities (lesson example)", shortestTour(fourCities), 80);
        check("3 cities, asymmetric",      shortestTour(oneWay), 17);
        check("2 cities",                  shortestTour(new int[][]{{0, 5}, {5, 0}}), 10);
        check("1 city",                    shortestTour(new int[][]{{0}}), 0);

        // 15 cities on a line at positions 0..14: any tour must go out to 14 and back -> 28.
        int n = 15;
        int[][] line = new int[n][n];
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) line[i][j] = Math.abs(i - j);
        check("15 cities on a line", shortestTour(line), 28);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
