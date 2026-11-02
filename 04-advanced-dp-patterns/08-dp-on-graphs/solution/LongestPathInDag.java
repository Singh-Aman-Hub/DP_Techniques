/**
 * Longest Path in a DAG (AtCoder DP G "Longest Path") — memoized DFS and topological-order tabulation
 *
 * Idea: longestFrom[v] = number of edges on the longest path that STARTS at v.
 *       longestFrom[v] = max over edges v -> w of (1 + longestFrom[w]); 0 if v has no outgoing edge.
 *       Because the graph has no cycle, following edges always reaches "finished" vertices,
 *       so the recursion terminates and every vertex is solved once (memoization).
 *       Bottom-up, we use the mirror state longestTo[v] (longest path ENDING at v) and fill it
 *       in topological order (Kahn's algorithm): every edge u -> v is processed after u is final.
 *
 * Time complexity:  O(V + E) — each vertex is finalised once and each edge is relaxed once.
 * Space complexity: O(V + E) — adjacency lists plus one value per vertex (and the DFS stack).
 *
 * Run: java LongestPathInDag.java
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LongestPathInDag {

    // ---------- top-down: memoized DFS ----------
    // memo[v] = longest path (in edges) starting at v; -1 = not computed yet
    private static int[] memo;
    private static List<List<Integer>> graph;

    static int longestPathMemo(int n, int[][] edges) {
        graph = buildGraph(n, edges);
        memo = new int[n];
        Arrays.fill(memo, -1);
        int best = 0;
        for (int v = 0; v < n; v++) best = Math.max(best, longestFrom(v));
        return best;
    }

    private static int longestFrom(int v) {
        if (memo[v] != -1) return memo[v];
        int best = 0;                                    // base case: no outgoing edge -> path of length 0
        for (int next : graph.get(v)) best = Math.max(best, 1 + longestFrom(next));
        memo[v] = best;
        return best;
    }

    // ---------- bottom-up: tabulation in topological order ----------
    static int longestPathTopo(int n, int[][] edges) {
        List<List<Integer>> adj = buildGraph(n, edges);
        int[] inDegree = new int[n];
        for (int[] e : edges) inDegree[e[1]]++;

        // longestTo[v] = longest path (in edges) that ENDS at v
        int[] longestTo = new int[n];
        ArrayDeque<Integer> ready = new ArrayDeque<>();   // vertices whose predecessors are all done
        for (int v = 0; v < n; v++) if (inDegree[v] == 0) ready.add(v);

        int best = 0;
        while (!ready.isEmpty()) {
            int u = ready.poll();                         // longestTo[u] is final now
            best = Math.max(best, longestTo[u]);
            for (int v : adj.get(u)) {
                longestTo[v] = Math.max(longestTo[v], longestTo[u] + 1);
                if (--inDegree[v] == 0) ready.add(v);
            }
        }
        return best;
    }

    private static List<List<Integer>> buildGraph(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);
        return adj;
    }

    /** AtCoder numbers vertices from 1; convert to 0-based. */
    private static int[][] fromOneBased(int[][] edges) {
        int[][] result = new int[edges.length][];
        for (int i = 0; i < edges.length; i++) result[i] = new int[]{edges[i][0] - 1, edges[i][1] - 1};
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] lesson = {{0, 1}, {0, 2}, {1, 2}, {1, 3}, {2, 3}, {2, 4}, {3, 4}};
        int[][] sample1 = fromOneBased(new int[][]{{1, 2}, {1, 3}, {3, 2}, {2, 4}, {3, 4}});
        int[][] sample2 = fromOneBased(new int[][]{{2, 3}, {4, 5}, {5, 6}});
        int[][] sample3 = fromOneBased(new int[][]{{5, 3}, {2, 3}, {2, 4}, {5, 2}, {5, 1}, {1, 4}, {4, 3}, {1, 3}});

        check("lesson DAG (memo)",  longestPathMemo(5, lesson), 4);
        check("lesson DAG (topo)",  longestPathTopo(5, lesson), 4);
        check("sample 1 (memo)",    longestPathMemo(4, sample1), 3);
        check("sample 1 (topo)",    longestPathTopo(4, sample1), 3);
        check("sample 2 (memo)",    longestPathMemo(6, sample2), 2);
        check("sample 2 (topo)",    longestPathTopo(6, sample2), 2);
        check("sample 3 (memo)",    longestPathMemo(5, sample3), 3);
        check("sample 3 (topo)",    longestPathTopo(5, sample3), 3);
        check("no edges (memo)",    longestPathMemo(3, new int[][]{}), 0);
        check("no edges (topo)",    longestPathTopo(3, new int[][]{}), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
