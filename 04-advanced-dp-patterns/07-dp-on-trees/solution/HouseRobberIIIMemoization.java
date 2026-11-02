/**
 * House Robber III (LeetCode 337) — Memoization (top-down)
 *
 * Idea: the brute-force recursion asks rob(subtree) repeatedly. A subtree is
 *       identified by its root node, so we cache answers in a HashMap keyed by
 *       the node object. Each node's answer is then computed exactly once.
 *
 * Time complexity:  O(n) — n distinct states (one per node); each does O(1) work
 *                   besides looking up at most 6 cached children/grandchildren.
 * Space complexity: O(n) — the map holds one entry per node, plus O(h) stack.
 *
 * Run: java HouseRobberIIIMemoization.java
 */
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class HouseRobberIIIMemoization {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    // memo.get(node) = most money obtainable from the subtree rooted at node
    private static Map<TreeNode, Integer> memo;

    static int robTree(TreeNode root) {
        memo = new HashMap<>();
        return rob(root);
    }

    private static int rob(TreeNode node) {
        if (node == null) return 0;
        Integer cached = memo.get(node);
        if (cached != null) return cached;               // cache hit: this subtree was solved already

        int robThis = node.val;
        if (node.left != null)  robThis += rob(node.left.left)  + rob(node.left.right);
        if (node.right != null) robThis += rob(node.right.left) + rob(node.right.right);
        int skipThis = rob(node.left) + rob(node.right);

        int best = Math.max(robThis, skipThis);
        memo.put(node, best);
        return best;
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
        check("[3,2,3,null,3,null,1]", robTree(build(new Integer[]{3, 2, 3, null, 3, null, 1})), 7);
        check("[3,4,5,1,3,null,1]",    robTree(build(new Integer[]{3, 4, 5, 1, 3, null, 1})), 9);
        check("left chain 4-1-2-3",    robTree(build(new Integer[]{4, 1, null, 2, null, 3})), 7);
        check("single node [5]",       robTree(build(new Integer[]{5})), 5);
        check("empty tree",            robTree(build(new Integer[]{})), 0);

        // A 3000-node path would make the brute force hopeless; memoization is linear.
        TreeNode deep = new TreeNode(1);
        TreeNode cur = deep;
        for (int i = 2; i <= 3000; i++) { cur.left = new TreeNode(1); cur = cur.left; }
        check("path of 3000 ones", robTree(deep), 1500);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
