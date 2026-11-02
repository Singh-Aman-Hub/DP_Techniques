/**
 * Target Sum (LC 494) — One counting array with a reverse loop
 *
 * Idea: same "count subsets with sum s1" DP in one row. Iterate s from s1 DOWN to value so
 * each number is used at most once. For value = 0 the loop runs down to s = 0 and does
 * ways[s] += ways[s], which doubles every count — exactly the '+0' / '-0' choice.
 *
 * Time complexity:  O(n * s1) — each number sweeps the sums once.
 * Space complexity: O(s1) — one int array.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int findTargetSumWays(int[] nums, int target) {
        int total = 0;
        for (int x : nums) total += x;
        // s1 - s2 = target and s1 + s2 = total  =>  s1 = (total + target) / 2
        if (Math.abs(target) > total) return 0;          // even all-plus or all-minus cannot reach it
        if ((total + target) % 2 != 0) return 0;         // s1 would not be a whole number
        int s1 = (total + target) / 2;                   // 0 <= s1 <= total thanks to the checks above
        // ways[s] = number of subsets of the numbers processed so far that sum to s
        int[] ways = new int[s1 + 1];
        ways[0] = 1;
        for (int value : nums) {
            for (int s = s1; s >= value; s--) {
                ways[s] += ways[s - value];
            }
        }
        return ways[s1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,1,1,1,1], target 3", findTargetSumWays(new int[]{1, 1, 1, 1, 1}, 3), 5);
        check("[1,1,1,1,1], target -3 (negative target)", findTargetSumWays(new int[]{1, 1, 1, 1, 1}, -3), 5);
        check("[1,2,1], target 0 (+1-2+1 and -1+2-1)", findTargetSumWays(new int[]{1, 2, 1}, 0), 2);
        check("[0,0,1], target 1 (each zero doubles the count)", findTargetSumWays(new int[]{0, 0, 1}, 1), 4);
        check("[2], target 1 (parity: total + target is odd)", findTargetSumWays(new int[]{2}, 1), 0);
        check("[100], target -200 (|target| > total)", findTargetSumWays(new int[]{100}, -200), 0);
        check("[1], target 1 (edge: single element)", findTargetSumWays(new int[]{1}, 1), 1);
        check("[0,0,0,0,0,0,0,0,1], target 1 (2^8 = 256)", findTargetSumWays(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1}, 1), 256);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
