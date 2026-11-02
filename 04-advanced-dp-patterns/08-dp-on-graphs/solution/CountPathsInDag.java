/**
 * Counting paths in a DAG — tabulation in topological order, cross-checked by brute force
 *
 * Idea: ways[v] = number of different paths from source to v.
 *       ways[source] = 1, and every edge u -> v contributes ways[u] paths to v
 *       (each path to u extended by the edge u -> v). Processing vertices in
 *       topological order guarantees ways[u] is final before it is pushed forward.
 *       The brute force enumerates every path with a plain DFS (exponential).
 *
 * Time complexity:  O(V + E) for the DP — one pass over vertices and edges.
 *                   Exponential for the brute force — it walks every path one by one.
 * Space complexity: O(V + E) — adjacency lists, in-degrees and the ways array.
 *
 * Run: java CountPathsInDag.java
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class CountPathsInDag {

    static long countPaths(int n, int[][] edges, int source, int target) {
        List<List<Integer>> adj = buildGraph(n, edges);
        int[] inDegree = new int[n];
        for (int[] e : edges) inDegree[e[1]]++;

        // ways[v] = number of distinct paths source -> v
        long[] ways = new long[n];
        ways[source] = 1;                                 // the empty path

        ArrayDeque<Integer> ready = new ArrayDeque<>();
        for (int v = 0; v < n; v++) if (inDegree[v] == 0) ready.add(v);
        while (!ready.isEmpty()) {
            int u = ready.poll();                         // ways[u] is final: all its in-edges were pushed
            for (int v : adj.get(u)) {
                ways[v] += ways[u];
                if (--inDegree[v] == 0) ready.add(v);
            }
        }
        return ways[target];
    }

    static long bruteForce(int n, int[][] edges, int source, int target) {
        return walk(buildGraph(n, edges), source, target);
    }

    private static long walk(List<List<Integer>> adj, int v, int target) {
        if (v == target) return 1;
        long total = 0;
        for (int next : adj.get(v)) total += walk(adj, next, target);
        return total;
    }

    private static List<List<Integer>> buildGraph(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);
        return adj;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] lesson = {{0, 1}, {0, 2}, {1, 2}, {1, 3}, {2, 3}, {2, 4}, {3, 4}};
        check("lesson DAG 0->4",       countPaths(5, lesson, 0, 4), 5);
        check("lesson DAG 0->4 brute", bruteForce(5, lesson, 0, 4), 5);
        check("lesson DAG 0->3",       countPaths(5, lesson, 0, 3), 3);
        check("source == target",      countPaths(5, lesson, 2, 2), 1);
        check("unreachable 4->0",      countPaths(5, lesson, 4, 0), 0);

        // Three diamonds in a row: 0->{1,2}->3->{4,5}->6->{7,8}->9 gives 2*2*2 = 8 paths.
        int[][] diamonds = {{0, 1}, {0, 2}, {1, 3}, {2, 3}, {3, 4}, {3, 5}, {4, 6}, {5, 6},
                            {6, 7}, {6, 8}, {7, 9}, {8, 9}};
        check("three diamonds",       countPaths(10, diamonds, 0, 9), 8);
        check("three diamonds brute", bruteForce(10, diamonds, 0, 9), 8);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
