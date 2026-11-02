/**
 * 1339. Maximum Product of Splitted Binary Tree — iterative bottom-up tree DP
 *
 * Idea: a BFS lists parents before children. Walking that list backwards processes
 * children before parents, so sub[v] = val + sub[left] + sub[right] can be filled
 * without recursion (safe for very deep, skewed trees).
 *
 * Time complexity:  O(n) — one BFS, one backward pass, one pass over the sums.
 * Space complexity: O(n) — the BFS order array and the map of subtree sums.
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

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    private static final long MOD = 1_000_000_007L;

    static int maxProduct(TreeNode root) {
        // order = nodes in BFS order (every parent appears before its children)
        List<TreeNode> order = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            order.add(node);
            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }
        // subtreeSum.get(v) = sum of the subtree rooted at v
        Map<TreeNode, Long> subtreeSum = new HashMap<>();
        for (int i = order.size() - 1; i >= 0; i--) {   // backwards: children are already done
            TreeNode node = order.get(i);
            long sum = node.val;
            if (node.left != null) sum += subtreeSum.get(node.left);
            if (node.right != null) sum += subtreeSum.get(node.right);
            subtreeSum.put(node, sum);
        }
        long total = subtreeSum.get(root);
        long best = 0;
        for (long sum : subtreeSum.values()) best = Math.max(best, sum * (total - sum));
        return (int) (best % MOD);
    }

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

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,2,3,4,5,6]", maxProduct(build(new Integer[]{1, 2, 3, 4, 5, 6})), 110);
        check("[1,null,2,3,4,null,null,5,6]", maxProduct(build(new Integer[]{1, null, 2, 3, 4, null, null, 5, 6})), 90);
        check("[1,1] (edge: two nodes)", maxProduct(build(new Integer[]{1, 1})), 1);
        check("[2,3,9,10,7,8,6,5,4,11,1]", maxProduct(build(new Integer[]{2, 3, 9, 10, 7, 8, 6, 5, 4, 11, 1})), 1025);
        // deep skewed chain 1-2-...-50000 would overflow a small recursion stack; here it is fine
        TreeNode head = new TreeNode(10000);
        TreeNode cur = head;
        for (int i = 1; i < 50000; i++) { cur.right = new TreeNode(10000); cur = cur.right; }
        // total = 5e8, best split is 2.5e8 * 2.5e8 = 6.25e16, reduced mod 1e9+7 only at the end
        check("50,000-node chain (mod applied after max)", maxProduct(head), (250_000_000L * 250_000_000L) % MOD);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
