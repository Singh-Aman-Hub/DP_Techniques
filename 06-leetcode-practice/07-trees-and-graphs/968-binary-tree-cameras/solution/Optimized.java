/**
 * 968. Binary Tree Cameras — greedy labels derived from the DP
 *
 * Idea: the DP shows a leaf never wants a camera (waiting = 0 < withCam = 1), so cameras are
 * pushed as high as possible. Post-order, each node returns one label:
 *   NOT_COVERED, HAS_CAMERA, COVERED.
 * If any child is NOT_COVERED we must place a camera here; if any child has a camera we are
 * covered; otherwise we wait for the parent. If the root ends NOT_COVERED, add one camera.
 *
 * Time complexity:  O(n) — one post-order pass.
 * Space complexity: O(h) — recursion stack only.
 *
 * Run: java Optimized.java
 */
import java.util.LinkedList;
import java.util.Queue;

public class Optimized {

    private static final int NOT_COVERED = 0, HAS_CAMERA = 1, COVERED = 2;
    private static int cameras;

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
        cameras = 0;
        if (label(root) == NOT_COVERED) cameras++;   // nobody above the root can watch it
        return cameras;
    }

    private static int label(TreeNode node) {
        if (node == null) return COVERED;            // empty spots never need a camera
        int left = label(node.left);
        int right = label(node.right);
        if (left == NOT_COVERED || right == NOT_COVERED) {
            cameras++;                               // a child depends on me
            return HAS_CAMERA;
        }
        if (left == HAS_CAMERA || right == HAS_CAMERA) return COVERED;
        return NOT_COVERED;                          // defer: my parent will place the camera
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
