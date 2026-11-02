/**
 * Sum of Distances in Tree (LeetCode 834) — rerooting DP
 *
 * Idea: Pass 1 (post-order from root 0) computes, for every node v,
 *         size[v]     = number of nodes in v's subtree
 *         downSum[v]  = sum of distances from v to the nodes in its subtree.
 *       downSum[0] is the full answer for node 0.
 *       Pass 2 (pre-order) moves the root from a parent p to a child c:
 *         size[c] nodes get 1 step closer, the other n - size[c] get 1 step farther, so
 *         answer[c] = answer[p] - size[c] + (n - size[c]).
 *       Both passes are iterative so a long path of 30 000 nodes cannot overflow the stack.
 *       bruteForce runs a BFS from every node and is used to cross-check.
 *
 * Time complexity:  O(n) for rerooting — two passes, O(1) work per edge.
 *                   O(n^2) for bruteForce — one O(n) BFS per node.
 * Space complexity: O(n) — adjacency lists, the order array and the size/answer arrays.
 *
 * Run: java SumOfDistancesInTree.java
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SumOfDistancesInTree {

    static int[] sumOfDistances(int n, int[][] edges) {
        List<List<Integer>> adj = buildAdjacency(n, edges);

        // Visit order from root 0 where every parent comes before its children (BFS order).
        int[] parent = new int[n];
        int[] order = new int[n];
        Arrays.fill(parent, -1);
        boolean[] seen = new boolean[n];
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        queue.add(0);
        seen[0] = true;
        int filled = 0;
        while (!queue.isEmpty()) {
            int v = queue.poll();
            order[filled++] = v;
            for (int next : adj.get(v)) {
                if (!seen[next]) { seen[next] = true; parent[next] = v; queue.add(next); }
            }
        }

        // size[v] = nodes in v's subtree; downSum[v] = sum of distances from v into its subtree
        int[] size = new int[n];
        long[] downSum = new long[n];
        // Pass 1: children before parents (reverse BFS order) = post-order.
        for (int i = n - 1; i >= 0; i--) {
            int v = order[i];
            size[v] += 1;                                // v itself
            if (parent[v] >= 0) {
                int p = parent[v];
                size[p] += size[v];
                // every node in v's subtree is one edge farther from p than from v
                downSum[p] += downSum[v] + size[v];
            }
        }

        // answer[v] = sum of distances from v to ALL nodes
        long[] answer = new long[n];
        answer[0] = downSum[0];
        // Pass 2: parents before children (BFS order) = pre-order, re-rooting one edge at a time.
        for (int i = 1; i < n; i++) {
            int c = order[i];
            int p = parent[c];
            answer[c] = answer[p] - size[c] + (n - size[c]);
        }

        int[] result = new int[n];
        for (int v = 0; v < n; v++) result[v] = (int) answer[v];
        return result;
    }

    /** Reference answer: BFS from every node and add up the distances. */
    static int[] bruteForce(int n, int[][] edges) {
        List<List<Integer>> adj = buildAdjacency(n, edges);
        int[] result = new int[n];
        for (int start = 0; start < n; start++) {
            int[] dist = new int[n];
            Arrays.fill(dist, -1);
            dist[start] = 0;
            ArrayDeque<Integer> queue = new ArrayDeque<>();
            queue.add(start);
            while (!queue.isEmpty()) {
                int v = queue.poll();
                result[start] += dist[v];
                for (int next : adj.get(v)) {
                    if (dist[next] < 0) { dist[next] = dist[v] + 1; queue.add(next); }
                }
            }
        }
        return result;
    }

    private static List<List<Integer>> buildAdjacency(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) { adj.get(e[0]).add(e[1]); adj.get(e[1]).add(e[0]); }
        return adj;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] star = {{0, 1}, {0, 2}, {2, 3}, {2, 4}, {2, 5}};
        check("6-node example", sumOfDistances(6, star), new int[]{8, 12, 6, 10, 10, 10});
        check("6-node brute",   bruteForce(6, star),     new int[]{8, 12, 6, 10, 10, 10});

        check("single node",    sumOfDistances(1, new int[][]{}), new int[]{0});
        check("two nodes",      sumOfDistances(2, new int[][]{{1, 0}}), new int[]{1, 1});

        int[][] path = {{0, 1}, {1, 2}, {2, 3}};
        check("path 0-1-2-3",   sumOfDistances(4, path), new int[]{6, 4, 4, 6});

        // Larger tree built by a fixed rule (each v hangs under some smaller node), compared with brute force.
        int n = 200;
        int[][] bigEdges = new int[n - 1][];
        for (int v = 1; v < n; v++) bigEdges[v - 1] = new int[]{v, (v * 37 + 11) % v};   // (..) % v is always < v, so this is a tree
        check("200-node tree vs brute", sumOfDistances(n, bigEdges), bruteForce(n, bigEdges));
    }

    private static void check(String label, int[] actual, int[] expected) {
        boolean ok = Arrays.equals(actual, expected);
        String status = ok ? "PASS" : "FAIL";
        String shown = actual.length <= 8 ? Arrays.toString(actual) : "(" + actual.length + " values)";
        System.out.println(status + "  " + label + " -> " + shown);
        if (!ok) System.exit(1);
    }
}
