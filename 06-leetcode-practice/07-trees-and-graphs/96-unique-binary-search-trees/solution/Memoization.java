/**
 * 96. Unique Binary Search Trees — Memoization (top-down)
 *
 * Idea: the number of BST shapes depends only on how many values we arrange.
 * Choose each value as the root: (root-1) values go left, (k-root) go right, and
 * the two sides are independent, so countShapes(k) = sum of left * right.
 * Caching countShapes(k) turns the exponential recursion into a polynomial one.
 *
 * Time complexity:  O(n^2) — n distinct states, each loops over up to n roots.
 * Space complexity: O(n)   — the memo array plus a recursion stack of depth n.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[k] = number of BST shapes with k nodes; 0 means "not computed yet"
    // (every real answer is >= 1, so 0 is a safe sentinel)
    private static long[] memo;

    static long numTrees(int n) {
        memo = new long[n + 1];
        return countShapes(n);
    }

    private static long countShapes(int nodeCount) {
        if (nodeCount <= 1) return 1;          // empty tree or a single node: exactly one shape
        if (memo[nodeCount] != 0) return memo[nodeCount];
        long total = 0;
        for (int root = 1; root <= nodeCount; root++) {
            // left subtree gets root-1 values, right subtree gets nodeCount-root values
            total += countShapes(root - 1) * countShapes(nodeCount - root);
        }
        memo[nodeCount] = total;
        return total;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 3", numTrees(3), 5);
        check("n = 1", numTrees(1), 1);
        check("n = 0 (edge: empty tree)", numTrees(0), 1);
        check("n = 4", numTrees(4), 14);
        check("n = 19 (largest allowed, needs long)", numTrees(19), 1767263190L);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
