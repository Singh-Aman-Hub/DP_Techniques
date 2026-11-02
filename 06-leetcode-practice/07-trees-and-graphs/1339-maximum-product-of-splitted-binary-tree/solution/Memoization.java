/**
 * 1339. Maximum Product of Splitted Binary Tree — recursive post-order tree DP
 *
 * Idea: cutting the edge above node v splits off exactly v's subtree. A post-order DFS
 * computes every subtree sum once (sub[v] = val + sub[left] + sub[right]) and stores it.
 * With total = sub[root], the best cut is max over v of sub[v] * (total - sub[v]).
 * Each subtree sum is "memoised" in the list and reused for the product step.
 *
 * Time complexity:  O(n) — every node is visited once, then n products are checked.
 * Space complexity: O(n) — the list of n subtree sums plus a recursion stack of height h.
 *
 * Run: java Memoization.java
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Memoization {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    private static final long MOD = 1_000_000_007L;

    static int maxProduct(TreeNode root) {
        // subtreeSums holds sub[v] for every node v (filled in post-order)
        List<Long> subtreeSums = new ArrayList<>();
        long total = subtreeSum(root, subtreeSums);
        long best = 0;
        for (long sum : subtreeSums) {
            best = Math.max(best, sum * (total - sum));   // compare real values, not modded ones
        }
        return (int) (best % MOD);
    }

    private static long subtreeSum(TreeNode node, List<Long> subtreeSums) {
        if (node == null) return 0;                       // empty subtree contributes nothing
        long sum = node.val + subtreeSum(node.left, subtreeSums) + subtreeSum(node.right, subtreeSums);
        subtreeSums.add(sum);
        return sum;
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

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,2,3,4,5,6]", maxProduct(build(new Integer[]{1, 2, 3, 4, 5, 6})), 110);
        check("[1,null,2,3,4,null,null,5,6]", maxProduct(build(new Integer[]{1, null, 2, 3, 4, null, null, 5, 6})), 90);
        check("[1,1] (edge: two nodes)", maxProduct(build(new Integer[]{1, 1})), 1);
        check("[2,3,9,10,7,8,6,5,4,11,1]", maxProduct(build(new Integer[]{2, 3, 9, 10, 7, 8, 6, 5, 4, 11, 1})), 1025);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
