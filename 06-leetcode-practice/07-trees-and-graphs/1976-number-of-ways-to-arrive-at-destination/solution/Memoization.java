/**
 * 1976. Number of Ways to Arrive at Destination — Dijkstra, then memoized path counting
 *
 * Idea: Dijkstra gives dist[v], the shortest time from 0. A road u-v can be the last road of
 * a shortest route to v only if dist[u] + time == dist[v]. Those roads form a DAG (dist strictly
 * increases along them), and ways(v) = sum of ways(u) over such roads, with ways(0) = 1.
 *
 * Time complexity:  O((n + E) log n) — Dijkstra; the memoized count touches each road once more.
 * Space complexity: O(n + E) — adjacency lists, dist, memo, and the priority queue.
 *
 * Run: java Memoization.java
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class Memoization {

    private static final long MOD = 1_000_000_007L;
    private static List<List<int[]>> adjacency;   // adjacency.get(v) = list of {neighbour, time}
    private static long[] dist;                   // dist[v] = shortest time from 0 to v
    private static long[] memo;                   // memo[v] = ways(v); -1 = not computed

    static int countPaths(int n, int[][] roads) {
        adjacency = new ArrayList<>();
        for (int i = 0; i < n; i++) adjacency.add(new ArrayList<>());
        for (int[] r : roads) {
            adjacency.get(r[0]).add(new int[]{r[1], r[2]});
            adjacency.get(r[1]).add(new int[]{r[0], r[2]});
        }
        dist = dijkstra(n);
        memo = new long[n];
        Arrays.fill(memo, -1);
        return (int) ways(n - 1);
    }

    // number of shortest routes from 0 to v, modulo 1e9+7
    private static long ways(int v) {
        if (v == 0) return 1;                      // the empty route
        if (memo[v] != -1) return memo[v];
        long total = 0;
        for (int[] road : adjacency.get(v)) {
            int u = road[0];
            if (dist[u] + road[1] == dist[v]) total = (total + ways(u)) % MOD;   // u -> v is on a shortest route
        }
        memo[v] = total;
        return total;
    }

    private static long[] dijkstra(int n) {
        long[] d = new long[n];
        Arrays.fill(d, Long.MAX_VALUE);
        d[0] = 0;
        PriorityQueue<long[]> queue = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        queue.add(new long[]{0, 0});
        while (!queue.isEmpty()) {
            long[] top = queue.poll();
            int u = (int) top[1];
            if (top[0] > d[u]) continue;           // stale entry
            for (int[] road : adjacency.get(u)) {
                long candidate = d[u] + road[1];
                if (candidate < d[road[0]]) {
                    d[road[0]] = candidate;
                    queue.add(new long[]{candidate, road[0]});
                }
            }
        }
        return d;
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
