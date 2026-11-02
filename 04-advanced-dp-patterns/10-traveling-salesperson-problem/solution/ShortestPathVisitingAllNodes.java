/**
 * Shortest Path Visiting All Nodes (LeetCode 847) — BFS over (node, visited mask)
 *
 * Idea: the state is the same as Held–Karp: (set of visited nodes, node we stand on).
 *       Every edge costs 1, so instead of a min-DP we can run BFS over these states:
 *       BFS reaches each state first by the fewest moves. Nodes may be revisited
 *       (the mask just does not change), which is why a plain mask DP in increasing
 *       mask order would not work but BFS does. Start from every node at once.
 *
 * Time complexity:  O(2^n * n^2) — 2^n * n states, each scans up to n neighbours.
 * Space complexity: O(2^n * n) — the seen[][] table and the queue.
 *
 * Run: java ShortestPathVisitingAllNodes.java
 */
import java.util.ArrayDeque;

public class ShortestPathVisitingAllNodes {

    static int shortestPathLength(int[][] graph) {
        int n = graph.length;
        int full = (1 << n) - 1;
        // seen[mask][node] = state (mask, node) already reached by an earlier, shorter or equal walk
        boolean[][] seen = new boolean[1 << n][n];
        ArrayDeque<int[]> queue = new ArrayDeque<>();     // entries: {mask, node, moves}
        for (int start = 0; start < n; start++) {
            seen[1 << start][start] = true;
            queue.add(new int[]{1 << start, start, 0});   // any node may be the start, 0 moves so far
        }
        while (!queue.isEmpty()) {
            int[] state = queue.poll();
            int mask = state[0], node = state[1], moves = state[2];
            if (mask == full) return moves;               // first time BFS sees "all visited" = fewest moves
            for (int next : graph[node]) {
                int nextMask = mask | (1 << next);
                if (seen[nextMask][next]) continue;
                seen[nextMask][next] = true;
                queue.add(new int[]{nextMask, next, moves + 1});
            }
        }
        return -1;                                        // unreachable for a connected graph
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("star with centre 0",  shortestPathLength(new int[][]{{1, 2, 3}, {0}, {0}, {0}}), 4);
        check("LeetCode example 2",  shortestPathLength(new int[][]{{1}, {0, 2, 4}, {1, 3, 4}, {2}, {1, 2}}), 4);
        check("single node",         shortestPathLength(new int[][]{{}}), 0);
        check("path 0-1-2",          shortestPathLength(new int[][]{{1}, {0, 2}, {1}}), 2);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
