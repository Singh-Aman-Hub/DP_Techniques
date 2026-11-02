/**
 * House Robber III (LeetCode 337) — Optimized: post-order DFS returning a pair
 *
 * Idea: instead of a table, each recursive call RETURNS its two DP cells
 *       {take, skip} to its parent. Post-order (children first, then the node)
 *       is exactly the bottom-up order, so every value is used right after it
 *       is computed and never needs to be stored in a map.
 *
 * Time complexity:  O(n) — every node is visited once and does O(1) work.
 * Space complexity: O(h) — only the recursion stack (h = tree height); no maps.
 *
 * Run: java HouseRobberIIIOptimized.java
 */
import java.util.ArrayDeque;
import java.util.Queue;

public class HouseRobberIIIOptimized {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    static int rob(TreeNode root) {
        int[] best = solve(root);
        return Math.max(best[0], best[1]);
    }

    /** Returns {take, skip}: best loot in this subtree if node is robbed / not robbed. */
    private static int[] solve(TreeNode node) {
        if (node == null) return new int[]{0, 0};       // empty subtree: nothing either way

        int[] left = solve(node.left);                  // children FIRST (post-order) ...
        int[] right = solve(node.right);

        int take = node.val + left[1] + right[1];       // ... then the node itself
        int skip = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
        return new int[]{take, skip};
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
