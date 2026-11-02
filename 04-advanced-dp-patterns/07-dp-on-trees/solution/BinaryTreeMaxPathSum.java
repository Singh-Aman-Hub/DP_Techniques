/**
 * Binary Tree Maximum Path Sum (LeetCode 124) — post-order DFS, global answer vs. returned value
 *
 * Idea: gain(node) = best sum of a straight downward path that STARTS at node
 *       (node plus at most one child's arm). A negative arm is worse than no arm,
 *       so each arm is clamped at 0. The best path whose highest node is `node`
 *       uses BOTH arms: node + leftArm + rightArm. That bent value goes into a
 *       global maximum; only the straight gain is returned to the parent.
 *       A slow reference version (bruteForce) recomputes every gain from scratch
 *       and is used to cross-check the fast one.
 *
 * Time complexity:  O(n) for maxPathSum — each node is visited once.
 *                   O(n^2) for bruteForce — every node recomputes its subtree's gains.
 * Space complexity: O(h) — recursion stack only.
 *
 * Run: java BinaryTreeMaxPathSum.java
 */
import java.util.ArrayDeque;
import java.util.Queue;

public class BinaryTreeMaxPathSum {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    // best path sum seen anywhere so far — the GLOBAL answer (not returned upward)
    private static int bestSum;

    static int maxPathSum(TreeNode root) {
        bestSum = Integer.MIN_VALUE;                    // all values may be negative, so no 0 start
        gain(root);
        return bestSum;
    }

    /** Best sum of a downward path starting at node (0 for null). */
    private static int gain(TreeNode node) {
        if (node == null) return 0;
        int leftArm = Math.max(0, gain(node.left));     // a negative arm is simply not used
        int rightArm = Math.max(0, gain(node.right));

        bestSum = Math.max(bestSum, node.val + leftArm + rightArm);   // path bending at node
        return node.val + Math.max(leftArm, rightArm);                // parent may extend one arm only
    }

    // ---------- slow reference: no reuse of child results ----------
    static int bruteForce(TreeNode root) {
        if (root == null) return Integer.MIN_VALUE;
        int bendHere = root.val + Math.max(0, downward(root.left)) + Math.max(0, downward(root.right));
        return Math.max(bendHere, Math.max(bruteForce(root.left), bruteForce(root.right)));
    }

    private static int downward(TreeNode node) {
        if (node == null) return 0;
        return node.val + Math.max(0, Math.max(downward(node.left), downward(node.right)));
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
        Integer[][] trees = {
            {1, 2, 3},
            {-10, 9, 20, null, null, 15, 7},
            {-3},
            {2, -1},
            {5, 4, 8, 11, null, 13, 4, 7, 2, null, null, null, 1},
        };
        int[] expected = {6, 42, -3, 2, 48};
        String[] labels = {"[1,2,3]", "[-10,9,20,null,null,15,7]", "single negative [-3]",
                           "[2,-1]", "11-level example"};
        for (int t = 0; t < trees.length; t++) {
            TreeNode root = build(trees[t]);
            check(labels[t] + " fast", maxPathSum(root), expected[t]);
            check(labels[t] + " brute", bruteForce(root), expected[t]);
        }
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
