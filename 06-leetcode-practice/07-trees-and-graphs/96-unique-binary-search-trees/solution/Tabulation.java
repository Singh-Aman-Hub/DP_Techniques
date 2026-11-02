/**
 * 96. Unique Binary Search Trees — Tabulation (bottom-up)
 *
 * Idea: ways[k] needs only ways[0..k-1], so fill the table for k = 0, 1, ..., n.
 * For each k, try every root; left side has root-1 nodes, right side k-root nodes.
 *
 * Time complexity:  O(n^2) — for each of n sizes we try up to n roots.
 * Space complexity: O(n)   — one entry per size.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static long numTrees(int n) {
        // ways[k] = number of structurally different BSTs on k consecutive values
        long[] ways = new long[n + 1];
        ways[0] = 1;                              // the empty tree is one arrangement
        for (int nodeCount = 1; nodeCount <= n; nodeCount++) {
            for (int root = 1; root <= nodeCount; root++) {
                ways[nodeCount] += ways[root - 1] * ways[nodeCount - root];
            }
        }
        return ways[n];
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
