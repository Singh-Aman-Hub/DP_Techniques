/**
 * 2246. Longest Path With Different Adjacent Characters — iterative bottom-up tree DP
 *
 * Idea: BFS from the root lists parents before children. Walking that list backwards, each
 * node's down value is final when we reach it, and it "pushes" that value into its parent's
 * top-two slots (only if the letters differ). No recursion, so 10^5-deep chains are safe.
 *
 * Time complexity:  O(n) — BFS plus one backward pass.
 * Space complexity: O(n) — order, children lists, and the best1/best2 arrays.
 *
 * Run: java Tabulation.java
 */
import java.util.ArrayList;
import java.util.List;

public class Tabulation {

    static int longestPath(int[] parent, String s) {
        int n = parent.length;
        List<List<Integer>> children = new ArrayList<>();
        for (int i = 0; i < n; i++) children.add(new ArrayList<>());
        for (int i = 1; i < n; i++) children.get(parent[i]).add(i);

        // BFS order: parents always before children
        int[] order = new int[n];
        int head = 0, tail = 0;
        order[tail++] = 0;
        while (head < tail) {
            int node = order[head++];
            for (int child : children.get(node)) order[tail++] = child;
        }

        // best1[v], best2[v] = two longest valid downward chains among v's children
        int[] best1 = new int[n];
        int[] best2 = new int[n];
        int bestPath = 1;
        for (int i = n - 1; i >= 0; i--) {
            int node = order[i];
            int downHere = 1 + best1[node];                    // all children already pushed
            bestPath = Math.max(bestPath, 1 + best1[node] + best2[node]);
            int up = parent[node];
            if (up >= 0 && s.charAt(up) != s.charAt(node)) {   // edge usable: offer chain to parent
                if (downHere > best1[up]) { best2[up] = best1[up]; best1[up] = downHere; }
                else if (downHere > best2[up]) best2[up] = downHere;
            }
        }
        return bestPath;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("parent=[-1,0,0,1,1,2], s=abacbe", longestPath(new int[]{-1, 0, 0, 1, 1, 2}, "abacbe"), 3);
        check("parent=[-1,0,0,0], s=aabc", longestPath(new int[]{-1, 0, 0, 0}, "aabc"), 3);
        check("single node (edge)", longestPath(new int[]{-1}, "z"), 1);
        check("chain with equal letters aaa", longestPath(new int[]{-1, 0, 1}, "aaa"), 1);
        // letters 0:a 1:b 2:c 3:a 4:c; nodes 3,4 hang under 1. Path 3-1-0-2 = a-b-a-c has 4 nodes
        check("path bending at the root, length 4", longestPath(new int[]{-1, 0, 0, 1, 1}, "abcac"), 4);
        // long chain alternating ab: whole chain is valid (tests deep trees)
        int n = 100000;
        int[] chainParent = new int[n];
        StringBuilder letters = new StringBuilder();
        for (int i = 0; i < n; i++) { chainParent[i] = i - 1; letters.append(i % 2 == 0 ? 'a' : 'b'); }
        check(n + "-node alternating chain (too deep for plain recursion)", longestPath(chainParent, letters.toString()), n);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
