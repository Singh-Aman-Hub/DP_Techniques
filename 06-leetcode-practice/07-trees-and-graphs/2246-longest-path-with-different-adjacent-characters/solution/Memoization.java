/**
 * 2246. Longest Path With Different Adjacent Characters — recursive post-order tree DP
 *
 * Idea: down(v) = nodes on the longest valid chain going down from v. A child c extends v's
 * chain only if s[c] != s[v]. At every node, the best path bending there joins the two best
 * valid child chains: 1 + best1 + best2. We pass only 1 + best1 up to the parent.
 *
 * Time complexity:  O(n) — each node and each parent-child edge is processed once.
 * Space complexity: O(n) — children lists plus a recursion stack up to n deep (chain).
 *
 * Run: java Memoization.java
 */
import java.util.ArrayList;
import java.util.List;

public class Memoization {

    private static List<List<Integer>> children;
    private static String letters;
    private static int bestPath;

    static int longestPath(int[] parent, String s) {
        int n = parent.length;
        children = new ArrayList<>();
        for (int i = 0; i < n; i++) children.add(new ArrayList<>());
        for (int i = 1; i < n; i++) children.get(parent[i]).add(i);
        letters = s;
        bestPath = 1;
        down(0);
        return bestPath;
    }

    // returns down(v): node count of the longest valid downward chain starting at v
    private static int down(int node) {
        int best1 = 0, best2 = 0;                 // two longest valid child chains
        for (int child : children.get(node)) {
            int chain = down(child);              // recurse even if the edge is unusable
            if (letters.charAt(child) == letters.charAt(node)) continue;
            if (chain > best1) { best2 = best1; best1 = chain; }
            else if (chain > best2) best2 = chain;
        }
        bestPath = Math.max(bestPath, 1 + best1 + best2);   // path that bends at this node
        return 1 + best1;
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
        int n = 2000;   // recursion depth = n; Tabulation.java tests a 100000-node chain
        int[] chainParent = new int[n];
        StringBuilder letters = new StringBuilder();
        for (int i = 0; i < n; i++) { chainParent[i] = i - 1; letters.append(i % 2 == 0 ? 'a' : 'b'); }
        check(n + "-node alternating chain", longestPath(chainParent, letters.toString()), n);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
