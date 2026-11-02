/**
 * 1857. Largest Color Value in a Directed Graph — Memoization (DFS + cycle detection)
 *
 * Idea: best[v][c] = most nodes of color c on any path that STARTS at v
 *                  = (color of v == c) + max over edges v->w of best[w][c].
 * Each node's 26-number vector is cached. A node marked ON_PATH that we reach again means
 * the DFS walked around a cycle, so the answer is -1.
 *
 * Time complexity:  O(26 * (n + m)) — each node once, each edge merges a 26-vector once.
 * Space complexity: O(26 * n + m) — vectors, adjacency lists, and recursion up to n deep.
 *
 * Run: java Memoization.java
 */
import java.util.ArrayList;
import java.util.List;

public class Memoization {

    private static final int UNVISITED = 0, ON_PATH = 1, DONE = 2;
    private static List<List<Integer>> successors;
    private static String nodeColors;
    private static int[] state;
    private static int[][] best;          // best[v][c] = max count of color c on a path starting at v
    private static boolean cycleFound;

    static int largestPathValue(String colors, int[][] edges) {
        int n = colors.length();
        successors = new ArrayList<>();
        for (int i = 0; i < n; i++) successors.add(new ArrayList<>());
        for (int[] e : edges) successors.get(e[0]).add(e[1]);
        nodeColors = colors;
        state = new int[n];
        best = new int[n][26];
        cycleFound = false;
        int answer = 0;
        for (int v = 0; v < n && !cycleFound; v++) {
            solve(v);
            for (int c = 0; c < 26; c++) answer = Math.max(answer, best[v][c]);
        }
        return cycleFound ? -1 : answer;
    }

    private static void solve(int node) {
        if (state[node] == DONE) return;                 // cached
        if (state[node] == ON_PATH) { cycleFound = true; return; }
        state[node] = ON_PATH;
        for (int next : successors.get(node)) {
            solve(next);
            if (cycleFound) return;
            for (int c = 0; c < 26; c++) best[node][c] = Math.max(best[node][c], best[next][c]);
        }
        best[node][nodeColors.charAt(node) - 'a']++;      // the node itself is on the path
        state[node] = DONE;
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
