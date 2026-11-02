/**
 * 2140. Solving Questions With Brainpower — Memoization (top-down)
 *
 * Idea: best(i) = most points obtainable from question i onward. Either skip i (go to i+1)
 * or solve it (earn points, jump to i + brainpower + 1). Cache best(i) per index.
 *
 * Time complexity:  O(n) — n states, each does O(1) work once.
 * Space complexity: O(n) — memo array plus a recursion stack up to depth n.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static int[][] questions;
    private static long[] memo;    // memo[i] = best points from questions i..n-1; -1 = unknown

    static long mostPoints(int[][] qs) {
        questions = qs;
        memo = new long[qs.length];
        Arrays.fill(memo, -1);
        return best(0);
    }

    private static long best(int i) {
        if (i >= questions.length) return 0;          // ran past the last question
        if (memo[i] != -1) return memo[i];
        long skip = best(i + 1);
        long solve = questions[i][0] + best(i + questions[i][1] + 1);
        memo[i] = Math.max(skip, solve);
        return memo[i];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example 1", mostPoints(new int[][]{{3, 2}, {4, 3}, {4, 4}, {2, 5}}), 5);
        check("example 2", mostPoints(new int[][]{{1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}}), 7);
        check("single question (edge)", mostPoints(new int[][]{{1, 10}}), 1);
        check("brainpower 0", mostPoints(new int[][]{{10, 0}, {10, 0}}), 20);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
