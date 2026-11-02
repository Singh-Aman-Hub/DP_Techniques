/**
 * Diameter of Binary Tree (LeetCode 543) — post-order DFS with a global answer
 *
 * Idea: height(node) = edges on the longest downward path starting at node.
 *       The longest path whose HIGHEST point is node has length
 *       height(left)+1 + height(right)+1 (counting a missing child as -1).
 *       That "bent" path cannot be returned to the parent (a path cannot fork),
 *       so we record it in a global best, and RETURN only the straight height.
 *
 * Time complexity:  O(n) — each node is visited once with O(1) work.
 * Space complexity: O(h) — recursion stack only.
 *
 * Run: java DiameterOfBinaryTree.java
 */
import java.util.ArrayDeque;
import java.util.Queue;

public class DiameterOfBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    // best path length (in edges) seen at any node so far — the GLOBAL answer
    private static int bestDiameter;

    static int diameter(TreeNode root) {
        bestDiameter = 0;
        height(root);
        return bestDiameter;
    }

    /** Returns the number of edges on the longest downward path from node (-1 for null). */
    private static int height(TreeNode node) {
        if (node == null) return -1;                    // so that a leaf gets height -1 + 1 = 0
        int leftHeight = height(node.left);
        int rightHeight = height(node.right);

        // Path that bends at node: down-left arm + down-right arm.
        int throughNode = (leftHeight + 1) + (rightHeight + 1);
        bestDiameter = Math.max(bestDiameter, throughNode);

        // The parent can only extend ONE arm, so return the longer one.
        return 1 + Math.max(leftHeight, rightHeight);
    }

    static TreeNode build(Integer[] levelOrder) {
        if (levelOrder.length == 0 || levelOrder[0] == null) return null;
        TreeNode root = new TreeNode(levelOrder[0]);
        Queue<TreeNode> waiting = new ArrayDeque<>();
        waiting.add(root);
        int i = 1;
        while (i < levelOrder.length) {
            TreeNode parent = waiting.poll();
            if (levelOrder[i] != null) { parent.left = new TreeNode(levelOrder[i]); waiting.add(parent.left); }
            i++;
            if (i < levelOrder.length && levelOrder[i] != null) { parent.right = new TreeNode(levelOrder[i]); waiting.add(parent.right); }
            i++;
        }
        return root;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,2,3,4,5]", diameter(build(new Integer[]{1, 2, 3, 4, 5})), 3);
        check("[1,2]",       diameter(build(new Integer[]{1, 2})), 1);
        check("single node", diameter(build(new Integer[]{1})), 0);
        // Diameter does NOT pass through the root here: it lives inside the left subtree.
        check("bent below root", diameter(build(new Integer[]{1, 2, null, 3, 4, 5, null, null, 6, 7, null, null, 8})), 6);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
