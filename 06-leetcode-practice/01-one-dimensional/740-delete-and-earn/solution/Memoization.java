/**
 * 740. Delete and Earn — Memoization (top-down)
 *
 * Idea: Bucket the input so points[v] = v * count(v). Choosing value v forbids v-1 and v+1,
 * so the problem becomes House Robber on the points array: best(v) = max(skip v, take v).
 *
 * Time complexity:  O(n + M) — O(n) to bucket, then M+1 states each solved once in O(1).
 * Space complexity: O(M) — points array, memo array and a recursion stack of depth M.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static int[] points;   // points[v] = total earned by taking every copy of value v
    private static int[] memo;     // memo[v] = best total using only values 0..v; -1 = unknown

    static int deleteAndEarn(int[] nums) {
        int maxValue = 0;
        for (int x : nums) maxValue = Math.max(maxValue, x);
        points = new int[maxValue + 1];
        for (int x : nums) points[x] += x;
        memo = new int[maxValue + 1];
        Arrays.fill(memo, -1);
        return best(maxValue);
    }

    private static int best(int v) {
        if (v < 0) return 0;                       // no values left to choose from
        if (memo[v] != -1) return memo[v];
        int skip = best(v - 1);
        int take = best(v - 2) + points[v];        // taking v destroys v-1, so jump to v-2
        memo[v] = Math.max(skip, take);
        return memo[v];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[3,4,2]", deleteAndEarn(new int[]{3, 4, 2}), 6);
        check("[2,2,3,3,3,4]", deleteAndEarn(new int[]{2, 2, 3, 3, 3, 4}), 9);
        check("[1] (edge)", deleteAndEarn(new int[]{1}), 1);
        check("[1,1,1,2,4,5,5,5,6]", deleteAndEarn(new int[]{1, 1, 1, 2, 4, 5, 5, 5, 6}), 18);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
