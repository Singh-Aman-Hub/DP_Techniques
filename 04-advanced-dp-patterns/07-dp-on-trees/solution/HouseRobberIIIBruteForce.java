/**
 * House Robber III (LeetCode 337) — Brute force recursion
 *
 * Idea: rob(node) = the most money we can take from the subtree rooted at node.
 *       Either we rob node (then its children are forbidden, so we jump to the
 *       grandchildren) or we skip node (then we ask each child freely).
 *       The same subtree is asked for again and again (once by its parent,
 *       once by its grandparent), so the work explodes on deep trees.
 *
 * Time complexity:  exponential in the height of the tree — every node triggers
 *                   calls for its children AND its grandchildren, which grows like
 *                   Fibonacci numbers along a long path.
 * Space complexity: O(h) — the recursion stack is as deep as the tree height h.
 *
 * Run: java HouseRobberIIIBruteForce.java
 */
import java.util.ArrayDeque;
import java.util.Queue;

public class HouseRobberIIIBruteForce {

    /** A minimal binary-tree node, the same shape LeetCode uses. */
    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    static int rob(TreeNode node) {
        if (node == null) return 0;                      // empty subtree: nothing to steal

        // Choice 1: rob this house -> children are off-limits, continue at grandchildren.
        int robThis = node.val;
        if (node.left != null)  robThis += rob(node.left.left)  + rob(node.left.right);
        if (node.right != null) robThis += rob(node.right.left) + rob(node.right.right);

        // Choice 2: skip this house -> each child subtree is solved independently.
        int skipThis = rob(node.left) + rob(node.right);

        return Math.max(robThis, skipThis);
    }

    /** Builds a tree from LeetCode's level-order format; null means "no node here". */
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
        check("[3,2,3,null,3,null,1]", rob(build(new Integer[]{3, 2, 3, null, 3, null, 1})), 7);
        check("[3,4,5,1,3,null,1]",    rob(build(new Integer[]{3, 4, 5, 1, 3, null, 1})), 9);
        check("left chain 4-1-2-3",    rob(build(new Integer[]{4, 1, null, 2, null, 3})), 7);
        check("single node [5]",       rob(build(new Integer[]{5})), 5);
        check("empty tree",            rob(build(new Integer[]{})), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
