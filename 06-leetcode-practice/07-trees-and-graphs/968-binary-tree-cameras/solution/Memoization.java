/**
 * 968. Binary Tree Cameras — recursive 3-state tree DP (post-order)
 *
 * Idea: for every node return three minima over its subtree (everything below the node watched):
 *   [0] withCam  - node has a camera
 *   [1] watched  - no camera on node, but a child has one
 *   [2] waiting  - node not watched yet; its parent must place a camera
 * A parent combines its two children's triples in O(1). Answer = min(withCam, watched) at the root.
 *
 * Time complexity:  O(n) — each node is visited once and does constant work.
 * Space complexity: O(h) — recursion stack of height h (h = n for a chain).
 *
 * Run: java Memoization.java
 */
import java.util.LinkedList;
import java.util.Queue;

public class Memoization {

    // large enough to mean "impossible", small enough that INF + INF + 1 fits in an int
    private static final int INF = 1_000_000;

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    // Builds a tree from LeetCode-style level order (null = missing child).
    static TreeNode build(Integer[] levelOrder) {
        if (levelOrder.length == 0 || levelOrder[0] == null) return null;
        TreeNode root = new TreeNode(levelOrder[0]);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        int i = 1;
        while (!queue.isEmpty() && i < levelOrder.length) {
            TreeNode parent = queue.poll();
            if (i < levelOrder.length && levelOrder[i] != null) { parent.left = new TreeNode(levelOrder[i]); queue.add(parent.left); }
            i++;
            if (i < levelOrder.length && levelOrder[i] != null) { parent.right = new TreeNode(levelOrder[i]); queue.add(parent.right); }
            i++;
        }
        return root;
    }

    static int minCameraCover(TreeNode root) {
        int[] rootStates = solve(root);
        return Math.min(rootStates[0], rootStates[1]);   // the root has no parent to rescue it
    }

    // returns {withCam, watched, waiting} for the subtree rooted at node
    private static int[] solve(TreeNode node) {
        if (node == null) return new int[]{INF, 0, INF};  // nothing to watch, no camera possible
        int[] left = solve(node.left);
        int[] right = solve(node.right);
        // my camera watches both children, so they may be in any state (even waiting)
        int withCam = 1 + min3(left) + min3(right);
        // at least one child has a camera; the other must be watched by itself (not waiting)
        int watched = Math.min(left[0] + Math.min(right[0], right[1]),
                               right[0] + Math.min(left[0], left[1]));
        // no help from children: they must be watched without cameras
        int waiting = left[1] + right[1];
        return new int[]{Math.min(withCam, INF), Math.min(watched, INF), Math.min(waiting, INF)};
    }

    private static int min3(int[] states) {
        return Math.min(states[0], Math.min(states[1], states[2]));
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[0,0,null,0,0]", minCameraCover(build(new Integer[]{0, 0, null, 0, 0})), 1);
        check("[0,0,null,0,null,0,null,null,0] (chain of 5)", minCameraCover(build(new Integer[]{0, 0, null, 0, null, 0, null, null, 0})), 2);
        check("[0] (edge: single node)", minCameraCover(build(new Integer[]{0})), 1);
        check("perfect tree of 7 nodes", minCameraCover(build(new Integer[]{0, 0, 0, 0, 0, 0, 0})), 2);
        check("[0,0] (two nodes)", minCameraCover(build(new Integer[]{0, 0})), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
