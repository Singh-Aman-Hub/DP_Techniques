/**
 * House Robber III (LeetCode 337) — Tabulation (bottom-up, iterative)
 *
 * Idea: on a tree, "bottom-up" literally means leaves first, root last.
 *       A breadth-first (level-order) listing puts every parent before its
 *       children, so walking that list BACKWARDS visits every child before its
 *       parent: a valid computation order with no recursion at all.
 *       For each node we fill two table cells:
 *         take[node] = best loot in node's subtree if node IS robbed
 *         skip[node] = best loot in node's subtree if node is NOT robbed
 *
 * Time complexity:  O(n) — one BFS pass plus one backward pass, O(1) per node.
 * Space complexity: O(n) — the order list and the two tables (maps) per node.
 *
 * Run: java HouseRobberIIITabulation.java
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class HouseRobberIIITabulation {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    static int rob(TreeNode root) {
        if (root == null) return 0;

        // 1) Level-order list: parents always appear before their children.
        List<TreeNode> order = new ArrayList<>();
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            order.add(node);
            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }

        // take.get(v) / skip.get(v): the two DP cells for node v (see header).
        Map<TreeNode, Integer> take = new HashMap<>();
        Map<TreeNode, Integer> skip = new HashMap<>();

        // 2) Backwards: every child is finished before its parent is processed.
        for (int i = order.size() - 1; i >= 0; i--) {
            TreeNode node = order.get(i);
            int leftTake = 0, leftSkip = 0, rightTake = 0, rightSkip = 0;   // a missing child contributes 0
            if (node.left != null)  { leftTake = take.get(node.left);   leftSkip = skip.get(node.left); }
            if (node.right != null) { rightTake = take.get(node.right); rightSkip = skip.get(node.right); }

            // Robbing node: both children must be skipped.
            take.put(node, node.val + leftSkip + rightSkip);
            // Not robbing node: each child picks whichever of its own two options is better.
            skip.put(node, Math.max(leftTake, leftSkip) + Math.max(rightTake, rightSkip));
        }
        return Math.max(take.get(root), skip.get(root));
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
