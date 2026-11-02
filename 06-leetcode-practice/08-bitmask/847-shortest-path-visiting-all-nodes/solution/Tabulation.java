/**
 * 847. Shortest Path Visiting All Nodes — Tabulation (multi-source BFS over (node, mask))
 *
 * Idea: state = (current node, set of visited nodes). Every move costs 1, so BFS discovers the
 * states in increasing distance: the BFS layers are the DP layers. All (v, {v}) are start
 * states because the walk may begin anywhere. The first state with a full mask is the answer.
 *
 * Time complexity:  O(2^n * (n + E)) — each state is enqueued once and scans its node's edges.
 * Space complexity: O(2^n * n) — the seen table and the queue.
 *
 * Run: java Tabulation.java
 */
import java.util.ArrayDeque;
import java.util.Deque;

public class Tabulation {

    static int shortestPathLength(int[][] graph) {
        int n = graph.length;
        int full = (1 << n) - 1;
        // seen[node][mask] = state (node, mask) has already been reached at its shortest distance
        boolean[][] seen = new boolean[n][1 << n];
        Deque<int[]> queue = new ArrayDeque<>();               // entries: {node, mask, distance}
        for (int v = 0; v < n; v++) {
            seen[v][1 << v] = true;
            queue.add(new int[]{v, 1 << v, 0});
        }
        while (!queue.isEmpty()) {
            int[] state = queue.poll();
            int node = state[0], mask = state[1], distance = state[2];
            if (mask == full) return distance;                 // first full mask popped is the shortest
            for (int next : graph[node]) {
                int nextMask = mask | (1 << next);
                if (!seen[next][nextMask]) {
                    seen[next][nextMask] = true;
                    queue.add(new int[]{next, nextMask, distance + 1});
                }
            }
        }
        return -1;                                             // unreachable for a connected graph
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
