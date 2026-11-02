/**
 * Partition Equal Subset Sum (LC 416) — One boolean array with a reverse loop
 *
 * Idea: same subset-sum DP compressed to one row. Loop sums from half DOWN to value so each
 * number is used at most once, and stop early as soon as half becomes reachable.
 *
 * Time complexity:  O(n * total/2) — worst case every number sweeps every sum.
 * Space complexity: O(total/2) — one boolean array.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static boolean canPartition(int[] nums) {
        int total = 0;
        for (int x : nums) total += x;
        if (total % 2 != 0) return false;           // an odd total cannot be split into two equal integers
        int half = total / 2;
        // reachable[s] = some subset of the numbers processed so far sums to s
        boolean[] reachable = new boolean[half + 1];
        reachable[0] = true;
        for (int value : nums) {
            for (int s = half; s >= value; s--) {
                if (reachable[s - value]) reachable[s] = true;
            }
            if (reachable[half]) return true;       // early exit: later numbers can only go in the other half
        }
        return reachable[half];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,5,11,5] -> {1,5,5} and {11}", canPartition(new int[]{1, 5, 11, 5}), true);
        check("[1,2,3,5] (total 11 is odd)", canPartition(new int[]{1, 2, 3, 5}), false);
        check("[1,2,5] (total 8, but no subset makes 4)", canPartition(new int[]{1, 2, 5}), false);
        check("[3,3,3,4,5] -> {4,5} and {3,3,3}", canPartition(new int[]{3, 3, 3, 4, 5}), true);
        check("[2,2] (edge: two equal items)", canPartition(new int[]{2, 2}), true);
        check("[1] (edge: a single element can never be split evenly)", canPartition(new int[]{1}), false);
    }

    private static void check(String label, boolean actual, boolean expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
