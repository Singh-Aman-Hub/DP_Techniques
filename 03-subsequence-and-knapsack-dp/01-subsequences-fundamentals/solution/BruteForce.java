/**
 * Count Subsequences With Sum K — Brute force (take/skip recursion)
 *
 * Idea: walk through the array and, at each index, either take the element (adding it to
 * the running sum) or skip it. At the end, a path counts as 1 if its running sum equals K.
 * The answer is the number of root-to-leaf paths that end with sum K.
 *
 * Time complexity:  O(2^n) — every index doubles the number of paths; each call does O(1) work.
 * Space complexity: O(n) — the recursion is at most n + 1 calls deep.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static long countWithSum(int[] arr, int k) {
        return count(arr, 0, 0, k);
    }

    // Number of ways to finish the decisions for arr[index..n-1], given sumSoFar from earlier choices
    private static long count(int[] arr, int index, int sumSoFar, int k) {
        if (index == arr.length) {
            return (sumSoFar == k) ? 1 : 0;   // one finished subsequence: does it hit K?
        }
        long take = count(arr, index + 1, sumSoFar + arr[index], k);
        long skip = count(arr, index + 1, sumSoFar, k);
        return take + skip;                    // the two choices produce different subsequences, so add
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,2,3], K=3  ({1,2},{3})", countWithSum(new int[]{1, 2, 3}, 3), 2);
        check("[1,2,1], K=2  ({2},{1,1})", countWithSum(new int[]{1, 2, 1}, 2), 2);
        check("[1,1,1,1], K=2  (choose 2 of 4)", countWithSum(new int[]{1, 1, 1, 1}, 2), 6);
        check("[0,1], K=1  ({1},{0,1})", countWithSum(new int[]{0, 1}, 1), 2);
        check("[1,2,3], K=0  (only the empty subsequence)", countWithSum(new int[]{1, 2, 3}, 0), 1);
        check("[1,2,3], K=7  (total is only 6)", countWithSum(new int[]{1, 2, 3}, 7), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
