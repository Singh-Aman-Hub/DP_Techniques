/**
 * 1857. Largest Color Value in a Directed Graph — Tabulation (Kahn's topological order)
 *
 * Idea: count[v][c] = most nodes of color c on any path that ENDS at v. Process nodes in
 * topological order: when u is popped every predecessor has already pushed its vector, so add
 * u's own color and push the vector to each successor (element-wise max). If fewer than n nodes
 * are popped, the rest sit on a cycle and the answer is -1.
 *
 * Time complexity:  O(26 * (n + m)) — each pop and each edge handles a 26-vector.
 * Space complexity: O(26 * n + m) — the count table, adjacency lists, in-degrees, queue.
 *
 * Run: java Tabulation.java
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Tabulation {

    static int largestPathValue(String colors, int[][] edges) {
        int n = colors.length();
        List<List<Integer>> successors = new ArrayList<>();
        for (int i = 0; i < n; i++) successors.add(new ArrayList<>());
        int[] inDegree = new int[n];
        for (int[] e : edges) {
            successors.get(e[0]).add(e[1]);
            inDegree[e[1]]++;
        }
        // count[v][c] = max number of color-c nodes on a path ending at v
        int[][] count = new int[n][26];
        Deque<Integer> ready = new ArrayDeque<>();
        for (int v = 0; v < n; v++) if (inDegree[v] == 0) ready.add(v);
        int processed = 0, answer = 0;
        while (!ready.isEmpty()) {
            int node = ready.poll();
            processed++;
            count[node][colors.charAt(node) - 'a']++;   // all predecessors are in: add my own color
            for (int c = 0; c < 26; c++) answer = Math.max(answer, count[node][c]);
            for (int next : successors.get(node)) {
                for (int c = 0; c < 26; c++) count[next][c] = Math.max(count[next][c], count[node][c]);
                if (--inDegree[next] == 0) ready.add(next);
            }
        }
        return processed < n ? -1 : answer;              // unprocessed nodes are stuck in a cycle
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("abaca, 0->1,0->2,2->3,3->4", largestPathValue("abaca", new int[][]{{0, 1}, {0, 2}, {2, 3}, {3, 4}}), 3);
        check("self-loop is a cycle", largestPathValue("a", new int[][]{{0, 0}}), -1);
        check("single node, no edges (edge)", largestPathValue("a", new int[][]{}), 1);
        check("two-node cycle", largestPathValue("ab", new int[][]{{0, 1}, {1, 0}}), -1);
        check("chain aaa", largestPathValue("aaa", new int[][]{{0, 1}, {1, 2}}), 3);
        // the best color differs by predecessor: 0(a)->2, 1(b)->2(b) -> path 1->2 has two b's
        check("best color depends on predecessor", largestPathValue("abb", new int[][]{{0, 2}, {1, 2}}), 2);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
