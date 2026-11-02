/**
 * 968. Binary Tree Cameras — iterative 3-state tree DP (bottom-up)
 *
 * Idea: list nodes in BFS order (parents first), then walk the list backwards so both
 * children are finished before their parent, filling {withCam, watched, waiting} per node.
 *
 * Time complexity:  O(n) — one BFS and one backward pass with O(1) work per node.
 * Space complexity: O(n) — the BFS list and one triple per node.
 *
 * Run: java Tabulation.java
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Tabulation {

    private static final int INF = 1_000_000;
    private static final int[] NULL_STATES = {INF, 0, INF};   // a missing child: watched, no camera possible

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
        List<TreeNode> order = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            order.add(node);
            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }
        // states.get(v) = {withCam, watched, waiting} for v's subtree
        Map<TreeNode, int[]> states = new HashMap<>();
        for (int i = order.size() - 1; i >= 0; i--) {
            TreeNode node = order.get(i);
            int[] left = node.left == null ? NULL_STATES : states.get(node.left);
            int[] right = node.right == null ? NULL_STATES : states.get(node.right);
            int withCam = 1 + min3(left) + min3(right);
            int watched = Math.min(left[0] + Math.min(right[0], right[1]),
                                   right[0] + Math.min(left[0], left[1]));
            int waiting = left[1] + right[1];
            states.put(node, new int[]{Math.min(withCam, INF), Math.min(watched, INF), Math.min(waiting, INF)});
        }
        int[] rootStates = states.get(root);
        return Math.min(rootStates[0], rootStates[1]);
    }

    private static int min3(int[] s) {
        return Math.min(s[0], Math.min(s[1], s[2]));
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
