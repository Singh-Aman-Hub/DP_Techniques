/**
 * 1976. Number of Ways to Arrive at Destination — counting inside Dijkstra (bottom-up)
 *
 * Idea: Dijkstra finalises nodes in increasing dist, which is a topological order of the
 * shortest-path DAG. When u is popped, ways[u] is complete, so push it along every road:
 *   strictly shorter route to v -> dist[v] = new time, ways[v] = ways[u]   (reset)
 *   equally short route to v    -> ways[v] += ways[u]                      (add)
 *
 * Time complexity:  O((n + E) log n) — standard Dijkstra with a binary heap.
 * Space complexity: O(n + E) — adjacency lists, dist, ways, and the priority queue.
 *
 * Run: java Tabulation.java
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class Tabulation {

    private static final long MOD = 1_000_000_007L;

    static int countPaths(int n, int[][] roads) {
        List<List<int[]>> adjacency = new ArrayList<>();
        for (int i = 0; i < n; i++) adjacency.add(new ArrayList<>());
        for (int[] r : roads) {
            adjacency.get(r[0]).add(new int[]{r[1], r[2]});
            adjacency.get(r[1]).add(new int[]{r[0], r[2]});
        }
        long[] dist = new long[n];     // dist[v] = shortest known time from 0 to v
        long[] ways = new long[n];     // ways[v] = number of routes achieving dist[v] (mod 1e9+7)
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[0] = 0;
        ways[0] = 1;
        PriorityQueue<long[]> queue = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        queue.add(new long[]{0, 0});
        while (!queue.isEmpty()) {
            long[] top = queue.poll();
            int u = (int) top[1];
            if (top[0] > dist[u]) continue;        // stale entry: u was already finalised
            for (int[] road : adjacency.get(u)) {
                int v = road[0];
                long candidate = dist[u] + road[1];
                if (candidate < dist[v]) {         // better time: old routes are no longer shortest
                    dist[v] = candidate;
                    ways[v] = ways[u];
                    queue.add(new long[]{candidate, v});
                } else if (candidate == dist[v]) { // another equally short route
                    ways[v] = (ways[v] + ways[u]) % MOD;
                }
            }
        }
        return (int) ways[n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] city = {{0, 6, 7}, {0, 1, 2}, {1, 2, 3}, {1, 3, 3}, {6, 3, 3},
                        {3, 5, 1}, {6, 5, 1}, {2, 5, 1}, {0, 4, 5}, {4, 6, 2}};
        check("n = 7 example", countPaths(7, city), 4);
        check("n = 2 single road", countPaths(2, new int[][]{{1, 0, 10}}), 1);
        check("n = 1 (edge: start is destination)", countPaths(1, new int[][]{}), 1);
        check("diamond", countPaths(4, new int[][]{{0, 1, 1}, {0, 2, 1}, {1, 3, 1}, {2, 3, 1}}), 2);
        // 40 diamonds in a row: 2^40 shortest routes, so the modulo matters;
        // road times of 10^9 also make distances exceed int range
        int diamonds = 40;
        int n = 1 + 3 * diamonds;
        int[][] chain = new int[4 * diamonds][];
        for (int i = 0; i < diamonds; i++) {
            int start = 3 * i, top = start + 1, bottom = start + 2, join = start + 3;
            chain[4 * i] = new int[]{start, top, 1_000_000_000};
            chain[4 * i + 1] = new int[]{start, bottom, 1_000_000_000};
            chain[4 * i + 2] = new int[]{top, join, 1_000_000_000};
            chain[4 * i + 3] = new int[]{bottom, join, 1_000_000_000};
        }
        long expected = 1;
        for (int i = 0; i < diamonds; i++) expected = expected * 2 % 1_000_000_007L;
        check("40 diamonds -> 2^40 mod 1e9+7", countPaths(n, chain), expected);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
