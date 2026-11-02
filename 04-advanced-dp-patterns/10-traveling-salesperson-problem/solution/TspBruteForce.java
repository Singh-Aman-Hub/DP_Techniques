/**
 * Traveling Salesperson Problem — Brute force over every tour
 *
 * Idea: fix city 0 as the start (a tour is a cycle, so any city can be the start).
 *       Try every order of the other n-1 cities with a recursive search, add the
 *       cost of returning to 0, and keep the cheapest total.
 *
 * Time complexity:  O(n!) — (n-1)! orders, each built one step at a time.
 * Space complexity: O(n) — the visited[] array and the recursion stack.
 *
 * Run: java TspBruteForce.java
 */
public class TspBruteForce {

    static int shortestTour(int[][] dist) {
        int n = dist.length;
        boolean[] visited = new boolean[n];
        visited[0] = true;
        return extend(dist, visited, 0, 1);
    }

    /** Cheapest way to visit every unvisited city starting from `current` and return to 0. */
    private static int extend(int[][] dist, boolean[] visited, int current, int visitedCount) {
        int n = dist.length;
        if (visitedCount == n) return dist[current][0];   // all visited: just go home
        int best = Integer.MAX_VALUE;
        for (int next = 0; next < n; next++) {
            if (visited[next]) continue;
            visited[next] = true;
            best = Math.min(best, dist[current][next] + extend(dist, visited, next, visitedCount + 1));
            visited[next] = false;
        }
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
        int[][] oneWay = {      // asymmetric: going 0->1->2->0 is cheaper than the reverse
            {0, 1, 15},
            {6, 0, 7},
            {9, 6, 0},
        };
        check("4 cities (lesson example)", shortestTour(fourCities), 80);
        check("3 cities, asymmetric",      shortestTour(oneWay), 17);
        check("2 cities",                  shortestTour(new int[][]{{0, 5}, {5, 0}}), 10);
        check("1 city",                    shortestTour(new int[][]{{0}}), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
