/**
 * 847. Shortest Path Visiting All Nodes — Memoization (TSP over shortest distances)
 *
 * Idea: the (node, visited) state graph has cycles, so we only track FIRST visits.
 * dist[u][v] comes from a BFS per node. best(mask, last) = shortest walk that first-visits
 * exactly the nodes in mask and ends at 'last' = min over the previous new node u of
 * best(mask without last, u) + dist[u][last]. Every step removes a bit, so no cycles.
 *
 * Time complexity:  O(n*E + 2^n * n^2) — n BFS runs, then n*2^n states with n predecessors each.
 * Space complexity: O(2^n * n) — the memo table (recursion depth at most n).
 *
 * Run: java Memoization.java
 */
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class Memoization {

    private static final int INF = 1_000_000;
    private static int n;
    private static int[][] dist;      // dist[u][v] = fewest edges between u and v
    private static int[][] memo;      // memo[mask][last]; -1 = not computed

    static int shortestPathLength(int[][] graph) {
        n = graph.length;
        dist = new int[n][n];
        for (int source = 0; source < n; source++) dist[source] = bfs(graph, source);
        memo = new int[1 << n][n];
        for (int[] row : memo) Arrays.fill(row, -1);
        int full = (1 << n) - 1, answer = INF;
        for (int last = 0; last < n; last++) answer = Math.min(answer, best(full, last));
        return answer;
    }

    private static int best(int mask, int last) {
        if (mask == (1 << last)) return 0;            // only 'last' visited: the walk starts here
        if (memo[mask][last] != -1) return memo[mask][last];
        int previousMask = mask ^ (1 << last);        // remove 'last' from the set
        int result = INF;
        for (int u = 0; u < n; u++) {
            if ((previousMask & (1 << u)) == 0) continue;
            result = Math.min(result, best(previousMask, u) + dist[u][last]);
        }
        memo[mask][last] = result;
        return result;
    }

    private static int[] bfs(int[][] graph, int source) {
        int[] d = new int[graph.length];
        Arrays.fill(d, -1);
        d[source] = 0;
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(source);
        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : graph[u]) if (d[v] == -1) { d[v] = d[u] + 1; queue.add(v); }
        }
        return d;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("star with 3 leaves", shortestPathLength(new int[][]{{1, 2, 3}, {0}, {0}, {0}}), 4);
        check("example graph of 5 nodes", shortestPathLength(new int[][]{{1}, {0, 2, 4}, {1, 3, 4}, {2}, {1, 2}}), 4);
        check("single node (edge)", shortestPathLength(new int[][]{{}}), 0);
        check("path 0-1-2-3", shortestPathLength(new int[][]{{1}, {0, 2}, {1, 3}, {2}}), 3);
        check("complete graph K4", shortestPathLength(new int[][]{{1, 2, 3}, {0, 2, 3}, {0, 1, 3}, {0, 1, 2}}), 3);
        check("star with 4 leaves", shortestPathLength(new int[][]{{1, 2, 3, 4}, {0}, {0}, {0}, {0}}), 6);
        int n = 12;                                   // path of 12 nodes: largest input size
        int[][] line = new int[n][];
        for (int i = 0; i < n; i++) {
            if (i == 0) line[i] = new int[]{1};
            else if (i == n - 1) line[i] = new int[]{n - 2};
            else line[i] = new int[]{i - 1, i + 1};
        }
        check("path of 12 nodes", shortestPathLength(line), 11);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
