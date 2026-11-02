/**
 * Recursion Trees and Brute Force — "try every choice"
 *
 * Idea: Brute force explores every possible sequence of decisions. Written as
 *       recursion, each call is one node of a recursion tree and each recursive
 *       call is one branch (one choice). Three examples:
 *         - fib(n): two branches per node (n-1 and n-2);
 *         - countSubsetsWithSum: take/skip each array element, count the
 *           subsets whose sum equals a target;
 *         - maxSubsetSumAtMost: take/skip again, but keep the best total that
 *           does not exceed a limit (an "optimization" instead of a "count").
 *       We also count the nodes of each tree, which is exactly the running time.
 *
 * Time complexity:  fib O(2^n): at most 2^n nodes (exactly 2*fib(n+1) - 1).
 *                   take/skip O(2^n): a full binary tree of depth n has 2^(n+1) - 1 nodes.
 * Space complexity: O(n) — only one root-to-leaf path is on the call stack at a time.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    private static long nodeCount = 0;   // number of calls = number of nodes in the recursion tree

    static long fib(int n) {
        nodeCount++;
        if (n <= 1) return n;
        return fib(n - 1) + fib(n - 2);
    }

    /**
     * How many subsets of values[index..] add up to exactly `remaining`?
     * At each index we have two choices: take values[index] or skip it.
     */
    static long countSubsetsWithSum(int[] values, int index, int remaining) {
        nodeCount++;
        // Every element has been decided: this leaf is one complete subset.
        if (index == values.length) return (remaining == 0) ? 1 : 0;
        long take = countSubsetsWithSum(values, index + 1, remaining - values[index]);
        long skip = countSubsetsWithSum(values, index + 1, remaining);
        return take + skip;      // the two choices produce different subsets, so add
    }

    /**
     * Largest total we can collect from values[index..] without exceeding `limit`.
     * Same take/skip tree, but now we combine branches with max instead of +.
     */
    static int maxSubsetSumAtMost(int[] values, int index, int limit) {
        nodeCount++;
        if (index == values.length) return 0;
        int skip = maxSubsetSumAtMost(values, index + 1, limit);
        int take = Integer.MIN_VALUE;  // "impossible" unless the element fits
        if (values[index] <= limit) {
            take = values[index] + maxSubsetSumAtMost(values, index + 1, limit - values[index]);
        }
        return Math.max(take, skip);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        // fib: answers and tree sizes. nodes(n) = 1 + nodes(n-1) + nodes(n-2), nodes(0) = nodes(1) = 1
        nodeCount = 0; check("fib(1)  [edge]", fib(1), 1);  check("nodes in fib(1) tree", nodeCount, 1);
        nodeCount = 0; check("fib(4)", fib(4), 3);           check("nodes in fib(4) tree", nodeCount, 9);
        nodeCount = 0; check("fib(5)", fib(5), 5);           check("nodes in fib(5) tree", nodeCount, 15);
        nodeCount = 0; check("fib(20)", fib(20), 6765);      check("nodes in fib(20) tree", nodeCount, 21891);

        // take/skip on [1, 2, 3]: subsets with sum 3 are {1, 2} and {3}
        int[] small = {1, 2, 3};
        nodeCount = 0;
        check("subsets of [1,2,3] with sum 3", countSubsetsWithSum(small, 0, 3), 2);
        check("nodes in take/skip tree (n = 3)", nodeCount, 15);   // 2^(3+1) - 1
        check("subsets of [1,2,3] with sum 6", countSubsetsWithSum(small, 0, 6), 1);
        check("subsets of [1,2,3] with sum 0  [edge: only the empty set]", countSubsetsWithSum(small, 0, 0), 1);
        check("subsets of []  with sum 0  [edge: empty array]", countSubsetsWithSum(new int[]{}, 0, 0), 1);
        check("subsets of [2,4,6,10] with sum 16", countSubsetsWithSum(new int[]{2, 4, 6, 10}, 0, 16), 2);

        check("max subset sum of [1,2,3] at most 4", maxSubsetSumAtMost(small, 0, 4), 4);
        check("max subset sum of [5,3,9] at most 10", maxSubsetSumAtMost(new int[]{5, 3, 9}, 0, 10), 9);
        check("max subset sum of [7] at most 3  [edge: nothing fits]", maxSubsetSumAtMost(new int[]{7}, 0, 3), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
